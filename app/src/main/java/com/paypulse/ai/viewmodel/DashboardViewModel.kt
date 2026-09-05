package com.paypulse.ai.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.paypulse.ai.data.api.PayPulseApiService
import com.paypulse.ai.data.model.*
import com.paypulse.ai.ui.utils.BorrowerScoringEngine
import com.paypulse.ai.ui.utils.LoanStorageManager
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class UiState {
    object Loading : UiState()
    data class Success(
        val overviewData: OverviewScreenData = OverviewScreenData(),
        val metrics: PulseMetrics = PulseMetrics(),
        val trends: List<TrendDay> = emptyList(),
        val breakdown: RecoveryBreakdown = RecoveryBreakdown(),
        val criticalIncident: CriticalIncident = CriticalIncident(),
        val priorityActions: List<PriorityAction> = emptyList(),
        val queueSummary: QueueSummary = QueueSummary(),
        val queue: List<TransactionItem> = emptyList(),
        val networkPerformance: List<NetworkPerformanceItem> = emptyList(),
        val optimizationSuggestions: List<OptimizationSuggestion> = emptyList(),
        val aiDecisionDetail: AIDecisionDetail = AIDecisionDetail(),
        val workflows: List<WorkflowItem> = emptyList(),
        val merchantPolicy: MerchantPolicyState = MerchantPolicyState(),
        val policyDecisions: List<PolicyDecisionLogItem> = emptyList(),
        val selectedCategoryFilter: String = "all",
        val searchQuery: String = "",
        val isIncidentModalOpen: Boolean = false,
        val isDecisionModalOpen: Boolean = false,
        val modalCategory: String? = null,
        val isExecutingAction: Boolean = false,
        val selectedWorkflowForConfig: WorkflowItem? = null,
        val mlAccuracyPct: Int = 94,
        val selectedModel: String = "random_forest",
        val executionResult: String? = null,
        // Screen 2 — batch selection & quick-execute state
        val selectedTransactionIds: Set<String> = emptySet(),
        val quickExecuteTransaction: TransactionItem? = null,
        val batchSummaryVisible: Boolean = false,
        // Screen 3 — Upgraded Insights DTO & Time Range state
        val insightsData: InsightsResponseDto = InsightsResponseDto(),
        val insightsTimeRange: String = "24H",
        // Screen 4 — Upgraded Recovery Automation & Policy Control state
        val automationStatus: AutomationStatusDto = AutomationStatusDto(),
        val detailedWorkflows: List<WorkflowDetailDto> = emptyList(),
        val pendingPolicyDraft: MerchantPolicyState? = null,
        val policyPreviewResult: PolicyPreviewResponse? = null,
        // Transaction Intelligence & Recovery Simulator state
        val selectedIntelligenceTransaction: TransactionIntelligenceDto? = null,
        val simulationData: SimulationResponseDto? = null,
        val isSimulatorOpen: Boolean = false,
        // Loan Management & Smart Borrower Safety Score state
        val loansList: List<LoanItem> = emptyList(),
        val isAddLoanModalOpen: Boolean = false,
        val isRepayLoanModalOpen: Boolean = false,
        val isBorrowerProfileModalOpen: Boolean = false,
        val selectedLoanForRepay: LoanItem? = null,
        val selectedBorrowerProfile: BorrowerProfile? = null,
        // Decoupled Confirmation & Outcome Modal state
        val isConfirmDialogOpen: Boolean = false,
        val pendingConfirmationTxnId: String? = null,
        val selectedStrategyOverride: String? = null,
        val executionOutcome: ExecutionOutcomeDto? = null,
        // Legal Demand Notice & Ledger Export state
        val isLegalNoticeModalOpen: Boolean = false,
        val selectedLegalNoticeTxn: TransactionItem? = null,
        // WhatsApp Conversational 1-Click Settlement Simulator state
        val isWhatsAppSimulatorOpen: Boolean = false,
        val selectedWhatsAppTxn: TransactionItem? = null,
        // Affordability Analysis & Calculator state
        val isAffordabilityModalOpen: Boolean = false,
        // Razorpay Smart Payment Gateway & Magic Checkout state
        val isRazorpayCheckoutOpen: Boolean = false,
        val checkoutTargetId: String = "",
        val checkoutAmount: Double = 0.0,
        val checkoutTitle: String = "",
        // Razorpay UPI AutoPay & eMandate state
        val isRazorpayAutoPayOpen: Boolean = false,
        val autoPayTargetBorrower: String = "Rahul Sharma",
        val autoPayAmount: Double = 5000.0,
        val autoPayLoanId: String? = null,
        // Digital Loan Agreement state
        val isLoanAgreementModalOpen: Boolean = false,
        val selectedLoanForAgreement: LoanItem? = null
    ) : UiState()
    data class Error(val message: String, val retryable: Boolean = true) : UiState()
}


class DashboardViewModel : ViewModel() {

    private val apiService = PayPulseApiService.create()

    // Initialize instantly with pre-loaded rich data so 0 milliseconds startup delay!
    private val _uiState = MutableStateFlow<UiState>(loadFallbackData())
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    private var currentPolicyDto: AutomationPolicyDto = AutomationPolicyDto()

    init {
        syncWithLiveBackend()
    }

    // ─── Silent / Fast Backend Sync ─────────────────────────────────────────

    fun loadDashboardData() {
        syncWithLiveBackend()
    }

    private fun syncWithLiveBackend(timeRange: String = "24h") {
        viewModelScope.launch {
            try {
                val overviewResult = runCatching { apiService.getOverview() }
                val queueResult = runCatching { apiService.getRecoveryQueue(limit = 50) }
                val policyResult = runCatching { apiService.getAutomationPolicies() }
                val insightsResult = runCatching { apiService.getInsights(timeRange.lowercase()) }

                val overview = overviewResult.getOrNull()?.body()
                val queue = queueResult.getOrNull()?.body()
                val policy = policyResult.getOrNull()?.body()
                val insights = insightsResult.getOrNull()?.body()

                val current = _uiState.value as? UiState.Success ?: loadFallbackData()

                val overviewData = if (overview != null) buildOverviewScreenData(overview) else current.overviewData

                val (queueSummary, transactionItems) = if (queue != null && queue.transactions.isNotEmpty()) {
                    buildQueueData(queue)
                } else {
                    Pair(current.queueSummary, current.queue)
                }

                policy?.let { currentPolicyDto = it }
                val merchantPolicy = if (policy != null) buildMerchantPolicy(policy) else current.merchantPolicy

                val aiDecision = buildAiDecisionFromQueue(transactionItems)
                val updatedInsights = insights ?: current.insightsData

                _uiState.value = current.copy(
                    overviewData = overviewData,
                    metrics = PulseMetrics(
                        revenueAtRiskFormatted = overviewData.recoveryPulse.revenueAtRiskFormatted,
                        recoverableFormatted = overviewData.recoveryPulse.expectedRecoveryFormatted,
                        recoveredFormatted = overviewData.recoveryPulse.recoveredAmountFormatted,
                        recoveryRatePct = overviewData.recoveryPulse.recoveryRateValue,
                        riskChangePct = overviewData.recoveryPulse.riskChangePct,
                        recoverablePctOfRisk = overviewData.recoveryPulse.expectedPctOfRisk,
                        recoveredChangePct = 8.2f,
                        recoveryRateChangePct = 5.4f
                    ),
                    queueSummary = queueSummary,
                    queue = transactionItems,
                    aiDecisionDetail = aiDecision,
                    merchantPolicy = merchantPolicy,
                    insightsData = updatedInsights,
                    insightsTimeRange = timeRange.uppercase()
                )
            } catch (_: Exception) {
                // Fail silently — UI is already active with instant rich data
            }
        }
    }

    fun onInsightsTimeRangeSelected(range: String) {
        val current = _uiState.value as? UiState.Success ?: return
        _uiState.value = current.copy(insightsTimeRange = range.uppercase())
        syncWithLiveBackend(range)
    }

    // ─── Transaction Intelligence ─────────────────────────────────────────────

    fun openTransactionIntelligence(transactionId: String) {
        val current = _uiState.value as? UiState.Success ?: return
        val cleanId = if (transactionId.isBlank() || transactionId.contains("degradation")) {
            current.queue.firstOrNull()?.id ?: "RP_83921"
        } else {
            transactionId
        }

        val queueItem = current.queue.firstOrNull { it.id == cleanId }

        viewModelScope.launch {
            try {
                val response = apiService.getTransactionDecision(cleanId)
                if (response.isSuccessful && response.body() != null) {
                    val decision = response.body()!!
                    val detail = AIDecisionDetail(
                        transactionId = cleanId,
                        amount = formatInr(decision.transaction?.amount ?: (queueItem?.amount ?: 24500.0)),
                        failureReason = formatFailureReason(decision.transaction?.failureReason ?: (queueItem?.failureReason ?: "upi_timeout")),
                        diagnosisTitle = decision.aiDiagnosis?.diagnosisTitle ?: when {
                            (queueItem?.failureReason ?: "").contains("Timeout", ignoreCase = true) -> "Transient Gateway Timeout during peak banking traffic."
                            (queueItem?.failureReason ?: "").contains("Decline", ignoreCase = true) -> "Issuer Bank Decline. Alternative payment channel recommended."
                            (queueItem?.failureReason ?: "").contains("Mandate", ignoreCase = true) -> "Subscription renewal mandate execution failure."
                            else -> "Likely transient gateway failure."
                        },
                        explanation = decision.aiDiagnosis?.explanation ?: "Gateway health for ${queueItem?.gateway ?: "Switch"} and ${queueItem?.customerName ?: "customer"}'s profile support ${queueItem?.recommendedStrategy ?: "Delayed Retry"}.",
                        confidence = decision.prediction?.recoveryProbability?.toFloat() ?: ((queueItem?.probabilityPct ?: 87) / 100f),
                        probabilityPct = queueItem?.probabilityPct ?: 87,
                        recommendation = decision.recommendation.ifBlank { queueItem?.recommendedStrategy ?: "Delayed Retry" },
                        policyDecision = decision.policyEvaluation?.decision ?: (if (queueItem?.policyStatus == "AUTO-ELIGIBLE") "approved" else "requires_approval"),
                        isApproved = decision.policyEvaluation?.approved ?: (queueItem?.policyStatus == "AUTO-ELIGIBLE")
                    )
                    _uiState.value = current.copy(aiDecisionDetail = detail, isDecisionModalOpen = true)
                    return@launch
                }
            } catch (_: Exception) {}

            val detail = if (queueItem != null) {
                AIDecisionDetail(
                    transactionId = queueItem.id,
                    amount = queueItem.amountFormatted,
                    failureReason = queueItem.failureReason,
                    diagnosisTitle = when {
                        queueItem.failureReason.contains("Timeout", ignoreCase = true) -> "Transient Gateway Timeout during peak banking traffic."
                        queueItem.failureReason.contains("Decline", ignoreCase = true) -> "Issuer Bank Decline. Alternative payment channel recommended."
                        queueItem.failureReason.contains("Mandate", ignoreCase = true) -> "Subscription renewal mandate execution failure."
                        else -> "Checkout session abandoned during payment step."
                    },
                    explanation = "Gateway telemetry for ${queueItem.gateway} and ${queueItem.customerName}'s profile support ${queueItem.recommendedStrategy}.",
                    confidence = queueItem.probabilityPct / 100f,
                    probabilityPct = queueItem.probabilityPct,
                    recommendation = queueItem.recommendedStrategy,
                    policyDecision = if (queueItem.policyStatus == "AUTO-ELIGIBLE") "approved" else "requires_approval",
                    isApproved = queueItem.policyStatus == "AUTO-ELIGIBLE"
                )
            } else {
                AIDecisionDetail(
                    transactionId = cleanId,
                    amount = "₹24,500",
                    failureReason = "UPI Timeout",
                    diagnosisTitle = "Transient Gateway Failure",
                    explanation = "Gateway health supports a retry after a cooling window.",
                    confidence = 0.87f,
                    probabilityPct = 87,
                    recommendation = "Delayed Retry",
                    policyDecision = "approved",
                    isApproved = true
                )
            }
            _uiState.value = current.copy(aiDecisionDetail = detail, isDecisionModalOpen = true)
        }
    }

    // ─── Workflow Configuration ──────────────────────────────────────────────

    fun openWorkflowConfig(workflow: WorkflowItem) {
        val current = _uiState.value as? UiState.Success ?: return
        _uiState.value = current.copy(selectedWorkflowForConfig = workflow)
    }

    fun closeWorkflowConfig() {
        val current = _uiState.value as? UiState.Success ?: return
        _uiState.value = current.copy(selectedWorkflowForConfig = null)
    }

    fun saveWorkflowConfig(updated: WorkflowItem) {
        val current = _uiState.value as? UiState.Success ?: return
        val updatedWorkflows = current.workflows.map { if (it.id == updated.id) updated else it }
        _uiState.value = current.copy(
            workflows = updatedWorkflows,
            selectedWorkflowForConfig = null,
            executionResult = "✅ Workflow '${updated.title}' Deployed Successfully!"
        )
    }

    fun triggerDemoFailure() {
        val current = _uiState.value as? UiState.Success ?: return
        viewModelScope.launch {
            try {
                val res = apiService.triggerDemoFailure()
                if (res.isSuccessful && res.body() != null) {
                    val body = res.body()!!
                    val newTxn = TransactionItem(
                        id = body.transactionId,
                        customerName = body.customerName,
                        amount = 38500.0,
                        amountFormatted = body.amountFormatted,
                        category = "upi_degradation",
                        failureReason = body.failureReason,
                        paymentMethod = "UPI",
                        gateway = "Razorpay Switch",
                        probabilityPct = body.recoveryProbabilityPct,
                        potentialRecoveryFormatted = body.amountFormatted,
                        expectedRecovery = 34265.0,
                        expectedRecoveryFormatted = "₹34,265",
                        recommendedStrategy = body.recommendedStrategy,
                        recommendationNote = "ML Model predicts 89% recovery probability. Gateway switch timeout.",
                        priority = "HIGH",
                        priorityBadge = "HIGH",
                        policyStatus = "AUTO-ELIGIBLE",
                        policyReason = "Recovery Probability 89% >= Merchant Policy 70%",
                        statusType = "ready",
                        statusLabel = "Ready"
                    )
                    val updatedQueue = listOf(newTxn) + current.queue
                    val updatedCount = current.queueSummary.pendingActionsCount + 1
                    _uiState.value = current.copy(
                        queue = updatedQueue,
                        queueSummary = current.queueSummary.copy(pendingActionsCount = updatedCount),
                        executionResult = "⚡ Live Failure Triggered: ${body.customerName} (${body.amountFormatted})"
                    )
                } else {
                    val newTxn = TransactionItem(
                        id = "RP_99801",
                        customerName = "Rahul Sharma",
                        amount = 38500.0,
                        amountFormatted = "₹38,500",
                        category = "upi_degradation",
                        failureReason = "UPI Gateway Timeout (504)",
                        paymentMethod = "UPI",
                        gateway = "Razorpay Switch",
                        probabilityPct = 89,
                        potentialRecoveryFormatted = "₹38,500",
                        expectedRecovery = 34265.0,
                        expectedRecoveryFormatted = "₹34,265",
                        recommendedStrategy = "Delayed Retry (15m)",
                        priority = "HIGH",
                        priorityBadge = "HIGH",
                        policyStatus = "AUTO-ELIGIBLE",
                        policyReason = "Recovery Probability 89% >= Merchant Policy 70%"
                    )
                    val updatedQueue = listOf(newTxn) + current.queue
                    _uiState.value = current.copy(
                        queue = updatedQueue,
                        executionResult = "⚡ Live Failure Triggered: Rahul Sharma (₹38,500)"
                    )
                }
            } catch (e: Exception) {
                val newTxn = TransactionItem(
                    id = "RP_99801",
                    customerName = "Rahul Sharma",
                    amount = 38500.0,
                    amountFormatted = "₹38,500",
                    category = "upi_degradation",
                    failureReason = "UPI Gateway Timeout (504)",
                    paymentMethod = "UPI",
                    gateway = "Razorpay Switch",
                    probabilityPct = 89,
                    potentialRecoveryFormatted = "₹38,500",
                    expectedRecovery = 34265.0,
                    expectedRecoveryFormatted = "₹34,265",
                    recommendedStrategy = "Delayed Retry (15m)",
                    priority = "HIGH",
                    priorityBadge = "HIGH",
                    policyStatus = "AUTO-ELIGIBLE",
                    policyReason = "Recovery Probability 89% >= Merchant Policy 70%"
                )
                val updatedQueue = listOf(newTxn) + current.queue
                _uiState.value = current.copy(
                    queue = updatedQueue,
                    executionResult = "⚡ Live Failure Triggered: Rahul Sharma (₹38,500)"
                )
            }
        }
    }

    fun executeBatchRecovery() {
        val current = _uiState.value as? UiState.Success ?: return
        val selectedIds = current.selectedTransactionIds.toList()
        if (selectedIds.isEmpty()) return

        val updatedQueue = current.queue.map { item ->
            if (selectedIds.contains(item.id)) {
                item.copy(statusLabel = "Recovered", statusType = "ready")
            } else {
                item
            }
        }

        _uiState.value = current.copy(
            queue = updatedQueue,
            selectedTransactionIds = emptySet(),
            executionResult = "✅ Successfully Recovered ${selectedIds.size} Selected Transactions!"
        )

        viewModelScope.launch {
            try {
                apiService.batchExecuteRecovery(BatchRecoveryRequestDto(targetIds = selectedIds))
            } catch (_: Exception) {}
        }
    }

    // ─── Execute Recommended Recovery Action ─────────────────────────────────

    fun openStrategySimulator(transactionId: String = "TXN_83921") {
        val current = _uiState.value as? UiState.Success ?: return
        _uiState.value = current.copy(isSimulatorOpen = true)
        viewModelScope.launch {
            try {
                val res = apiService.simulateRecoveryStrategies(transactionId)
                if (res.isSuccessful && res.body() != null) {
                    val curr = _uiState.value as? UiState.Success ?: return@launch
                    _uiState.value = curr.copy(simulationData = res.body()!!)
                }
            } catch (_: Exception) {}
        }
    }

    fun closeSimulator() {
        val current = _uiState.value as? UiState.Success ?: return
        _uiState.value = current.copy(isSimulatorOpen = false)
    }

    fun fetchTransactionIntelligence(transactionId: String = "TXN_83921") {
        viewModelScope.launch {
            try {
                val res = apiService.getTransactionIntelligence(transactionId)
                if (res.isSuccessful && res.body() != null) {
                    val curr = _uiState.value as? UiState.Success ?: return@launch
                    _uiState.value = curr.copy(selectedIntelligenceTransaction = res.body()!!)
                }
            } catch (_: Exception) {}
        }
    }

    fun showConfirmRecovery(transactionId: String = "RP_83922", strategyOverride: String? = null) {
        val current = _uiState.value as? UiState.Success ?: return
        _uiState.value = current.copy(
            isConfirmDialogOpen = true,
            pendingConfirmationTxnId = transactionId,
            selectedStrategyOverride = strategyOverride,
            isDecisionModalOpen = false,
            isSimulatorOpen = false
        )
    }

    fun closeConfirmDialog() {
        val current = _uiState.value as? UiState.Success ?: return
        _uiState.value = current.copy(isConfirmDialogOpen = false)
    }

    fun closeOutcomeModal() {
        val current = _uiState.value as? UiState.Success ?: return
        _uiState.value = current.copy(executionOutcome = null)
    }

    fun executeRecovery(transactionId: String) {
        showConfirmRecovery(transactionId)
    }

    fun confirmAndExecuteRecovery() {
        val current = _uiState.value as? UiState.Success ?: return
        val txnId = current.pendingConfirmationTxnId ?: "RP_83922"
        val strategy = current.selectedStrategyOverride ?: "Delayed Retry (15 min)"

        _uiState.value = current.copy(isExecutingAction = true)

        viewModelScope.launch {
            delay(500)
            var outcomeDto: ExecutionOutcomeDto? = null

            try {
                val res = apiService.confirmAndExecuteRecoveryAction(txnId, strategy)
                if (res.isSuccessful && res.body() != null) {
                    outcomeDto = res.body()
                }
            } catch (_: Exception) {}

            if (outcomeDto == null) {
                outcomeDto = ExecutionOutcomeDto(
                    executionId = "REC_83922",
                    transactionId = txnId,
                    status = "RECOVERED",
                    amountFormatted = "₹24,500",
                    expectedRecoveryFormatted = "₹21,315",
                    actualRecoveryFormatted = "₹24,500",
                    strategyUsed = strategy,
                    attemptsCount = 1,
                    policyApproved = true,
                    environment = "Demo Payment Gateway Simulation"
                )
            }

            // Stateful Queue transition: POLICY_APPROVED -> EXECUTING -> RECOVERED
            val updatedQueue = current.queue.map { item ->
                if (item.id == txnId || txnId.contains(item.id)) {
                    item.copy(
                        statusLabel = "Recovered",
                        statusType = "ready",
                        recommendedStrategy = "Completed (Recovered)",
                        policyStatus = "POLICY APPROVED"
                    )
                } else item
            }

            // Stateful Overview metrics update
            val updatedPulse = current.overviewData.recoveryPulse.copy(
                recoveredAmountFormatted = "₹23.85L"
            )

            val updatedDecisions = listOf(
                PolicyDecisionLogItem(
                    id = "dec-${System.currentTimeMillis() % 10000}",
                    actionType = "approved",
                    amountFormatted = "₹24,500",
                    reason = "Policy Approved: Probability 87% >= 70% min threshold ($strategy Executed)"
                )
            ) + current.policyDecisions

            _uiState.value = current.copy(
                isExecutingAction = false,
                isConfirmDialogOpen = false,
                executionOutcome = outcomeDto,
                queue = updatedQueue,
                overviewData = current.overviewData.copy(recoveryPulse = updatedPulse),
                policyDecisions = updatedDecisions
            )
        }
    }

    // ─── Update Policy ───────────────────────────────────────────────────────

    // ─── Update Policy & Policy Preview ──────────────────────────────────────

    fun toggleAutomationStatus(enabled: Boolean) {
        val current = _uiState.value as? UiState.Success ?: return
        val newStatus = current.automationStatus.copy(
            automationEnabled = enabled,
            statusLabel = if (enabled) "ACTIVE" else "PAUSED",
            pauseExplanation = if (enabled) "Automatic recovery actions are currently active. AI Policy Engine evaluates every failed payment."
            else "Automatic recovery actions are currently paused. Failed payments will still be analyzed, monitored, and displayed in the Recovery Queue."
        )
        _uiState.value = current.copy(automationStatus = newStatus)
        viewModelScope.launch {
            try {
                val res = apiService.getAutomationStatus(enabled)
                if (res.isSuccessful && res.body() != null) {
                    _uiState.value = (_uiState.value as? UiState.Success)?.copy(automationStatus = res.body()!!) ?: return@launch
                }
            } catch (_: Exception) {}
        }
    }

    fun updateMinProbability(value: Float) {
        val current = _uiState.value as? UiState.Success ?: return
        val proposed = (current.pendingPolicyDraft ?: current.merchantPolicy).copy(minProbabilityPct = value)
        stagePolicyDraft(proposed)
    }

    fun updateMaxRetries(value: Float) {
        val current = _uiState.value as? UiState.Success ?: return
        val proposed = (current.pendingPolicyDraft ?: current.merchantPolicy).copy(maxAutoRetries = value)
        stagePolicyDraft(proposed)
    }

    fun updateEscalateToggle(value: Boolean) {
        val current = _uiState.value as? UiState.Success ?: return
        val proposed = (current.pendingPolicyDraft ?: current.merchantPolicy).copy(escalateLowConfidence = value)
        stagePolicyDraft(proposed)
    }

    private fun stagePolicyDraft(proposed: MerchantPolicyState) {
        val current = _uiState.value as? UiState.Success ?: return
        _uiState.value = current.copy(pendingPolicyDraft = proposed)
        fetchPolicyPreview(proposed)
    }

    private fun fetchPolicyPreview(proposed: MerchantPolicyState) {
        viewModelScope.launch {
            try {
                val req = PolicyPreviewRequest(
                    minRecoveryProbability = proposed.minProbabilityPct.toDouble(),
                    maxRetryCount = proposed.maxAutoRetries.toInt(),
                    maxAutomaticRecoveryAmount = 50000.0,
                    hardDeclinePolicy = "BLOCK",
                    customerCommunicationEnabled = true
                )
                val res = apiService.previewPolicyImpact(req)
                if (res.isSuccessful && res.body() != null) {
                    val current = _uiState.value as? UiState.Success ?: return@launch
                    _uiState.value = current.copy(policyPreviewResult = res.body()!!)
                } else {
                    fallbackPolicyPreview(proposed)
                }
            } catch (_: Exception) {
                fallbackPolicyPreview(proposed)
            }
        }
    }

    private fun fallbackPolicyPreview(proposed: MerchantPolicyState) {
        val current = _uiState.value as? UiState.Success ?: return
        val currProb = current.merchantPolicy.minProbabilityPct
        val propProb = proposed.minProbabilityPct
        val diff = (propProb - currProb).toInt()
        val newlyBlocked = if (diff > 0) (diff * 37) else (diff * 25)
        val newEligible = maxOf(100, 1284 - newlyBlocked)

        val preview = PolicyPreviewResponse(
            currentMinProbability = currProb.toDouble(),
            proposedMinProbability = propProb.toDouble(),
            currentEligibleCount = 1284,
            proposedEligibleCount = newEligible,
            currentExpectedRecoveryFormatted = "₹8.4L",
            proposedExpectedRecoveryFormatted = if (diff > 0) "₹7.2L" else "₹9.1L",
            newlyBlockedCount = if (newlyBlocked > 0) newlyBlocked else 0,
            estimatedChangeSummary = if (diff > 0) "-$newlyBlocked transactions require merchant review (+₹1.2L shifted to review queue)"
            else "+${kotlin.math.abs(newlyBlocked)} transactions eligible for auto-recovery"
        )
        _uiState.value = current.copy(policyPreviewResult = preview)
    }

    fun cancelPolicyPreview() {
        val current = _uiState.value as? UiState.Success ?: return
        _uiState.value = current.copy(pendingPolicyDraft = null, policyPreviewResult = null)
    }

    fun applyPolicyChange() {
        val current = _uiState.value as? UiState.Success ?: return
        val newPolicy = current.pendingPolicyDraft ?: return

        val newDecisions = listOf(
            PolicyDecisionLogItem(
                id = "pd-${System.currentTimeMillis() % 10000}",
                actionType = "approved",
                amountFormatted = "Policy Update",
                reason = "Min Prob updated to ${newPolicy.minProbabilityPct.toInt()}%"
            )
        ) + current.policyDecisions

        _uiState.value = current.copy(
            merchantPolicy = newPolicy,
            pendingPolicyDraft = null,
            policyPreviewResult = null,
            policyDecisions = newDecisions,
            executionResult = "✅ Merchant Policy Updated & Audited Successfully!"
        )

        patchPolicy(newPolicy)
    }

    private fun patchPolicy(policy: MerchantPolicyState) {
        viewModelScope.launch {
            try {
                val request = PolicyUpdateRequest(
                    minRecoveryProbability = policy.minProbabilityPct.toDouble() / 100.0,
                    maxRetryCount = policy.maxAutoRetries.toInt(),
                    maxAutomaticRecoveryAmount = 50000.0,
                    approvalThreshold = 25000.0,
                    escalationProbability = if (policy.escalateLowConfidence) 0.5 else 0.0,
                    blockHardDeclines = true
                )
                apiService.updateAutomationPolicies(request)
            } catch (_: Exception) { /* Silent fail */ }
        }
    }

    // ─── Loan Management & Smart Borrower Safety Score Handlers ─────────────

    fun openAddLoanModal() {
        val current = _uiState.value as? UiState.Success ?: return
        _uiState.value = current.copy(isAddLoanModalOpen = true)
    }

    fun closeAddLoanModal() {
        val current = _uiState.value as? UiState.Success ?: return
        _uiState.value = current.copy(isAddLoanModalOpen = false)
    }

    fun openBorrowerProfile(borrowerName: String = "Dinesh") {
        val current = _uiState.value as? UiState.Success ?: return

        val matchingLoan = current.loansList.firstOrNull { it.borrowerName.equals(borrowerName, ignoreCase = true) }
        val matchingQueueTxn = current.queue.firstOrNull { it.customerName.equals(borrowerName, ignoreCase = true) }

        val explicitScore = matchingQueueTxn?.probabilityPct
            ?: matchingLoan?.safetyScore?.score
            ?: when (borrowerName.lowercase()) {
                "arjun mehta" -> 87
                "priya sharma" -> 71
                "rohit kumar" -> 65
                "sneha patel" -> 78
                "vikram singh" -> 31
                "dinesh" -> 89
                else -> 85
            }

        val loanAmount = matchingQueueTxn?.amount ?: matchingLoan?.principalAmount ?: 20000.0
        val collateral = matchingLoan?.collateral ?: CollateralInfo(type = "Gold / Jewelry", estimatedValue = loanAmount * 1.25)

        val safetyScore = BorrowerScoringEngine.calculateSafetyScore(
            borrowerName = borrowerName,
            loanAmount = loanAmount,
            collateral = collateral,
            targetScore = explicitScore
        )

        val profile = BorrowerProfile(
            borrowerId = matchingLoan?.borrowerId ?: matchingQueueTxn?.id ?: "BW_001",
            name = matchingLoan?.borrowerName ?: matchingQueueTxn?.customerName ?: borrowerName,
            upiId = matchingLoan?.borrowerUpiId ?: "${borrowerName.lowercase().replace(" ", "")}@upi",
            safetyScore = safetyScore,
            totalBorrowed = matchingLoan?.principalAmount ?: matchingQueueTxn?.amount ?: 50000.0,
            totalBorrowedFormatted = "₹%,.0f".format(matchingLoan?.principalAmount ?: matchingQueueTxn?.amount ?: 50000.0),
            totalRepaid = matchingLoan?.amountRepaid ?: 45000.0,
            totalRepaidFormatted = "₹%,.0f".format(matchingLoan?.amountRepaid ?: 45000.0),
            outstandingAmount = matchingLoan?.amountRemaining ?: matchingQueueTxn?.amount ?: 5000.0,
            outstandingAmountFormatted = "₹%,.0f".format(matchingLoan?.amountRemaining ?: matchingQueueTxn?.amount ?: 5000.0),
            totalLoansCount = 5,
            onTimeRepaymentPct = explicitScore,
            avgDelayDays = if (explicitScore >= 80) 0.8 else if (explicitScore >= 60) 2.4 else 6.5,
            collateralEstimatedValueFormatted = collateral.estimatedValueFormatted,
            recentLoans = current.loansList
        )

        _uiState.value = current.copy(isBorrowerProfileModalOpen = true, selectedBorrowerProfile = profile)
    }

    fun closeBorrowerProfileModal() {
        val current = _uiState.value as? UiState.Success ?: return
        _uiState.value = current.copy(isBorrowerProfileModalOpen = false, selectedBorrowerProfile = null)
    }

    fun openLegalNoticeModal(transactionId: String? = null, loanItem: LoanItem? = null) {
        val current = _uiState.value as? UiState.Success ?: return
        val txn = if (transactionId != null) {
            current.queue.firstOrNull { it.id.equals(transactionId, ignoreCase = true) }
        } else null

        val targetTxn = txn ?: TransactionItem(
            id = loanItem?.id ?: "RP_83925",
            customerName = loanItem?.borrowerName ?: "Vikram Singh",
            amount = loanItem?.amountRemaining ?: 92000.0,
            amountFormatted = loanItem?.amountRemainingFormatted ?: "₹92,000",
            probabilityPct = loanItem?.safetyScore?.score ?: 31,
            repaymentDeadline = loanItem?.repaymentDeadline ?: "2026-08-30",
            deadlineLabel = loanItem?.deadlineLabel ?: "⚠ Overdue by 3 days (Aug 30)",
            deadlineDaysRemaining = -3
        )

        _uiState.value = current.copy(isLegalNoticeModalOpen = true, selectedLegalNoticeTxn = targetTxn)
    }

    fun closeLegalNoticeModal() {
        val current = _uiState.value as? UiState.Success ?: return
        _uiState.value = current.copy(isLegalNoticeModalOpen = false, selectedLegalNoticeTxn = null)
    }

    fun openWhatsAppSimulator(transactionId: String? = null, loanItem: LoanItem? = null) {
        val current = _uiState.value as? UiState.Success ?: return
        val txn = if (transactionId != null) {
            current.queue.firstOrNull { it.id.equals(transactionId, ignoreCase = true) }
        } else null

        val targetTxn = txn ?: TransactionItem(
            id = loanItem?.id ?: "RP_83925",
            customerName = loanItem?.borrowerName ?: "Vikram Singh",
            amount = loanItem?.amountRemaining ?: 92000.0,
            amountFormatted = loanItem?.amountRemainingFormatted ?: "₹92,000",
            probabilityPct = loanItem?.safetyScore?.score ?: 85,
            repaymentDeadline = loanItem?.repaymentDeadline ?: "2026-08-30",
            deadlineLabel = loanItem?.deadlineLabel ?: "⚠ Overdue by 3 days (Aug 30)",
            deadlineDaysRemaining = -3
        )

        _uiState.value = current.copy(isWhatsAppSimulatorOpen = true, selectedWhatsAppTxn = targetTxn)
    }

    fun closeWhatsAppSimulator() {
        val current = _uiState.value as? UiState.Success ?: return
        _uiState.value = current.copy(isWhatsAppSimulatorOpen = false, selectedWhatsAppTxn = null)
    }

    fun openAffordabilityModal() {
        val current = _uiState.value as? UiState.Success ?: return
        _uiState.value = current.copy(isAffordabilityModalOpen = true)
    }

    fun closeAffordabilityModal() {
        val current = _uiState.value as? UiState.Success ?: return
        _uiState.value = current.copy(isAffordabilityModalOpen = false)
    }

    fun openRazorpayCheckout(targetId: String, amount: Double, title: String) {
        val current = _uiState.value as? UiState.Success ?: return
        _uiState.value = current.copy(
            isRazorpayCheckoutOpen = true,
            checkoutTargetId = targetId,
            checkoutAmount = amount,
            checkoutTitle = title
        )
    }

    fun closeRazorpayCheckout() {
        val current = _uiState.value as? UiState.Success ?: return
        _uiState.value = current.copy(isRazorpayCheckoutOpen = false)
    }

    fun recordRazorpayPaymentSuccess(targetId: String, amount: Double, context: Context? = null) {
        val current = _uiState.value as? UiState.Success ?: return
        
        val updatedLoans = current.loansList.map { loan ->
            if (loan.id == targetId || loan.borrowerName.contains(targetId, ignoreCase = true)) {
                val newRemaining = (loan.amountRemaining - amount).coerceAtLeast(0.0)
                val newRepaid = loan.amountRepaid + amount
                val newStatus = if (newRemaining <= 0) "REPAID" else loan.status
                loan.copy(
                    amountRemaining = newRemaining,
                    amountRemainingFormatted = "₹%,.0f".format(newRemaining),
                    amountRepaid = newRepaid,
                    amountRepaidFormatted = "₹%,.0f".format(newRepaid),
                    status = newStatus
                )
            } else loan
        }

        val updatedQueue = current.queue.map { txn ->
            if (txn.id == targetId || txn.customerName.contains(targetId, ignoreCase = true)) {
                txn.copy(
                    statusLabel = "RECOVERED VIA RAZORPAY UPI",
                    statusType = "recovered",
                    policyStatus = "RECOVERED"
                )
            } else txn
        }

        _uiState.value = current.copy(
            loansList = updatedLoans,
            queue = updatedQueue,
            isRazorpayCheckoutOpen = false
        )

        context?.let { ctx ->
            LoanStorageManager.saveLoans(ctx, updatedLoans)
        }
    }

    fun openRepayLoanModal(loan: LoanItem) {
        val current = _uiState.value as? UiState.Success ?: return
        _uiState.value = current.copy(isRepayLoanModalOpen = true, selectedLoanForRepay = loan)
    }

    fun closeRepayLoanModal() {
        val current = _uiState.value as? UiState.Success ?: return
        _uiState.value = current.copy(isRepayLoanModalOpen = false, selectedLoanForRepay = null)
    }

    fun initPersistentStorage(context: Context) {
        val current = _uiState.value as? UiState.Success ?: return
        val storedLoans = LoanStorageManager.loadLoans(context)
        if (storedLoans != null && storedLoans.isNotEmpty()) {
            val extraQueueTxns = storedLoans.map { loan ->
                TransactionItem(
                    id = loan.id,
                    customerName = loan.borrowerName,
                    amountFormatted = loan.amountRemainingFormatted,
                    amount = loan.amountRemaining,
                    failureReason = if (loan.status == "REPAID") "Repayment Completed ✓" else "Loan Repayment Due (${loan.repaymentDeadline})",
                    paymentMethod = "UPI",
                    gateway = "UPI Switch",
                    probabilityPct = loan.safetyScore.score,
                    expectedRecovery = loan.amountRemaining * (loan.safetyScore.score / 100.0),
                    expectedRecoveryFormatted = "₹%,.0f".format(loan.amountRemaining * (loan.safetyScore.score / 100.0)),
                    potentialRecoveryFormatted = loan.principalAmountFormatted,
                    recommendedStrategy = "Request via UPI",
                    actionDelay = "Due ${loan.repaymentDeadline}",
                    recommendationNote = "Collateral: ${loan.collateral.type}",
                    statusLabel = if (loan.status == "REPAID") "✓ Repaid" else "Active Loan",
                    statusType = if (loan.status == "REPAID") "ready" else "awaiting_approval",
                    priorityBadge = if (loan.safetyScore.score >= 80) "LOW" else "HIGH",
                    policyStatus = "AUTO-ELIGIBLE",
                    policyReason = "Borrower Safety Score ${loan.safetyScore.score}/100.",
                    priority = "HIGH",
                    retryCount = 0,
                    category = "loans"
                )
            }
            val existingIds = extraQueueTxns.map { it.id }.toSet()
            val filteredQueue = current.queue.filterNot { existingIds.contains(it.id) }

            _uiState.value = current.copy(
                loansList = storedLoans,
                queue = extraQueueTxns + filteredQueue
            )
        }
    }

    fun toggleLoanCompleted(loanId: String, context: Context? = null) {
        val current = _uiState.value as? UiState.Success ?: return
        val updatedList = current.loansList.map { loan ->
            if (loan.id == loanId) {
                val isNowCompleted = loan.status != "REPAID"
                loan.copy(
                    status = if (isNowCompleted) "REPAID" else "ACTIVE",
                    amountRepaid = if (isNowCompleted) loan.principalAmount else 0.0,
                    amountRepaidFormatted = if (isNowCompleted) loan.principalAmountFormatted else "₹0",
                    amountRemaining = if (isNowCompleted) 0.0 else loan.principalAmount,
                    amountRemainingFormatted = if (isNowCompleted) "₹0" else loan.principalAmountFormatted,
                    deadlineLabel = if (isNowCompleted) "✓ Fully Repaid" else "${loan.deadlineDaysRemaining} days remaining"
                )
            } else loan
        }

        val updatedQueue = current.queue.map { item ->
            if (item.id == loanId) {
                val target = updatedList.firstOrNull { it.id == loanId }
                val isCompleted = target?.status == "REPAID"
                item.copy(
                    statusLabel = if (isCompleted) "✓ Completed" else "Active Loan",
                    failureReason = if (isCompleted) "Payment Completed ✓" else "Loan Repayment Due"
                )
            } else item
        }

        if (context != null) {
            LoanStorageManager.saveLoans(context, updatedList)
        }

        val changedLoan = updatedList.firstOrNull { it.id == loanId }
        _uiState.value = current.copy(
            loansList = updatedList,
            queue = updatedQueue,
            executionResult = if (changedLoan?.status == "REPAID") "✓ Marked ${changedLoan.borrowerName}'s payment (${changedLoan.id}) as Completed!" else "● Restored ${changedLoan?.borrowerName}'s loan (${changedLoan?.id}) to Active"
        )
    }

    fun openLoanAgreementModal(loan: LoanItem) {
        val current = _uiState.value as? UiState.Success ?: return
        _uiState.value = current.copy(isLoanAgreementModalOpen = true, selectedLoanForAgreement = loan)
    }

    fun openLoanAgreementModalForTxn(transactionId: String) {
        val current = _uiState.value as? UiState.Success ?: return
        val existingLoan = current.loansList.firstOrNull { it.id == transactionId }
        val loanItem = existingLoan ?: run {
            val txn = current.queue.firstOrNull { it.id == transactionId }
            LoanItem(
                id = transactionId,
                borrowerName = txn?.customerName ?: "Vikram Singh",
                borrowerUpiId = "${(txn?.customerName ?: "Vikram").lowercase().replace(" ", "")}@upi",
                lenderName = "PayPulse Merchant (Lender)",
                principalAmount = txn?.amount ?: 24500.0,
                principalAmountFormatted = txn?.amountFormatted ?: "₹24,500",
                amountRemaining = txn?.amount ?: 24500.0,
                amountRemainingFormatted = txn?.amountFormatted ?: "₹24,500",
                repaymentDeadline = txn?.repaymentDeadline ?: "2026-10-02",
                repaymentTerms = "Single lump sum payment on or before repayment due date via UPI. 0% interest personal loan."
            )
        }
        _uiState.value = current.copy(isLoanAgreementModalOpen = true, selectedLoanForAgreement = loanItem)
    }

    fun closeLoanAgreementModal() {
        val current = _uiState.value as? UiState.Success ?: return
        _uiState.value = current.copy(isLoanAgreementModalOpen = false, selectedLoanForAgreement = null)
    }

    fun toggleLoanAgreementAcknowledgement(loanId: String, context: Context? = null) {
        val current = _uiState.value as? UiState.Success ?: return
        val updatedLoans = current.loansList.map { loan ->
            if (loan.id == loanId) {
                loan.copy(
                    isDigitallyAcknowledged = !loan.isDigitallyAcknowledged,
                    acknowledgementTimestamp = if (!loan.isDigitallyAcknowledged) "2026-09-04 10:30 AM" else loan.acknowledgementTimestamp
                )
            } else loan
        }
        _uiState.value = current.copy(loansList = updatedLoans)
        context?.let { LoanStorageManager.saveLoans(it, updatedLoans) }
    }

    fun addNewLoan(
        borrowerName: String,
        borrowerUpiId: String,
        amount: Double,
        repaymentDeadline: String,
        collateral: CollateralInfo,
        notes: String,
        lenderName: String = "PayPulse Merchant (Lender)",
        repaymentTerms: String = "Single lump sum payment on or before repayment due date via UPI. 0% interest personal loan.",
        context: Context? = null
    ) {
        val current = _uiState.value as? UiState.Success ?: return
        val newId = "LN_${(System.currentTimeMillis() % 10000)}"
        val score = BorrowerScoringEngine.calculateSafetyScore(borrowerName, amount, collateral = collateral)

        val newLoan = LoanItem(
            id = newId,
            borrowerName = borrowerName,
            borrowerUpiId = borrowerUpiId,
            lenderName = lenderName,
            principalAmount = amount,
            principalAmountFormatted = "₹%,.0f".format(amount),
            amountRepaid = 0.0,
            amountRepaidFormatted = "₹0",
            amountRemaining = amount,
            amountRemainingFormatted = "₹%,.0f".format(amount),
            loanGivenDate = "2026-09-02",
            repaymentDeadline = repaymentDeadline,
            repaymentTerms = repaymentTerms,
            isDigitallyAcknowledged = true,
            acknowledgementHash = "PP-ACK-${(System.currentTimeMillis() % 100000)}",
            acknowledgementTimestamp = "2026-09-04 10:30 AM",
            deadlineDaysRemaining = 30,
            deadlineLabel = "30 days remaining",
            status = "ACTIVE",
            collateral = collateral,
            notes = notes,
            safetyScore = score
        )

        val newQueueTxn = TransactionItem(
            id = newId,
            customerName = borrowerName,
            amountFormatted = "₹%,.0f".format(amount),
            amount = amount,
            failureReason = "Loan Repayment Due ($repaymentDeadline)",
            paymentMethod = "UPI",
            gateway = "UPI Switch",
            probabilityPct = score.score,
            expectedRecovery = amount * (score.score / 100.0),
            expectedRecoveryFormatted = "₹%,.0f".format(amount * (score.score / 100.0)),
            potentialRecoveryFormatted = "₹%,.0f".format(amount),
            recommendedStrategy = "Request via UPI",
            actionDelay = "Due $repaymentDeadline",
            recommendationNote = "Collateral: ${collateral.type} (${collateral.estimatedValueFormatted})",
            statusLabel = "Active Loan",
            statusType = "ready",
            priorityBadge = if (score.score >= 80) "LOW" else "HIGH",
            policyStatus = "AUTO-ELIGIBLE",
            policyReason = "Borrower Safety Score ${score.score}/100. Pledged Collateral: ${collateral.type} (${collateral.estimatedValueFormatted})",
            priority = "HIGH",
            retryCount = 0,
            category = "loans"
        )

        val updatedLoans = listOf(newLoan) + current.loansList
        if (context != null) {
            LoanStorageManager.saveLoans(context, updatedLoans)
        }

        _uiState.value = current.copy(
            loansList = updatedLoans,
            queue = listOf(newQueueTxn) + current.queue,
            isAddLoanModalOpen = false,
            executionResult = "✅ Disbursed ₹%,.0f Loan to $borrowerName ($borrowerUpiId)".format(amount)
        )
    }

    fun recordLoanRepayment(repaymentAmount: Double, context: Context? = null) {
        val current = _uiState.value as? UiState.Success ?: return
        val targetLoan = current.selectedLoanForRepay ?: return

        val newAmountRepaid = targetLoan.amountRepaid + repaymentAmount
        val newRemaining = (targetLoan.principalAmount - newAmountRepaid).coerceAtLeast(0.0)
        val isFullyRepaid = newRemaining <= 0.0
        val newStatus = if (isFullyRepaid) "REPAID" else "PARTIALLY_REPAID"

        val updatedLoan = targetLoan.copy(
            amountRepaid = newAmountRepaid,
            amountRepaidFormatted = "₹%,.0f".format(newAmountRepaid),
            amountRemaining = newRemaining,
            amountRemainingFormatted = "₹%,.0f".format(newRemaining),
            status = newStatus,
            deadlineLabel = if (isFullyRepaid) "✓ Fully Repaid" else "${targetLoan.deadlineDaysRemaining} days remaining"
        )

        val updatedList = current.loansList.map { if (it.id == targetLoan.id) updatedLoan else it }
        if (context != null) {
            LoanStorageManager.saveLoans(context, updatedList)
        }

        _uiState.value = current.copy(
            loansList = updatedList,
            isRepayLoanModalOpen = false,
            selectedLoanForRepay = null,
            executionResult = if (isFullyRepaid) "✓ ${targetLoan.borrowerName} has fully repaid ₹%,.0f loan (${targetLoan.id})".format(targetLoan.principalAmount) else "✓ Recorded ₹%,.0f partial repayment from ${targetLoan.borrowerName} (Owed: ₹%,.0f)".format(repaymentAmount, newRemaining)
        )
    }

    // ─── UI Event Handlers ───────────────────────────────────────────────────

    fun openIncidentModal(category: String?) {
        val current = _uiState.value as? UiState.Success ?: return
        _uiState.value = current.copy(isIncidentModalOpen = true, modalCategory = category)
    }

    fun closeIncidentModal() {
        val current = _uiState.value as? UiState.Success ?: return
        _uiState.value = current.copy(isIncidentModalOpen = false)
    }

    fun openDecisionModal(transactionId: String? = null) {
        val current = _uiState.value as? UiState.Success ?: return
        if (transactionId != null) {
            openTransactionIntelligence(transactionId)
        } else {
            _uiState.value = current.copy(isDecisionModalOpen = true)
        }
    }

    fun closeDecisionModal() {
        val current = _uiState.value as? UiState.Success ?: return
        _uiState.value = current.copy(isDecisionModalOpen = false)
    }

    fun onCategorySelected(category: String) {
        val current = _uiState.value as? UiState.Success ?: return
        _uiState.value = current.copy(selectedCategoryFilter = category)
    }

    fun onSearchQueryChanged(query: String) {
        val current = _uiState.value as? UiState.Success ?: return
        _uiState.value = current.copy(searchQuery = query)
    }

    fun clearExecutionResult() {
        val current = _uiState.value as? UiState.Success ?: return
        _uiState.value = current.copy(executionResult = null)
    }

    // ─── Data Builders ────────────────────────────────────────────────────────

    private fun buildOverviewScreenData(resp: OverviewResponse): OverviewScreenData {
        val recoveryPulse = RecoveryPulseMetrics(
            revenueAtRiskFormatted = resp.revenueAtRiskFormatted.ifBlank { "₹38.6L" },
            expectedRecoveryFormatted = resp.expectedRecoveryFormatted.ifBlank { "₹28.7L" },
            recoveredAmountFormatted = resp.recoveredAmountFormatted.ifBlank { "₹21.4L" },
            recoveryRateFormatted = resp.recoveryRateFormatted.ifBlank { "74.6%" },
            recoveryRateValue = (resp.recoveryRate * 100).toFloat().coerceIn(0f, 100f),
            recoveryChange = resp.recoveryChange.ifBlank { "+18.4% vs Previous Period" },
            riskChangePct = 12.0f,
            expectedPctOfRisk = if (resp.revenueAtRisk > 0) ((resp.expectedRecovery / resp.revenueAtRisk) * 100).toInt().coerceIn(0, 100) else 74
        )

        val todayImpact = TodayRecoveryImpact(
            recoveredTodayFormatted = resp.recoveredTodayFormatted.ifBlank { "₹2.84L" },
            successfulRecoveriesToday = if (resp.successfulRecoveriesToday > 0) resp.successfulRecoveriesToday else 347,
            recoveryRateTodayFormatted = resp.recoveryRateTodayFormatted.ifBlank { "71%" },
            changeVsPreviousPeriod = "+18.4% vs Yesterday",
            retryCostAvoidedFormatted = resp.retryCostAvoidedFormatted.ifBlank { "₹42.8K saved" }
        )

        val categories = if (resp.recoveryByFailureType.isNotEmpty()) {
            resp.recoveryByFailureType.map { dto ->
                FailureCategoryOpportunity(
                    category = dto.category,
                    categoryLabel = dto.categoryLabel,
                    transactionCount = dto.transactionCount,
                    expectedRecoveryFormatted = dto.expectedRecoveryFormatted,
                    expectedRecoveryAmount = dto.expectedRecovery,
                    recommendedAction = dto.recommendedAction
                )
            }
        } else {
            listOf(
                FailureCategoryOpportunity("upi_timeout", "UPI Timeout", 27, "₹1.42L", 142000.0, "Delayed Retry"),
                FailureCategoryOpportunity("mandate_failed", "Mandate Failure", 18, "₹84K", 84000.0, "Retry Mandate"),
                FailureCategoryOpportunity("checkout_abandoned", "Checkout Abandonment", 14, "₹31K", 31000.0, "Smart Payment Link"),
                FailureCategoryOpportunity("bank_decline", "Bank Decline", 9, "₹18.5K", 18500.0, "Alt. Payment Method")
            )
        }

        val events = if (resp.recentRecoveryEvents.isNotEmpty()) {
            resp.recentRecoveryEvents.map { dto ->
                LiveRecoveryEvent(
                    transactionId = dto.transactionId,
                    amountFormatted = dto.amountFormatted,
                    amount = dto.amount,
                    failureReason = dto.failureReason,
                    aiAction = dto.aiAction,
                    status = dto.status,
                    timestamp = dto.timestamp
                )
            }
        } else {
            listOf(
                LiveRecoveryEvent("TXN_10482", "₹24,500", 24500.0, "UPI Timeout", "Delayed Retry", "Policy Approved", "2 min ago"),
                LiveRecoveryEvent("TXN_10479", "₹45,000", 45000.0, "Mandate Failed", "Retry Mandate", "Retry Scheduled", "8 min ago"),
                LiveRecoveryEvent("TXN_10471", "₹12,800", 12800.0, "Checkout Abandoned", "Payment Link", "Recovered", "14 min ago"),
                LiveRecoveryEvent("TXN_10465", "₹8,200", 8200.0, "Bank Decline", "Alt. Payment", "Analyzing", "22 min ago")
            )
        }

        val gateways = if (resp.gatewayHealth.isNotEmpty()) {
            resp.gatewayHealth.map { dto ->
                GatewayHealthTelemetry(
                    gateway = dto.gateway,
                    healthScore = dto.healthScore,
                    latencyMs = dto.latencyMs,
                    failureRateFormatted = "${dto.failureRatePct}%",
                    status = dto.status,
                    trend = dto.trend
                )
            }
        } else {
            listOf(
                GatewayHealthTelemetry("Razorpay Switch", 92, 410, "3.2%", "Healthy", "Stable"),
                GatewayHealthTelemetry("HDFC Bank Direct", 95, 280, "1.8%", "Healthy", "Improving"),
                GatewayHealthTelemetry("UPI Generic Switch", 62, 840, "18.5%", "Degraded", "Elevated Failure"),
                GatewayHealthTelemetry("ICICI NetBanking", 88, 620, "5.4%", "Healthy", "Elevated Latency")
            )
        }

        val alert = resp.aiAlerts.firstOrNull()?.let { dto ->
            OperationalAlert(
                type = dto.type,
                severity = dto.severity,
                title = dto.title,
                message = dto.message,
                recommendedAction = dto.recommendedAction,
                createdAt = dto.createdAt
            )
        } ?: OperationalAlert()

        val observations = if (resp.aiObservations.isNotEmpty()) {
            resp.aiObservations
        } else {
            listOf(
                "Delayed retry currently has the highest expected recovery (87%) for UPI timeout failures.",
                "Mandate failures are concentrated around morning salary-cycle settlement windows."
            )
        }

        val profitLoss = ProfitLossSummary(
            grossRevenueAtRiskFormatted = recoveryPulse.revenueAtRiskFormatted.ifBlank { "₹38.60L" },
            netRecoveredRevenueFormatted = recoveryPulse.recoveredAmountFormatted.ifBlank { "₹21.40L" },
            unrecoveredRiskFormatted = "₹17.20L",
            recoveryRatePct = recoveryPulse.recoveryRateValue,
            gatewayFeeSavingsFormatted = todayImpact.retryCostAvoidedFormatted,
            aiOperatingCostFormatted = "₹1.4K",
            netProfitFormatted = "+₹20.97L",
            netRecoveryRoiPct = 1497.0f
        )

        val cibilSummary = CibilScoreSummary()
        val borrowerCreditList = listOf(
            BorrowerCreditBureauSummary("Rahul Sharma", 785, "LOW RISK", 3, "₹50,000", 96, "#059669"),
            BorrowerCreditBureauSummary("Priya Nair", 742, "LOW RISK", 2, "₹35,000", 91, "#059669"),
            BorrowerCreditBureauSummary("Sneha Patel", 680, "MODERATE RISK", 4, "₹18,000", 82, "#D97706"),
            BorrowerCreditBureauSummary("Vikram Singh", 590, "HIGH RISK", 5, "₹92,000", 45, "#DC2626")
        )

        return OverviewScreenData(
            recoveryPulse = recoveryPulse,
            todayImpact = todayImpact,
            profitLoss = profitLoss,
            cibilSummary = cibilSummary,
            borrowerCreditList = borrowerCreditList,
            potentialRecoveryNowFormatted = resp.potentialRecoveryNowFormatted.ifBlank { "₹2.57L" },
            transactionsRecoverableNow = if (resp.transactionsRecoverableNow > 0) resp.transactionsRecoverableNow else 27,
            failureCategories = categories,
            recentEvents = events,
            gatewayHealthList = gateways,
            operationalAlert = alert,
            aiObservations = observations
        )
    }

    private fun buildQueueData(queue: RecoveryQueueResponse?): Pair<QueueSummary, List<TransactionItem>> {
        if (queue == null || queue.transactions.isEmpty()) {
            return Pair(QueueSummary(), buildFallbackQueueItems())
        }
        val transactions = queue.transactions
        val highPriority = transactions.filter { it.priority == "HIGH" }
        val autoEligible = transactions.filter { it.policyStatus == "AUTO-ELIGIBLE" }
        val totalPotential = queue.totalExpectedRecovery.takeIf { it > 0 }
            ?: transactions.sumOf { it.expectedRecovery }

        val summary = QueueSummary(
            pendingActionsCount = if (queue.total > 0) queue.total else transactions.size,
            pendingPotentialRecoveryFormatted = formatInr(totalPotential) + " potential",
            highPriorityCount = highPriority.size,
            highPriorityAtRiskFormatted = formatInr(highPriority.sumOf { it.amount }) + " at risk",
            autoEligibleCount = if (queue.autoEligibleCount > 0) queue.autoEligibleCount else autoEligible.size,
            autoEligiblePctFormatted = "${if ((queue.total.takeIf { it > 0 } ?: transactions.size) > 0) ((queue.autoEligibleCount.takeIf { it > 0 } ?: autoEligible.size) * 100 / (queue.total.takeIf { it > 0 } ?: transactions.size)) else 0}% of actions",
            totalExpectedRecovery = totalPotential,
            totalExpectedRecoveryFormatted = queue.totalExpectedRecoveryFormatted.ifBlank { formatInr(totalPotential) }
        )

        // Transactions arrive already sorted by expected_recovery DESC from server
        val items = transactions.map { txn ->
            val probPct = (txn.recoveryProbability * 100).toInt()
            val expectedRec = txn.expectedRecovery.takeIf { it > 0 }
                ?: (txn.amount * txn.recoveryProbability)
            val expectedFmt = txn.expectedRecoveryFormatted.ifBlank { formatInr(expectedRec) }

            val policyStatus = txn.policyStatus.ifBlank {
                if (txn.policyEligible) "AUTO-ELIGIBLE" else "NEEDS REVIEW"
            }

            // Derive statusType/statusLabel from policyStatus
            val (statusLabel, statusType) = when (policyStatus) {
                "AUTO-ELIGIBLE" -> Pair("Auto-Eligible", "ready")
                "BLOCKED" -> Pair("Blocked", "escalated")
                else -> Pair("Needs Review", "awaiting_approval")
            }

            TransactionItem(
                id = txn.transactionId,
                customerName = txn.customerName,
                amountFormatted = formatInr(txn.amount),
                amount = txn.amount,
                failureReason = formatFailureReason(txn.failureReason),
                paymentMethod = formatPaymentMethod(txn.paymentMethod),
                gateway = formatGateway(txn.gateway),
                probabilityPct = probPct,
                expectedRecovery = expectedRec,
                expectedRecoveryFormatted = expectedFmt,
                potentialRecoveryFormatted = txn.potentialRecoveryFormatted.ifBlank { formatInr(txn.potentialRecovery) },
                recommendedStrategy = formatStrategy(txn.recommendedStrategy),
                actionDelay = txn.actionDelay,
                statusLabel = statusLabel,
                statusType = statusType,
                priorityBadge = txn.priority.takeIf { it.isNotBlank() },
                policyStatus = policyStatus,
                policyReason = txn.policyReason,
                priority = txn.priority.ifBlank { "MEDIUM" },
                retryCount = txn.retryCount,
                category = when (txn.failureReason) {
                    "checkout_abandoned" -> "checkout_abandonment"
                    "mandate_failed" -> "subscriptions"
                    else -> "payment_failures"
                }
            )
        }
        return Pair(summary, items)
    }


    private fun buildMerchantPolicy(dto: AutomationPolicyDto?): MerchantPolicyState {
        if (dto == null) return MerchantPolicyState()
        return MerchantPolicyState(
            minProbabilityPct = (dto.minRecoveryProbability * 100).toFloat(),
            maxAutoRetries = dto.maxRetryCount.toFloat(),
            maxAutoRecoveryAmount = "%,.0f".format(dto.maxAutomaticRecoveryAmount),
            approvalRequiredAbove = "%,.0f".format(dto.approvalThreshold),
            escalateLowConfidence = dto.escalationProbability > 0.0
        )
    }

    private fun buildAiDecisionFromQueue(items: List<TransactionItem>): AIDecisionDetail {
        val topItem = items.firstOrNull { it.priorityBadge == "HIGH" } ?: items.firstOrNull()
        return if (topItem != null) {
            AIDecisionDetail(
                transactionId = topItem.id,
                amount = topItem.amountFormatted,
                failureReason = topItem.failureReason,
                diagnosisTitle = "Likely transient gateway failure.",
                explanation = "Gateway health and customer history support a retry after a cooling window.",
                confidence = topItem.probabilityPct / 100f,
                recommendation = topItem.recommendedStrategy,
                policyDecision = if (topItem.probabilityPct >= 70) "approved" else "requires_approval",
                isApproved = topItem.probabilityPct >= 70
            )
        } else AIDecisionDetail()
    }

    private fun buildWorkflows(): List<WorkflowItem> = listOf(
        WorkflowItem("wf-1", "UPI Payment Recovery", "UPI timeout / failure", "Delayed retry → payment link", "87% success rate", true, "upi"),
        WorkflowItem("wf-2", "Subscription Renewal", "Mandate failure", "Retry mandate → customer alert", "82% success rate", true, "subscription"),
        WorkflowItem("wf-3", "Checkout Recovery", "Cart abandonment", "Smart payment link via WhatsApp", "68% success rate", true, "checkout"),
        WorkflowItem("wf-4", "Alt. Payment Method", "Card / bank decline", "Offer alternative methods", "71% success rate", true, "alt")
    )

    private fun buildPolicyDecisions(): List<PolicyDecisionLogItem> = listOf(
        PolicyDecisionLogItem("pd-1", "blocked", "₹1,24,000", "Exceeded max auto-recovery limit"),
        PolicyDecisionLogItem("pd-2", "escalated", "₹45,200", "Low-confidence prediction (42%)"),
        PolicyDecisionLogItem("pd-3", "approved", "₹18,500", "Smart retry within policy gates")
    )

    private fun buildFallbackQueueItems(): List<TransactionItem> = listOf(
        TransactionItem("RP_83921", "Arjun Mehta", "₹24,500", 24500.0, "UPI Timeout", "UPI", "Razorpay Switch", 87, 21315.0, "₹21,315", "₹21,315", "Delayed Retry", "15 min", null, "Auto-Eligible", "ready", "HIGH", "AUTO-ELIGIBLE", "Probability 87% > threshold. Retry count 0 within limit.", "HIGH", 0, "payment_failures", "2026-09-05", 3, "Due in 3 days (Sep 5)"),
        TransactionItem("RP_83922", "Priya Sharma", "₹8,200", 8200.0, "Bank Decline", "UPI", "HDFC Bank", 71, 5822.0, "₹5,822", "₹5,822", "Alt. Payment Method", "", null, "Needs Review", "awaiting_approval", "MEDIUM", "NEEDS REVIEW", "Merchant review required before automatic recovery", "MEDIUM", 0, "payment_failures", "2026-09-12", 10, "Due in 10 days (Sep 12)"),
        TransactionItem("RP_83923", "Rohit Kumar", "₹45,000", 45000.0, "Mandate Failed", "Mandate", "Razorpay Switch", 65, 29250.0, "₹29,250", "₹29,250", "Retry Mandate", "", null, "Needs Review", "awaiting_approval", "MEDIUM", "NEEDS REVIEW", "Merchant review required before automatic recovery", "MEDIUM", 1, "subscriptions", "2026-09-18", 16, "Due in 16 days (Sep 18)"),
        TransactionItem("RP_83924", "Sneha Patel", "₹3,600", 3600.0, "Checkout Abandoned", "Card", "ICICI", 78, 2808.0, "₹2,808", "₹2,808", "Payment Link", "", null, "Auto-Eligible", "ready", "MEDIUM", "AUTO-ELIGIBLE", "Probability 78% > threshold. Retry count 0 within limit.", "MEDIUM", 0, "checkout_abandonment", "2026-09-25", 23, "Due in 23 days (Sep 25)"),
        TransactionItem("RP_83925", "Vikram Singh", "₹92,000", 92000.0, "Auth Failed", "Card", "Axis Bank", 31, 28520.0, "₹28,520", "₹28,520", "Escalate", "", "Requires human review", "Blocked", "escalated", "LOW", "BLOCKED", "Transaction exceeds auto-recovery amount limit", "LOW", 3, "payment_failures", "2026-08-30", -3, "⚠ Overdue by 3 days (Aug 30)")
    )


    private fun buildFallbackInsightsData(): InsightsResponseDto {
        val modelMetrics = ModelMetricsDto(
            rocAuc = 0.91f,
            f1 = 0.88f,
            precision = 0.91f,
            recall = 0.86f,
            accuracySecondary = 0.94f,
            selectedModel = "Random Forest Classifier (v3)",
            evaluationLabel = "Validation Performance (held-out data)",
            datasetRows = 8000,
            truePositivesCount = 3420,
            falsePositivesCount = 340,
            falseNegativesCount = 560,
            trueNegativesCount = 3680
        )

        val businessMetrics = BusinessMetricsDto(
            expectedRecovery = 2870000.0,
            expectedRecoveryFormatted = "₹28.7L",
            recoveredAmount = 2140000.0,
            recoveredAmountFormatted = "₹21.4L",
            recoveryRate = 0.746f,
            recoveryRateFormatted = "74.6%",
            recoveryLiftPct = 18.4f,
            recoveryLiftFormatted = "+18.4% vs Simple Retry Baseline",
            baselineStrategyLabel = "Simple Retry Baseline",
            retryCostAvoided = 42800.0,
            retryCostAvoidedFormatted = "₹42.8K"
        )

        val failureCategories = listOf(
            FailureCategoryIntelligenceDto("upi_timeout", "UPI Timeout", 27, 185000.0, "₹1.85L", 142000.0, "₹1.42L", 109000.0, "₹1.09L", 76.8f, "76.8%", "Delayed Retry (15m)"),
            FailureCategoryIntelligenceDto("mandate_failed", "Mandate Failure", 18, 112000.0, "₹1.12L", 84000.0, "₹84K", 63000.0, "₹63K", 75.0f, "75.0%", "Retry Mandate"),
            FailureCategoryIntelligenceDto("checkout_abandoned", "Checkout Abandonment", 14, 45000.0, "₹45K", 31000.0, "₹31K", 21300.0, "₹21.3K", 68.9f, "68.9%", "Smart Payment Link"),
            FailureCategoryIntelligenceDto("bank_decline", "Bank Decline", 9, 28000.0, "₹28K", 18500.0, "₹18.5K", 12200.0, "₹12.2K", 66.1f, "66.1%", "Alt. Payment Method")
        )

        val gateways = listOf(
            GatewayIntelligenceDto("Razorpay Switch", 95, 1.2f, "1.2%", 410, 81.4f, "81.4%", "Stable", "Healthy"),
            GatewayIntelligenceDto("SBI UPI Switch", 62, 18.5f, "18.5%", 4200, 58.0f, "58.0%", "Congestion Delay", "Degraded", alertMessage = "SBI UPI Switch latency spiked to 4,200ms. PayPulse AI automatically delays retries by 15m."),
            GatewayIntelligenceDto("HDFC Direct Gateway", 98, 0.8f, "0.8%", 280, 88.2f, "88.2%", "Improving", "Healthy"),
            GatewayIntelligenceDto("ICICI NetBanking", 88, 5.4f, "5.4%", 620, 71.0f, "71.0%", "Elevated Latency", "Healthy")
        )

        val strategies = listOf(
            StrategyEffectivenessDto("delayed_retry", "Delayed Retry (15m)", 184, 137, 74.5f, "74.5%", 342000.0, "₹3.42L", 298000.0, "₹2.98L"),
            StrategyEffectivenessDto("send_payment_link", "Smart Payment Link", 120, 85, 70.8f, "70.8%", 210000.0, "₹2.10L", 184000.0, "₹1.84L"),
            StrategyEffectivenessDto("retry_mandate", "Mandate Retry", 95, 72, 75.8f, "75.8%", 180000.0, "₹1.80L", 156000.0, "₹1.56L"),
            StrategyEffectivenessDto("alternative_payment", "Alt. Payment Method", 64, 42, 65.6f, "65.6%", 115000.0, "₹1.15L", 92000.0, "₹92K"),
            StrategyEffectivenessDto("immediate_retry", "Immediate Retry", 42, 18, 42.8f, "42.8%", 65000.0, "₹65K", 41000.0, "₹41K")
        )

        val trends = listOf(
            FailureTrendPointDto("00:00", 12, 4.2f, 45000.0, "₹45K"),
            FailureTrendPointDto("04:00", 8, 2.8f, 28000.0, "₹28K"),
            FailureTrendPointDto("08:00", 34, 18.5f, 142000.0, "₹1.42L"),
            FailureTrendPointDto("12:00", 28, 12.1f, 98000.0, "₹98K"),
            FailureTrendPointDto("16:00", 22, 9.4f, 75000.0, "₹75K"),
            FailureTrendPointDto("20:00", 19, 7.8f, 62000.0, "₹62K")
        )

        val aiInsights = listOf(
            AiInsightDto("ins_1", "gateway_degradation", "WARNING", "UPI Failure Spike Detected", "UPI timeout failures increased 18.5% across generic bank switches.", "Impact: ₹1.42L expected recovery affected", "Delay retries by 15m cooling window.", "upi_degradation", "Just now"),
            AiInsightDto("ins_2", "strategy_performance", "INFO", "Optimal Recovery Strategy Identified", "Delayed retry currently produces the highest recovery rate (74.5%) for transient UPI timeout failures.", "Lift: +18.4% recovery rate vs immediate retries", "Keep 15m cooling window enabled.", "upi_timeout", "10 min ago"),
            AiInsightDto("ins_3", "customer_signal", "INFO", "High Customer Historical Success Signal", "Transactions from customers with high past success (>85%) have a 91% predicted recovery probability.", "Volume: ₹3.2L high-confidence opportunity", "Auto-approve smart recovery links.", "payment_failures", "30 min ago")
        )

        return InsightsResponseDto(
            timeRange = "24H",
            modelMetrics = modelMetrics,
            businessMetrics = businessMetrics,
            failureBreakdown = failureCategories,
            failureTrends = trends,
            gatewayMetrics = gateways,
            strategyMetrics = strategies,
            aiInsights = aiInsights,
            modelHealth = ModelHealthDto(),
            featureDrift = listOf(
                FeatureDriftStatusDto("Gateway Health Score", "Normal"),
                FeatureDriftStatusDto("Recent Failure Rate", "Warning"),
                FeatureDriftStatusDto("Payment Method Distribution", "Normal"),
                FeatureDriftStatusDto("Customer Historical Success Rate", "Normal")
            )
        )
    }

    private fun loadFallbackData(): UiState.Success {
        return UiState.Success(
            overviewData = OverviewScreenData(),
            metrics = PulseMetrics(),
            queueSummary = QueueSummary(),
            queue = buildFallbackQueueItems(),
            workflows = buildWorkflows(),
            merchantPolicy = MerchantPolicyState(),
            policyDecisions = buildPolicyDecisions(),
            insightsData = buildFallbackInsightsData(),
            mlAccuracyPct = 94,
            selectedModel = "random_forest"
        )
    }

    // ─── Razorpay AutoPay & eMandate Handler ──────────────────────────────────

    fun openRazorpayAutoPay(loanId: String?, borrowerName: String, amount: Double) {
        val current = _uiState.value as? UiState.Success ?: return
        _uiState.value = current.copy(
            isRazorpayAutoPayOpen = true,
            autoPayTargetBorrower = borrowerName,
            autoPayAmount = amount,
            autoPayLoanId = loanId
        )
    }

    fun closeRazorpayAutoPay() {
        val current = _uiState.value as? UiState.Success ?: return
        _uiState.value = current.copy(isRazorpayAutoPayOpen = false)
    }

    fun recordAutoPaySuccess(upiApp: String, umrn: String, context: Context) {
        val current = _uiState.value as? UiState.Success ?: return
        val targetId = current.autoPayLoanId
        val updatedLoans = current.loansList.map { loan ->
            if (loan.id == targetId) {
                loan.copy(notes = "⚡ UPI AutoPay Active (${upiApp} • ${umrn})")
            } else loan
        }
        LoanStorageManager.saveLoans(context, updatedLoans)
        _uiState.value = current.copy(
            loansList = updatedLoans,
            isRazorpayAutoPayOpen = false
        )
    }

    // ─── Batch Selection ─────────────────────────────────────────────────────

    fun toggleTransactionSelection(transactionId: String) {
        val current = _uiState.value as? UiState.Success ?: return
        val newSelection = current.selectedTransactionIds.toMutableSet()
        if (newSelection.contains(transactionId)) {
            newSelection.remove(transactionId)
        } else {
            newSelection.add(transactionId)
        }
        _uiState.value = current.copy(
            selectedTransactionIds = newSelection,
            batchSummaryVisible = newSelection.isNotEmpty()
        )
    }

    fun clearBatchSelection() {
        val current = _uiState.value as? UiState.Success ?: return
        _uiState.value = current.copy(selectedTransactionIds = emptySet(), batchSummaryVisible = false)
    }

    // ─── Quick Execute (still goes through backend Policy Engine) ────────────

    fun openQuickExecute(item: TransactionItem) {
        val current = _uiState.value as? UiState.Success ?: return
        _uiState.value = current.copy(quickExecuteTransaction = item)
    }

    fun closeQuickExecute() {
        val current = _uiState.value as? UiState.Success ?: return
        _uiState.value = current.copy(quickExecuteTransaction = null)
    }

    fun confirmQuickExecute(transactionId: String) {
        closeQuickExecute()
        executeRecovery(transactionId)
    }

    // ─── Formatters ──────────────────────────────────────────────────────────

    private fun formatInr(amount: Double): String {
        return when {
            amount >= 100000 -> "₹%.1fL".format(amount / 100000)
            amount >= 1000 -> "₹%,.0f".format(amount)
            else -> "₹%.0f".format(amount)
        }
    }

    private fun formatFailureReason(raw: String): String = when (raw) {
        "upi_timeout" -> "UPI Timeout"
        "gateway_timeout" -> "Gateway Timeout"
        "insufficient_funds" -> "Insufficient Funds"
        "bank_decline" -> "Bank Decline"
        "authentication_failed" -> "Auth Failed"
        "checkout_abandoned" -> "Checkout Abandoned"
        "mandate_failed" -> "Mandate Failed"
        else -> raw.replace("_", " ").split(" ").joinToString(" ") { it.replaceFirstChar { c -> c.uppercase() } }
    }

    private fun formatPaymentMethod(raw: String): String = when (raw.lowercase()) {
        "upi" -> "UPI"
        "card", "credit_card", "debit_card" -> "Card"
        "netbanking", "net_banking" -> "NetBanking"
        "mandate", "nach" -> "Mandate"
        "wallet" -> "Wallet"
        else -> raw.replaceFirstChar { it.uppercase() }
    }

    private fun formatGateway(raw: String): String = when (raw.lowercase()) {
        "razorpay", "razorpay_switch" -> "Razorpay Switch"
        "hdfc", "hdfc_bank" -> "HDFC Bank"
        "icici", "icici_netbanking" -> "ICICI NetBanking"
        "upi_generic", "upi_switch" -> "UPI Switch"
        "axis", "axis_bank" -> "Axis Bank"
        else -> raw.split("_").joinToString(" ") { it.replaceFirstChar { c -> c.uppercase() } }
    }

    private fun formatStrategy(raw: String): String = when (raw) {
        "delayed_retry" -> "Delayed Retry"
        "send_payment_link" -> "Payment Link"
        "alternative_payment_method" -> "Alt. Payment Method"
        "retry_mandate" -> "Retry Mandate"
        "do_not_retry" -> "Do Not Retry"
        "escalate" -> "Escalate"
        else -> raw.replace("_", " ").split(" ").joinToString(" ") { it.replaceFirstChar { c -> c.uppercase() } }
    }

    private fun buildFallbackLoans(): List<LoanItem> {
        val dineshScore = BorrowerScoringEngine.calculateSafetyScore(
            borrowerName = "Dinesh",
            loanAmount = 20000.0,
            previousLoansCount = 5,
            repaidOnTimeCount = 4,
            collateral = CollateralInfo("Gold", 25000.0, "₹25,000", "Gold chain collateral"),
            currentOutstanding = 5000.0
        )

        val dineshLoan = LoanItem(
            id = "LN_101",
            borrowerId = "BW_001",
            borrowerName = "Dinesh",
            borrowerUpiId = "dinesh@upi",
            principalAmount = 20000.0,
            principalAmountFormatted = "₹20,000",
            amountRepaid = 0.0,
            amountRepaidFormatted = "₹0",
            amountRemaining = 20000.0,
            amountRemainingFormatted = "₹20,000",
            loanGivenDate = "2026-09-02",
            repaymentDeadline = "2026-10-02",
            deadlineDaysRemaining = 30,
            deadlineLabel = "30 days remaining",
            status = "ACTIVE",
            collateral = CollateralInfo("Gold", 25000.0, "₹25,000", "Gold chain collateral"),
            notes = "Disbursed for personal business working capital",
            safetyScore = dineshScore
        )

        val rameshScore = BorrowerScoringEngine.calculateSafetyScore(
            borrowerName = "Ramesh Kumar",
            loanAmount = 45000.0,
            previousLoansCount = 3,
            repaidOnTimeCount = 2,
            overdueCount = 1,
            avgDelayDays = 4.5,
            collateral = CollateralInfo("Laptop", 35000.0, "₹35,000", "MacBook Pro"),
            currentOutstanding = 15000.0,
            isImprovingTrend = false
        )

        val rameshLoan = LoanItem(
            id = "LN_102",
            borrowerId = "BW_002",
            borrowerName = "Ramesh Kumar",
            borrowerUpiId = "ramesh@upi",
            principalAmount = 45000.0,
            principalAmountFormatted = "₹45,000",
            amountRepaid = 15000.0,
            amountRepaidFormatted = "₹15,000",
            amountRemaining = 30000.0,
            amountRemainingFormatted = "₹30,000",
            loanGivenDate = "2026-08-15",
            repaymentDeadline = "2026-09-05",
            deadlineDaysRemaining = 3,
            deadlineLabel = "3 days remaining",
            status = "PARTIALLY_REPAID",
            collateral = CollateralInfo("Laptop", 35000.0, "₹35,000", "MacBook Pro"),
            notes = "Partial payment of ₹15,000 received on Aug 28",
            safetyScore = rameshScore
        )

        return listOf(dineshLoan, rameshLoan)
    }
}
