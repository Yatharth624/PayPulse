package com.paypulse.ai

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.animation.core.*
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.FormatListBulleted
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.paypulse.ai.ui.components.AIDecisionDialog
import com.paypulse.ai.ui.components.AddLoanDialog
import com.paypulse.ai.ui.components.BorrowerProfileDialog
import com.paypulse.ai.ui.components.ConfirmRecoveryDialog
import com.paypulse.ai.ui.components.DigitalLoanAgreementDialog
import com.paypulse.ai.ui.components.IncidentDialog
import com.paypulse.ai.ui.components.RecoveryResultDialog
import com.paypulse.ai.ui.components.RecoverySimulatorDialog
import com.paypulse.ai.ui.components.RepayLoanDialog
import com.paypulse.ai.ui.components.WorkflowConfigDialog
import com.paypulse.ai.ui.screens.InsightsScreen
import com.paypulse.ai.ui.screens.OverviewScreen
import com.paypulse.ai.ui.screens.QueueScreen
import com.paypulse.ai.ui.screens.SettingsScreen
import com.paypulse.ai.ui.theme.AmberGold
import com.paypulse.ai.ui.theme.EmeraldGreen
import com.paypulse.ai.ui.theme.NavyDark
import com.paypulse.ai.ui.theme.PayPulseAITheme
import com.paypulse.ai.viewmodel.DashboardViewModel
import com.paypulse.ai.viewmodel.UiState
import kotlinx.coroutines.launch

import android.Manifest
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import com.paypulse.ai.ui.utils.NotificationHelper

class MainActivity : ComponentActivity() {

    private val viewModel: DashboardViewModel by viewModels()

    @OptIn(ExperimentalFoundationApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            PayPulseAITheme {
                val uiState by viewModel.uiState.collectAsState()
                val context = LocalContext.current

                LaunchedEffect(Unit) {
                    viewModel.initPersistentStorage(context)
                    NotificationHelper.createNotificationChannel(context)
                }

                // Request Notification Permission on Android 13+
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    val permissionLauncher = rememberLauncherForActivityResult(
                        contract = ActivityResultContracts.RequestPermission(),
                        onResult = { _ -> }
                    )
                    LaunchedEffect(Unit) {
                        permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                    }
                }

                val pagerState = rememberPagerState(initialPage = 0, pageCount = { 5 })
                val coroutineScope = rememberCoroutineScope()
                val currentPage by remember { derivedStateOf { pagerState.currentPage } }

                var showSplash by remember { mutableStateOf(true) }

                if (showSplash) {
                    com.paypulse.ai.ui.screens.SplashScreen(
                        onSplashFinished = {
                            showSplash = false
                            // Trigger system notification demo on splash finish
                            NotificationHelper.sendSystemNotification(
                                context = context,
                                title = "🔔 PayPulse Smart Alerts Enabled",
                                message = "Real-time Due Soon reminders active (7-Day, 3-Day, Due Today & Overdue)."
                            )
                        }
                    )
                } else {
                    Scaffold(
                        topBar = {
                            Surface(
                                color = Color.White,
                                shadowElevation = 2.dp,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 16.dp, vertical = 12.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    // Left: Merchant / App Badge Icon
                                    Row(
                                        modifier = Modifier.align(Alignment.CenterStart),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Surface(
                                            color = Color(0xFFF1F5F9),
                                            shape = CircleShape,
                                            modifier = Modifier.size(36.dp)
                                        ) {
                                            Box(contentAlignment = Alignment.Center) {
                                                Icon(
                                                    imageVector = Icons.Default.Storefront,
                                                    contentDescription = "Store",
                                                    tint = NavyDark,
                                                    modifier = Modifier.size(20.dp)
                                                )
                                            }
                                        }
                                    }

                                    // Center: Razorpay-style Centered App Title "Paypulse"
                                    Text(
                                        text = "Paypulse",
                                        fontWeight = FontWeight.ExtraBold,
                                        fontSize = 18.sp,
                                        color = NavyDark,
                                        modifier = Modifier.align(Alignment.Center)
                                    )

                                    // Right: Notifications Bell Icon
                                    var isNotificationsOpen by remember { mutableStateOf(false) }

                                    val activeAlerts = remember(uiState) {
                                        if (uiState is UiState.Success) {
                                            val s = uiState as UiState.Success
                                            com.paypulse.ai.ui.utils.SmartAlertEngine.generateAllAlerts(s.loansList, s.queue)
                                        } else emptyList()
                                    }

                                    Row(
                                        modifier = Modifier.align(Alignment.CenterEnd),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        IconButton(onClick = { isNotificationsOpen = true }) {
                                            BadgedBox(
                                                badge = {
                                                    if (activeAlerts.isNotEmpty()) {
                                                        Badge(containerColor = Color(0xFFDC2626), contentColor = Color.White) {
                                                            Text("${activeAlerts.size}", fontSize = 9.sp, fontWeight = FontWeight.Bold)
                                                        }
                                                    }
                                                }
                                            ) {
                                                Icon(
                                                    imageVector = Icons.Default.Notifications,
                                                    contentDescription = "Notifications",
                                                    tint = NavyDark,
                                                    modifier = Modifier.size(22.dp)
                                                )
                                            }
                                        }
                                    }

                                    if (isNotificationsOpen) {
                                        val successState = uiState as? UiState.Success
                                        com.paypulse.ai.ui.components.NotificationsDialog(
                                            loans = successState?.loansList ?: emptyList(),
                                            transactions = successState?.queue ?: emptyList(),
                                            onDismiss = { isNotificationsOpen = false },
                                            onOpenRepayLoan = { loan -> viewModel.openRepayLoanModal(loan) },
                                            onOpenWhatsAppSimulator = { id -> viewModel.openWhatsAppSimulator(id) },
                                            onOpenRazorpayCheckout = { id, amount, title -> viewModel.openRazorpayCheckout(id, amount, title) }
                                        )
                                    }
                                }
                            }
                        },
                    bottomBar = {
                        NavigationBar(
                            containerColor = Color.White,
                            tonalElevation = 8.dp
                        ) {
                            NavigationBarItem(
                                selected = currentPage == 0,
                                onClick = { coroutineScope.launch { pagerState.scrollToPage(0) } },
                                icon = { Icon(Icons.Default.GridView, contentDescription = null) },
                                label = { Text("Overview", fontSize = 10.sp, fontWeight = FontWeight.Bold) }
                            )
                            NavigationBarItem(
                                selected = currentPage == 1,
                                onClick = { coroutineScope.launch { pagerState.scrollToPage(1) } },
                                icon = { Icon(Icons.AutoMirrored.Filled.FormatListBulleted, contentDescription = null) },
                                label = { Text("Queue", fontSize = 10.sp, fontWeight = FontWeight.Bold) }
                            )
                            NavigationBarItem(
                                selected = currentPage == 2,
                                onClick = { coroutineScope.launch { pagerState.scrollToPage(2) } },
                                icon = { Icon(Icons.Default.AutoAwesome, contentDescription = null) },
                                label = { Text("Insights", fontSize = 10.sp, fontWeight = FontWeight.Bold) }
                            )
                            NavigationBarItem(
                                selected = currentPage == 3,
                                onClick = { coroutineScope.launch { pagerState.scrollToPage(3) } },
                                icon = { Icon(Icons.Default.Tune, contentDescription = null) },
                                label = { Text("Recovery", fontSize = 10.sp, fontWeight = FontWeight.Bold) }
                            )
                            NavigationBarItem(
                                selected = currentPage == 4,
                                onClick = { coroutineScope.launch { pagerState.scrollToPage(4) } },
                                icon = { Icon(Icons.Default.CalendarMonth, contentDescription = null) },
                                label = { Text("Calendar", fontSize = 10.sp, fontWeight = FontWeight.Bold) }
                            )
                        }
                    }
                ) { innerPadding ->
                    Box(modifier = Modifier.padding(innerPadding)) {
                        when (val state = uiState) {
                            is UiState.Loading -> {
                                Box(
                                    modifier = Modifier.fillMaxSize(),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                        CircularProgressIndicator(color = AmberGold)
                                        Spacer(modifier = Modifier.height(12.dp))
                                        Text("Initializing AI Engine...", fontSize = 12.sp, color = NavyDark, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                            is UiState.Success -> {
                                HorizontalPager(
                                    state = pagerState,
                                    modifier = Modifier.fillMaxSize(),
                                    beyondBoundsPageCount = 3
                                ) { page ->
                                    when (page) {
                                        0 -> OverviewScreen(
                                            overview = state.overviewData,
                                            onNavigateToQueue = {
                                                coroutineScope.launch { pagerState.animateScrollToPage(1) }
                                            },
                                            onSelectTransaction = { txnId ->
                                                viewModel.openTransactionIntelligence(txnId)
                                            },
                                            onOpenIncidentModal = { category ->
                                                viewModel.openIncidentModal(category)
                                            },
                                            onOpenAffordabilityCheck = {
                                                viewModel.openAffordabilityModal()
                                            },
                                            onOpenBorrowerProfile = { borrowerName ->
                                                viewModel.openBorrowerProfile(borrowerName)
                                            }
                                        )
                                        1 -> QueueScreen(
                                            summary = state.queueSummary,
                                            transactions = state.queue,
                                            loans = state.loansList,
                                            selectedCategory = state.selectedCategoryFilter,
                                            searchQuery = state.searchQuery,
                                            selectedTransactionIds = state.selectedTransactionIds,
                                            onCategorySelected = { viewModel.onCategorySelected(it) },
                                            onSearchQueryChanged = { viewModel.onSearchQueryChanged(it) },
                                            onReviewTransaction = { txnId ->
                                                viewModel.openTransactionIntelligence(txnId)
                                            },
                                            onToggleTransactionSelection = { txnId ->
                                                viewModel.toggleTransactionSelection(txnId)
                                            },
                                            onClearBatchSelection = {
                                                viewModel.clearBatchSelection()
                                            },
                                            onQuickExecute = { txnId ->
                                                viewModel.executeRecovery(txnId)
                                            },
                                            onExecuteBatch = {
                                                viewModel.executeBatchRecovery()
                                            },
                                            onOpenAddLoan = { viewModel.openAddLoanModal() },
                                            onOpenBorrowerProfile = { borrowerName -> viewModel.openBorrowerProfile(borrowerName) },
                                            onOpenRepayLoan = { loan -> viewModel.openRepayLoanModal(loan) },
                                            onToggleLoanCompleted = { loanId -> viewModel.toggleLoanCompleted(loanId, context) },
                                            onOpenLegalNotice = { txnId, loan -> viewModel.openLegalNoticeModal(txnId, loan) },
                                            onOpenWhatsAppSimulator = { txnId -> viewModel.openWhatsAppSimulator(txnId) },
                                            onOpenLoanAgreement = { loan -> viewModel.openLoanAgreementModal(loan) }
                                        )
                                        2 -> InsightsScreen(
                                            insightsData = state.insightsData,
                                            loans = state.loansList,
                                            selectedTimeRange = state.insightsTimeRange,
                                            onTimeRangeSelected = { range -> viewModel.onInsightsTimeRangeSelected(range) },
                                            onNavigateToQueueFiltered = { category ->
                                                viewModel.onCategorySelected(category)
                                                coroutineScope.launch { pagerState.animateScrollToPage(1) }
                                            },
                                            onOpenDecisionModal = { viewModel.openDecisionModal() },
                                            onOpenWhatsAppSimulator = { viewModel.openWhatsAppSimulator() },
                                            onOpenAddLoan = { viewModel.openAddLoanModal() },
                                            onOpenBorrowerProfile = { borrowerName -> viewModel.openBorrowerProfile(borrowerName) },
                                            onOpenRepayLoan = { loan -> viewModel.openRepayLoanModal(loan) },
                                            onRefresh = { viewModel.loadDashboardData() },
                                            onOpenRazorpayCheckout = { id, amt, name -> viewModel.openRazorpayCheckout(id, amt, name) },
                                            onOpenAutoPay = { loan -> viewModel.openRazorpayAutoPay(loan.id, loan.borrowerName, loan.amountRemaining) }
                                        )
                                        3 -> SettingsScreen(
                                            workflows = state.workflows,
                                            detailedWorkflows = state.detailedWorkflows,
                                            policy = state.merchantPolicy,
                                            pendingPolicyDraft = state.pendingPolicyDraft,
                                            policyPreviewResult = state.policyPreviewResult,
                                            automationStatus = state.automationStatus,
                                            decisions = state.policyDecisions,
                                            loans = state.loansList,
                                            onMinProbabilityChange = { viewModel.updateMinProbability(it) },
                                            onMaxRetriesChange = { viewModel.updateMaxRetries(it) },
                                            onEscalateToggleChange = { viewModel.updateEscalateToggle(it) },
                                            onAutomationToggle = { enabled -> viewModel.toggleAutomationStatus(enabled) },
                                            onApplyPolicy = { viewModel.applyPolicyChange() },
                                            onCancelPolicyPreview = { viewModel.cancelPolicyPreview() },
                                            onConfigureWorkflow = { wf -> viewModel.openWorkflowConfig(wf) },
                                            onOpenLoanAgreement = { loan -> viewModel.openLoanAgreementModal(loan) }
                                        )
                                        4 -> com.paypulse.ai.ui.screens.CalendarScreen(
                                            loans = state.loansList,
                                            transactions = state.queue,
                                            onOpenAddLoan = { viewModel.openAddLoanModal() },
                                            onOpenRepayLoan = { loan -> viewModel.openRepayLoanModal(loan) },
                                            onOpenBorrowerProfile = { borrowerName -> viewModel.openBorrowerProfile(borrowerName) },
                                            onReviewTransaction = { txnId -> viewModel.openTransactionIntelligence(txnId) },
                                            onOpenRazorpayCheckout = { id, amt, name -> viewModel.openRazorpayCheckout(id, amt, name) }
                                        )
                                    }
                                }

                                // Workflow Configuration Dialog
                                state.selectedWorkflowForConfig?.let { wf ->
                                    WorkflowConfigDialog(
                                        workflow = wf,
                                        onDismiss = { viewModel.closeWorkflowConfig() },
                                        onSave = { updatedWf -> viewModel.saveWorkflowConfig(updatedWf) }
                                    )
                                }

                                // Incident Modal (wired to executeRecovery)
                                if (state.isIncidentModalOpen) {
                                    IncidentDialog(
                                        category = state.modalCategory,
                                        transactions = state.queue,
                                        isExecuting = state.isExecutingAction,
                                        executionResult = state.executionResult,
                                        onDismiss = {
                                            viewModel.closeIncidentModal()
                                            viewModel.clearExecutionResult()
                                        },
                                        onExecute = { txnId -> viewModel.executeRecovery(txnId) }
                                    )
                                }

                                // AI Decision Modal / Transaction Intelligence Sheet
                                if (state.isDecisionModalOpen) {
                                    AIDecisionDialog(
                                        detail = state.aiDecisionDetail,
                                        isExecuting = state.isExecutingAction,
                                        executionResult = state.executionResult,
                                        onDismiss = {
                                            viewModel.closeDecisionModal()
                                            viewModel.clearExecutionResult()
                                        },
                                        onExecute = { txnId -> viewModel.executeRecovery(txnId) },
                                        onOpenSimulator = { viewModel.openStrategySimulator() }
                                    )
                                }

                                // Recovery Strategy Simulator Modal
                                if (state.isSimulatorOpen) {
                                    RecoverySimulatorDialog(
                                        simulation = state.simulationData,
                                        onDismiss = { viewModel.closeSimulator() },
                                        onUseStrategy = { txnId, strategyName -> viewModel.showConfirmRecovery(txnId, strategyName) }
                                    )
                                }

                                // Step 1 Confirmation Modal: Confirm Recommended Recovery
                                if (state.isConfirmDialogOpen) {
                                    val confirmItem = state.queue.firstOrNull { it.id == (state.pendingConfirmationTxnId ?: "") }
                                    ConfirmRecoveryDialog(
                                        transactionId = confirmItem?.id ?: (state.pendingConfirmationTxnId ?: "RP_83922"),
                                        amountFormatted = confirmItem?.amountFormatted ?: "₹24,500",
                                        failureReason = confirmItem?.failureReason ?: "UPI Timeout",
                                        probabilityPct = (confirmItem?.probabilityPct ?: 87).toDouble(),
                                        expectedRecoveryFormatted = confirmItem?.expectedRecoveryFormatted ?: "₹21,315",
                                        strategyName = state.selectedStrategyOverride ?: (confirmItem?.recommendedStrategy ?: "Delayed Retry"),
                                        delayFormatted = confirmItem?.actionDelay?.ifBlank { "15 minutes" } ?: "15 minutes",
                                        policyStatus = confirmItem?.policyStatus ?: "APPROVED",
                                        retryCountText = "${confirmItem?.retryCount ?: 0} / 2",
                                        isExecuting = state.isExecutingAction,
                                        onDismiss = { viewModel.closeConfirmDialog() },
                                        onConfirm = { viewModel.confirmAndExecuteRecovery() }
                                    )
                                }

                                // Step 6 Recovery Result Outcome Modal
                                state.executionOutcome?.let { outcome ->
                                    RecoveryResultDialog(
                                        outcome = outcome,
                                        onDismiss = { viewModel.closeOutcomeModal() }
                                    )
                                }

                                // Loan Management Dialogs
                                if (state.isAddLoanModalOpen) {
                                    AddLoanDialog(
                                        onDismiss = { viewModel.closeAddLoanModal() },
                                        onAddLoan = { name, upi, amount, deadline, collateral, notes, lender, terms ->
                                            viewModel.addNewLoan(name, upi, amount, deadline, collateral, notes, lender, terms, context)
                                        }
                                    )
                                }

                                if (state.isLoanAgreementModalOpen) {
                                    state.selectedLoanForAgreement?.let { loan ->
                                        DigitalLoanAgreementDialog(
                                            loan = loan,
                                            onDismiss = { viewModel.closeLoanAgreementModal() },
                                            onToggleAcknowledgement = { loanId ->
                                                viewModel.toggleLoanAgreementAcknowledgement(loanId, context)
                                            }
                                        )
                                    }
                                }

                                if (state.isBorrowerProfileModalOpen) {
                                    state.selectedBorrowerProfile?.let { profile ->
                                        BorrowerProfileDialog(
                                            profile = profile,
                                            onDismiss = { viewModel.closeBorrowerProfileModal() }
                                        )
                                    }
                                }

                                if (state.isRepayLoanModalOpen) {
                                    state.selectedLoanForRepay?.let { loan ->
                                        RepayLoanDialog(
                                            loan = loan,
                                            onDismiss = { viewModel.closeRepayLoanModal() },
                                            onRecordRepayment = { amt -> viewModel.recordLoanRepayment(amt, context) }
                                        )
                                    }
                                }

                                if (state.isLegalNoticeModalOpen) {
                                    state.selectedLegalNoticeTxn?.let { txn ->
                                        com.paypulse.ai.ui.components.LegalNoticeDialog(
                                            transaction = txn,
                                            onDismiss = { viewModel.closeLegalNoticeModal() }
                                        )
                                    }
                                }

                                if (state.isWhatsAppSimulatorOpen) {
                                    state.selectedWhatsAppTxn?.let { txn ->
                                        com.paypulse.ai.ui.components.WhatsAppSimulatorDialog(
                                            transaction = txn,
                                            onDismiss = { viewModel.closeWhatsAppSimulator() }
                                        )
                                    }
                                }

                                 if (state.isAffordabilityModalOpen) {
                                     com.paypulse.ai.ui.components.AffordabilityDialog(
                                         existingLoans = state.loansList,
                                         onDismiss = { viewModel.closeAffordabilityModal() }
                                     )
                                 }

                                 if (state.isRazorpayCheckoutOpen) {
                                     com.paypulse.ai.ui.components.RazorpayCheckoutDialog(
                                         targetId = state.checkoutTargetId,
                                         amount = state.checkoutAmount,
                                         title = state.checkoutTitle,
                                         onDismiss = { viewModel.closeRazorpayCheckout() },
                                         onPaymentSuccess = { id, amt ->
                                             viewModel.recordRazorpayPaymentSuccess(id, amt, context)
                                         }
                                     )
                                 }

                                 if (state.isRazorpayAutoPayOpen) {
                                     com.paypulse.ai.ui.components.RazorpayAutoPayDialog(
                                         borrowerName = state.autoPayTargetBorrower,
                                         monthlyAmount = state.autoPayAmount,
                                         onDismiss = { viewModel.closeRazorpayAutoPay() },
                                         onAutoPaySuccess = { upiApp, umrn ->
                                             viewModel.recordAutoPaySuccess(upiApp, umrn, context)
                                         }
                                     )
                                 }
                            }
                            is UiState.Error -> {
                                Box(
                                    modifier = Modifier.fillMaxSize(),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.padding(24.dp)) {
                                        Icon(imageVector = Icons.Default.WifiOff, contentDescription = null, tint = NavyDark, modifier = Modifier.size(40.dp))
                                        Spacer(modifier = Modifier.height(12.dp))
                                        Text("PayPulse AI Offline Mode", fontSize = 14.sp, color = NavyDark, fontWeight = FontWeight.Bold)
                                        Text("Operating with local autonomous AI policy engine", fontSize = 12.sp, color = Color.Gray)
                                        Spacer(modifier = Modifier.height(16.dp))
                                        Button(onClick = { viewModel.loadDashboardData() }) {
                                            Text("Refresh Telemetry", fontWeight = FontWeight.Bold)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
}
