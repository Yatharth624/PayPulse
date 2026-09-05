from __future__ import annotations

import base64
import hashlib
import hmac
import os
from typing import Any, Dict, Optional

import requests


class RazorpayClient:
    base_url = "https://api.razorpay.com/v1"

    def __init__(self) -> None:
        self.key_id = os.getenv("RAZORPAY_KEY_ID")
        self.key_secret = os.getenv("RAZORPAY_KEY_SECRET")
        self.webhook_secret = os.getenv("RAZORPAY_WEBHOOK_SECRET")

    @property
    def configured(self) -> bool:
        return bool(self.key_id and self.key_secret)

    def _auth_header(self) -> Dict[str, str]:
        token = base64.b64encode(f"{self.key_id}:{self.key_secret}".encode("utf-8")).decode("ascii")
        return {"Authorization": f"Basic {token}", "Content-Type": "application/json"}

    def create_payment_link(self, transaction: Dict[str, Any], notes: Optional[Dict[str, Any]] = None) -> Dict[str, Any]:
        if not self.configured:
            return {
                "mode": "test_stub",
                "payment_link_id": f"plink_{transaction['transaction_id']}",
                "short_url": f"https://rzp.io/i/test-{transaction['transaction_id']}",
            }

        payload = {
            "amount": int(round(float(transaction["amount"]) * 100)),
            "currency": "INR",
            "accept_partial": False,
            "reference_id": transaction["transaction_id"],
            "description": "PayPulse AI revenue recovery payment link",
            "customer": {"name": transaction.get("customer_name", "PayPulse Customer")},
            "notify": {"sms": True, "email": False},
            "notes": notes or {},
        }
        response = requests.post(
            f"{self.base_url}/payment_links",
            json=payload,
            headers=self._auth_header(),
            timeout=15,
        )
        response.raise_for_status()
        return response.json()

    def verify_webhook_signature(self, raw_body: bytes, signature: str) -> bool:
        if not self.webhook_secret:
            return False
        expected = hmac.new(self.webhook_secret.encode("utf-8"), raw_body, hashlib.sha256).hexdigest()
        return hmac.compare_digest(expected, signature or "")
