from typing import List, Dict, Any
from datetime import datetime
from .models import AuditLogItem

class RecoveryAgent:
    def __init__(self):
        self.audit_trail: List[AuditLogItem] = [
            AuditLogItem(
                id="AUD-8901",
                timestamp="10:14 AM",
                action_type="Delayed Retry Sequencer",
                target_id="TXN-RP-1014",
                status="SUCCESS",
                reasoning="Retried ICICI UPI mandate after 15-min bank recovery window. Payment captured.",
                money_saved_formatted="₹4,999"
            ),
            AuditLogItem(
                id="AUD-8902",
                timestamp="10:30 AM",
                action_type="Hinglish WhatsApp Recovery Link",
                target_id="TXN-RP-1022",
                status="SUCCESS",
                reasoning="Sent automated Hinglish notification with 1-click Razorpay payment link. Customer completed via PhonePe.",
                money_saved_formatted="₹12,999"
            )
        ]

    def diagnose_and_execute(self, action_type: str, target_ids: List[str], max_discount_pct: float = 0.0) -> Dict[str, Any]:
        """Closed-loop recovery execution with safety gates and explainable audit logging"""
        executed_count = len(target_ids)
        total_recovered = 0
        logs_created = []

        for tid in target_ids:
            # Simulate amount recovered based on ID seed
            amount_val = 5200 + (hash(tid) % 9500)
            total_recovered += amount_val

            reasoning_map = {
                "delayed_retry": f"Scheduled automated retry for {tid} based on ICICI bank success rate recovery trend. Max retries gated at 2.",
                "payment_link": f"Dispatched Razorpay Smart Payment Link via WhatsApp/SMS to {tid} with {max_discount_pct}% capped recovery incentive.",
                "alt_method": f"Prompted alternative payment method (NetBanking/Cards) for {tid} after 2 failed UPI attempts.",
                "hinglish_voice": f"Initiated AI Hinglish voice reminder for subscription renewal for {tid}. Promise-to-pay date logged."
            }

            log_item = AuditLogItem(
                id=f"AUD-{len(self.audit_trail) + 1000}",
                timestamp=datetime.now().strftime("%I:%M %p"),
                action_type=action_type.replace("_", " ").title(),
                target_id=tid,
                status="SUCCESS",
                reasoning=reasoning_map.get(action_type, "Executed bounded AI recovery workflow."),
                money_saved_formatted=f"₹{amount_val:,}"
            )
            self.audit_trail.insert(0, log_item)
            logs_created.append(log_item.dict())

        return {
            "status": "COMPLETED",
            "executed_count": executed_count,
            "total_recovered_formatted": f"₹{total_recovered:,}",
            "audit_logs": logs_created
        }

    def get_audit_trail(self) -> List[AuditLogItem]:
        return self.audit_trail

agent_instance = RecoveryAgent()
