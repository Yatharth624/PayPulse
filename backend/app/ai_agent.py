from __future__ import annotations

from typing import Any, Dict


class RecoveryDiagnosisAgent:
    def diagnose(self, transaction: Dict[str, Any], prediction: Dict[str, float]) -> Dict[str, Any]:
        reason = transaction["failure_reason"]
        method = transaction["payment_method"]
        probability = prediction["recovery_probability"]

        if probability < 0.35 or transaction["retry_count"] >= 3:
            recommendation = "do_not_retry"
            title = "Low-confidence recovery."
            explanation = "The transaction has weak historical recovery signals or too many retries."
        elif reason in {"upi_timeout", "gateway_timeout"}:
            recommendation = "delayed_retry"
            title = "Likely transient gateway failure."
            explanation = "Gateway health and customer history indicate a retry after a cooling window is reasonable."
        elif reason == "checkout_abandoned":
            recommendation = "send_payment_link"
            title = "Checkout abandonment recovery opportunity."
            explanation = "A customer-led payment link is safer than an automatic retry for abandoned checkout."
        elif method == "mandate" or reason == "mandate_failed":
            recommendation = "retry_mandate"
            title = "Mandate retry candidate."
            explanation = "Subscription context and prior payment history support a controlled mandate retry."
        elif reason in {"insufficient_funds", "bank_decline", "authentication_failed"}:
            recommendation = "alternative_payment_method"
            title = "Customer action required."
            explanation = "The failure reason suggests an automatic retry may repeat the same failure."
        else:
            recommendation = "send_payment_link"
            title = "Manual confirmation preferred."
            explanation = "A payment link gives the customer control while preserving the recovery opportunity."

        return {
            "diagnosis_title": title,
            "explanation": explanation,
            "confidence": round(probability, 4),
            "recommendation": recommendation,
        }
