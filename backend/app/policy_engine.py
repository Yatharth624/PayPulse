from __future__ import annotations

from typing import Any, Dict, List

HARD_DECLINES = {"insufficient_funds", "bank_decline", "authentication_failed"}


class PolicyEngine:
    def evaluate(
        self,
        transaction: Dict[str, Any],
        prediction: Dict[str, float],
        policy: Dict[str, Any],
        recommendation: str,
    ) -> Dict[str, Any]:
        checks: List[Dict[str, Any]] = []
        probability = float(prediction["recovery_probability"])
        amount = float(transaction["amount"])
        retry_count = int(transaction["retry_count"])
        block_hard_declines = bool(policy["block_hard_declines"])

        def add_check(name: str, passed: bool, detail: str) -> None:
            checks.append({"name": name, "passed": passed, "detail": detail})

        add_check(
            "minimum_recovery_probability",
            probability >= float(policy["min_recovery_probability"]),
            f"{probability:.2%} >= {float(policy['min_recovery_probability']):.2%}",
        )
        add_check(
            "maximum_retry_count",
            retry_count < int(policy["max_retry_count"]),
            f"{retry_count} < {int(policy['max_retry_count'])}",
        )
        add_check(
            "maximum_automatic_recovery_amount",
            amount <= float(policy["max_automatic_recovery_amount"]),
            f"{amount:.2f} <= {float(policy['max_automatic_recovery_amount']):.2f}",
        )
        add_check(
            "hard_decline_block",
            not (block_hard_declines and transaction["failure_reason"] in HARD_DECLINES),
            "hard decline failures require customer-led recovery",
        )

        retry_limit_blocked = retry_count >= int(policy["max_retry_count"])
        amount_limit_blocked = amount > float(policy["max_automatic_recovery_amount"])
        hard_decline_blocked = block_hard_declines and transaction["failure_reason"] in HARD_DECLINES
        blocked = retry_limit_blocked or amount_limit_blocked or hard_decline_blocked
        requires_approval = amount >= float(policy["approval_threshold"])
        should_escalate = probability < float(policy["escalation_probability"]) or requires_approval
        passed = all(check["passed"] for check in checks)

        if blocked:
            decision = "blocked"
        elif passed:
            decision = "approved"
        else:
            decision = "requires_approval"

        return {
            "decision": decision,
            "approved": decision == "approved",
            "requires_approval": requires_approval,
            "should_escalate": should_escalate,
            "checks": checks,
            "validated_recommendation": "escalate" if decision != "approved" and should_escalate else recommendation,
        }
