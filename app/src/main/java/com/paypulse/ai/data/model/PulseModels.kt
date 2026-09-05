package com.paypulse.ai.data.model

import com.google.gson.annotations.SerializedName

// ─── Screen 1: Upgraded Overview Models ───────────────────────────────────────

data class RecoveryPulseMetrics(
    val revenueAtRiskFormatted: String = "₹38.6L",
    val expectedRecoveryFormatted: String = "₹28.7L",
    val recoveredAmountFormatted: String = "₹21.4L",
    val recoveryRateFormatted: String = "74.6%",
    val recoveryRateValue: Float = 74.6f,
    val recoveryChange: String = "+18.4% vs Previous Period",
    val riskChangePct: Float = 12.0f,
    val expectedPctOfRisk: Int = 74
)

data class TodayRecoveryImpact(
    val recoveredTodayFormatted: String = "₹2.84L",
    val successfulRecoveriesToday: Int = 347,
    val recoveryRateTodayFormatted: String = "71%",
    val changeVsPreviousPeriod: String = "+18.4% vs Yesterday",
    val retryCostAvoidedFormatted: String = "₹42.8K saved"
)

data class FailureCategoryOpportunity(
    val category: String = "upi_timeout",
    val categoryLabel: String = "UPI Timeout",
    val transactionCount: Int = 27,
    val expectedRecoveryFormatted: String = "₹1.42L",
    val expectedRecoveryAmount: Double = 142000.0,
    val recommendedAction: String = "Delayed Retry"
)

data class LiveRecoveryEvent(
    val transactionId: String = "TXN_10482",
    val amountFormatted: String = "₹24,500",
    val amount: Double = 24500.0,
    val failureReason: String = "UPI Timeout",
    val aiAction: String = "Delayed Retry",
    val status: String = "Policy Approved",
    val timestamp: String = "2 min ago"
)

data class GatewayHealthTelemetry(
    val gateway: String = "Razorpay Switch",
    val healthScore: Int = 92,
    val latencyMs: Int = 410,
    val failureRateFormatted: String = "3.2%",
    val status: String = "Healthy",
    val trend: String = "Stable"
)

data class OperationalAlert(
    val type: String = "gateway_degradation",
    val severity: String = "CRITICAL",
    val title: String = "Gateway degradation detected",
    val message: String = "UPI failures increased by 18.5% across generic bank switches.",
    val recommendedAction: String = "Delay automatic retries by 15 min cooling window for affected transactions.",
    val createdAt: String = "Just now"
)

data class ProfitLossSummary(
    val grossRevenueAtRiskFormatted: String = "₹38.60L",
    val netRecoveredRevenueFormatted: String = "₹21.40L",
    val unrecoveredRiskFormatted: String = "₹17.20L",
    val recoveryRatePct: Float = 55.4f,
    val gatewayFeeSavingsFormatted: String = "₹42.8K",
    val aiOperatingCostFormatted: String = "₹1.4K",
    val netProfitFormatted: String = "+₹20.97L",
    val netRecoveryRoiPct: Float = 1497.0f,
    val autoRetryPct: Int = 42,
    val smartLinkPct: Int = 28,
    val eMandatePct: Int = 18,
    val altChannelPct: Int = 12
)

data class CibilScoreSummary(
    val avgCreditScore: Int = 768,
    val scoreStatusLabel: String = "EXCELLENT (LOW RISK)",
    val borrowersAnalyzedCount: Int = 84,
    val lowRiskPct: Int = 72,
    val mediumRiskPct: Int = 21,
    val highRiskPct: Int = 7,
    val creditUtilizationFormatted: String = "28.4% Avg"
)

data class BorrowerCreditBureauSummary(
    val borrowerName: String = "Rahul Sharma",
    val cibilScore: Int = 785,
    val riskTier: String = "LOW RISK",
    val activeLoansCount: Int = 3,
    val totalBorrowedFormatted: String = "₹50,000",
    val onTimePaymentPct: Int = 96,
    val statusColorHex: String = "#059669"
)

data class OverviewScreenData(
    val recoveryPulse: RecoveryPulseMetrics = RecoveryPulseMetrics(),
    val todayImpact: TodayRecoveryImpact = TodayRecoveryImpact(),
    val profitLoss: ProfitLossSummary = ProfitLossSummary(),
    val cibilSummary: CibilScoreSummary = CibilScoreSummary(),
    val borrowerCreditList: List<BorrowerCreditBureauSummary> = listOf(
        BorrowerCreditBureauSummary("Rahul Sharma", 785, "LOW RISK", 3, "₹50,000", 96, "#059669"),
        BorrowerCreditBureauSummary("Priya Nair", 742, "LOW RISK", 2, "₹35,000", 91, "#059669"),
        BorrowerCreditBureauSummary("Sneha Patel", 680, "MODERATE RISK", 4, "₹18,000", 82, "#D97706"),
        BorrowerCreditBureauSummary("Vikram Singh", 590, "HIGH RISK", 5, "₹92,000", 45, "#DC2626")
    ),
    val potentialRecoveryNowFormatted: String = "₹2.57L",
    val transactionsRecoverableNow: Int = 27,
    val failureCategories: List<FailureCategoryOpportunity> = listOf(
        FailureCategoryOpportunity("upi_timeout", "UPI Timeout", 27, "₹1.42L", 142000.0, "Delayed Retry"),
        FailureCategoryOpportunity("mandate_failed", "Mandate Failure", 18, "₹84K", 84000.0, "Retry Mandate"),
        FailureCategoryOpportunity("checkout_abandoned", "Checkout Abandonment", 14, "₹31K", 31000.0, "Smart Payment Link"),
        FailureCategoryOpportunity("bank_decline", "Bank Decline", 9, "₹18.5K", 18500.0, "Alt. Payment Method")
    ),
    val recentEvents: List<LiveRecoveryEvent> = listOf(
        LiveRecoveryEvent("TXN_10482", "₹24,500", 24500.0, "UPI Timeout", "Delayed Retry", "Policy Approved", "2 min ago"),
        LiveRecoveryEvent("TXN_10479", "₹45,000", 45000.0, "Mandate Failed", "Retry Mandate", "Retry Scheduled", "8 min ago"),
        LiveRecoveryEvent("TXN_10471", "₹12,800", 12800.0, "Checkout Abandoned", "Payment Link", "Recovered", "14 min ago"),
        LiveRecoveryEvent("TXN_10465", "₹8,200", 8200.0, "Bank Decline", "Alt. Payment", "Analyzing", "22 min ago")
    ),
    val gatewayHealthList: List<GatewayHealthTelemetry> = listOf(
        GatewayHealthTelemetry("Razorpay Switch", 92, 410, "3.2%", "Healthy", "Stable"),
        GatewayHealthTelemetry("HDFC Bank Direct", 95, 280, "1.8%", "Healthy", "Improving"),
        GatewayHealthTelemetry("UPI Generic Switch", 62, 840, "18.5%", "Degraded", "Elevated Failure"),
        GatewayHealthTelemetry("ICICI NetBanking", 88, 620, "5.4%", "Healthy", "Elevated Latency")
    ),
    val operationalAlert: OperationalAlert? = OperationalAlert(),
    val aiObservations: List<String> = listOf(
        "Delayed retry currently has the highest expected recovery (87%) for UPI timeout failures.",
        "Mandate failures are concentrated around morning salary-cycle settlement windows."
    )
)

// Legacy / Existing Overview models for backward compatibility
data class PulseMetrics(
    val revenueAtRiskFormatted: String = "₹38.6L",
    val recoverableFormatted: String = "₹28.7L",
    val recoveredFormatted: String = "₹21.4L",
    val recoveryRatePct: Float = 74.6f,
    val riskChangePct: Float = 12.3f,
    val recoverablePctOfRisk: Int = 74,
    val recoveredChangePct: Float = 8.2f,
    val recoveryRateChangePct: Float = 5.4f
)

data class TrendDay(
    val day: String = "",
    val atRisk: Float = 0f,
    val recoverable: Float = 0f,
    val recovered: Float = 0f
)

data class RecoveryBreakdown(
    val upiPct: Float = 42f,
    val cardPct: Float = 28f,
    val netbankingPct: Float = 18f,
    val otherPct: Float = 12f
)

data class CriticalIncident(
    val headline: String = "UPI degradation affecting 27 transactions. ₹1.42L at risk.",
    val recoverableSummary: String = "₹1.06L recoverable via smart retry",
    val category: String = "upi_degradation"
)

data class PriorityAction(
    val id: String = "",
    val title: String = "",
    val recoverableFormatted: String = "",
    val itemCountLabel: String = "",
    val category: String = ""
)

// ─── Screen 2: Queue ─────────────────────────────────────────────────────────

data class QueueSummary(
    val pendingActionsCount: Int = 26,
    val pendingPotentialRecoveryFormatted: String = "₹28.7L potential",
    val highPriorityCount: Int = 8,
    val highPriorityAtRiskFormatted: String = "₹14.2L at risk",
    val autoEligibleCount: Int = 19,
    val autoEligiblePctFormatted: String = "74% of actions",
    // New: server-supplied totals
    val totalExpectedRecovery: Double = 0.0,
    val totalExpectedRecoveryFormatted: String = "₹28.7L"
)

data class TransactionHistoryItem(
    val timestamp: String = "Just now",
    val title: String = "",
    val detail: String = "",
    val iconType: String = "info"
)

data class TransactionItem(
    val id: String = "",
    val customerName: String = "",
    val amountFormatted: String = "",
    val amount: Double = 0.0,
    val failureReason: String = "",
    val paymentMethod: String = "",
    val gateway: String = "",
    val probabilityPct: Int = 0,
    val expectedRecovery: Double = 0.0,
    val expectedRecoveryFormatted: String = "",
    val potentialRecoveryFormatted: String = "",
    val recommendedStrategy: String = "",
    val actionDelay: String = "",
    val recommendationNote: String? = null,
    val statusLabel: String = "",
    val statusType: String = "awaiting_approval",
    val priorityBadge: String? = null,
    // Policy Engine fields
    val policyStatus: String = "NEEDS REVIEW",     // AUTO-ELIGIBLE | NEEDS REVIEW | BLOCKED
    val policyReason: String = "",
    val priority: String = "MEDIUM",               // HIGH | MEDIUM | LOW
    val retryCount: Int = 0,
    val category: String = "",
    val repaymentDeadline: String = "2026-09-15",
    val deadlineDaysRemaining: Int = 13,
    val deadlineLabel: String = "Due in 13 days (Sep 15)",
    val history: List<TransactionHistoryItem> = emptyList()
)

// ─── Screen 3: Insights ───────────────────────────────────────────────────────

data class NetworkPerformanceItem(
    val gateway: String = "",
    val successRateFormatted: String = "",
    val volumeFormatted: String = "",
    val statusLabel: String = "",
    val statusType: String = "optimal"
)

data class OptimizationSuggestion(
    val id: String = "",
    val title: String = "",
    val description: String = "",
    val iconType: String = "schedule"
)

// ─── Screen 4: Settings / Automation ─────────────────────────────────────────

data class WorkflowItem(
    val id: String = "",
    val title: String = "",
    val trigger: String = "",
    val strategy: String = "",
    val performanceFormatted: String = "",
    val status: Boolean = true,
    val iconType: String = "upi"
)

data class MerchantPolicyState(
    val minProbabilityPct: Float = 70f,
    val maxAutoRetries: Float = 2f,
    val maxAutoRecoveryAmount: String = "50,000",
    val approvalRequiredAbove: String = "25,000",
    val escalateLowConfidence: Boolean = true
)

data class PolicyDecisionLogItem(
    val id: String = "",
    val actionType: String = "",
    val amountFormatted: String = "",
    val reason: String = ""
)

// ─── AI Decision Dialog ───────────────────────────────────────────────────────

data class AIDecisionDetail(
    val transactionId: String = "RP_83921",
    val amount: String = "₹24,500",
    val failureReason: String = "UPI Timeout",
    val diagnosisTitle: String = "Likely transient gateway failure.",
    val explanation: String = "Gateway health and customer history indicate a retry after a cooling window is reasonable.",
    val confidence: Float = 0.87f,
    val probabilityPct: Int = 87,
    val recommendation: String = "delayed_retry",
    val policyDecision: String = "approved",
    val isApproved: Boolean = true
)

// ─── Network API DTOs (Backend ↔ Android) ─────────────────────────────────────

// /api/overview
data class OverviewResponse(
    @SerializedName("revenue_at_risk") val revenueAtRisk: Double = 0.0,
    @SerializedName("revenue_at_risk_formatted") val revenueAtRiskFormatted: String = "",
    @SerializedName("expected_recovery") val expectedRecovery: Double = 0.0,
    @SerializedName("expected_recovery_formatted") val expectedRecoveryFormatted: String = "",
    @SerializedName("recovered_amount") val recoveredAmount: Double = 0.0,
    @SerializedName("recovered_amount_formatted") val recoveredAmountFormatted: String = "",
    @SerializedName("recovery_rate") val recoveryRate: Double = 0.0,
    @SerializedName("recovery_rate_formatted") val recoveryRateFormatted: String = "",
    @SerializedName("recovery_change") val recoveryChange: String = "",
    
    @SerializedName("recovered_today") val recoveredToday: Double = 0.0,
    @SerializedName("recovered_today_formatted") val recoveredTodayFormatted: String = "",
    @SerializedName("successful_recoveries_today") val successfulRecoveriesToday: Int = 0,
    @SerializedName("recovery_rate_today") val recoveryRateToday: Double = 0.0,
    @SerializedName("recovery_rate_today_formatted") val recoveryRateTodayFormatted: String = "",
    @SerializedName("retry_cost_avoided") val retryCostAvoided: Double = 0.0,
    @SerializedName("retry_cost_avoided_formatted") val retryCostAvoidedFormatted: String = "",

    @SerializedName("potential_recovery_now") val potentialRecoveryNow: Double = 0.0,
    @SerializedName("potential_recovery_now_formatted") val potentialRecoveryNowFormatted: String = "",
    @SerializedName("transactions_recoverable_now") val transactionsRecoverableNow: Int = 0,

    @SerializedName("recovery_by_failure_type") val recoveryByFailureType: List<FailureCategoryDto> = emptyList(),
    @SerializedName("recent_recovery_events") val recentRecoveryEvents: List<RecoveryEventDto> = emptyList(),
    @SerializedName("gateway_health") val gatewayHealth: List<GatewayHealthDto> = emptyList(),
    @SerializedName("ai_alerts") val aiAlerts: List<AiAlertDto> = emptyList(),
    @SerializedName("ai_observations") val aiObservations: List<String> = emptyList()
)

data class FailureCategoryDto(
    @SerializedName("category") val category: String = "",
    @SerializedName("category_label") val categoryLabel: String = "",
    @SerializedName("transaction_count") val transactionCount: Int = 0,
    @SerializedName("expected_recovery") val expectedRecovery: Double = 0.0,
    @SerializedName("expected_recovery_formatted") val expectedRecoveryFormatted: String = "",
    @SerializedName("recommended_action") val recommendedAction: String = ""
)

data class RecoveryEventDto(
    @SerializedName("transaction_id") val transactionId: String = "",
    @SerializedName("amount") val amount: Double = 0.0,
    @SerializedName("amount_formatted") val amountFormatted: String = "",
    @SerializedName("failure_reason") val failureReason: String = "",
    @SerializedName("ai_action") val aiAction: String = "",
    @SerializedName("status") val status: String = "",
    @SerializedName("timestamp") val timestamp: String = ""
)

data class GatewayHealthDto(
    @SerializedName("gateway") val gateway: String = "",
    @SerializedName("health_score") val healthScore: Int = 0,
    @SerializedName("latency_ms") val latencyMs: Int = 0,
    @SerializedName("failure_rate_pct") val failureRatePct: Double = 0.0,
    @SerializedName("status") val status: String = "",
    @SerializedName("trend") val trend: String = ""
)

data class AiAlertDto(
    @SerializedName("type") val type: String = "",
    @SerializedName("severity") val severity: String = "",
    @SerializedName("title") val title: String = "",
    @SerializedName("message") val message: String = "",
    @SerializedName("recommended_action") val recommendedAction: String = "",
    @SerializedName("created_at") val createdAt: String = ""
)

// /api/dashboard/summary
data class DashboardSummaryResponse(
    @SerializedName("revenue_at_risk") val revenueAtRisk: Double = 0.0,
    @SerializedName("revenue_at_risk_formatted") val revenueAtRiskFormatted: String = "",
    @SerializedName("expected_recovery") val expectedRecovery: Double = 0.0,
    @SerializedName("expected_recovery_formatted") val expectedRecoveryFormatted: String = "",
    @SerializedName("pending_transactions") val pendingTransactions: Int = 0,
    @SerializedName("high_priority_count") val highPriorityCount: Int = 0,
    @SerializedName("recovery_rate_estimate") val recoveryRateEstimate: Double = 0.0,
    @SerializedName("model_report") val modelReport: ModelReport? = null
)

data class ModelReport(
    @SerializedName("selected_model") val selectedModel: String = "",
    @SerializedName("dataset_rows") val datasetRows: Int = 0,
    @SerializedName("positive_rate") val positiveRate: Double = 0.0,
    @SerializedName("models") val models: Map<String, ModelMetrics> = emptyMap()
)

data class ModelMetrics(
    @SerializedName("validation") val validation: MetricSet? = null,
    @SerializedName("test") val test: MetricSet? = null
)

data class MetricSet(
    @SerializedName("precision") val precision: Double = 0.0,
    @SerializedName("recall") val recall: Double = 0.0,
    @SerializedName("f1") val f1: Double = 0.0,
    @SerializedName("roc_auc") val rocAuc: Double = 0.0
)

// /api/recovery-queue
data class RecoveryQueueResponse(
    @SerializedName("total") val total: Int = 0,
    @SerializedName("total_expected_recovery") val totalExpectedRecovery: Double = 0.0,
    @SerializedName("total_expected_recovery_formatted") val totalExpectedRecoveryFormatted: String = "",
    @SerializedName("auto_eligible_count") val autoEligibleCount: Int = 0,
    @SerializedName("transactions") val transactions: List<TransactionDto> = emptyList()
)

data class TransactionDto(
    @SerializedName("transaction_id") val transactionId: String = "",
    @SerializedName("customer_name") val customerName: String = "",
    @SerializedName("amount") val amount: Double = 0.0,
    @SerializedName("payment_method") val paymentMethod: String = "",
    @SerializedName("gateway") val gateway: String = "",
    @SerializedName("failure_reason") val failureReason: String = "",
    @SerializedName("retry_count") val retryCount: Int = 0,
    @SerializedName("status") val status: String = "",
    @SerializedName("created_at") val createdAt: String = "",
    // ML output
    @SerializedName("recovery_probability") val recoveryProbability: Double = 0.0,
    @SerializedName("expected_recovery") val expectedRecovery: Double = 0.0,
    @SerializedName("expected_recovery_formatted") val expectedRecoveryFormatted: String = "",
    @SerializedName("potential_recovery") val potentialRecovery: Double = 0.0,
    @SerializedName("potential_recovery_formatted") val potentialRecoveryFormatted: String = "",
    // AI recommendation
    @SerializedName("recommended_strategy") val recommendedStrategy: String = "",
    @SerializedName("action_delay") val actionDelay: String = "",
    // Priority & Policy (from Policy Engine)
    @SerializedName("priority") val priority: String = "MEDIUM",
    @SerializedName("risk_level") val riskLevel: String = "medium",
    @SerializedName("policy_eligible") val policyEligible: Boolean = false,
    @SerializedName("policy_status") val policyStatus: String = "NEEDS REVIEW",
    @SerializedName("policy_reason") val policyReason: String = ""
)

// /api/transactions/{id}/decision
data class DecisionResponse(
    @SerializedName("transaction") val transaction: TransactionDto? = null,
    @SerializedName("prediction") val prediction: PredictionDto? = null,
    @SerializedName("ai_diagnosis") val aiDiagnosis: AiDiagnosisDto? = null,
    @SerializedName("policy_evaluation") val policyEvaluation: PolicyEvaluationDto? = null,
    @SerializedName("recommendation") val recommendation: String = ""
)

data class PredictionDto(
    @SerializedName("recovery_probability") val recoveryProbability: Double = 0.0,
    @SerializedName("potential_recovery") val potentialRecovery: Double = 0.0
)

data class AiDiagnosisDto(
    @SerializedName("diagnosis_title") val diagnosisTitle: String = "",
    @SerializedName("explanation") val explanation: String = "",
    @SerializedName("confidence") val confidence: Double = 0.0,
    @SerializedName("recommendation") val recommendation: String = ""
)

data class PolicyEvaluationDto(
    @SerializedName("decision") val decision: String = "",
    @SerializedName("approved") val approved: Boolean = false,
    @SerializedName("requires_approval") val requiresApproval: Boolean = false,
    @SerializedName("validated_recommendation") val validatedRecommendation: String = ""
)

// /api/automation/policies
data class AutomationPolicyDto(
    @SerializedName("min_recovery_probability") val minRecoveryProbability: Double = 0.7,
    @SerializedName("max_retry_count") val maxRetryCount: Int = 2,
    @SerializedName("max_automatic_recovery_amount") val maxAutomaticRecoveryAmount: Double = 50000.0,
    @SerializedName("approval_threshold") val approvalThreshold: Double = 25000.0,
    @SerializedName("escalation_probability") val escalationProbability: Double = 0.5,
    @SerializedName("block_hard_declines") val blockHardDeclines: Boolean = true
)

data class PolicyUpdateRequest(
    @SerializedName("min_recovery_probability") val minRecoveryProbability: Double,
    @SerializedName("max_retry_count") val maxRetryCount: Int,
    @SerializedName("max_automatic_recovery_amount") val maxAutomaticRecoveryAmount: Double,
    @SerializedName("approval_threshold") val approvalThreshold: Double,
    @SerializedName("escalation_probability") val escalationProbability: Double,
    @SerializedName("block_hard_declines") val blockHardDeclines: Boolean
)

// /api/recovery/{id}/execute
data class ExecuteRecoveryRequest(
    @SerializedName("action_override") val actionOverride: String? = null,
    @SerializedName("approved_by") val approvedBy: String = "merchant_app"
)

data class ExecuteRecoveryResponse(
    @SerializedName("status") val status: String = "",
    @SerializedName("transaction_id") val transactionId: String = "",
    @SerializedName("execution") val execution: Map<String, Any>? = null
)

// /api/audit-log
data class AuditLogResponse(
    @SerializedName("audit_logs") val auditLogs: List<AuditLogDto> = emptyList()
)

data class AuditLogDto(
    @SerializedName("id") val id: String = "",
    @SerializedName("event_type") val eventType: String = "",
    @SerializedName("status") val status: String = "",
    @SerializedName("transaction_id") val transactionId: String? = null,
    @SerializedName("details") val details: String = "",
    @SerializedName("created_at") val createdAt: String = ""
)

// /api/overview legacy wrapper
data class OverviewData(
    @SerializedName("metrics") val metrics: Map<String, Any> = emptyMap(),
    @SerializedName("trends") val trends: List<Map<String, Any>> = emptyList(),
    @SerializedName("critical_incident") val criticalIncident: Map<String, Any> = emptyMap(),
    @SerializedName("priority_actions") val priorityActions: List<Map<String, Any>> = emptyList()
)

// Legacy recovery request (used by Screen 4 execute button)
data class RecoveryRequest(
    @SerializedName("action_type") val actionType: String,
    @SerializedName("target_ids") val targetIds: List<String>,
    @SerializedName("max_discount_pct") val maxDiscountPct: Float = 0f
)

// ─── Upgraded Screen 3: Insights & Payment Intelligence DTOs ────────────────

data class ModelMetricsDto(
    @SerializedName("roc_auc") val rocAuc: Float = 0.91f,
    @SerializedName("f1") val f1: Float = 0.88f,
    @SerializedName("precision") val precision: Float = 0.91f,
    @SerializedName("recall") val recall: Float = 0.86f,
    @SerializedName("accuracy_secondary") val accuracySecondary: Float = 0.94f,
    @SerializedName("selected_model") val selectedModel: String = "Random Forest Classifier",
    @SerializedName("evaluation_label") val evaluationLabel: String = "Validation Performance (held-out data)",
    @SerializedName("dataset_rows") val datasetRows: Int = 8000,
    @SerializedName("true_positives_count") val truePositivesCount: Int = 3420,
    @SerializedName("false_positives_count") val falsePositivesCount: Int = 340,
    @SerializedName("false_negatives_count") val falseNegativesCount: Int = 560,
    @SerializedName("true_negatives_count") val trueNegativesCount: Int = 3680
)

data class BusinessMetricsDto(
    @SerializedName("expected_recovery") val expectedRecovery: Double = 2870000.0,
    @SerializedName("expected_recovery_formatted") val expectedRecoveryFormatted: String = "₹28.7L",
    @SerializedName("recovered_amount") val recoveredAmount: Double = 2140000.0,
    @SerializedName("recovered_amount_formatted") val recoveredAmountFormatted: String = "₹21.4L",
    @SerializedName("recovery_rate") val recoveryRate: Float = 0.746f,
    @SerializedName("recovery_rate_formatted") val recoveryRateFormatted: String = "74.6%",
    @SerializedName("recovery_lift_pct") val recoveryLiftPct: Float = 18.4f,
    @SerializedName("recovery_lift_formatted") val recoveryLiftFormatted: String = "+18.4% vs Simple Retry",
    @SerializedName("baseline_strategy_label") val baselineStrategyLabel: String = "Simple Retry Baseline",
    @SerializedName("retry_cost_avoided") val retryCostAvoided: Double = 42800.0,
    @SerializedName("retry_cost_avoided_formatted") val retryCostAvoidedFormatted: String = "₹42.8K"
)

data class FailureCategoryIntelligenceDto(
    @SerializedName("category") val category: String = "",
    @SerializedName("category_label") val categoryLabel: String = "",
    @SerializedName("transaction_count") val transactionCount: Int = 0,
    @SerializedName("transaction_value") val transactionValue: Double = 0.0,
    @SerializedName("transaction_value_formatted") val transactionValueFormatted: String = "",
    @SerializedName("expected_recovery") val expectedRecovery: Double = 0.0,
    @SerializedName("expected_recovery_formatted") val expectedRecoveryFormatted: String = "",
    @SerializedName("recovered_amount") val recoveredAmount: Double = 0.0,
    @SerializedName("recovered_amount_formatted") val recoveredAmountFormatted: String = "",
    @SerializedName("recovery_rate_pct") val recoveryRatePct: Float = 0f,
    @SerializedName("recovery_rate_formatted") val recoveryRateFormatted: String = "",
    @SerializedName("recommended_strategy") val recommendedStrategy: String = ""
)

data class FailureTrendPointDto(
    @SerializedName("timestamp_label") val timestampLabel: String = "",
    @SerializedName("failure_count") val failureCount: Int = 0,
    @SerializedName("failure_rate_pct") val failureRatePct: Float = 0f,
    @SerializedName("expected_recovery") val expectedRecovery: Double = 0.0,
    @SerializedName("expected_recovery_formatted") val expectedRecoveryFormatted: String = ""
)

data class GatewayIntelligenceDto(
    @SerializedName("gateway") val gateway: String = "",
    @SerializedName("health_score") val healthScore: Int = 0,
    @SerializedName("failure_rate_pct") val failureRatePct: Float = 0f,
    @SerializedName("failure_rate_formatted") val failureRateFormatted: String = "",
    @SerializedName("latency_ms") val latencyMs: Int = 0,
    @SerializedName("recovery_rate_pct") val recoveryRatePct: Float = 0f,
    @SerializedName("recovery_rate_formatted") val recoveryRateFormatted: String = "",
    @SerializedName("trend") val trend: String = "",
    @SerializedName("status") val status: String = "",
    @SerializedName("alert_message") val alertMessage: String? = null
)

data class StrategyEffectivenessDto(
    @SerializedName("strategy") val strategy: String = "",
    @SerializedName("strategy_label") val strategyLabel: String = "",
    @SerializedName("attempts") val attempts: Int = 0,
    @SerializedName("successful_recoveries") val successfulRecoveries: Int = 0,
    @SerializedName("success_rate_pct") val successRatePct: Float = 0f,
    @SerializedName("success_rate_formatted") val successRateFormatted: String = "",
    @SerializedName("expected_recovery") val expectedRecovery: Double = 0.0,
    @SerializedName("expected_recovery_formatted") val expectedRecoveryFormatted: String = "",
    @SerializedName("recovered_amount") val recoveredAmount: Double = 0.0,
    @SerializedName("recovered_amount_formatted") val recoveredAmountFormatted: String = ""
)

data class AiInsightDto(
    @SerializedName("id") val id: String = "",
    @SerializedName("type") val type: String = "",
    @SerializedName("severity") val severity: String = "INFO",
    @SerializedName("title") val title: String = "",
    @SerializedName("message") val message: String = "",
    @SerializedName("financial_impact_formatted") val financialImpactFormatted: String = "",
    @SerializedName("recommended_action") val recommendedAction: String = "",
    @SerializedName("related_filter") val relatedFilter: String = "",
    @SerializedName("created_at") val createdAt: String = ""
)

data class ModelHealthDto(
    @SerializedName("active_model_name") val activeModelName: String = "Recovery Model v3 (RandomForest)",
    @SerializedName("transactions_analyzed") val transactionsAnalyzed: Int = 8420,
    @SerializedName("predictions_generated") val predictionsGenerated: Int = 14280,
    @SerializedName("last_updated") val lastUpdated: String = "Today, 8:32 PM",
    @SerializedName("dataset_size_rows") val datasetSizeRows: Int = 8000,
    @SerializedName("data_status") val dataStatus: String = "Healthy",
    @SerializedName("drift_status") val driftStatus: String = "Normal"
)

data class FeatureDriftStatusDto(
    @SerializedName("feature_name") val featureName: String = "",
    @SerializedName("status") val status: String = "Normal"
)

data class InsightsResponseDto(
    @SerializedName("time_range") val timeRange: String = "24H",
    @SerializedName("model_metrics") val modelMetrics: ModelMetricsDto = ModelMetricsDto(),
    @SerializedName("business_metrics") val businessMetrics: BusinessMetricsDto = BusinessMetricsDto(),
    @SerializedName("failure_breakdown") val failureBreakdown: List<FailureCategoryIntelligenceDto> = emptyList(),
    @SerializedName("failure_trends") val failureTrends: List<FailureTrendPointDto> = emptyList(),
    @SerializedName("gateway_metrics") val gatewayMetrics: List<GatewayIntelligenceDto> = emptyList(),
    @SerializedName("strategy_metrics") val strategyMetrics: List<StrategyEffectivenessDto> = emptyList(),
    @SerializedName("ai_insights") val aiInsights: List<AiInsightDto> = emptyList(),
    @SerializedName("model_health") val modelHealth: ModelHealthDto = ModelHealthDto(),
    @SerializedName("feature_drift") val featureDrift: List<FeatureDriftStatusDto> = emptyList()
)


// ─── Upgraded Screen 4: Recovery Automation & Policy Control DTOs ────────────

data class AutomationStatusDto(
    @SerializedName("automation_enabled") val automationEnabled: Boolean = true,
    @SerializedName("status_label") val statusLabel: String = "ACTIVE",
    @SerializedName("transactions_monitored") val transactionsMonitored: Int = 2480,
    @SerializedName("auto_recovery_enabled_count") val autoRecoveryEnabledCount: Int = 1840,
    @SerializedName("recoveries_today_count") val recoveriesTodayCount: Int = 347,
    @SerializedName("recoveries_today_formatted") val recoveriesTodayFormatted: String = "₹2.84L",
    @SerializedName("expected_recovery_today_formatted") val expectedRecoveryTodayFormatted: String = "₹3.82L",
    @SerializedName("pause_explanation") val pauseExplanation: String = "Automatic recovery actions are currently active. AI Policy Engine evaluates every failed payment."
)

data class DecisionStepDto(
    @SerializedName("step_number") val stepNumber: Int = 1,
    @SerializedName("title") val title: String = "",
    @SerializedName("description") val description: String = "",
    @SerializedName("status") val status: String = "completed"
)

data class WorkflowDetailDto(
    @SerializedName("workflow_id") val workflowId: String = "",
    @SerializedName("name") val name: String = "",
    @SerializedName("status_label") val statusLabel: String = "ACTIVE",
    @SerializedName("is_active") val isActive: Boolean = true,
    @SerializedName("icon_type") val iconType: String = "upi",
    @SerializedName("trigger") val trigger: String = "",
    @SerializedName("ml_condition") val mlCondition: String = "",
    @SerializedName("ai_diagnosis") val aiDiagnosis: String = "",
    @SerializedName("action") val action: String = "",
    @SerializedName("delay_minutes") val delayMinutes: Int = 15,
    @SerializedName("max_retries") val maxRetries: Int = 2,
    @SerializedName("performance_formatted") val performanceFormatted: String = "",
    @SerializedName("decision_chain") val decisionChain: List<DecisionStepDto> = emptyList()
)

data class PolicyPreviewRequest(
    @SerializedName("min_recovery_probability") val minRecoveryProbability: Double = 70.0,
    @SerializedName("max_retry_count") val maxRetryCount: Int = 2,
    @SerializedName("max_automatic_recovery_amount") val maxAutomaticRecoveryAmount: Double = 50000.0,
    @SerializedName("hard_decline_policy") val hardDeclinePolicy: String = "BLOCK",
    @SerializedName("customer_communication_enabled") val customerCommunicationEnabled: Boolean = true
)

data class PolicyPreviewResponse(
    @SerializedName("current_min_probability") val currentMinProbability: Double = 70.0,
    @SerializedName("proposed_min_probability") val proposedMinProbability: Double = 80.0,
    @SerializedName("current_eligible_count") val currentEligibleCount: Int = 1284,
    @SerializedName("proposed_eligible_count") val proposedEligibleCount: Int = 913,
    @SerializedName("current_expected_recovery_formatted") val currentExpectedRecoveryFormatted: String = "₹8.4L",
    @SerializedName("proposed_expected_recovery_formatted") val proposedExpectedRecoveryFormatted: String = "₹7.2L",
    @SerializedName("newly_blocked_count") val newlyBlockedCount: Int = 371,
    @SerializedName("estimated_change_summary") val estimatedChangeSummary: String = "-371 transactions require merchant review (+₹1.2L shifted to manual review queue)"
)


// ─── Strategy Simulator & Transaction Intelligence DTOs ──────────────────────

data class StrategySimulationItemDto(
    @SerializedName("strategy_id") val strategyId: String = "",
    @SerializedName("name") val name: String = "",
    @SerializedName("type") val type: String = "",
    @SerializedName("probability_pct") val probabilityPct: Double = 0.0,
    @SerializedName("expected_recovery_formatted") val expectedRecoveryFormatted: String = "",
    @SerializedName("estimated_cost_formatted") val estimatedCostFormatted: String = "",
    @SerializedName("net_expected_recovery_formatted") val netExpectedRecoveryFormatted: String = "",
    @SerializedName("policy_eligible") val policyEligible: Boolean = true,
    @SerializedName("policy_reason") val policyReason: String = "",
    @SerializedName("policy_status_label") val policyStatusLabel: String = "ELIGIBLE",
    @SerializedName("recommended") val recommended: Boolean = false
)

data class SimulationResponseDto(
    @SerializedName("transaction_id") val transactionId: String = "RP_83922",
    @SerializedName("amount_formatted") val amountFormatted: String = "₹24,500",
    @SerializedName("failure_reason") val failureReason: String = "UPI Timeout",
    @SerializedName("current_recommended_strategy") val currentRecommendedStrategy: String = "Delayed Retry (15 min)",
    @SerializedName("options") val options: List<StrategySimulationItemDto> = emptyList()
)

data class TransactionIntelligenceDto(
    @SerializedName("transaction_id") val transactionId: String = "RP_83922",
    @SerializedName("amount_formatted") val amountFormatted: String = "₹24,500",
    @SerializedName("customer_name") val customerName: String = "Rahul Sharma",
    @SerializedName("payment_method") val paymentMethod: String = "UPI",
    @SerializedName("gateway") val gateway: String = "Razorpay Switch",
    @SerializedName("failure_reason") val failureReason: String = "UPI Gateway Timeout (504)",
    @SerializedName("recovery_probability_pct") val recoveryProbabilityPct: Double = 87.0,
    @SerializedName("expected_recovery_formatted") val expectedRecoveryFormatted: String = "₹21,315",
    @SerializedName("ai_diagnosis") val aiDiagnosis: String = "Transient Gateway Failure during morning settlement peak traffic",
    @SerializedName("ml_signals") val mlSignals: List<String> = emptyList(),
    @SerializedName("policy_status") val policyStatus: String = "POLICY APPROVED",
    @SerializedName("policy_reason") val policyReason: String = "Recovery probability 87% >= merchant threshold 70%",
    @SerializedName("recommended_action") val recommendedAction: String = "Delayed Retry · 15 min",
    @SerializedName("action_delay") val actionDelay: String = "15 minutes",
    @SerializedName("timeline_steps") val timelineSteps: List<DecisionStepDto> = emptyList()
)

data class ExecutionConfirmationDto(
    @SerializedName("transaction_id") val transactionId: String = "RP_83922",
    @SerializedName("amount_formatted") val amountFormatted: String = "₹24,500",
    @SerializedName("failure_reason") val failureReason: String = "UPI Timeout",
    @SerializedName("recovery_probability_pct") val recoveryProbabilityPct: Double = 87.0,
    @SerializedName("expected_recovery_formatted") val expectedRecoveryFormatted: String = "₹21,315",
    @SerializedName("recommended_action") val recommendedAction: String = "Delayed Retry",
    @SerializedName("action_delay") val actionDelay: String = "15 minutes",
    @SerializedName("policy_status") val policyStatus: String = "APPROVED",
    @SerializedName("retry_count") val retryCount: Int = 0,
    @SerializedName("max_retries") val maxRetries: Int = 2
)

data class ExecutionOutcomeDto(
    @SerializedName("execution_id") val executionId: String = "REC_83922",
    @SerializedName("transaction_id") val transactionId: String = "RP_83922",
    @SerializedName("status") val status: String = "RECOVERED",
    @SerializedName("amount_formatted") val amountFormatted: String = "₹24,500",
    @SerializedName("expected_recovery_formatted") val expectedRecoveryFormatted: String = "₹21,315",
    @SerializedName("actual_recovery_formatted") val actualRecoveryFormatted: String = "₹24,500",
    @SerializedName("strategy_used") val strategyUsed: String = "Delayed Retry",
    @SerializedName("attempts_count") val attemptsCount: Int = 1,
    @SerializedName("policy_approved") val policyApproved: Boolean = true,
    @SerializedName("environment") val environment: String = "Demo Payment Gateway Simulation",
    @SerializedName("timeline") val timeline: List<DecisionStepDto> = emptyList()
)

data class DemoTriggerResponseDto(
    @SerializedName("status") val status: String = "SUCCESS",
    @SerializedName("message") val message: String = "",
    @SerializedName("transaction_id") val transactionId: String = "",
    @SerializedName("amount_formatted") val amountFormatted: String = "",
    @SerializedName("customer_name") val customerName: String = "",
    @SerializedName("failure_reason") val failureReason: String = "",
    @SerializedName("recovery_probability_pct") val recoveryProbabilityPct: Int = 89,
    @SerializedName("recommended_strategy") val recommendedStrategy: String = ""
)

data class OutageRadarItemDto(
    @SerializedName("gateway_name") val gatewayName: String = "",
    @SerializedName("latency_ms") val latencyMs: Int = 0,
    @SerializedName("status") val status: String = "",
    @SerializedName("failure_rate_formatted") val failureRateFormatted: String = "",
    @SerializedName("impact_level") val impactLevel: String = "",
    @SerializedName("ai_recommendation") val aiRecommendation: String = ""
)

data class BatchRecoveryRequestDto(
    @SerializedName("target_ids") val targetIds: List<String> = emptyList()
)

data class BatchRecoveryResponseDto(
    @SerializedName("recovered_count") val recoveredCount: Int = 0,
    @SerializedName("total_amount_formatted") val totalAmountFormatted: String = "",
    @SerializedName("updated_transaction_ids") val updatedTransactionIds: List<String> = emptyList(),
    @SerializedName("status_message") val statusMessage: String = ""
)




