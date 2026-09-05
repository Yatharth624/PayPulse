package com.paypulse.ai.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.math.pow
import com.paypulse.ai.data.model.*
import com.paypulse.ai.ui.theme.*

@Composable
fun SettingsScreen(
    workflows: List<WorkflowItem> = emptyList(),
    detailedWorkflows: List<WorkflowDetailDto> = emptyList(),
    policy: MerchantPolicyState = MerchantPolicyState(),
    pendingPolicyDraft: MerchantPolicyState? = null,
    policyPreviewResult: PolicyPreviewResponse? = null,
    automationStatus: AutomationStatusDto = AutomationStatusDto(),
    decisions: List<PolicyDecisionLogItem> = emptyList(),
    loans: List<LoanItem> = emptyList(),
    onMinProbabilityChange: (Float) -> Unit = {},
    onMaxRetriesChange: (Float) -> Unit = {},
    onEscalateToggleChange: (Boolean) -> Unit = {},
    onAutomationToggle: (Boolean) -> Unit = {},
    onApplyPolicy: () -> Unit = {},
    onCancelPolicyPreview: () -> Unit = {},
    onConfigureWorkflow: (WorkflowItem) -> Unit = {},
    onOpenLoanAgreement: (LoanItem) -> Unit = {}
) {
    val scrollState = rememberScrollState()
    val activePolicy = pendingPolicyDraft ?: policy
    var expandedWorkflowId by remember { mutableStateOf<String?>(null) }
    var smsEnabled by remember { mutableStateOf(true) }
    var whatsappEnabled by remember { mutableStateOf(true) }
    var paymentLinkEnabled by remember { mutableStateOf(true) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundSlate)
            .verticalScroll(scrollState)
            .padding(18.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        // ─── 1. TOP AUTOMATION STATUS BANNER ─────────────────────────────────
        Surface(
            shape = RoundedCornerShape(22.dp),
            color = if (automationStatus.automationEnabled) Color.White else Color(0xFFFEF2F2),
            shadowElevation = 1.dp,
            modifier = Modifier
                .fillMaxWidth()
                .border(
                    1.dp,
                    if (automationStatus.automationEnabled) Color(0xFFBAE6FD) else Color(0xFFFECACA),
                    RoundedCornerShape(22.dp)
                )
        ) {
            Column(modifier = Modifier.padding(18.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Recovery Automation",
                            fontSize = 28.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = NavyDark
                        )
                        Text(
                            text = "Policy Control & Autonomous Governance Center",
                            fontSize = 14.sp,
                            color = TextMuted,
                            fontWeight = FontWeight.Medium,
                            modifier = Modifier.padding(top = 2.dp)
                        )
                    }

                    // Clean Automation Switch Toggle (Removed cluttered green active pill button)
                    Switch(
                        checked = automationStatus.automationEnabled,
                        onCheckedChange = onAutomationToggle,
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = PrimaryBlue,
                            checkedTrackColor = Color(0xFFBAE6FD)
                        )
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Automation Monitoring KPI Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    StatusKpiCard(
                        label = "Monitored",
                        value = "${automationStatus.transactionsMonitored}",
                        subtitle = "Failed Txns",
                        modifier = Modifier.weight(1f)
                    )
                    StatusKpiCard(
                        label = "Auto-Eligible",
                        value = "${automationStatus.autoRecoveryEnabledCount}",
                        subtitle = "Policy Approved",
                        valueColor = PrimaryBlue,
                        modifier = Modifier.weight(1f)
                    )
                    StatusKpiCard(
                        label = "Recoveries Today",
                        value = automationStatus.recoveriesTodayFormatted,
                        subtitle = "${automationStatus.recoveriesTodayCount} txns",
                        valueColor = EmeraldGreen,
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                Surface(
                    color = if (automationStatus.automationEnabled) Color(0xFFF0F9FF) else Color(0xFFFFF1F2),
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = if (automationStatus.automationEnabled) Icons.Default.Info else Icons.Default.PauseCircle,
                            contentDescription = null,
                            tint = if (automationStatus.automationEnabled) PrimaryBlue else Color(0xFFDC2626),
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = automationStatus.pauseExplanation,
                            fontSize = 13.sp,
                            color = if (automationStatus.automationEnabled) NavyDark else Color(0xFF991B1B),
                            lineHeight = 17.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
        }

        // ─── 1.5. DIGITAL LOAN AGREEMENTS & GOVERNANCE ─────────────────────
        Surface(
            shape = RoundedCornerShape(22.dp),
            color = Color.White,
            shadowElevation = 1.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(18.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            color = Color(0xFFEFF6FF),
                            shape = CircleShape,
                            modifier = Modifier.size(36.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Default.Description,
                                    contentDescription = null,
                                    tint = PrimaryBlue,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "Digital Loan Agreements",
                                fontSize = 20.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = NavyDark
                            )
                            Text(
                                text = "Legally Binding Lender & Borrower Contracts",
                                fontSize = 12.sp,
                                color = TextMuted,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                Text(
                    text = "Generate and inspect digitally stamped loan agreements with Lender, Borrower, Amount, Due Date, Repayment Terms, and Electronic Acknowledgement status.",
                    fontSize = 13.sp,
                    color = TextMuted,
                    lineHeight = 17.sp
                )

                if (loans.isNotEmpty()) {
                    loans.take(3).forEach { loan ->
                        Surface(
                            color = Color(0xFFF8FAFC),
                            shape = RoundedCornerShape(14.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .border(1.dp, Color(0xFFE2E8F0), RoundedCornerShape(14.dp))
                        ) {
                            Column(
                                modifier = Modifier.padding(14.dp),
                                verticalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column {
                                        Text(
                                            text = "${loan.borrowerName} • ${loan.principalAmountFormatted}",
                                            fontSize = 15.sp,
                                            fontWeight = FontWeight.ExtraBold,
                                            color = NavyDark
                                        )
                                        Spacer(modifier = Modifier.height(2.dp))
                                        Text(
                                            text = "Repayment Due: ${loan.repaymentDeadline}",
                                            fontSize = 12.sp,
                                            color = TextMuted,
                                            fontWeight = FontWeight.Medium
                                        )
                                    }

                                    Surface(
                                        color = if (loan.isDigitallyAcknowledged) Color(0xFFECFDF5) else Color(0xFFFEF3C7),
                                        shape = RoundedCornerShape(6.dp),
                                        modifier = Modifier.border(1.dp, if (loan.isDigitallyAcknowledged) EmeraldGreen else Color(0xFFD97706), RoundedCornerShape(6.dp))
                                    ) {
                                        Text(
                                            text = if (loan.isDigitallyAcknowledged) "✓ ACKNOWLEDGED" else "⏳ PENDING",
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.ExtraBold,
                                            color = if (loan.isDigitallyAcknowledged) EmeraldGreen else Color(0xFFB45309),
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                        )
                                    }
                                }

                                Button(
                                    onClick = { onOpenLoanAgreement(loan) },
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(10.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue),
                                    contentPadding = PaddingValues(vertical = 8.dp)
                                ) {
                                    Icon(Icons.Default.Description, null, modifier = Modifier.size(15.dp), tint = Color.White)
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("📄 View Digital Loan Agreement", fontSize = 12.sp, fontWeight = FontWeight.ExtraBold, color = Color.White)
                                }
                            }
                        }
                    }
                } else {
                    Button(
                        onClick = {
                            val sampleLoan = LoanItem(
                                id = "LN_101",
                                borrowerName = "Dinesh",
                                borrowerUpiId = "dinesh@upi",
                                lenderName = "PayPulse Merchant (Lender)",
                                principalAmount = 20000.0,
                                principalAmountFormatted = "₹20,000",
                                amountRemaining = 20000.0,
                                amountRemainingFormatted = "₹20,000",
                                repaymentDeadline = "2026-10-02"
                            )
                            onOpenLoanAgreement(sampleLoan)
                        },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue)
                    ) {
                        Icon(Icons.Default.Description, null, modifier = Modifier.size(16.dp), tint = Color.White)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("📄 Open Sample Digital Loan Agreement", fontSize = 13.sp, fontWeight = FontWeight.ExtraBold, color = Color.White)
                    }
                }
            }
        }

        // ─── 2. ACTIVE RECOVERY WORKFLOWS & DECISION CHAIN ───────────────────
        Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
            Column {
                Text(
                    text = "Active Recovery Workflows",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = NavyDark
                )
                Text(
                    text = "Automated strategies PayPulse can use when a failed payment meets your recovery policies.",
                    fontSize = 15.sp,
                    color = TextMuted,
                    lineHeight = 19.sp,
                    modifier = Modifier.padding(top = 2.dp)
                )
            }

            val workflowList = detailedWorkflows.ifEmpty { buildFallbackWorkflowDetails() }

            workflowList.forEach { wf ->
                val isExpanded = expandedWorkflowId == wf.workflowId
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = Color.White,
                    shadowElevation = 1.dp,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(20.dp))
                        .clickable {
                            expandedWorkflowId = if (isExpanded) null else wf.workflowId
                        }
                ) {
                    Column(modifier = Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
                        // 1. WORKFLOW HEADER
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(
                                modifier = Modifier.weight(1f),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                val icon = when (wf.iconType) {
                                    "upi" -> Icons.Default.Payments
                                    "checkout" -> Icons.Default.ShoppingCart
                                    "subscription" -> Icons.Default.Autorenew
                                    else -> Icons.Default.Shield
                                }
                                Surface(
                                    color = if (wf.iconType == "hard_decline") Color(0xFFFEF2F2) else Color(0xFFE0F2FE),
                                    shape = CircleShape,
                                    modifier = Modifier.size(40.dp)
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Icon(
                                            imageVector = icon,
                                            contentDescription = null,
                                            tint = if (wf.iconType == "hard_decline") Color(0xFFDC2626) else PrimaryBlue,
                                            modifier = Modifier.size(20.dp)
                                        )
                                    }
                                }
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Text(wf.name, fontSize = 18.sp, fontWeight = FontWeight.ExtraBold, color = NavyDark)
                                    Text(wf.trigger, fontSize = 13.sp, color = TextMuted, modifier = Modifier.padding(top = 1.dp), maxLines = 1)
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Surface(
                                        color = if (wf.statusLabel == "ACTIVE") Color(0xFFDCFCE7) else Color(0xFFFEF3C7),
                                        shape = RoundedCornerShape(6.dp)
                                    ) {
                                        Text(
                                            text = "● ${wf.statusLabel}",
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.ExtraBold,
                                            color = if (wf.statusLabel == "ACTIVE") EmeraldGreen else Color(0xFFB45309),
                                            modifier = Modifier.padding(horizontal = 7.dp, vertical = 3.dp)
                                        )
                                    }
                                }
                            }

                            Icon(
                                imageVector = if (isExpanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                                contentDescription = null,
                                tint = TextMuted,
                                modifier = Modifier.size(20.dp)
                            )
                        }

                        // COLLAPSED SUMMARY BOX (Shown by default when collapsed)
                        if (!isExpanded) {
                            Surface(
                                color = Color(0xFFF8FAFC),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(14.dp),
                                    verticalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Column(verticalArrangement = Arrangement.spacedBy(3.dp)) {
                                        Text("RECOVERY ACTION", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = TextMuted)
                                        Text(wf.action, fontSize = 13.sp, fontWeight = FontWeight.ExtraBold, color = PrimaryBlue)
                                    }

                                    HorizontalDivider(color = Color(0xFFE2E8F0))

                                    Column(verticalArrangement = Arrangement.spacedBy(3.dp)) {
                                        Text("PERFORMANCE IMPACT", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = TextMuted)
                                        Text(wf.performanceFormatted, fontSize = 14.sp, fontWeight = FontWeight.ExtraBold, color = EmeraldGreen)
                                    }
                                }
                            }
                        }

                        // EXPANDED HIERARCHY DETAILS
                        AnimatedVisibility(
                            visible = isExpanded,
                            enter = fadeIn() + expandVertically(),
                            exit = fadeOut() + shrinkVertically()
                        ) {
                            Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                                HorizontalDivider(color = Color(0xFFF1F5F9))

                                // LEVEL 3: RECOVERY ACTION — PRIMARY INFORMATION
                                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                    Text("RECOVERY ACTION", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = TextMuted)
                                    Text(wf.action, fontSize = 17.sp, fontWeight = FontWeight.ExtraBold, color = PrimaryBlue)
                                    Text(
                                        text = if (wf.delayMinutes > 0) "Cooling window delay: ${wf.delayMinutes} minutes before retrying." else "Immediate action fallback.",
                                        fontSize = 12.sp,
                                        color = TextMuted
                                    )
                                }

                                // LEVEL 4: TRIGGER CONDITION
                                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                    Text("TRIGGER CONDITION", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = TextMuted)
                                    Surface(
                                        color = Color(0xFFF0F9FF),
                                        shape = RoundedCornerShape(10.dp),
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Row(
                                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Surface(color = PrimaryBlue, shape = RoundedCornerShape(4.dp)) {
                                                Text("ML", fontSize = 9.sp, fontWeight = FontWeight.ExtraBold, color = Color.White, modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp))
                                            }
                                            Spacer(modifier = Modifier.width(8.dp))
                                            Text(wf.mlCondition, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = NavyDark)
                                        }
                                    }
                                }

                                // SAFETY RULES
                                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                    Text("SAFETY & GOVERNANCE RULES", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = TextMuted)
                                    Surface(
                                        color = Color(0xFFF8FAFC),
                                        shape = RoundedCornerShape(10.dp),
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Column(
                                            modifier = Modifier.padding(10.dp),
                                            verticalArrangement = Arrangement.spacedBy(4.dp)
                                        ) {
                                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                                Text("Max Retries: ${wf.maxRetries}", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = NavyDark)
                                                Text("Max Amount: ₹50,000", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = NavyDark)
                                            }
                                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                                Text("Hard Declines: Blocked", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color(0xFFDC2626))
                                                Text("Policy Status: Enforced", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = EmeraldGreen)
                                            }
                                        }
                                    }
                                }

                                // LEVEL 5: PERFORMANCE (Side-by-Side Clean Metric Boxes)
                                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                    Text("PERFORMANCE", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = TextMuted)

                                    val perfParts = parsePerfFormatted(wf.performanceFormatted)

                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                                    ) {
                                        Surface(
                                            color = Color(0xFFECFDF5),
                                            shape = RoundedCornerShape(12.dp),
                                            modifier = Modifier.weight(1f)
                                        ) {
                                            Column(modifier = Modifier.padding(12.dp)) {
                                                Text(perfParts.first, fontSize = 18.sp, fontWeight = FontWeight.ExtraBold, color = EmeraldGreen)
                                                Text("Recovered Amount", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = TextMuted)
                                            }
                                        }

                                        Surface(
                                            color = Color(0xFFF0F9FF),
                                            shape = RoundedCornerShape(12.dp),
                                            modifier = Modifier.weight(1f)
                                        ) {
                                            Column(modifier = Modifier.padding(12.dp)) {
                                                Text(perfParts.second, fontSize = 18.sp, fontWeight = FontWeight.ExtraBold, color = PrimaryBlue)
                                                Text("Success Rate", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = TextMuted)
                                            }
                                        }
                                    }
                                }

                                // RECENT DECISION CHAIN FLOW
                                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                    Text("STEP-BY-STEP DECISION CHAIN FLOW", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = TextMuted)

                                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                        wf.decisionChain.forEach { step ->
                                            Row(
                                                verticalAlignment = Alignment.CenterVertically,
                                                modifier = Modifier.fillMaxWidth()
                                            ) {
                                                Surface(
                                                    color = Color(0xFFEFF6FF),
                                                    shape = CircleShape,
                                                    modifier = Modifier.size(22.dp)
                                                ) {
                                                    Box(contentAlignment = Alignment.Center) {
                                                        Text("${step.stepNumber}", fontSize = 10.sp, fontWeight = FontWeight.ExtraBold, color = PrimaryBlue)
                                                    }
                                                }
                                                Spacer(modifier = Modifier.width(10.dp))
                                                Column {
                                                    Text(step.title, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = NavyDark)
                                                    Text(step.description, fontSize = 11.sp, color = TextMuted)
                                                }
                                            }
                                        }
                                    }
                                }

                                // CONFIGURE BUTTON
                                OutlinedButton(
                                    onClick = {
                                        val item = WorkflowItem(id = wf.workflowId, title = wf.name)
                                        onConfigureWorkflow(item)
                                    },
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(12.dp)
                                ) {
                                    Icon(imageVector = Icons.Default.Tune, contentDescription = null, modifier = Modifier.size(16.dp), tint = PrimaryBlue)
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("Configure Workflow", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = PrimaryBlue)
                                }
                            }
                        }
                    }
                }
            }
        }

        // ─── 3. SMART INTEREST & EMI CALCULATOR ─────────────────────────────
        InterestCalculatorCard()

        // ─── 4. POLICY PREVIEW CARD (VERY IMPORTANT) ─────────────────────────
        if (pendingPolicyDraft != null || policyPreviewResult != null) {
            val preview = policyPreviewResult ?: PolicyPreviewResponse()
            Surface(
                shape = RoundedCornerShape(22.dp),
                color = Color(0xFFEFF6FF),
                shadowElevation = 2.dp,
                modifier = Modifier
                    .fillMaxWidth()
                    .border(2.dp, PrimaryBlue, RoundedCornerShape(22.dp))
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(imageVector = Icons.Default.Preview, contentDescription = null, tint = PrimaryBlue, modifier = Modifier.size(22.dp))
                            Spacer(modifier = Modifier.width(10.dp))
                            Text("Preview Policy Changes", fontSize = 18.sp, fontWeight = FontWeight.ExtraBold, color = NavyDark)
                        }
                        Surface(
                            color = Color(0xFFDBEAFE),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text("SIMULATED IMPACT", fontSize = 10.sp, fontWeight = FontWeight.ExtraBold, color = PrimaryBlue, modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp))
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = "Review business impact before applying settings to live recovery engine:",
                        fontSize = 13.sp,
                        color = TextMuted
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        PreviewStatBox(
                            title = "Eligible Txns",
                            value = "${preview.currentEligibleCount} → ${preview.proposedEligibleCount}",
                            color = PrimaryBlue,
                            modifier = Modifier.weight(1f)
                        )
                        PreviewStatBox(
                            title = "Expected Recovery",
                            value = "${preview.currentExpectedRecoveryFormatted} → ${preview.proposedExpectedRecoveryFormatted}",
                            color = EmeraldGreen,
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Surface(
                        color = Color.White,
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = preview.estimatedChangeSummary,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = NavyDark,
                            modifier = Modifier.padding(12.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        OutlinedButton(
                            onClick = onCancelPolicyPreview,
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(14.dp)
                        ) {
                            Text("Cancel", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = TextMuted)
                        }

                        Button(
                            onClick = onApplyPolicy,
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue),
                            shape = RoundedCornerShape(14.dp)
                        ) {
                            Text("Apply Policy", fontSize = 14.sp, fontWeight = FontWeight.ExtraBold, color = Color.White)
                        }
                    }
                }
            }
        }

        // ─── 5. HOW PAYPULSE DECIDES ("HOW PAYPULSE PROTECTS YOUR ACCOUNT") ──
        Surface(
            shape = RoundedCornerShape(22.dp),
            color = Color(0xFFF0F9FF),
            shadowElevation = 1.dp,
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, Color(0xFFBAE6FD), RoundedCornerShape(22.dp))
        ) {
            Column(
                modifier = Modifier.padding(18.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text("How PayPulse Protects Your Merchant Account", fontSize = 18.sp, fontWeight = FontWeight.ExtraBold, color = PrimaryBlue)

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    ArchStep("ML Prediction", "Recovery Prob", PrimaryBlue)
                    ArchArrow()
                    ArchStep("AI Diagnosis", "Strategy Select", PrimaryBlue)
                    ArchArrow()
                    ArchStep("Policy Engine", "Merchant Rules", Color(0xFF0284C7))
                    ArchArrow()
                    ArchStep("Recovery Engine", "Execution", EmeraldGreen)
                    ArchArrow()
                    ArchStep("Audit Log", "Immutability", Color(0xFF7C3AED))
                }

                Surface(
                    color = Color.White,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "🔒 Trust Rule: AI recommendations cannot bypass merchant safety policies.",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = NavyDark,
                        modifier = Modifier.padding(12.dp)
                    )
                }
            }
        }

        // ─── 6. RECENT AUTOMATED DECISIONS & AUDIT LOG ───────────────────────
        Surface(
            shape = RoundedCornerShape(22.dp),
            color = Color.White,
            shadowElevation = 1.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(18.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Column {
                    Text("Recent Automated Decisions", fontSize = 18.sp, fontWeight = FontWeight.ExtraBold, color = NavyDark)
                    Text("Live Audit Log • Real-time Policy & Execution Ledger", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TextMuted, modifier = Modifier.padding(top = 2.dp))
                }

                decisions.forEach { dec ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        val icon = when (dec.actionType.lowercase()) {
                            "blocked" -> Icons.Default.Block
                            "escalated" -> Icons.Default.Warning
                            else -> Icons.Default.Check
                        }
                        val bg = when (dec.actionType.lowercase()) {
                            "blocked" -> Color(0xFFFEE2E2)
                            "escalated" -> Color(0xFFFEF3C7)
                            else -> Color(0xFFDCFCE7)
                        }
                        val tint = when (dec.actionType.lowercase()) {
                            "blocked" -> Color(0xFFB91C1C)
                            "escalated" -> Color(0xFFB45309)
                            else -> EmeraldGreen
                        }

                        Surface(
                            color = bg,
                            shape = CircleShape,
                            modifier = Modifier.size(32.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(imageVector = icon, contentDescription = null, tint = tint, modifier = Modifier.size(16.dp))
                            }
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "${dec.actionType.replaceFirstChar { it.uppercase() }} · ${dec.amountFormatted}",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = NavyDark
                            )
                            Text(dec.reason, fontSize = 14.sp, color = TextMuted)
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(28.dp))
    }
}

// ─── HELPER COMPOSABLES FOR SCREEN 4 ─────────────────────────────────────────

@Composable
private fun StatusKpiCard(label: String, value: String, subtitle: String, valueColor: Color = NavyDark, modifier: Modifier = Modifier) {
    Surface(
        color = Color(0xFFF8FAFC),
        shape = RoundedCornerShape(14.dp),
        modifier = modifier
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(label, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TextMuted)
            Text(value, fontSize = 20.sp, fontWeight = FontWeight.ExtraBold, color = valueColor, modifier = Modifier.padding(vertical = 3.dp))
            Text(subtitle, fontSize = 11.sp, color = TextMuted)
        }
    }
}

@Composable
private fun ChannelChip(label: String, enabled: Boolean, onToggle: () -> Unit, modifier: Modifier = Modifier) {
    Surface(
        color = if (enabled) Color(0xFFEFF6FF) else Color(0xFFF1F5F9),
        shape = RoundedCornerShape(12.dp),
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .border(1.dp, if (enabled) PrimaryBlue else Color(0xFFE2E8F0), RoundedCornerShape(12.dp))
            .clickable { onToggle() }
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = if (enabled) Icons.Default.CheckCircle else Icons.Default.RadioButtonUnchecked,
                contentDescription = null,
                tint = if (enabled) PrimaryBlue else TextMuted,
                modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(label, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = if (enabled) PrimaryBlue else TextMuted)
        }
    }
}

@Composable
private fun PreviewStatBox(title: String, value: String, color: Color, modifier: Modifier = Modifier) {
    Surface(
        color = Color.White,
        shape = RoundedCornerShape(14.dp),
        modifier = modifier
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(title, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TextMuted)
            Text(value, fontSize = 15.sp, fontWeight = FontWeight.ExtraBold, color = color)
        }
    }
}

@Composable
private fun ArchStep(title: String, subtitle: String, color: Color) {
    Surface(
        color = Color.White,
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier.width(105.dp)
    ) {
        Column(
            modifier = Modifier.padding(10.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(title, fontSize = 12.sp, fontWeight = FontWeight.ExtraBold, color = color, textAlign = TextAlign.Center)
            Text(subtitle, fontSize = 10.sp, color = TextMuted, textAlign = TextAlign.Center)
        }
    }
}

@Composable
private fun ArchArrow() {
    Icon(imageVector = Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null, tint = TextMuted, modifier = Modifier.size(14.dp))
}

private fun buildFallbackWorkflowDetails(): List<WorkflowDetailDto> {
    return listOf(
        WorkflowDetailDto(
            workflowId = "wf_upi",
            name = "UPI Failure Recovery",
            statusLabel = "ACTIVE",
            isActive = true,
            iconType = "upi",
            trigger = "UPI Timeout / transient gateway failure",
            mlCondition = "Recovery Probability ≥ 70%",
            aiDiagnosis = "Transient Gateway Failure",
            action = "Delayed Retry (15m cooling window)",
            delayMinutes = 15,
            maxRetries = 2,
            performanceFormatted = "₹9.2L recovered (81% success)",
            decisionChain = listOf(
                DecisionStepDto(stepNumber = 1, title = "Payment Failed", description = "UPI Gateway Timeout detected", status = "completed"),
                DecisionStepDto(stepNumber = 2, title = "ML Prediction", description = "87% recovery probability calculated", status = "completed"),
                DecisionStepDto(stepNumber = 3, title = "AI Diagnosis", description = "Transient gateway failure identified", status = "completed"),
                DecisionStepDto(stepNumber = 4, title = "Policy Check", description = "Passed 87% ≥ 70%, retries < 2", status = "completed"),
                DecisionStepDto(stepNumber = 5, title = "Cooling Window", description = "Waiting 15 minutes before retry", status = "pending"),
                DecisionStepDto(stepNumber = 6, title = "Execution", description = "Auto-retry executed via Razorpay Switch", status = "pending")
            )
        ),
        WorkflowDetailDto(
            workflowId = "wf_checkout",
            name = "Checkout Abandonment Recovery",
            statusLabel = "ACTIVE",
            isActive = true,
            iconType = "checkout",
            trigger = "Cart / Checkout session abandoned",
            mlCondition = "Customer Recovery Prob ≥ 65%",
            aiDiagnosis = "Checkout Friction / Abandonment",
            action = "Send Payment Link via SMS / WhatsApp",
            delayMinutes = 30,
            maxRetries = 1,
            performanceFormatted = "₹5.4L recovered (72% success)",
            decisionChain = listOf(
                DecisionStepDto(stepNumber = 1, title = "Cart Abandoned", description = "Customer abandoned checkout", status = "completed"),
                DecisionStepDto(stepNumber = 2, title = "ML Prediction", description = "78% link conversion probability", status = "completed"),
                DecisionStepDto(stepNumber = 3, title = "AI Diagnosis", description = "Payment friction detected", status = "completed"),
                DecisionStepDto(stepNumber = 4, title = "Policy Check", description = "Communication policy enabled", status = "completed"),
                DecisionStepDto(stepNumber = 5, title = "Payment Link", description = "Dispatched via WhatsApp", status = "pending")
            )
        ),
        WorkflowDetailDto(
            workflowId = "wf_mandate",
            name = "Subscription / Mandate Recovery",
            statusLabel = "ACTIVE",
            isActive = true,
            iconType = "subscription",
            trigger = "Mandate auto-debit renewal failure",
            mlCondition = "Subscription Active & Prob ≥ 70%",
            aiDiagnosis = "Recurring Settlement Failure",
            action = "Retry Mandate during off-peak window",
            delayMinutes = 1440,
            maxRetries = 3,
            performanceFormatted = "₹2.7L recovered (76% success)",
            decisionChain = listOf(
                DecisionStepDto(stepNumber = 1, title = "Mandate Failed", description = "Auto-debit failed in morning cycle", status = "completed"),
                DecisionStepDto(stepNumber = 2, title = "ML Prediction", description = "83% off-peak retry probability", status = "completed"),
                DecisionStepDto(stepNumber = 3, title = "AI Diagnosis", description = "Banking congestion detected", status = "completed"),
                DecisionStepDto(stepNumber = 4, title = "Policy Check", description = "Retry count within limit (1/3)", status = "completed"),
                DecisionStepDto(stepNumber = 5, title = "Mandate Retry", description = "Scheduled for off-peak evening window", status = "pending")
            )
        ),
        WorkflowDetailDto(
            workflowId = "wf_hard_decline",
            name = "Hard Decline Protection",
            statusLabel = "PROTECTED",
            isActive = true,
            iconType = "hard_decline",
            trigger = "Insufficient Funds / Bank Decline",
            mlCondition = "Hard Decline Code",
            aiDiagnosis = "Permanent / Hard Decline",
            action = "Do NOT retry automatically. Offer Alt Method",
            delayMinutes = 0,
            maxRetries = 0,
            performanceFormatted = "₹42.8K retry costs saved",
            decisionChain = listOf(
                DecisionStepDto(stepNumber = 1, title = "Hard Decline", description = "Bank decline code returned", status = "completed"),
                DecisionStepDto(stepNumber = 2, title = "AI Diagnosis", description = "Hard decline identified (Do Not Retry)", status = "completed"),
                DecisionStepDto(stepNumber = 3, title = "Policy Check", description = "Policy enforced: AUTOMATIC RETRY BLOCKED", status = "completed"),
                DecisionStepDto(stepNumber = 4, title = "Protection", description = "Saved retry fees & prevented account lock", status = "completed")
            )
        )
    )
}

private fun parsePerfFormatted(perfText: String): Pair<String, String> {
    if (perfText.contains("(")) {
        val parts = perfText.split("(")
        val amount = parts[0].replace("recovered", "").replace("costs saved", "").trim()
        val rate = parts[1].replace(")", "").replace("success", "").trim()
        return Pair(amount, rate)
    }
    return Pair(perfText, "81%")
}

@Composable
fun InterestCalculatorCard() {
    var principalAmount by remember { mutableStateOf(100000f) }
    var interestRate by remember { mutableStateOf(12f) }
    var tenureMonths by remember { mutableStateOf(12f) }
    var calcType by remember { mutableStateOf("EMI") }

    val p = principalAmount.toDouble()
    val r = (interestRate / 12f / 100f).toDouble()
    val n = tenureMonths.toInt().toDouble()

    val emi = if (r > 0 && n > 0) {
        val factor = (1 + r).pow(n)
        if (factor > 1) (p * r * factor) / (factor - 1) else p / n
    } else {
        p / n
    }

    val totalPayable = if (calcType == "EMI") emi * n else p + (p * (interestRate / 100f) * (tenureMonths / 12f))
    val totalInterest = (totalPayable - p).coerceAtLeast(0.0)
    val monthlyPayment = if (calcType == "EMI") emi else totalPayable / n

    Surface(
        color = Color.White,
        shape = RoundedCornerShape(20.dp),
        shadowElevation = 2.dp,
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, Color(0xFFE2E8F0), RoundedCornerShape(20.dp))
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Header Line 1: Icon & Main Title
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    color = Color(0xFFEFF6FF),
                    shape = CircleShape,
                    modifier = Modifier.size(40.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Default.Calculate,
                            contentDescription = null,
                            tint = PrimaryBlue,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = "Smart Interest & EMI Calculator",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = NavyDark
                    )
                    Text(
                        text = "Simulate interest cost & EMI for loans & defaults",
                        fontSize = 11.sp,
                        color = TextMuted,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            // Header Line 2: Mode Selector (Next Line Full-Width 2-Segment Control)
            Surface(
                color = Color(0xFFF1F5F9),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(3.dp),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Surface(
                        color = if (calcType == "EMI") PrimaryBlue else Color.Transparent,
                        shape = RoundedCornerShape(10.dp),
                        shadowElevation = if (calcType == "EMI") 2.dp else 0.dp,
                        modifier = Modifier
                            .weight(1f)
                            .clickable { calcType = "EMI" }
                    ) {
                        Text(
                            text = "Reducing EMI",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = if (calcType == "EMI") Color.White else Color(0xFF64748B),
                            modifier = Modifier.padding(vertical = 8.dp),
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )
                    }
                    Surface(
                        color = if (calcType == "SIMPLE") PrimaryBlue else Color.Transparent,
                        shape = RoundedCornerShape(10.dp),
                        shadowElevation = if (calcType == "SIMPLE") 2.dp else 0.dp,
                        modifier = Modifier
                            .weight(1f)
                            .clickable { calcType = "SIMPLE" }
                    ) {
                        Text(
                            text = "Simple Interest",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = if (calcType == "SIMPLE") Color.White else Color(0xFF64748B),
                            modifier = Modifier.padding(vertical = 8.dp),
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )
                    }
                }
            }

            HorizontalDivider(color = Color(0xFFE2E8F0))

            // Preset Quick Amount Chips
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                listOf(25000f to "₹25K", 50000f to "₹50K", 100000f to "₹1L", 250000f to "₹2.5L", 500000f to "₹5L").forEach { (amt, label) ->
                    val isSel = (principalAmount == amt)
                    Surface(
                        color = if (isSel) Color(0xFFEFF6FF) else Color(0xFFF8FAFC),
                        shape = RoundedCornerShape(10.dp),
                        shadowElevation = if (isSel) 1.dp else 0.dp,
                        modifier = Modifier
                            .weight(1f)
                            .border(1.dp, if (isSel) PrimaryBlue else Color(0xFFE2E8F0), RoundedCornerShape(10.dp))
                            .clickable { principalAmount = amt }
                    ) {
                        Text(
                            text = label,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = if (isSel) PrimaryBlue else NavyDark,
                            modifier = Modifier.padding(vertical = 7.dp),
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )
                    }
                }
            }

            // Slider 1: Loan Amount
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Loan / Principal Amount", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = NavyDark)
                    Surface(
                        color = Color(0xFFEFF6FF),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.border(1.dp, Color(0xFFBFDBFE), RoundedCornerShape(8.dp))
                    ) {
                        Text(
                            text = "₹${String.format("%,.0f", principalAmount)}",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = PrimaryBlue,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                        )
                    }
                }
                Slider(
                    value = principalAmount,
                    onValueChange = { principalAmount = it },
                    valueRange = 5000f..1000000f,
                    steps = 198,
                    colors = SliderDefaults.colors(thumbColor = PrimaryBlue, activeTrackColor = PrimaryBlue)
                )
            }

            // Slider 2: Interest Rate (% p.a.)
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Interest Rate (% p.a.)", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = NavyDark)
                    Surface(
                        color = Color(0xFFF3E8FF),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.border(1.dp, Color(0xFFE9D5FF), RoundedCornerShape(8.dp))
                    ) {
                        Text(
                            text = "${String.format("%.1f", interestRate)}% p.a.",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color(0xFF7C3AED),
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                        )
                    }
                }
                Slider(
                    value = interestRate,
                    onValueChange = { interestRate = it },
                    valueRange = 6f..36f,
                    colors = SliderDefaults.colors(thumbColor = Color(0xFF7C3AED), activeTrackColor = Color(0xFF7C3AED))
                )
            }

            // Slider 3: Tenure Months
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Tenure (Duration)", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = NavyDark)
                    Surface(
                        color = Color(0xFFECFDF5),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.border(1.dp, Color(0xFFA7F3D0), RoundedCornerShape(8.dp))
                    ) {
                        Text(
                            text = "${tenureMonths.toInt()} Months (${String.format("%.1f", tenureMonths / 12f)} yrs)",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color(0xFF059669),
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                        )
                    }
                }
                Slider(
                    value = tenureMonths,
                    onValueChange = { tenureMonths = it },
                    valueRange = 1f..60f,
                    steps = 58,
                    colors = SliderDefaults.colors(thumbColor = EmeraldGreen, activeTrackColor = EmeraldGreen)
                )
            }

            // Output Result Card (Executive Midnight Blue Hero Card)
            Surface(
                color = Color(0xFF0F172A),
                shape = RoundedCornerShape(18.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, Color(0xFF334155), RoundedCornerShape(18.dp))
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = if (calcType == "EMI") "ESTIMATED MONTHLY EMI" else "MONTHLY EQUIVALENT",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = Color(0xFF94A3B8),
                                letterSpacing = 0.5.sp
                            )
                            Text(
                                text = "₹${String.format("%,.0f", monthlyPayment)}",
                                fontSize = 26.sp,
                                fontWeight = FontWeight.Black,
                                color = Color(0xFF38BDF8)
                            )
                        }
                        Surface(
                            color = Color(0xFF16233B),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.border(1.dp, Color(0xFF1E293B), RoundedCornerShape(12.dp))
                        ) {
                            Column(
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                                horizontalAlignment = Alignment.End
                            ) {
                                Text("TOTAL INTEREST", fontSize = 9.sp, fontWeight = FontWeight.ExtraBold, color = Color(0xFF94A3B8))
                                Text("+₹${String.format("%,.0f", totalInterest)}", fontSize = 15.sp, fontWeight = FontWeight.ExtraBold, color = Color(0xFF4ADE80))
                            }
                        }
                    }

                    HorizontalDivider(color = Color(0xFF1E293B))

                    // Shifted amount to next line as requested
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(2.dp)
                    ) {
                        Text("Total Amount Payable (Principal + Interest)", fontSize = 11.sp, color = Color(0xFFCBD5E1), fontWeight = FontWeight.Medium)
                        Text("₹${String.format("%,.0f", totalPayable)}", fontSize = 18.sp, fontWeight = FontWeight.Black, color = Color.White)
                    }

                    // Ratio Bar (Principal Cyan vs Interest Green)
                    val totalForBar = (p + totalInterest).toFloat()
                    val principalRatio = if (totalForBar > 0) (p / totalForBar).coerceIn(0.0, 1.0).toFloat() else 0.8f

                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(8.dp)
                                .clip(RoundedCornerShape(4.dp))
                                .background(Color(0xFF4ADE80))
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxHeight()
                                    .fillMaxWidth(principalRatio)
                                    .background(Color(0xFF38BDF8))
                            )
                        }
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(modifier = Modifier.size(6.dp).clip(CircleShape).background(Color(0xFF38BDF8)))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Principal (${String.format("%.0f", principalRatio * 100)}%)", fontSize = 10.sp, color = Color(0xFF94A3B8), fontWeight = FontWeight.Medium)
                            }
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(modifier = Modifier.size(6.dp).clip(CircleShape).background(Color(0xFF4ADE80)))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Interest (${String.format("%.0f", (1f - principalRatio) * 100)}%)", fontSize = 10.sp, color = Color(0xFF94A3B8), fontWeight = FontWeight.Medium)
                            }
                        }
                    }
                }
            }
        }
    }
}
