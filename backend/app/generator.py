import random
from typing import List, Dict, Any
from .models import (
    PulseMetrics, TrendDay, RecoveryBreakdown, PriorityAction, CriticalIncident, QueueSummary,
    NetworkPerformanceItem, OptimizationSuggestion, AIDecisionDetail, WorkflowItem, MerchantPolicyState, PolicyDecisionLogItem,
    ModelMetricsDto, BusinessMetricsDto, FailureCategoryIntelligenceDto, FailureTrendPointDto,
    GatewayIntelligenceDto, StrategyEffectivenessDto, AiInsightDto, ModelHealthDto, FeatureDriftStatusDto,
    InsightsResponseDto, AutomationStatusDto, WorkflowDetailDto, DecisionStepDto, PolicyPreviewRequest, PolicyPreviewResponse,
    StrategySimulationItemDto, SimulationResponseDto, TransactionIntelligenceDto
)

def get_pulse_metrics() -> PulseMetrics:
    return PulseMetrics(
        revenue_at_risk=38.6,
        revenue_at_risk_formatted="₹38.6L",
        risk_change_pct=12.0,
        recoverable=28.7,
        recoverable_formatted="₹28.7L",
        recoverable_pct_of_risk=74.3,
        recovered=21.4,
        recovered_formatted="₹21.4L",
        recovered_change_pct=8.2,
        recovered_today_formatted="₹3.2L recovered today",
        recovery_rate_pct=74.6,
        recovery_rate_change_pct=2.4
    )

def get_7day_trends() -> List[TrendDay]:
    return [
        TrendDay(day="Mon", at_risk=4.2, recoverable=3.1, recovered=2.8),
        TrendDay(day="Tue", at_risk=5.8, recoverable=4.5, recovered=3.9),
        TrendDay(day="Wed", at_risk=5.1, recoverable=3.9, recovered=3.4),
        TrendDay(day="Thu", at_risk=3.9, recoverable=2.8, recovered=2.5),
        TrendDay(day="Fri", at_risk=8.4, recoverable=6.2, recovered=5.1),
        TrendDay(day="Sat", at_risk=4.8, recoverable=3.5, recovered=2.9),
        TrendDay(day="Sun", at_risk=3.5, recoverable=2.5, recovered=2.0),
    ]

def get_recovery_breakdown() -> RecoveryBreakdown:
    return RecoveryBreakdown(
        delayed_retries=9.2,
        delayed_retries_formatted="₹9.2L",
        payment_links=5.4,
        payment_links_formatted="₹5.4L",
        alternative_methods=4.1,
        alternative_methods_formatted="₹4.1L",
        subscription_recovery=2.7,
        subscription_recovery_formatted="₹2.7L"
    )

def get_priority_actions() -> List[PriorityAction]:
    return [
        PriorityAction(
            id="act-upi-01",
            title="UPI degradation",
            recoverable_formatted="₹1.42L recoverable",
            item_count_label="27 transactions",
            category="upi_degradation"
        ),
        PriorityAction(
            id="act-sub-02",
            title="Failed subscriptions",
            recoverable_formatted="₹84K recoverable",
            item_count_label="18 customers",
            category="failed_subscriptions"
        ),
        PriorityAction(
            id="act-cart-03",
            title="Checkout abandonment",
            recoverable_formatted="₹31K recoverable",
            item_count_label="14 sessions",
            category="checkout_abandonment"
        )
    ]

def get_critical_incident() -> CriticalIncident:
    return CriticalIncident()

def get_queue_summary() -> QueueSummary:
    return QueueSummary(
        pending_actions_count=27,
        pending_potential_recovery_formatted="₹2.57L potential recovery",
        high_priority_count=8,
        high_priority_at_risk_formatted="₹1.42L at risk",
        auto_eligible_count=19,
        auto_eligible_pct_formatted="74% of pending actions"
    )

def get_workflows() -> List[WorkflowItem]:
    return [
        WorkflowItem(id="wf-1", title="UPI Payment", icon_type="upi", status=True, trigger="failure/timeout", strategy="15m Delayed retry", performance_formatted="₹9.2L (81%)"),
        WorkflowItem(id="wf-2", title="Subscription", icon_type="subscription", status=True, trigger="renewal failure", strategy="24h Retry mandate", performance_formatted="₹2.7L (76%)"),
        WorkflowItem(id="wf-3", title="Checkout", icon_type="checkout", status=True, trigger="abandoned", strategy="30m Recovery link", performance_formatted="₹5.4L (72%)"),
        WorkflowItem(id="wf-4", title="Alt. Payment", icon_type="alt_payment", status=True, trigger="card failure", strategy="Offer alternative", performance_formatted="₹4.1L (68%)")
    ]

def get_merchant_policy() -> MerchantPolicyState:
    return MerchantPolicyState(
        min_probability_pct=70,
        max_auto_retries=2,
        max_auto_recovery_amount=50000,
        max_auto_recovery_amount_formatted="₹ 50,000",
        approval_required_above=25000,
        approval_required_above_formatted="₹ 25,000",
        escalate_low_confidence=True
    )

def get_recent_policy_decisions() -> List[PolicyDecisionLogItem]:
    return [
        PolicyDecisionLogItem(id="dec-1", action_type="blocked", amount_formatted="₹74,000", reason="Above limit"),
        PolicyDecisionLogItem(id="dec-2", action_type="escalated", amount_formatted="₹24,999", reason="Below threshold"),
        PolicyDecisionLogItem(id="dec-3", action_type="approved", amount_formatted="₹8,499", reason="Passed 5/5 checks")
    ]

def generate_screen2_queue_transactions() -> List[Dict[str, Any]]:
    return [
        {
            "id": "RP_83921",
            "customer_name": "Rahul Sharma",
            "amount": 8499,
            "amount_formatted": "₹8,499",
            "category": "upi_degradation",
            "failure_reason": "UPI Timeout",
            "payment_method": "@okicici",
            "timestamp": "15 min ago",
            "probability_pct": 87,
            "potential_recovery_formatted": "₹7,394",
            "recommended_strategy": "Delayed retry · 15 min",
            "recommendation_note": None,
            "priority_badge": "HIGH PRIORITY",
            "status_type": "ready",
            "status_label": "Ready",
            "status": "Pending Recovery"
        },
        {
            "id": "RP_83922",
            "customer_name": "Priya Verma",
            "amount": 3200,
            "amount_formatted": "₹3,200",
            "category": "payment_failures",
            "failure_reason": "Card Decline",
            "payment_method": "HDFC Credit Card",
            "timestamp": "22 min ago",
            "probability_pct": 72,
            "potential_recovery_formatted": "₹2,304",
            "recommended_strategy": "Send payment link",
            "recommendation_note": None,
            "priority_badge": "HIGH PRIORITY",
            "status_type": "awaiting_approval",
            "status_label": "Awaiting approval",
            "status": "Pending Recovery"
        },
        {
            "id": "RP_83923",
            "customer_name": "Ankit Mehta",
            "amount": 24999,
            "amount_formatted": "₹24,999",
            "category": "payment_failures",
            "failure_reason": "Repeated Payment Failure",
            "payment_method": "SBI NetBanking",
            "timestamp": "45 min ago",
            "probability_pct": 31,
            "potential_recovery_formatted": "₹7,750",
            "recommended_strategy": "Do not retry",
            "recommendation_note": "Repeated failures exceeded safe retry threshold",
            "priority_badge": None,
            "status_type": "escalated",
            "status_label": "Escalated",
            "status": "Escalated"
        },
        {
            "id": "RP_83924",
            "customer_name": "Sneha Patel",
            "amount": 12500,
            "amount_formatted": "₹12,500",
            "category": "checkout_abandonment",
            "failure_reason": "Checkout Abandoned",
            "payment_method": "Razorpay Standard Checkout",
            "timestamp": "1 hr ago",
            "probability_pct": 79,
            "potential_recovery_formatted": "₹9,875",
            "recommended_strategy": "Send recovery link",
            "recommendation_note": None,
            "priority_badge": None,
            "status_type": "ready",
            "status_label": "Ready",
            "status": "Pending Recovery"
        },
        {
            "id": "RP_83925",
            "customer_name": "Vikram Singh",
            "amount": 6800,
            "amount_formatted": "₹6,800",
            "category": "subscriptions",
            "failure_reason": "Subscription Renewal Failed",
            "payment_method": "ICICI Mandate Autopay",
            "timestamp": "2 hrs ago",
            "probability_pct": 83,
            "potential_recovery_formatted": "₹5,644",
            "recommended_strategy": "Retry mandate",
            "recommendation_note": None,
            "priority_badge": None,
            "status_type": "ready",
            "status_label": "Ready",
            "status": "Pending Recovery"
        }
    ]


def get_insights_intelligence_data(time_range: str = "24h") -> InsightsResponseDto:
    # Scale multiplier based on selected time range for dynamic interactive filtering
    clean_tr = time_range.lower().strip()
    mult = 1.0
    if clean_tr in ("1h", "1_hour"):
        mult = 0.12
    elif clean_tr in ("6h", "6_hours"):
        mult = 0.45
    elif clean_tr in ("today", "1d"):
        mult = 0.65
    elif clean_tr in ("7d", "7_days"):
        mult = 3.8

    expected_rec = round(2870000.0 * mult, 2)
    recovered_amt = round(2140000.0 * mult, 2)
    rec_rate = round(recovered_amt / max(expected_rec, 1.0), 3) if expected_rec > 0 else 0.746
    cost_avoided = round(42800.0 * mult, 2)

    def _fmt_inr(v: float) -> str:
        if v >= 100000:
            return f"₹{v / 100000:.2f}L"
        elif v >= 1000:
            return f"₹{v / 1000:.1f}K"
        else:
            return f"₹{v:,.0f}"

    model_metrics = ModelMetricsDto(
        roc_auc=0.91,
        f1=0.88,
        precision=0.91,
        recall=0.86,
        accuracy_secondary=0.94,
        selected_model="Random Forest Classifier",
        evaluation_label="Validation Performance (evaluated on held-out test data)",
        dataset_rows=8000,
        true_positives_count=int(3420 * mult),
        false_positives_count=int(340 * mult),
        false_negatives_count=int(560 * mult),
        true_negatives_count=int(3680 * mult)
    )

    business_metrics = BusinessMetricsDto(
        expected_recovery=expected_rec,
        expected_recovery_formatted=_fmt_inr(expected_rec),
        recovered_amount=recovered_amt,
        recovered_amount_formatted=_fmt_inr(recovered_amt),
        recovery_rate=rec_rate,
        recovery_rate_formatted=f"{rec_rate * 100:.1f}%",
        recovery_lift_pct=18.4,
        recovery_lift_formatted="+18.4% vs Simple Retry",
        baseline_strategy_label="Simple Retry Baseline",
        retry_cost_avoided=cost_avoided,
        retry_cost_avoided_formatted=_fmt_inr(cost_avoided)
    )

    failure_categories = [
        FailureCategoryIntelligenceDto(
            category="upi_timeout",
            category_label="UPI Timeout",
            transaction_count=max(1, int(142 * mult)),
            transaction_value=round(420000.0 * mult, 2),
            transaction_value_formatted=_fmt_inr(420000.0 * mult),
            expected_recovery=round(290000.0 * mult, 2),
            expected_recovery_formatted=_fmt_inr(290000.0 * mult),
            recovered_amount=round(214600.0 * mult, 2),
            recovered_amount_formatted=_fmt_inr(214600.0 * mult),
            recovery_rate_pct=74.0,
            recovery_rate_formatted="74%",
            recommended_strategy="Delayed Retry"
        ),
        FailureCategoryIntelligenceDto(
            category="mandate_failed",
            category_label="Mandate Failure",
            transaction_count=max(1, int(88 * mult)),
            transaction_value=round(280000.0 * mult, 2),
            transaction_value_formatted=_fmt_inr(280000.0 * mult),
            expected_recovery=round(196000.0 * mult, 2),
            expected_recovery_formatted=_fmt_inr(196000.0 * mult),
            recovered_amount=round(152880.0 * mult, 2),
            recovered_amount_formatted=_fmt_inr(152880.0 * mult),
            recovery_rate_pct=78.0,
            recovery_rate_formatted="78%",
            recommended_strategy="Retry Mandate"
        ),
        FailureCategoryIntelligenceDto(
            category="checkout_abandoned",
            category_label="Checkout Abandonment",
            transaction_count=max(1, int(64 * mult)),
            transaction_value=round(195000.0 * mult, 2),
            transaction_value_formatted=_fmt_inr(195000.0 * mult),
            expected_recovery=round(132000.0 * mult, 2),
            expected_recovery_formatted=_fmt_inr(132000.0 * mult),
            recovered_amount=round(89760.0 * mult, 2),
            recovered_amount_formatted=_fmt_inr(89760.0 * mult),
            recovery_rate_pct=68.0,
            recovery_rate_formatted="68%",
            recommended_strategy="Smart Payment Link"
        ),
        FailureCategoryIntelligenceDto(
            category="bank_decline",
            category_label="Bank Decline",
            transaction_count=max(1, int(42 * mult)),
            transaction_value=round(140000.0 * mult, 2),
            transaction_value_formatted=_fmt_inr(140000.0 * mult),
            expected_recovery=round(85000.0 * mult, 2),
            expected_recovery_formatted=_fmt_inr(85000.0 * mult),
            recovered_amount=round(57800.0 * mult, 2),
            recovered_amount_formatted=_fmt_inr(57800.0 * mult),
            recovery_rate_pct=68.0,
            recovery_rate_formatted="68%",
            recommended_strategy="Alt. Payment Method"
        ),
        FailureCategoryIntelligenceDto(
            category="insufficient_funds",
            category_label="Insufficient Funds",
            transaction_count=max(1, int(35 * mult)),
            transaction_value=round(110000.0 * mult, 2),
            transaction_value_formatted=_fmt_inr(110000.0 * mult),
            expected_recovery=round(52000.0 * mult, 2),
            expected_recovery_formatted=_fmt_inr(52000.0 * mult),
            recovered_amount=round(27040.0 * mult, 2),
            recovered_amount_formatted=_fmt_inr(27040.0 * mult),
            recovery_rate_pct=52.0,
            recovery_rate_formatted="52%",
            recommended_strategy="Delayed Retry"
        )
    ]

    # Sort categories primarily by financial impact / expected recovery
    failure_categories.sort(key=lambda c: c.expected_recovery, reverse=True)

    if clean_tr in ("1h", "1_hour"):
        trends = [
            FailureTrendPointDto(timestamp_label="10:00", failure_count=12, failure_rate_pct=4.2, expected_recovery=38000.0, expected_recovery_formatted="₹38K"),
            FailureTrendPointDto(timestamp_label="10:15", failure_count=18, failure_rate_pct=5.8, expected_recovery=54000.0, expected_recovery_formatted="₹54K"),
            FailureTrendPointDto(timestamp_label="10:30", failure_count=27, failure_rate_pct=8.4, expected_recovery=82000.0, expected_recovery_formatted="₹82K"),
            FailureTrendPointDto(timestamp_label="10:45", failure_count=19, failure_rate_pct=6.1, expected_recovery=58000.0, expected_recovery_formatted="₹58K")
        ]
    elif clean_tr in ("6h", "6_hours"):
        trends = [
            FailureTrendPointDto(timestamp_label="06:00", failure_count=35, failure_rate_pct=3.1, expected_recovery=110000.0, expected_recovery_formatted="₹1.1L"),
            FailureTrendPointDto(timestamp_label="08:00", failure_count=62, failure_rate_pct=6.4, expected_recovery=195000.0, expected_recovery_formatted="₹1.95L"),
            FailureTrendPointDto(timestamp_label="10:00", failure_count=94, failure_rate_pct=9.2, expected_recovery=290000.0, expected_recovery_formatted="₹2.9L"),
            FailureTrendPointDto(timestamp_label="12:00", failure_count=58, failure_rate_pct=4.8, expected_recovery=175000.0, expected_recovery_formatted="₹1.75L")
        ]
    elif clean_tr in ("7d", "7_days"):
        trends = [
            FailureTrendPointDto(timestamp_label="Mon", failure_count=310, failure_rate_pct=4.8, expected_recovery=980000.0, expected_recovery_formatted="₹9.8L"),
            FailureTrendPointDto(timestamp_label="Tue", failure_count=420, failure_rate_pct=5.4, expected_recovery=1320000.0, expected_recovery_formatted="₹13.2L"),
            FailureTrendPointDto(timestamp_label="Wed", failure_count=380, failure_rate_pct=5.1, expected_recovery=1180000.0, expected_recovery_formatted="₹11.8L"),
            FailureTrendPointDto(timestamp_label="Thu", failure_count=290, failure_rate_pct=4.2, expected_recovery=920000.0, expected_recovery_formatted="₹9.2L"),
            FailureTrendPointDto(timestamp_label="Fri", failure_count=580, failure_rate_pct=7.6, expected_recovery=1840000.0, expected_recovery_formatted="₹18.4L"),
            FailureTrendPointDto(timestamp_label="Sat", failure_count=340, failure_rate_pct=4.5, expected_recovery=1050000.0, expected_recovery_formatted="₹10.5L"),
            FailureTrendPointDto(timestamp_label="Sun", failure_count=260, failure_rate_pct=3.8, expected_recovery=810000.0, expected_recovery_formatted="₹8.1L")
        ]
    else:
        trends = [
            FailureTrendPointDto(timestamp_label="00:00", failure_count=22, failure_rate_pct=2.8, expected_recovery=65000.0, expected_recovery_formatted="₹65K"),
            FailureTrendPointDto(timestamp_label="06:00", failure_count=48, failure_rate_pct=4.1, expected_recovery=145000.0, expected_recovery_formatted="₹1.45L"),
            FailureTrendPointDto(timestamp_label="12:00", failure_count=142, failure_rate_pct=9.5, expected_recovery=420000.0, expected_recovery_formatted="₹4.2L"),
            FailureTrendPointDto(timestamp_label="18:00", failure_count=76, failure_rate_pct=5.2, expected_recovery=230000.0, expected_recovery_formatted="₹2.3L")
        ]

    gateways = [
        GatewayIntelligenceDto(
            gateway="Razorpay Switch",
            health_score=92,
            failure_rate_pct=3.2,
            failure_rate_formatted="3.2%",
            latency_ms=410,
            recovery_rate_pct=74.0,
            recovery_rate_formatted="74%",
            trend="Stable",
            status="Healthy"
        ),
        GatewayIntelligenceDto(
            gateway="HDFC Bank Direct",
            health_score=95,
            failure_rate_pct=1.8,
            failure_rate_formatted="1.8%",
            latency_ms=280,
            recovery_rate_pct=82.0,
            recovery_rate_formatted="82%",
            trend="Improving",
            status="Healthy"
        ),
        GatewayIntelligenceDto(
            gateway="UPI Generic Switch",
            health_score=62,
            failure_rate_pct=18.5,
            failure_rate_formatted="18.5%",
            latency_ms=840,
            recovery_rate_pct=58.0,
            recovery_rate_formatted="58%",
            trend="Elevated Failure",
            status="Degraded",
            alert_message="UPI gateway failure rate (18.5%) is significantly above baseline. Recommended: Delay automatic retries by 15 min cooling window for affected transactions."
        ),
        GatewayIntelligenceDto(
            gateway="ICICI NetBanking",
            health_score=88,
            failure_rate_pct=5.4,
            failure_rate_formatted="5.4%",
            latency_ms=620,
            recovery_rate_pct=71.0,
            recovery_rate_formatted="71%",
            trend="Elevated Latency",
            status="Healthy"
        )
    ]

    strategies = [
        StrategyEffectivenessDto(
            strategy="delayed_retry",
            strategy_label="Delayed Retry",
            attempts=int(184 * mult),
            successful_recoveries=int(137 * mult),
            success_rate_pct=74.5,
            success_rate_formatted="74.5%",
            expected_recovery=round(342000.0 * mult, 2),
            expected_recovery_formatted=_fmt_inr(342000.0 * mult),
            recovered_amount=round(298000.0 * mult, 2),
            recovered_amount_formatted=_fmt_inr(298000.0 * mult)
        ),
        StrategyEffectivenessDto(
            strategy="send_payment_link",
            strategy_label="Payment Link",
            attempts=int(120 * mult),
            successful_recoveries=int(85 * mult),
            success_rate_pct=70.8,
            success_rate_formatted="70.8%",
            expected_recovery=round(210000.0 * mult, 2),
            expected_recovery_formatted=_fmt_inr(210000.0 * mult),
            recovered_amount=round(184000.0 * mult, 2),
            recovered_amount_formatted=_fmt_inr(184000.0 * mult)
        ),
        StrategyEffectivenessDto(
            strategy="retry_mandate",
            strategy_label="Mandate Retry",
            attempts=int(95 * mult),
            successful_recoveries=int(72 * mult),
            success_rate_pct=75.8,
            success_rate_formatted="75.8%",
            expected_recovery=round(180000.0 * mult, 2),
            expected_recovery_formatted=_fmt_inr(180000.0 * mult),
            recovered_amount=round(156000.0 * mult, 2),
            recovered_amount_formatted=_fmt_inr(156000.0 * mult)
        ),
        StrategyEffectivenessDto(
            strategy="alternative_payment_method",
            strategy_label="Alt. Payment Method",
            attempts=int(64 * mult),
            successful_recoveries=int(42 * mult),
            success_rate_pct=65.6,
            success_rate_formatted="65.6%",
            expected_recovery=round(115000.0 * mult, 2),
            expected_recovery_formatted=_fmt_inr(115000.0 * mult),
            recovered_amount=round(92000.0 * mult, 2),
            recovered_amount_formatted=_fmt_inr(92000.0 * mult)
        ),
        StrategyEffectivenessDto(
            strategy="immediate_retry",
            strategy_label="Immediate Retry",
            attempts=int(42 * mult),
            successful_recoveries=int(18 * mult),
            success_rate_pct=42.8,
            success_rate_formatted="42.8%",
            expected_recovery=round(65000.0 * mult, 2),
            expected_recovery_formatted=_fmt_inr(65000.0 * mult),
            recovered_amount=round(41000.0 * mult, 2),
            recovered_amount_formatted=_fmt_inr(41000.0 * mult)
        )
    ]

    ai_insights = [
        AiInsightDto(
            id="insight_1",
            type="gateway_degradation",
            severity="WARNING",
            title="UPI Failure Spike Detected",
            message="UPI timeout failures increased 18.5% compared with the previous baseline period across generic bank switches.",
            financial_impact_formatted=f"Impact: {_fmt_inr(124000.0 * mult)} expected recovery affected",
            recommended_action="Delay automatic retries by 15 min cooling window for affected transactions.",
            related_filter="upi_degradation",
            created_at="Just now"
        ),
        AiInsightDto(
            id="insight_2",
            type="strategy_performance",
            severity="INFO",
            title="Optimal Recovery Strategy Identified",
            message="Delayed retry currently produces the highest expected recovery rate (74.5%) for transient UPI timeout failures.",
            financial_impact_formatted=f"Lift: +18.4% recovery rate vs immediate retries",
            recommended_action="Keep 15m cooling window enabled in merchant policy settings.",
            related_filter="upi_timeout",
            created_at="10 min ago"
        ),
        AiInsightDto(
            id="insight_3",
            type="customer_signal",
            severity="INFO",
            title="High Customer Historical Success Signal",
            message="Transactions from customers with historically high payment success rates (>85%) have a 91% predicted recovery probability.",
            financial_impact_formatted=f"Volume: {_fmt_inr(320000.0 * mult)} high-confidence opportunity",
            recommended_action="Auto-approve smart recovery links for high-history customers.",
            related_filter="payment_failures",
            created_at="30 min ago"
        ),
        AiInsightDto(
            id="insight_4",
            type="subscription_window",
            severity="INFO",
            title="Mandate Renewal Window Concentration",
            message="Mandate failure rates spike during morning salary-cycle settlement windows (8:00 AM - 10:00 AM).",
            financial_impact_formatted=f"Opportunity: {_fmt_inr(180000.0 * mult)} recurring revenue",
            recommended_action="Schedule automated mandate retries outside peak banking congestion hours.",
            related_filter="subscriptions",
            created_at="1 hour ago"
        )
    ]

    model_health = ModelHealthDto(
        active_model_name="Recovery Model v3 (RandomForest)",
        transactions_analyzed=int(8420 * mult),
        predictions_generated=int(14280 * mult),
        last_updated="Today, 8:32 PM",
        dataset_size_rows=8000,
        data_status="Healthy",
        drift_status="Normal"
    )

    feature_drift = [
        FeatureDriftStatusDto(feature_name="Gateway Health Score", status="Normal"),
        FeatureDriftStatusDto(feature_name="Recent Failure Rate", status="Warning"),
        FeatureDriftStatusDto(feature_name="Payment Method Distribution", status="Normal"),
        FeatureDriftStatusDto(feature_name="Customer Historical Success Rate", status="Normal")
    ]

    return InsightsResponseDto(
        time_range=clean_tr.upper(),
        model_metrics=model_metrics,
        business_metrics=business_metrics,
        failure_breakdown=failure_categories,
        failure_trends=trends,
        gateway_metrics=gateways,
        strategy_metrics=strategies,
        ai_insights=ai_insights,
        model_health=model_health,
        feature_drift=feature_drift
    )


def get_automation_status(enabled: bool = True) -> AutomationStatusDto:
    if enabled:
        return AutomationStatusDto(
            automation_enabled=True,
            status_label="ACTIVE",
            transactions_monitored=2480,
            auto_recovery_enabled_count=1840,
            recoveries_today_count=347,
            recoveries_today_formatted="₹2.84L",
            expected_recovery_today_formatted="₹3.82L",
            pause_explanation="Automatic recovery actions are currently active. AI Policy Engine evaluates every failed payment."
        )
    else:
        return AutomationStatusDto(
            automation_enabled=False,
            status_label="PAUSED",
            transactions_monitored=2480,
            auto_recovery_enabled_count=0,
            recoveries_today_count=347,
            recoveries_today_formatted="₹2.84L",
            expected_recovery_today_formatted="₹3.82L",
            pause_explanation="Automatic recovery actions are currently paused. Failed payments will still be analyzed, monitored, and displayed in the Recovery Queue."
        )


def get_detailed_workflows() -> List[WorkflowDetailDto]:
    return [
        WorkflowDetailDto(
            workflow_id="wf_upi",
            name="UPI Failure Recovery",
            status_label="ACTIVE",
            is_active=True,
            icon_type="upi",
            trigger="UPI Timeout / transient gateway failure",
            ml_condition="Recovery Probability ≥ Merchant Min Threshold (70%)",
            ai_diagnosis="Transient Gateway Failure",
            action="Delayed Retry (15m cooling window)",
            delay_minutes=15,
            max_retries=2,
            performance_formatted="₹9.2L recovered (81% success)",
            decision_chain=[
                DecisionStepDto(step_number=1, title="Payment Failed", description="UPI Gateway Timeout detected", status="completed"),
                DecisionStepDto(step_number=2, title="ML Prediction", description="87% recovery probability calculated", status="completed"),
                DecisionStepDto(step_number=3, title="AI Diagnosis", description="Transient gateway failure identified", status="completed"),
                DecisionStepDto(step_number=4, title="Policy Check", description="Passed 87% ≥ 70%, retries < 2, amount < ₹50k", status="completed"),
                DecisionStepDto(step_number=5, title="Cooling Window", description="Waiting 15 minutes before retry", status="pending"),
                DecisionStepDto(step_number=6, title="Execution", description="Auto-retry executed via Razorpay Switch", status="pending")
            ]
        ),
        WorkflowDetailDto(
            workflow_id="wf_checkout",
            name="Checkout Abandonment Recovery",
            status_label="ACTIVE",
            is_active=True,
            icon_type="checkout",
            trigger="Cart / Checkout session abandoned",
            ml_condition="Customer Recovery Probability ≥ 65%",
            ai_diagnosis="Checkout Friction / Abandonment",
            action="Send Smart Payment Link via SMS / WhatsApp",
            delay_minutes=30,
            max_retries=1,
            performance_formatted="₹5.4L recovered (72% success)",
            decision_chain=[
                DecisionStepDto(step_number=1, title="Cart Abandoned", description="Customer abandoned payment checkout", status="completed"),
                DecisionStepDto(step_number=2, title="ML Prediction", description="78% probability of recovery link conversion", status="completed"),
                DecisionStepDto(step_number=3, title="AI Diagnosis", description="Customer payment friction detected", status="completed"),
                DecisionStepDto(step_number=4, title="Policy Check", description="Communication policy enabled", status="completed"),
                DecisionStepDto(step_number=5, title="Payment Link", description="Smart payment link dispatched via WhatsApp", status="pending")
            ]
        ),
        WorkflowDetailDto(
            workflow_id="wf_mandate",
            name="Subscription / Mandate Recovery",
            status_label="ACTIVE",
            is_active=True,
            icon_type="subscription",
            trigger="Mandate auto-debit renewal failure",
            ml_condition="Subscription Active & Prob ≥ 70%",
            ai_diagnosis="Recurring Settlement Failure",
            action="Retry Mandate during off-peak banking window",
            delay_minutes=1440,
            max_retries=3,
            performance_formatted="₹2.7L recovered (76% success)",
            decision_chain=[
                DecisionStepDto(step_number=1, title="Mandate Failed", description="Auto-debit failed during morning cycle", status="completed"),
                DecisionStepDto(step_number=2, title="ML Prediction", description="83% recovery probability for off-peak retry", status="completed"),
                DecisionStepDto(step_number=3, title="AI Diagnosis", description="Banking settlement congestion detected", status="completed"),
                DecisionStepDto(step_number=4, title="Policy Check", description="Retry count within limit (1/3)", status="completed"),
                DecisionStepDto(step_number=5, title="Mandate Retry", description="Scheduled for off-peak evening window", status="pending")
            ]
        ),
        WorkflowDetailDto(
            workflow_id="wf_hard_decline",
            name="Hard Decline Protection",
            status_label="PROTECTED",
            is_active=True,
            icon_type="hard_decline",
            trigger="Insufficient Funds / Bank Decline / Auth Failed",
            ml_condition="Hard Decline Failure Code",
            ai_diagnosis="Permanent / Hard Decline",
            action="Do NOT retry automatically. Offer Alternative Payment Method",
            delay_minutes=0,
            max_retries=0,
            performance_formatted="₹42.8K retry costs saved",
            decision_chain=[
                DecisionStepDto(step_number=1, title="Hard Decline", description="Bank decline code returned", status="completed"),
                DecisionStepDto(step_number=2, title="AI Diagnosis", description="Hard decline identified (Do Not Retry)", status="completed"),
                DecisionStepDto(step_number=3, title="Policy Check", description="Hard decline policy enforced: AUTOMATIC RETRY BLOCKED", status="completed"),
                DecisionStepDto(step_number=4, title="Protection", description="Saved gateway retry fees & prevented account lock", status="completed")
            ]
        )
    ]


def preview_policy_impact(req: PolicyPreviewRequest) -> PolicyPreviewResponse:
    curr_prob = 70.0
    prop_prob = float(req.min_recovery_probability)

    # Base counts for 70% threshold
    base_eligible = 1284
    base_expected = 840000.0

    # Calculate simulated shift based on change in probability threshold
    diff_pct = prop_prob - curr_prob
    if diff_pct > 0:
        blocked_shift = int(diff_pct * 37.1)
        new_eligible = max(100, base_eligible - blocked_shift)
        new_expected = max(50000.0, base_expected * (1.0 - (diff_pct * 0.014)))
    elif diff_pct < 0:
        blocked_shift = int(diff_pct * 25.0)  # negative shift
        new_eligible = base_eligible - blocked_shift
        new_expected = base_expected * (1.0 - (diff_pct * 0.012))
    else:
        blocked_shift = 0
        new_eligible = base_eligible
        new_expected = base_expected

    def _fmt_inr(v: float) -> str:
        if v >= 100000:
            return f"₹{v / 100000:.1f}L"
        elif v >= 1000:
            return f"₹{v / 1000:.0f}K"
        else:
            return f"₹{v:,.0f}"

    newly_blocked = max(0, base_eligible - new_eligible) if diff_pct > 0 else min(0, base_eligible - new_eligible)
    val_change_formatted = _fmt_inr(abs(base_expected - new_expected))

    if diff_pct > 0:
        summary = f"-{newly_blocked} transactions require merchant review (+{val_change_formatted} shifted to manual review queue)"
    elif diff_pct < 0:
        summary = f"+{abs(newly_blocked)} transactions eligible for auto-recovery (+{val_change_formatted} added to auto-execution)"
    else:
        summary = "No change in policy parameters. Estimated eligible transactions remain unchanged."

    return PolicyPreviewResponse(
        current_min_probability=curr_prob,
        proposed_min_probability=prop_prob,
        current_eligible_count=base_eligible,
        proposed_eligible_count=new_eligible,
        current_expected_recovery_formatted=_fmt_inr(base_expected),
        proposed_expected_recovery_formatted=_fmt_inr(new_expected),
        newly_blocked_count=newly_blocked,
        estimated_change_summary=summary
    )


def simulate_recovery_strategies(transaction_id: str = "RP_83922") -> SimulationResponseDto:
    options = [
        StrategySimulationItemDto(
            strategy_id="opt_delayed_retry",
            name="Delayed Retry (15 min)",
            type="delayed_retry",
            probability_pct=87.0,
            expected_recovery_formatted="₹21,315",
            estimated_cost_formatted="₹12",
            net_expected_recovery_formatted="₹21,303",
            policy_eligible=True,
            policy_reason="Passed 87% >= 70% min threshold",
            policy_status_label="RECOMMENDED",
            recommended=True
        ),
        StrategySimulationItemDto(
            strategy_id="opt_immediate_retry",
            name="Immediate Gateway Retry",
            type="immediate_retry",
            probability_pct=42.0,
            expected_recovery_formatted="₹10,290",
            estimated_cost_formatted="₹18",
            net_expected_recovery_formatted="₹10,272",
            policy_eligible=False,
            policy_reason="Blocked: Probability 42% < 70% threshold & gateway congested",
            policy_status_label="BLOCKED",
            recommended=False
        ),
        StrategySimulationItemDto(
            strategy_id="opt_payment_link",
            name="Smart Payment Link (SMS / WhatsApp)",
            type="payment_link",
            probability_pct=74.0,
            expected_recovery_formatted="₹18,130",
            estimated_cost_formatted="₹2.50",
            net_expected_recovery_formatted="₹18,127",
            policy_eligible=True,
            policy_reason="Communication policy active & customer conversion high",
            policy_status_label="ELIGIBLE",
            recommended=False
        ),
        StrategySimulationItemDto(
            strategy_id="opt_alt_method",
            name="Offer Alternative Payment Method",
            type="alt_payment_method",
            probability_pct=68.0,
            expected_recovery_formatted="₹16,660",
            estimated_cost_formatted="₹0",
            net_expected_recovery_formatted="₹16,660",
            policy_eligible=True,
            policy_reason="Safe fallback for checkout friction",
            policy_status_label="ELIGIBLE",
            recommended=False
        ),
        StrategySimulationItemDto(
            strategy_id="opt_no_action",
            name="No Automated Action",
            type="no_action",
            probability_pct=0.0,
            expected_recovery_formatted="₹0",
            estimated_cost_formatted="₹0",
            net_expected_recovery_formatted="₹0",
            policy_eligible=False,
            policy_reason="Manual merchant oversight option (PayPulse will not attempt auto-recovery)",
            policy_status_label="MANUAL",
            recommended=False
        )
    ]

    return SimulationResponseDto(
        transaction_id=transaction_id,
        amount_formatted="₹24,500",
        failure_reason="UPI Timeout",
        current_recommended_strategy="Delayed Retry (15 min)",
        options=options
    )


def get_transaction_intelligence(transaction_id: str = "RP_83922") -> TransactionIntelligenceDto:
    timeline = [
        DecisionStepDto(step_number=1, title="Payment Failed", description="UPI Gateway Timeout detected during peak banking cycle", status="completed"),
        DecisionStepDto(step_number=2, title="ML Prediction", description="87% recovery probability evaluated via RandomForest Model v3", status="completed"),
        DecisionStepDto(step_number=3, title="AI Diagnosis", description="Transient Gateway Failure identified (Razorpay Switch latency spike)", status="completed"),
        DecisionStepDto(step_number=4, title="Policy Engine Check", description="POLICY APPROVED: 87% probability >= merchant min 70% threshold", status="completed"),
        DecisionStepDto(step_number=5, title="Action Scheduled", description="Delayed Retry (15m cooling window) scheduled", status="completed"),
        DecisionStepDto(step_number=6, title="Execution & Outcome", description="Auto-retry executed -> Payment RECOVERED (₹24,500)", status="completed")
    ]

    ml_signals = [
        "Customer Historical Success Rate: 94%",
        "Gateway Latency Spike: 4200ms (Transient)",
        "Peak Banking Hours Settlement Congestion (Morning Cycle)",
        "Transaction Amount ₹24,500 within standard merchant limit (< ₹50,000)",
        "Zero prior retries attempted (1/2 allowed)"
    ]

    return TransactionIntelligenceDto(
        transaction_id=transaction_id,
        amount_formatted="₹24,500",
        customer_name="Rahul Sharma",
        payment_method="UPI",
        gateway="Razorpay Switch",
        failure_reason="UPI Gateway Timeout (504)",
        recovery_probability_pct=87.0,
        expected_recovery_formatted="₹21,315",
        ai_diagnosis="Transient Gateway Failure during morning settlement peak traffic",
        ml_signals=ml_signals,
        policy_status="POLICY APPROVED",
        policy_reason="Recovery probability 87% >= merchant threshold 70%",
        recommended_action="Delayed Retry · 15 min",
        action_delay="15 minutes",
        timeline_steps=timeline
    )


def evaluate_and_execute_recovery(transaction_id: str = "RP_83922", action_override: str | None = None) -> ExecutionOutcomeDto:
    strategy = action_override if action_override else "Delayed Retry (15 min)"
    timeline = [
        DecisionStepDto(step_number=1, title="ML Prediction", description="87% recovery probability evaluated", status="completed"),
        DecisionStepDto(step_number=2, title="AI Diagnosis", description="Transient Gateway Failure identified", status="completed"),
        DecisionStepDto(step_number=3, title="Policy Check", description="POLICY APPROVED: 87% >= 70% threshold", status="completed"),
        DecisionStepDto(step_number=4, title="Recovery Scheduled", description=f"Scheduled {strategy} (15 minutes window)", status="completed"),
        DecisionStepDto(step_number=5, title="Recovery Attempt", description="Attempt #1 via Gateway Switch", status="completed"),
        DecisionStepDto(step_number=6, title="Payment Result", description="Successful: ₹24,500 captured", status="completed")
    ]

    return ExecutionOutcomeDto(
        execution_id="REC_83922",
        transaction_id=transaction_id,
        status="RECOVERED",
        amount_formatted="₹24,500",
        expected_recovery_formatted="₹21,315",
        actual_recovery_formatted="₹24,500",
        strategy_used=strategy,
        attempts_count=1,
        policy_approved=True,
        environment="Demo Payment Gateway Simulation",
        timeline=timeline
    )


def generate_live_demo_failure() -> dict:
    import random
    import time
    txn_num = random.randint(9000, 9999)
    txn_id = f"RP_{txn_num}"
    amounts = [28500, 34000, 42000, 18500, 52000]
    names = ["Rahul Sharma", "Priya Nair", "Anand Verma", "Deepak Gupta", "Kavita Reddy"]
    amt = random.choice(amounts)
    name = random.choice(names)

    return {
        "id": txn_id,
        "customer_name": name,
        "amount": amt,
        "amount_formatted": f"₹{amt:,}",
        "category": "upi_degradation",
        "failure_reason": "UPI Gateway Timeout (504)",
        "payment_method": "UPI",
        "gateway": "Razorpay Switch",
        "timestamp": "Just Now",
        "probability_pct": 89,
        "potential_recovery": float(amt),
        "potential_recovery_formatted": f"₹{amt:,}",
        "expected_recovery": round(amt * 0.89, 2),
        "expected_recovery_formatted": f"₹{int(amt * 0.89):,}",
        "recommended_strategy": "Delayed Retry (15m)",
        "recommendation_note": "ML Model predicts 89% recovery probability. Gateway switch timeout.",
        "priority": "HIGH",
        "priority_badge": "HIGH",
        "policy_status": "AUTO-ELIGIBLE",
        "policy_reason": "Recovery Probability 89% >= Merchant Policy 70%",
        "status_type": "ready",
        "status_label": "Ready",
        "status": "Pending Recovery"
    }


def get_outage_radar_telemetry() -> list:
    return [
        {
            "gateway_name": "Razorpay Core Switch",
            "latency_ms": 410,
            "status": "Healthy",
            "failure_rate_formatted": "1.2%",
            "impact_level": "LOW",
            "ai_recommendation": "Optimal routing active"
        },
        {
            "gateway_name": "SBI UPI Switch",
            "latency_ms": 4200,
            "status": "Outage Radar Active",
            "failure_rate_formatted": "18.4%",
            "impact_level": "HIGH",
            "ai_recommendation": "Delayed retry cooling window enforced"
        },
        {
            "gateway_name": "HDFC Direct Gateway",
            "latency_ms": 280,
            "status": "Healthy",
            "failure_rate_formatted": "0.8%",
            "impact_level": "LOW",
            "ai_recommendation": "Normal processing"
        },
        {
            "gateway_name": "ICICI NetBanking",
            "latency_ms": 1420,
            "status": "Degraded",
            "failure_rate_formatted": "6.5%",
            "impact_level": "MEDIUM",
            "ai_recommendation": "Smart Payment Link fallback"
        }
    ]
