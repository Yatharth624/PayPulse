from __future__ import annotations

import json
import math
import os
import random
from dataclasses import dataclass
from pathlib import Path
from typing import Any, Dict, List, Tuple

import joblib
import numpy as np
import pandas as pd
from sklearn.compose import ColumnTransformer
from sklearn.ensemble import RandomForestClassifier
from sklearn.linear_model import LogisticRegression
from sklearn.metrics import (
    confusion_matrix,
    f1_score,
    precision_score,
    recall_score,
    roc_auc_score,
)
from sklearn.model_selection import train_test_split
from sklearn.pipeline import Pipeline
from sklearn.preprocessing import OneHotEncoder, StandardScaler

try:
    from xgboost import XGBClassifier
except Exception:  # pragma: no cover - dependency may be installed later.
    XGBClassifier = None


BASE_DIR = Path(__file__).resolve().parents[1]
ARTIFACT_DIR = BASE_DIR / "artifacts"
DATASET_PATH = ARTIFACT_DIR / "synthetic_transactions.csv"
MODEL_PATH = ARTIFACT_DIR / "recovery_model.joblib"
REPORT_PATH = ARTIFACT_DIR / "training_report.json"

CATEGORICAL_FEATURES = [
    "payment_method",
    "gateway",
    "failure_reason",
    "subscription_status",
]

NUMERIC_FEATURES = [
    "amount",
    "customer_payment_history",
    "customer_success_rate",
    "retry_count",
    "checkout_abandonment",
    "gateway_health",
    "recent_failure_rate",
    "recent_failure_spike",
    "previous_recovery_history",
]

FEATURE_COLUMNS = NUMERIC_FEATURES + CATEGORICAL_FEATURES
TARGET_COLUMN = "recovered"


def _sigmoid(value: float) -> float:
    return 1.0 / (1.0 + math.exp(-value))


def generate_synthetic_transactions(n_rows: int = 8000, seed: int = 42) -> pd.DataFrame:
    rng = np.random.default_rng(seed)
    random.seed(seed)

    payment_methods = ["upi", "card", "netbanking", "wallet", "mandate"]
    gateways = ["razorpay", "bank_gateway", "upi_switch", "card_network"]
    failure_reasons = [
        "upi_timeout",
        "insufficient_funds",
        "bank_decline",
        "gateway_timeout",
        "authentication_failed",
        "checkout_abandoned",
        "mandate_failed",
    ]
    subscription_statuses = ["none", "active", "past_due", "cancelled"]

    rows: List[Dict[str, Any]] = []
    for i in range(n_rows):
        amount = float(np.clip(rng.lognormal(mean=8.25, sigma=0.75), 199, 125000))
        payment_method = rng.choice(payment_methods, p=[0.43, 0.25, 0.14, 0.08, 0.10])
        gateway = rng.choice(gateways, p=[0.52, 0.18, 0.20, 0.10])

        if payment_method == "mandate":
            failure_reason = rng.choice(failure_reasons, p=[0.08, 0.15, 0.17, 0.11, 0.10, 0.04, 0.35])
            subscription_status = rng.choice(subscription_statuses, p=[0.02, 0.55, 0.35, 0.08])
        elif payment_method == "upi":
            failure_reason = rng.choice(failure_reasons, p=[0.30, 0.10, 0.14, 0.24, 0.08, 0.12, 0.02])
            subscription_status = rng.choice(subscription_statuses, p=[0.74, 0.14, 0.08, 0.04])
        else:
            failure_reason = rng.choice(failure_reasons, p=[0.04, 0.26, 0.20, 0.12, 0.18, 0.18, 0.02])
            subscription_status = rng.choice(subscription_statuses, p=[0.78, 0.10, 0.08, 0.04])

        customer_payment_history = int(rng.poisson(4) + rng.integers(0, 4))
        previous_recovery_history = int(np.clip(rng.poisson(1.2), 0, 8))
        customer_success_rate = float(np.clip(rng.beta(7, 3), 0.05, 0.99))
        retry_count = int(rng.choice([0, 1, 2, 3, 4], p=[0.32, 0.34, 0.20, 0.10, 0.04]))
        checkout_abandonment = int(failure_reason == "checkout_abandoned" or rng.random() < 0.08)
        gateway_health = float(np.clip(rng.normal(0.86, 0.11), 0.35, 0.99))
        recent_failure_rate = float(np.clip(rng.beta(2.2, 7.5), 0.01, 0.92))
        recent_failure_spike = int(rng.random() < (0.11 + recent_failure_rate * 0.45))

        score = -0.55
        score += 2.15 * (customer_success_rate - 0.55)
        score += 0.14 * min(customer_payment_history, 12)
        score += 0.28 * min(previous_recovery_history, 5)
        score += 1.10 * (gateway_health - 0.70)
        score -= 0.78 * retry_count
        score -= 1.28 * recent_failure_rate
        score -= 0.000012 * amount
        score += 0.52 if failure_reason in {"upi_timeout", "gateway_timeout"} and recent_failure_spike else 0.0
        score += 0.48 if failure_reason == "checkout_abandoned" and checkout_abandonment else 0.0
        score += 0.32 if subscription_status == "active" and payment_method == "mandate" else 0.0
        score -= 1.00 if failure_reason in {"insufficient_funds", "bank_decline", "authentication_failed"} else 0.0
        score -= 0.62 if subscription_status == "cancelled" else 0.0
        score += {"upi": 0.18, "wallet": 0.22, "mandate": 0.10, "netbanking": -0.08, "card": -0.02}[payment_method]

        recovery_probability = _sigmoid(score)
        recovered = int(rng.random() < recovery_probability)

        rows.append(
            {
                "transaction_id": f"TXN-{100000 + i}",
                "customer_name": f"Customer {1000 + i}",
                "amount": round(amount, 2),
                "payment_method": payment_method,
                "gateway": gateway,
                "failure_reason": failure_reason,
                "customer_payment_history": customer_payment_history,
                "customer_success_rate": round(customer_success_rate, 4),
                "retry_count": retry_count,
                "subscription_status": subscription_status,
                "checkout_abandonment": checkout_abandonment,
                "gateway_health": round(gateway_health, 4),
                "recent_failure_rate": round(recent_failure_rate, 4),
                "recent_failure_spike": recent_failure_spike,
                "previous_recovery_history": previous_recovery_history,
                "recovered": recovered,
            }
        )

    return pd.DataFrame(rows)


def make_preprocessor() -> ColumnTransformer:
    return ColumnTransformer(
        transformers=[
            ("numeric", StandardScaler(), NUMERIC_FEATURES),
            ("categorical", OneHotEncoder(handle_unknown="ignore"), CATEGORICAL_FEATURES),
        ]
    )


def _false_positive_cost(y_true: np.ndarray, y_pred: np.ndarray, amounts: np.ndarray) -> float:
    false_positive_mask = (y_pred == 1) & (y_true == 0)
    return float((amounts[false_positive_mask] * 0.015 + 12.0).sum())


def _evaluate(model: Pipeline, x: pd.DataFrame, y: pd.Series) -> Tuple[Dict[str, Any], np.ndarray, np.ndarray]:
    probabilities = model.predict_proba(x)[:, 1]
    predictions = (probabilities >= 0.55).astype(int)
    matrix = confusion_matrix(y, predictions)
    metrics = {
        "precision": round(float(precision_score(y, predictions, zero_division=0)), 4),
        "recall": round(float(recall_score(y, predictions, zero_division=0)), 4),
        "f1": round(float(f1_score(y, predictions, zero_division=0)), 4),
        "roc_auc": round(float(roc_auc_score(y, probabilities)), 4),
        "confusion_matrix": matrix.tolist(),
        "false_positive_cost": round(_false_positive_cost(y.to_numpy(), predictions, x["amount"].to_numpy()), 2),
    }
    return metrics, predictions, probabilities


def train_and_compare(n_rows: int = 8000, seed: int = 42) -> Dict[str, Any]:
    ARTIFACT_DIR.mkdir(parents=True, exist_ok=True)
    df = generate_synthetic_transactions(n_rows=n_rows, seed=seed)
    df.to_csv(DATASET_PATH, index=False)

    train_valid, test = train_test_split(
        df, test_size=0.15, random_state=seed, stratify=df[TARGET_COLUMN]
    )
    train, valid = train_test_split(
        train_valid, test_size=0.1765, random_state=seed, stratify=train_valid[TARGET_COLUMN]
    )

    candidates: Dict[str, Any] = {
        "logistic_regression": LogisticRegression(max_iter=1000, class_weight="balanced"),
        "random_forest": RandomForestClassifier(
            n_estimators=220,
            max_depth=12,
            min_samples_leaf=8,
            class_weight="balanced_subsample",
            random_state=seed,
            n_jobs=-1,
        ),
    }
    if XGBClassifier is not None:
        candidates["xgboost"] = XGBClassifier(
            n_estimators=260,
            max_depth=4,
            learning_rate=0.05,
            subsample=0.85,
            colsample_bytree=0.85,
            eval_metric="logloss",
            random_state=seed,
        )

    report: Dict[str, Any] = {
        "dataset_rows": int(len(df)),
        "positive_rate": round(float(df[TARGET_COLUMN].mean()), 4),
        "split": {"train": len(train), "validation": len(valid), "test": len(test)},
        "models": {},
    }

    best_name = ""
    best_pipeline: Pipeline | None = None
    best_score = -999.0

    for name, estimator in candidates.items():
        pipeline = Pipeline([("preprocess", make_preprocessor()), ("model", estimator)])
        pipeline.fit(train[FEATURE_COLUMNS], train[TARGET_COLUMN])
        valid_metrics, _, _ = _evaluate(pipeline, valid[FEATURE_COLUMNS], valid[TARGET_COLUMN])
        test_metrics, _, _ = _evaluate(pipeline, test[FEATURE_COLUMNS], test[TARGET_COLUMN])
        report["models"][name] = {"validation": valid_metrics, "test": test_metrics}

        use_case_score = (
            valid_metrics["f1"] * 0.40
            + valid_metrics["roc_auc"] * 0.30
            + valid_metrics["precision"] * 0.20
            + valid_metrics["recall"] * 0.10
            - min(valid_metrics["false_positive_cost"] / 50000.0, 0.25)
        )
        if use_case_score > best_score:
            best_name = name
            best_score = use_case_score
            best_pipeline = pipeline

    if best_pipeline is None:
        raise RuntimeError("No model candidates were available for training.")

    report["selected_model"] = best_name
    report["selection_reason"] = (
        "Selected using a revenue-recovery score that weights F1, ROC-AUC, precision, recall, "
        "and penalizes false-positive recovery costs instead of accuracy alone."
    )
    joblib.dump(best_pipeline, MODEL_PATH)
    REPORT_PATH.write_text(json.dumps(report, indent=2), encoding="utf-8")
    return report


def ensure_model_artifacts() -> Dict[str, Any]:
    if MODEL_PATH.exists() and REPORT_PATH.exists() and DATASET_PATH.exists():
        return json.loads(REPORT_PATH.read_text(encoding="utf-8"))
    return train_and_compare()


if __name__ == "__main__":
    print(json.dumps(train_and_compare(), indent=2))
