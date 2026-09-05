from __future__ import annotations

import json
import os
import sqlite3
from contextlib import contextmanager
from datetime import datetime, timezone
from pathlib import Path
from typing import Any, Dict, Iterable, List, Optional

import pandas as pd

from .ml_training import DATASET_PATH, ensure_model_artifacts

BASE_DIR = Path(__file__).resolve().parents[1]
DB_PATH = Path(os.getenv("PAYPULSE_DB_PATH", BASE_DIR / "paypulse.db"))


def now_iso() -> str:
    return datetime.now(timezone.utc).isoformat()


@contextmanager
def connect() -> Iterable[sqlite3.Connection]:
    DB_PATH.parent.mkdir(parents=True, exist_ok=True)
    conn = sqlite3.connect(DB_PATH)
    conn.row_factory = sqlite3.Row
    try:
        yield conn
        conn.commit()
    finally:
        conn.close()


def init_db() -> None:
    with connect() as conn:
        conn.executescript(
            """
            CREATE TABLE IF NOT EXISTS transactions (
                transaction_id TEXT PRIMARY KEY,
                customer_name TEXT NOT NULL,
                amount REAL NOT NULL,
                payment_method TEXT NOT NULL,
                gateway TEXT NOT NULL,
                failure_reason TEXT NOT NULL,
                customer_payment_history INTEGER NOT NULL,
                customer_success_rate REAL NOT NULL,
                retry_count INTEGER NOT NULL,
                subscription_status TEXT NOT NULL,
                checkout_abandonment INTEGER NOT NULL,
                gateway_health REAL NOT NULL,
                recent_failure_rate REAL NOT NULL,
                recent_failure_spike INTEGER NOT NULL,
                previous_recovery_history INTEGER NOT NULL,
                recovered INTEGER NOT NULL DEFAULT 0,
                status TEXT NOT NULL DEFAULT 'pending_recovery',
                created_at TEXT NOT NULL,
                updated_at TEXT NOT NULL
            );

            CREATE TABLE IF NOT EXISTS policies (
                id INTEGER PRIMARY KEY CHECK (id = 1),
                min_recovery_probability REAL NOT NULL,
                max_retry_count INTEGER NOT NULL,
                max_automatic_recovery_amount REAL NOT NULL,
                approval_threshold REAL NOT NULL,
                escalation_probability REAL NOT NULL,
                block_hard_declines INTEGER NOT NULL,
                updated_at TEXT NOT NULL
            );

            CREATE TABLE IF NOT EXISTS audit_log (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                timestamp TEXT NOT NULL,
                event_type TEXT NOT NULL,
                transaction_id TEXT,
                status TEXT NOT NULL,
                details TEXT NOT NULL
            );

            CREATE TABLE IF NOT EXISTS webhook_events (
                event_id TEXT PRIMARY KEY,
                event_name TEXT NOT NULL,
                received_at TEXT NOT NULL,
                payload TEXT NOT NULL
            );
            """
        )
        conn.execute(
            """
            INSERT OR IGNORE INTO policies (
                id, min_recovery_probability, max_retry_count, max_automatic_recovery_amount,
                approval_threshold, escalation_probability, block_hard_declines, updated_at
            ) VALUES (1, 0.70, 2, 50000, 25000, 0.45, 1, ?)
            """,
            (now_iso(),),
        )


def seed_transactions(limit: int = 250) -> None:
    ensure_model_artifacts()
    df = pd.read_csv(DATASET_PATH).head(limit)
    with connect() as conn:
        count = conn.execute("SELECT COUNT(*) AS total FROM transactions").fetchone()["total"]
        if count:
            return
        timestamp = now_iso()
        for row in df.to_dict(orient="records"):
            conn.execute(
                """
                INSERT INTO transactions (
                    transaction_id, customer_name, amount, payment_method, gateway, failure_reason,
                    customer_payment_history, customer_success_rate, retry_count, subscription_status,
                    checkout_abandonment, gateway_health, recent_failure_rate, recent_failure_spike,
                    previous_recovery_history, recovered, status, created_at, updated_at
                ) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
                """,
                (
                    row["transaction_id"],
                    row["customer_name"],
                    row["amount"],
                    row["payment_method"],
                    row["gateway"],
                    row["failure_reason"],
                    row["customer_payment_history"],
                    row["customer_success_rate"],
                    row["retry_count"],
                    row["subscription_status"],
                    row["checkout_abandonment"],
                    row["gateway_health"],
                    row["recent_failure_rate"],
                    row["recent_failure_spike"],
                    row["previous_recovery_history"],
                    row["recovered"],
                    "pending_recovery",
                    timestamp,
                    timestamp,
                ),
            )


def row_to_dict(row: sqlite3.Row) -> Dict[str, Any]:
    return dict(row)


def get_transaction(transaction_id: str) -> Optional[Dict[str, Any]]:
    with connect() as conn:
        row = conn.execute("SELECT * FROM transactions WHERE transaction_id = ?", (transaction_id,)).fetchone()
        return row_to_dict(row) if row else None


def list_transactions(limit: int = 50, status: Optional[str] = None) -> List[Dict[str, Any]]:
    with connect() as conn:
        if status:
            rows = conn.execute(
                "SELECT * FROM transactions WHERE status = ? ORDER BY amount DESC LIMIT ?",
                (status, limit),
            ).fetchall()
        else:
            rows = conn.execute("SELECT * FROM transactions ORDER BY amount DESC LIMIT ?", (limit,)).fetchall()
        return [row_to_dict(row) for row in rows]


def update_transaction_status(transaction_id: str, status: str, recovered: Optional[int] = None) -> None:
    with connect() as conn:
        if recovered is None:
            conn.execute(
                "UPDATE transactions SET status = ?, updated_at = ? WHERE transaction_id = ?",
                (status, now_iso(), transaction_id),
            )
        else:
            conn.execute(
                "UPDATE transactions SET status = ?, recovered = ?, updated_at = ? WHERE transaction_id = ?",
                (status, recovered, now_iso(), transaction_id),
            )


def get_policy() -> Dict[str, Any]:
    with connect() as conn:
        return row_to_dict(conn.execute("SELECT * FROM policies WHERE id = 1").fetchone())


def update_policy(values: Dict[str, Any]) -> Dict[str, Any]:
    allowed = {
        "min_recovery_probability",
        "max_retry_count",
        "max_automatic_recovery_amount",
        "approval_threshold",
        "escalation_probability",
        "block_hard_declines",
    }
    updates = {key: value for key, value in values.items() if key in allowed and value is not None}
    if not updates:
        return get_policy()
    assignments = ", ".join(f"{key} = ?" for key in updates)
    params = list(updates.values()) + [now_iso()]
    with connect() as conn:
        conn.execute(f"UPDATE policies SET {assignments}, updated_at = ? WHERE id = 1", params)
    return get_policy()


def add_audit_event(event_type: str, status: str, details: Dict[str, Any], transaction_id: str | None = None) -> None:
    with connect() as conn:
        conn.execute(
            "INSERT INTO audit_log (timestamp, event_type, transaction_id, status, details) VALUES (?, ?, ?, ?, ?)",
            (now_iso(), event_type, transaction_id, status, json.dumps(details, default=str)),
        )


def list_audit_events(limit: int = 100) -> List[Dict[str, Any]]:
    with connect() as conn:
        rows = conn.execute("SELECT * FROM audit_log ORDER BY id DESC LIMIT ?", (limit,)).fetchall()
    events = [row_to_dict(row) for row in rows]
    for event in events:
        event["details"] = json.loads(event["details"])
    return events


def save_webhook_event(event_id: str, event_name: str, payload: Dict[str, Any]) -> bool:
    try:
        with connect() as conn:
            conn.execute(
                "INSERT INTO webhook_events (event_id, event_name, received_at, payload) VALUES (?, ?, ?, ?)",
                (event_id, event_name, now_iso(), json.dumps(payload)),
            )
        return True
    except sqlite3.IntegrityError:
        return False
