from pydantic import BaseModel, Field
from typing import Any, Dict, List, Optional

class PulseMetrics(BaseModel):
    revenue_at_risk: float = Field(..., description="Revenue at risk in Lakhs")
    revenue_at_risk_formatted: str = "₹38.6L"
    risk_change_pct: float = 12.0
    recoverable: float = 28.7
    recoverable_formatted: str = "₹28.7L"
    recoverable_pct_of_risk: float = 74.3
    recovered: float = 21.4
    recovered_formatted: str = "₹21.4L"
    recovered_change_pct: float = 8.2
    recovered_today_formatted: str = "₹3.2L recovered today"
    recovery_rate_pct: float = 74.6
    recovery_rate_change_pct: float = 2.4

class TrendDay(BaseModel):
    day: str
    at_risk: float
    recoverable: float
    recovered: float

class RecoveryBreakdown(BaseModel):
    delayed_retries: float = 9.2
    delayed_retries_formatted: str = "₹9.2L"
    payment_links: float = 5.4
    payment_links_formatted: str = "₹5.4L"
    alternative_methods: float = 4.1
    alternative_methods_formatted: str = "₹4.1L"
    subscription_recovery: float = 2.7
    subscription_recovery_formatted: str = "₹2.7L"

class PriorityAction(BaseModel):
    id: str
    title: str
    recoverable_formatted: str
    item_count_label: str
    category: str
    status: str = "Attention Needed"

class CriticalIncident(BaseModel):
    id: str = "INC-UPI-9082"
    badge: str = "CRITICAL"
    headline: str = "UPI payment success rate dropped 12.5% in the last 60 minutes."
    recoverable_summary: str = "27 transactions eligible for recovery · ₹1.42L expected recovery"
    affected_handles: List[str] = ["@okicici", "@ybl", "@paytm"]
    drop_percentage: float = 12.5
    timestamp: str = "Last 60 mins"

class QueueSummary(BaseModel):
    pending_actions_count: int = 27
    pending_potential_recovery_formatted: str = "₹2.57L potential recovery"
    high_priority_count: int = 8
    high_priority_at_risk_formatted: str = "₹1.42L at risk"
    auto_eligible_count: int = 19
    auto_eligible_pct_formatted: str = "74% of pending actions"

class NetworkPerformanceItem(BaseModel):
    gateway: str
    success_rate_formatted: str
    volume_formatted: str
    status_label: str
    status_type: str

class OptimizationSuggestion(BaseModel):
    id: str
    title: str
    description: str
    icon_type: str

class AIDecisionDetail(BaseModel):
    transaction_id: str = "RP_83921"
    amount_formatted: str = "₹8,499"
    status_badge: str = "Payment Failed"
    timestamp: str = "UPI - Oct 24, 2023 - 12:43 UTC"
    payment_method: str = "UPI"
    failure_reason: str = "UPI Gateway Timeout"
    gateway: str = "Razorpay"
    customer_type: str = "Returning customer"
    prev_successes: int = 4
    diagnosis_title: str = "Temporary UPI payment degradation."
    confidence_pct: int = 91
    explanation: str = "PayPulse detected an unusual increase in UPI failures during the transaction window. Similar transactions in the same period show elevated timeout rates indicating a transient network issue rather than a hard failure."

class WorkflowItem(BaseModel):
    id: str
    title: str
    icon_type: str  # 'upi', 'subscription', 'checkout', 'alt_payment'
    status: bool = True
    trigger: str
    strategy: str
    performance_formatted: str

class MerchantPolicyState(BaseModel):
    min_probability_pct: int = 70
    max_auto_retries: int = 2
    max_auto_recovery_amount: int = 50000
    max_auto_recovery_amount_formatted: str = "₹ 50,000"
    approval_required_above: int = 25000
    approval_required_above_formatted: str = "₹ 25,000"
    escalate_low_confidence: bool = True

class PolicyDecisionLogItem(BaseModel):
    id: str
    action_type: str  # 'blocked', 'escalated', 'approved'
    amount_formatted: str
    reason: str

class TransactionItemModel(BaseModel):
    id: str
    customer_name: str
    amount: int
    amount_formatted: str
    category: str
    failure_reason: str
    payment_method: str
    timestamp: str
    probability_pct: int
    potential_recovery_formatted: str
    recommended_strategy: str
    recommendation_note: Optional[str] = None
    priority_badge: Optional[str] = None
    status_type: str = "ready"
    status_label: str = "Ready"
    status: str = "Pending Recovery"

class RecoveryActionRequest(BaseModel):
    action_type: str
    target_ids: List[str]
    max_discount_pct: Optional[float] = 0.0

class AuditLogItem(BaseModel):
    id: str
    timestamp: str
    action_type: str
    target_id: str
    status: str
    reasoning: str
    money_saved_formatted: str


class TransactionFeatures(BaseModel):
    amount: float
    payment_method: str
    gateway: str
    failure_reason: str
    customer_payment_history: int
    customer_success_rate: float
    retry_count: int
    subscription_status: str
    checkout_abandonment: int
    gateway_health: float
    recent_failure_rate: float
    recent_failure_spike: int
    previous_recovery_history: int


class PredictionResponse(BaseModel):
    recovery_probability: float
    potential_recovery: float


class PolicyUpdateRequest(BaseModel):
    min_recovery_probability: Optional[float] = None
    max_retry_count: Optional[int] = None
    max_automatic_recovery_amount: Optional[float] = None
    approval_threshold: Optional[float] = None
    escalation_probability: Optional[float] = None
    block_hard_declines: Optional[bool] = None


class ExecuteRecoveryRequest(BaseModel):
    approved_by: Optional[str] = None
    action_override: Optional[str] = None


class DecisionResponse(BaseModel):
    transaction: Dict[str, Any]
    prediction: Dict[str, float]
    ai_diagnosis: Dict[str, Any]
    policy_evaluation: Dict[str, Any]
    recommendation: str


# ─── Screen 3: Insights & Payment Intelligence DTOs ──────────────────────────

class ModelMetricsDto(BaseModel):
    roc_auc: float = 0.91
    f1: float = 0.88
    precision: float = 0.91
    recall: float = 0.86
    accuracy_secondary: float = 0.94
    selected_model: str = "Random Forest Classifier"
    evaluation_label: str = "Validation Performance (held-out data)"
    dataset_rows: int = 8000
    true_positives_count: int = 3420
    false_positives_count: int = 340
    false_negatives_count: int = 560
    true_negatives_count: int = 3680


class BusinessMetricsDto(BaseModel):
    expected_recovery: float = 2870000.0
    expected_recovery_formatted: str = "₹28.7L"
    recovered_amount: float = 2140000.0
    recovered_amount_formatted: str = "₹21.4L"
    recovery_rate: float = 0.746
    recovery_rate_formatted: str = "74.6%"
    recovery_lift_pct: float = 18.4
    recovery_lift_formatted: str = "+18.4% vs Simple Retry Baseline"
    baseline_strategy_label: str = "Simple Retry Baseline"
    retry_cost_avoided: float = 42800.0
    retry_cost_avoided_formatted: str = "₹42.8K"


class FailureCategoryIntelligenceDto(BaseModel):
    category: str
    category_label: str
    transaction_count: int
    transaction_value: float
    transaction_value_formatted: str
    expected_recovery: float
    expected_recovery_formatted: str
    recovered_amount: float
    recovered_amount_formatted: str
    recovery_rate_pct: float
    recovery_rate_formatted: str
    recommended_strategy: str


class FailureTrendPointDto(BaseModel):
    timestamp_label: str
    failure_count: int
    failure_rate_pct: float
    expected_recovery: float
    expected_recovery_formatted: str


class GatewayIntelligenceDto(BaseModel):
    gateway: str
    health_score: int
    failure_rate_pct: float
    failure_rate_formatted: str
    latency_ms: int
    recovery_rate_pct: float
    recovery_rate_formatted: str
    trend: str
    status: str
    alert_message: Optional[str] = None


class StrategyEffectivenessDto(BaseModel):
    strategy: str
    strategy_label: str
    attempts: int
    successful_recoveries: int
    success_rate_pct: float
    success_rate_formatted: str
    expected_recovery: float
    expected_recovery_formatted: str
    recovered_amount: float
    recovered_amount_formatted: str


class AiInsightDto(BaseModel):
    id: str
    type: str
    severity: str  # CRITICAL, WARNING, INFO
    title: str
    message: str
    financial_impact_formatted: str
    recommended_action: str
    related_filter: str
    created_at: str


class ModelHealthDto(BaseModel):
    active_model_name: str = "Recovery Model v3 (RandomForest)"
    transactions_analyzed: int = 8420
    predictions_generated: int = 14280
    last_updated: str = "Today, 8:32 PM"
    dataset_size_rows: int = 8000
    data_status: str = "Healthy"
    drift_status: str = "Normal"


class FeatureDriftStatusDto(BaseModel):
    feature_name: str
    status: str  # Normal, Warning, Drift Detected


class InsightsResponseDto(BaseModel):
    time_range: str = "24h"
    model_metrics: ModelMetricsDto
    business_metrics: BusinessMetricsDto
    failure_breakdown: List[FailureCategoryIntelligenceDto]
    failure_trends: List[FailureTrendPointDto]
    gateway_metrics: List[GatewayIntelligenceDto]
    strategy_metrics: List[StrategyEffectivenessDto]
    ai_insights: List[AiInsightDto]
    model_health: ModelHealthDto
    feature_drift: List[FeatureDriftStatusDto]


# ─── Screen 4: Recovery Automation & Policy DTOs ──────────────────────────────

class AutomationStatusDto(BaseModel):
    automation_enabled: bool = True
    status_label: str = "ACTIVE"
    transactions_monitored: int = 2480
    auto_recovery_enabled_count: int = 1840
    recoveries_today_count: int = 347
    recoveries_today_formatted: str = "₹2.84L"
    expected_recovery_today_formatted: str = "₹3.82L"
    pause_explanation: str = "Automatic recovery actions are currently active. AI Policy Engine evaluates every failed payment."


class DecisionStepDto(BaseModel):
    step_number: int
    title: str
    description: str
    status: str = "completed"


class WorkflowDetailDto(BaseModel):
    workflow_id: str
    name: str
    status_label: str = "ACTIVE"
    is_active: bool = True
    icon_type: str
    trigger: str
    ml_condition: str
    ai_diagnosis: str
    action: str
    delay_minutes: int
    max_retries: int
    performance_formatted: str
    decision_chain: List[DecisionStepDto] = []


class PolicyPreviewRequest(BaseModel):
    min_recovery_probability: float = 70.0
    max_retry_count: int = 2
    max_automatic_recovery_amount: float = 50000.0
    hard_decline_policy: str = "BLOCK"
    customer_communication_enabled: bool = True


class PolicyPreviewResponse(BaseModel):
    current_min_probability: float = 70.0
    proposed_min_probability: float = 80.0
    current_eligible_count: int = 1284
    proposed_eligible_count: int = 913
    current_expected_recovery_formatted: str = "₹8.4L"
    proposed_expected_recovery_formatted: str = "₹7.2L"
    newly_blocked_count: int = 371
    estimated_change_summary: str = "-371 transactions require merchant review (+₹1.2L shifted to manual review queue)"


# ─── Strategy Simulator & Transaction Intelligence DTOs ──────────────────────

class StrategySimulationItemDto(BaseModel):
    strategy_id: str
    name: str
    type: str
    probability_pct: float
    expected_recovery_formatted: str
    estimated_cost_formatted: str
    net_expected_recovery_formatted: str
    policy_eligible: bool
    policy_reason: str
    policy_status_label: str = "ELIGIBLE"
    recommended: bool = False


class SimulationResponseDto(BaseModel):
    transaction_id: str = "RP_83922"
    amount_formatted: str = "₹24,500"
    failure_reason: str = "UPI Timeout"
    current_recommended_strategy: str = "Delayed Retry (15 min)"
    options: List[StrategySimulationItemDto] = []


class TransactionIntelligenceDto(BaseModel):
    transaction_id: str = "RP_83922"
    amount_formatted: str = "₹24,500"
    customer_name: str = "Rahul Sharma"
    payment_method: str = "UPI"
    gateway: str = "Razorpay Switch"
    failure_reason: str = "UPI Gateway Timeout (504)"
    recovery_probability_pct: float = 87.0
    expected_recovery_formatted: str = "₹21,315"
    ai_diagnosis: str = "Transient Gateway Failure during peak traffic"
    ml_signals: List[str] = []
    policy_status: str = "POLICY APPROVED"
    policy_reason: str = "Recovery probability 87% >= merchant minimum 70%"
    recommended_action: str = "Delayed Retry · 15 min"
    action_delay: str = "15 minutes"
    timeline_steps: List[DecisionStepDto] = []


class ExecutionConfirmationDto(BaseModel):
    transaction_id: str = "RP_83922"
    amount_formatted: str = "₹24,500"
    failure_reason: str = "UPI Timeout"
    recovery_probability_pct: float = 87.0
    expected_recovery_formatted: str = "₹21,315"
    recommended_action: str = "Delayed Retry"
    action_delay: str = "15 minutes"
    policy_status: str = "APPROVED"
    retry_count: int = 0
    max_retries: int = 2


class ExecutionOutcomeDto(BaseModel):
    execution_id: str = "REC_83922"
    transaction_id: str = "RP_83922"
    status: str = "RECOVERED"
    amount_formatted: str = "₹24,500"
    expected_recovery_formatted: str = "₹21,315"
    actual_recovery_formatted: str = "₹24,500"
    strategy_used: str = "Delayed Retry"
    attempts_count: int = 1
    policy_approved: bool = True
    environment: str = "Demo Payment Gateway Simulation"
    timeline: List[DecisionStepDto] = []


class DemoTriggerResponseDto(BaseModel):
    status: str = "SUCCESS"
    message: str = "Live payment failure triggered"
    transaction_id: str = "RP_99801"
    amount_formatted: str = "₹38,500"
    customer_name: str = "Rahul Sharma"
    failure_reason: str = "UPI Gateway Timeout (504)"
    recovery_probability_pct: int = 89
    recommended_strategy: str = "Delayed Retry (15m)"


class OutageRadarItemDto(BaseModel):
    gateway_name: str
    latency_ms: int
    status: str  # Healthy, Degraded, Outage Radar Active
    failure_rate_formatted: str
    impact_level: str  # HIGH, MEDIUM, LOW
    ai_recommendation: str


class BatchRecoveryRequestDto(BaseModel):
    transaction_ids: List[str]


class BatchRecoveryResponseDto(BaseModel):
    recovered_count: int
    total_amount_formatted: str
    updated_transaction_ids: List[str]
    status_message: str





