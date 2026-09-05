package com.paypulse.ai.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.platform.LocalContext
import com.paypulse.ai.data.model.LoanItem
import com.paypulse.ai.data.model.QueueSummary
import com.paypulse.ai.data.model.TransactionItem
import com.paypulse.ai.ui.theme.*
import com.paypulse.ai.ui.utils.UpiPaymentLauncher

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QueueScreen(
    summary: QueueSummary,
    transactions: List<TransactionItem>,
    loans: List<LoanItem> = emptyList(),
    selectedCategory: String,
    searchQuery: String,
    selectedTransactionIds: Set<String> = emptySet(),
    onCategorySelected: (String) -> Unit,
    onSearchQueryChanged: (String) -> Unit,
    onReviewTransaction: (String) -> Unit,
    onToggleTransactionSelection: (String) -> Unit = {},
    onClearBatchSelection: () -> Unit = {},
    onQuickExecute: (String) -> Unit = {},
    onExecuteBatch: () -> Unit = {},
    onOpenAddLoan: () -> Unit = {},
    onOpenBorrowerProfile: (String) -> Unit = {},
    onOpenRepayLoan: (LoanItem) -> Unit = {},
    onToggleLoanCompleted: (String) -> Unit = {},
    onOpenLegalNotice: (String?, LoanItem?) -> Unit = { _, _ -> },
    onOpenWhatsAppSimulator: (String) -> Unit = {},
    onOpenLoanAgreement: (LoanItem) -> Unit = {}
) {
    var sortMode by remember { mutableStateOf("expected_recovery") }
    var sortMenuOpen by remember { mutableStateOf(false) }
    var quickExecuteItem by remember { mutableStateOf<TransactionItem?>(null) }

    val filteredTransactions = remember(transactions, selectedCategory, searchQuery, sortMode) {
        transactions
            .filter { item -> item.matchesFilter(selectedCategory) && item.matchesSearch(searchQuery) }
            .let { items ->
                when (sortMode) {
                    "probability" -> items.sortedByDescending { it.probabilityPct }
                    "amount" -> items.sortedByDescending { it.amount }
                    "priority" -> items.sortedWith(compareBy<TransactionItem> { priorityRank(it.priority) }.thenByDescending { it.expectedRecovery })
                    "failure_time" -> items.sortedByDescending { it.id }
                    else -> items.sortedByDescending { it.expectedRecovery }
                }
            }
    }

    val visibleExpectedRecovery = remember(filteredTransactions) { filteredTransactions.sumOf { it.expectedRecovery } }
    val visibleAutoEligible = remember(filteredTransactions) { filteredTransactions.count { it.policyStatus == "AUTO-ELIGIBLE" } }
    val selectedItems = remember(transactions, selectedTransactionIds) { transactions.filter { selectedTransactionIds.contains(it.id) } }

    quickExecuteItem?.let { item ->
        AlertDialog(
            onDismissRequest = { quickExecuteItem = null },
            title = { Text("Confirm Recovery", fontSize = 18.sp, fontWeight = FontWeight.ExtraBold, color = NavyDark) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    DetailRow("Action", item.recommendedStrategy)
                    DetailRow("Expected Recovery", item.expectedRecoveryFormatted)
                    DetailRow("Policy Evaluation", if (item.policyStatus == "AUTO-ELIGIBLE") "APPROVED" else "NEEDS REVIEW")
                    DetailRow("Policy Status", item.policyStatus)
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        quickExecuteItem = null
                        onQuickExecute(item.id)
                    },
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = EmeraldGreen)
                ) {
                    Text("Execute Action", fontWeight = FontWeight.ExtraBold)
                }
            },
            dismissButton = {
                OutlinedButton(onClick = { quickExecuteItem = null }, shape = RoundedCornerShape(12.dp)) {
                    Text("Cancel", fontWeight = FontWeight.Bold)
                }
            },
            shape = RoundedCornerShape(20.dp),
            containerColor = Color.White
        )
    }

    Box(modifier = Modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(BackgroundSlate)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            item {
                Column {
                    Text("AI Recovery Queue", fontSize = 26.sp, fontWeight = FontWeight.ExtraBold, color = NavyDark)
                    Text(
                        text = "${summary.pendingActionsCount} Opportunities • ${summary.totalExpectedRecoveryFormatted} Expected Recovery",
                        fontSize = 12.sp,
                        color = PrimaryBlue,
                        modifier = Modifier.padding(top = 2.dp),
                        fontWeight = FontWeight.Bold
                    )
                }
            }

        item {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                QueueMetricCard(
                    icon = Icons.Default.PendingActions,
                    label = "Recovery\nActions",
                    value = "${summary.pendingActionsCount}",
                    helper = "Backend ranked",
                    modifier = Modifier.weight(1f)
                )
                QueueMetricCard(
                    icon = Icons.AutoMirrored.Filled.TrendingUp,
                    label = "Expected\nRecovery",
                    value = summary.totalExpectedRecoveryFormatted,
                    helper = "Amount x prob",
                    tint = PrimaryBlue,
                    modifier = Modifier.weight(1f)
                )
                QueueMetricCard(
                    icon = Icons.Default.SmartToy,
                    label = "Auto-\nEligible",
                    value = "${summary.autoEligibleCount}",
                    helper = summary.autoEligiblePctFormatted,
                    tint = EmeraldGreen,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        item {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    filterOptions.forEach { option ->
                        FilterChip(
                            selected = selectedCategory == option.key,
                            onClick = { onCategorySelected(option.key) },
                            label = { Text(option.label, fontWeight = FontWeight.Bold, fontSize = 12.sp) },
                            shape = RoundedCornerShape(20.dp),
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = PrimaryBlue,
                                selectedLabelColor = Color.White,
                                containerColor = Color.White,
                                labelColor = TextMuted
                            )
                        )
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = onSearchQueryChanged,
                        placeholder = { Text("Search ID, method, gateway, failure...", fontSize = 13.sp, color = TextMuted) },
                        leadingIcon = {
                            Icon(imageVector = Icons.Default.Search, contentDescription = null, tint = TextMuted, modifier = Modifier.size(18.dp))
                        },
                        modifier = Modifier
                            .weight(1f)
                            .height(52.dp),
                        shape = RoundedCornerShape(14.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = Color.White,
                            unfocusedContainerColor = Color.White,
                            focusedBorderColor = PrimaryBlue,
                            unfocusedBorderColor = CardBorder
                        ),
                        singleLine = true
                    )

                    Box {
                        Surface(
                            shape = RoundedCornerShape(14.dp),
                            color = Color.White,
                            shadowElevation = 1.dp,
                            modifier = Modifier
                                .height(52.dp)
                                .clickable { sortMenuOpen = true }
                        ) {
                            Row(
                                modifier = Modifier
                                    .padding(horizontal = 12.dp)
                                    .fillMaxHeight(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(sortLabel(sortMode), fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TextMuted)
                                Spacer(modifier = Modifier.width(4.dp))
                                Icon(imageVector = Icons.Default.ArrowDownward, contentDescription = null, tint = TextMuted, modifier = Modifier.size(14.dp))
                            }
                        }
                        DropdownMenu(expanded = sortMenuOpen, onDismissRequest = { sortMenuOpen = false }) {
                            sortOptions.forEach { option ->
                                DropdownMenuItem(
                                    text = { Text(option.label, fontSize = 13.sp, fontWeight = FontWeight.Bold) },
                                    onClick = {
                                        sortMode = option.key
                                        sortMenuOpen = false
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }

        if (selectedItems.isNotEmpty()) {
            item {
                BatchSelectionBar(
                    selectedItems = selectedItems,
                    onReviewBatch = { selectedItems.firstOrNull()?.let { onReviewTransaction(it.id) } },
                    onExecuteBatch = onExecuteBatch,
                    onClear = onClearBatchSelection
                )
            }
        }

        item {
            Surface(shape = RoundedCornerShape(18.dp), color = Color.White, shadowElevation = 1.dp, modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text("Recovery Opportunity", fontSize = 15.sp, fontWeight = FontWeight.ExtraBold, color = NavyDark)
                        Text("${filteredTransactions.size} transactions", fontSize = 12.sp, color = TextMuted)
                    }
                    Column(horizontalAlignment = Alignment.End) {
                        Text(formatInr(visibleExpectedRecovery), fontSize = 15.sp, fontWeight = FontWeight.ExtraBold, color = PrimaryBlue)
                        Text("$visibleAutoEligible auto-eligible", fontSize = 12.sp, color = TextMuted)
                    }
                }
            }
        }

        item {
            Text("AI Recovery Queue", fontSize = 18.sp, fontWeight = FontWeight.ExtraBold, color = NavyDark, modifier = Modifier.padding(top = 2.dp))
        }

        if (transactions.isEmpty()) {
            item {
                QueueEmptyState(
                    title = "No Recovery Opportunities",
                    message = "PayPulse is monitoring failed payments. New recovery opportunities will appear here.",
                    onRetry = null
                )
            }
        } else if (filteredTransactions.isEmpty()) {
            item {
                val title = if (selectedCategory == "auto_eligible") "No Auto-Eligible Transactions" else "No Results"
                val message = if (selectedCategory == "auto_eligible") {
                    "No transactions currently meet your merchant recovery policy."
                } else {
                    "No transactions match your current filters."
                }
                QueueEmptyState(title = title, message = message, onRetry = { onCategorySelected("all") })
            }
        } else {
            items(filteredTransactions, key = { it.id }) { item ->
                RecoveryQueueCard(
                    item = item,
                    selected = selectedTransactionIds.contains(item.id),
                    onToggleSelection = onToggleTransactionSelection,
                    onReviewTransaction = onReviewTransaction,
                    onQuickExecute = { quickExecuteItem = item },
                    onOpenRepayLoan = onOpenRepayLoan,
                    onOpenBorrowerProfile = onOpenBorrowerProfile,
                    onOpenLegalNotice = { onOpenLegalNotice(it, null) },
                    onOpenWhatsAppSimulator = onOpenWhatsAppSimulator,
                    onOpenLoanAgreement = onOpenLoanAgreement
                )
            }
        }

        item {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(imageVector = Icons.Default.AccountBalance, contentDescription = null, tint = PrimaryBlue, modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Active Loans & Receivables", fontSize = 18.sp, fontWeight = FontWeight.ExtraBold, color = NavyDark)
                    }

                    Surface(
                        color = Color(0xFFE0F2FE),
                        shape = CircleShape,
                        modifier = Modifier.clickable { onOpenAddLoan() }
                    ) {
                        Text(
                            text = "+",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = PrimaryBlue,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                        )
                    }
                }

                if (loans.isEmpty()) {
                    Surface(color = Color.White, shape = RoundedCornerShape(14.dp), modifier = Modifier.fillMaxWidth()) {
                        Text("No loans active. Click + button to disburse loan to Dinesh.", fontSize = 12.sp, color = TextMuted, modifier = Modifier.padding(14.dp))
                    }
                } else {
                    loans.forEach { loan ->
                        LoanCard(
                            loan = loan,
                            onOpenBorrowerProfile = onOpenBorrowerProfile,
                            onOpenRepayLoan = onOpenRepayLoan,
                            onToggleCompleted = onToggleLoanCompleted,
                            onOpenLoanAgreement = onOpenLoanAgreement
                        )
                    }
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(72.dp))
        }
    }

        // Bottom-Right Floating Action Button (+)
        FloatingActionButton(
            onClick = onOpenAddLoan,
            containerColor = PrimaryBlue,
            contentColor = Color.White,
            shape = CircleShape,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(bottom = 20.dp, end = 20.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = "Disburse New Loan",
                modifier = Modifier.size(28.dp),
                tint = Color.White
            )
        }
    }
}

@Composable
private fun LoanCard(
    loan: LoanItem,
    onOpenBorrowerProfile: (String) -> Unit,
    onOpenRepayLoan: (LoanItem) -> Unit,
    onToggleCompleted: (String) -> Unit = {},
    onOpenLoanAgreement: (LoanItem) -> Unit = {}
) {
    val context = LocalContext.current
    val score = loan.safetyScore

    Surface(
        shape = RoundedCornerShape(20.dp),
        color = Color.White,
        shadowElevation = 2.dp,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.clickable { onOpenBorrowerProfile(loan.borrowerName) }) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = loan.borrowerName,
                            fontSize = 17.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = if (loan.status == "REPAID") EmeraldGreen else NavyDark
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        if (loan.status == "REPAID") {
                            Text("✓ Repaid", fontSize = 11.sp, fontWeight = FontWeight.ExtraBold, color = EmeraldGreen)
                        } else {
                            Icon(imageVector = Icons.Default.Info, contentDescription = "Profile", tint = PrimaryBlue, modifier = Modifier.size(16.dp))
                        }
                    }
                    Text("${loan.borrowerUpiId} • Loan ID: ${loan.id}", fontSize = 11.sp, color = TextMuted, fontWeight = FontWeight.Bold)
                }

                Surface(
                    color = Color(0xFFECFDF5),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.clickable { onOpenBorrowerProfile(loan.borrowerName) }
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.VerifiedUser, null, tint = EmeraldGreen, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Score ${score.score}/100 🟢", fontSize = 11.sp, fontWeight = FontWeight.ExtraBold, color = EmeraldGreen)
                    }
                }
            }

            HorizontalDivider(color = Color(0xFFF1F5F9))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Column {
                    Text("Owed Balance", fontSize = 11.sp, color = TextMuted, fontWeight = FontWeight.Bold)
                    Text(loan.amountRemainingFormatted, fontSize = 18.sp, fontWeight = FontWeight.ExtraBold, color = PrimaryBlue)
                    Text("Principal: ${loan.principalAmountFormatted}", fontSize = 10.sp, color = TextMuted)
                }

                Column(horizontalAlignment = Alignment.End) {
                    Text("Repayment Deadline", fontSize = 11.sp, color = TextMuted, fontWeight = FontWeight.Bold)
                    Text(loan.deadlineLabel, fontSize = 14.sp, fontWeight = FontWeight.ExtraBold, color = if (loan.status == "REPAID") EmeraldGreen else Color(0xFFD97706))
                    Text("Due: ${loan.repaymentDeadline}", fontSize = 10.sp, color = TextMuted)
                }
            }

            if (loan.collateral.type.lowercase() != "none") {
                Surface(color = Color(0xFFF0F9FF), shape = RoundedCornerShape(8.dp), modifier = Modifier.fillMaxWidth()) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Security, null, tint = PrimaryBlue, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Collateral: ${loan.collateral.type}", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = NavyDark)
                        }
                        Text("Est. ${loan.collateral.estimatedValueFormatted}", fontSize = 11.sp, fontWeight = FontWeight.ExtraBold, color = PrimaryBlue)
                    }
                }
            }

            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = {
                            UpiPaymentLauncher.launchUpiPayment(context, loan.id, loan.amountRemaining, loan.borrowerName)
                        },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue),
                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 7.dp)
                    ) {
                        Icon(Icons.Default.QrCodeScanner, null, modifier = Modifier.size(15.dp), tint = Color.White)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = if (loan.amountRepaid > 0) "Request Remaining ${loan.amountRemainingFormatted}" else "Request ${loan.principalAmountFormatted} via UPI",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color.White
                        )
                    }

                    Button(
                        onClick = { onOpenRepayLoan(loan) },
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = EmeraldGreen),
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 7.dp)
                    ) {
                        Icon(Icons.Default.Payments, null, modifier = Modifier.size(15.dp), tint = Color.White)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Repay / Collect", fontSize = 11.sp, fontWeight = FontWeight.ExtraBold, color = Color.White)
                    }
                }

                OutlinedButton(
                    onClick = { onOpenBorrowerProfile(loan.borrowerName) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 7.dp)
                ) {
                    Icon(Icons.Default.AccountCircle, null, modifier = Modifier.size(15.dp), tint = PrimaryBlue)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("View ${loan.borrowerName}'s Safety Score & History", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = PrimaryBlue)
                }

                Button(
                    onClick = { onOpenLoanAgreement(loan) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1E293B)),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp)
                ) {
                    Icon(Icons.Default.Description, null, modifier = Modifier.size(16.dp), tint = Color.White)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("📄 View Digital Loan Agreement", fontSize = 12.sp, fontWeight = FontWeight.ExtraBold, color = Color.White)
                }
            }
        }
    }
}

@Composable
private fun QueueMetricCard(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    value: String,
    helper: String,
    tint: Color = NavyDark,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = RoundedCornerShape(18.dp),
        color = Color.White,
        shadowElevation = 1.dp,
        modifier = modifier.height(125.dp)
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Text(
                    text = label,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextMuted,
                    lineHeight = 13.sp,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier
                        .weight(1f)
                        .padding(end = 4.dp)
                )

                Surface(
                    color = tint.copy(alpha = 0.1f),
                    shape = CircleShape,
                    modifier = Modifier.size(26.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = icon,
                            contentDescription = null,
                            tint = tint,
                            modifier = Modifier.size(14.dp)
                        )
                    }
                }
            }

            Text(
                text = value,
                fontSize = 18.sp,
                fontWeight = FontWeight.ExtraBold,
                color = tint,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Text(
                text = helper,
                fontSize = 10.sp,
                color = TextMuted,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
private fun RecoveryQueueCard(
    item: TransactionItem,
    selected: Boolean,
    onToggleSelection: (String) -> Unit,
    onReviewTransaction: (String) -> Unit,
    onQuickExecute: (String) -> Unit,
    onOpenRepayLoan: (LoanItem) -> Unit = {},
    onOpenBorrowerProfile: (String) -> Unit = {},
    onOpenLegalNotice: (String) -> Unit = {},
    onOpenWhatsAppSimulator: (String) -> Unit = {},
    onOpenLoanAgreement: (LoanItem) -> Unit = {}
) {
    val selectable = item.policyStatus == "AUTO-ELIGIBLE"

    Surface(shape = RoundedCornerShape(16.dp), color = Color.White, shadowElevation = 1.dp, modifier = Modifier.fillMaxWidth()) {
        Row(modifier = Modifier.height(IntrinsicSize.Min)) {
            Box(
                modifier = Modifier
                    .width(4.dp)
                    .fillMaxHeight()
                    .background(PrimaryBlue)
            )

            Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        if (selectable) {
                            Checkbox(
                                checked = selected,
                                onCheckedChange = { onToggleSelection(item.id) },
                                modifier = Modifier.size(28.dp),
                                colors = CheckboxDefaults.colors(checkedColor = PrimaryBlue)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                        }
                        Column {
                            Text(item.amountFormatted, fontWeight = FontWeight.ExtraBold, fontSize = 22.sp, color = if (item.amount > 20000) AmberRiskText else NavyDark)
                            Text("${item.customerName.ifBlank { "Borrower" }} • ID: ${item.id}", fontWeight = FontWeight.Bold, fontSize = 11.sp, color = TextMuted)
                        }
                    }

                    Column(horizontalAlignment = Alignment.End) {
                        Surface(
                            color = when {
                                item.probabilityPct >= 80 -> Color(0xFFECFDF5)
                                item.probabilityPct >= 60 -> Color(0xFFFEF3C7)
                                else -> Color(0xFFFEF2F2)
                            },
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.VerifiedUser,
                                    contentDescription = null,
                                    tint = when {
                                        item.probabilityPct >= 80 -> EmeraldGreen
                                        item.probabilityPct >= 60 -> Color(0xFFD97706)
                                        else -> Color(0xFFDC2626)
                                    },
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "Score ${item.probabilityPct}/100 ${if (item.probabilityPct >= 80) "🟢" else if (item.probabilityPct >= 60) "🟡" else "🔴"}",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = when {
                                        item.probabilityPct >= 80 -> EmeraldGreen
                                        item.probabilityPct >= 60 -> Color(0xFFD97706)
                                        else -> Color(0xFFDC2626)
                                    }
                                )
                            }
                        }
                    }
                }

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    Column {
                        Text("Expected Recovery", fontSize = 11.sp, color = TextMuted, fontWeight = FontWeight.Bold)
                        Text(item.expectedRecoveryFormatted.ifBlank { item.potentialRecoveryFormatted }, fontSize = 17.sp, fontWeight = FontWeight.ExtraBold, color = PrimaryBlue)
                    }
                    Column(horizontalAlignment = Alignment.End) {
                        Text("Recovery Probability", fontSize = 11.sp, color = TextMuted, fontWeight = FontWeight.Bold)
                        Text("${item.probabilityPct}%", fontSize = 17.sp, fontWeight = FontWeight.ExtraBold, color = NavyDark)
                    }
                }

                Surface(color = Color(0xFFF8FAFC), shape = RoundedCornerShape(12.dp), modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(10.dp), verticalArrangement = Arrangement.spacedBy(5.dp)) {
                        QueueInfoLine("Payment Deadline", item.deadlineLabel.ifBlank { "Due in 15 days (${item.repaymentDeadline})" })
                        QueueInfoLine("Failure Reason", item.failureReason)
                        QueueInfoLine("Payment Method", item.paymentMethod)
                        QueueInfoLine("Gateway", item.gateway)
                        QueueInfoLine("AI Recommendation", actionWithDelay(item))
                        if (item.policyReason.isNotBlank()) {
                            Column(modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(2.dp)) {
                                Text("Policy Reason", fontSize = 11.sp, color = TextMuted)
                                Text(item.policyReason, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = NavyDark, lineHeight = 15.sp)
                            }
                        }
                    }
                }

                val context = LocalContext.current

                Column(modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = if (item.policyStatus == "AUTO-ELIGIBLE") "● Policy Approved" else "● Requires Review",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = if (item.policyStatus == "AUTO-ELIGIBLE") EmeraldGreen else TextMuted
                        )

                        Surface(
                            color = if (item.deadlineDaysRemaining < 0) Color(0xFFFEF2F2) else Color(0xFFEFF6FF),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 7.dp, vertical = 3.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Event,
                                    contentDescription = null,
                                    tint = if (item.deadlineDaysRemaining < 0) Color(0xFFDC2626) else PrimaryBlue,
                                    modifier = Modifier.size(13.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = item.deadlineLabel,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = if (item.deadlineDaysRemaining < 0) Color(0xFFDC2626) else PrimaryBlue
                                )
                            }
                        }
                    }

                    // Row 1: Primary Communication
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = {
                                UpiPaymentLauncher.launchUpiPayment(context, item.id, item.amount, item.customerName)
                            },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue),
                            contentPadding = PaddingValues(horizontal = 6.dp, vertical = 6.dp)
                        ) {
                            Icon(Icons.Default.QrCodeScanner, null, modifier = Modifier.size(14.dp), tint = Color.White)
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Request via UPI", fontSize = 11.sp, fontWeight = FontWeight.ExtraBold, color = Color.White)
                        }

                        Button(
                            onClick = { onOpenWhatsAppSimulator(item.id) },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF075E54)),
                            contentPadding = PaddingValues(horizontal = 6.dp, vertical = 6.dp)
                        ) {
                            Icon(Icons.Default.Chat, null, modifier = Modifier.size(14.dp), tint = Color.White)
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Talk via WhatsApp", fontSize = 11.sp, fontWeight = FontWeight.ExtraBold, color = Color.White)
                        }
                    }

                    // Row 2: Collection & Legal Notice
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = {
                                val loanItem = LoanItem(
                                    id = item.id,
                                    borrowerName = item.customerName,
                                    borrowerUpiId = "${item.customerName.lowercase().replace(" ", "")}@upi",
                                    principalAmount = item.amount,
                                    principalAmountFormatted = item.amountFormatted,
                                    amountRepaid = 0.0,
                                    amountRepaidFormatted = "₹0",
                                    amountRemaining = item.amount,
                                    amountRemainingFormatted = item.amountFormatted,
                                    loanGivenDate = "2026-09-02",
                                    repaymentDeadline = "2026-10-02",
                                    deadlineDaysRemaining = 30,
                                    deadlineLabel = "30 days remaining",
                                    status = "ACTIVE"
                                )
                                onOpenRepayLoan(loanItem)
                            },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = EmeraldGreen),
                            contentPadding = PaddingValues(horizontal = 6.dp, vertical = 6.dp)
                        ) {
                            Icon(Icons.Default.Payments, null, modifier = Modifier.size(14.dp), tint = Color.White)
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Repay / Collect", fontSize = 11.sp, fontWeight = FontWeight.ExtraBold, color = Color.White)
                        }

                        OutlinedButton(
                            onClick = { onOpenLegalNotice(item.id) },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFF991B1B)),
                            contentPadding = PaddingValues(horizontal = 6.dp, vertical = 6.dp)
                        ) {
                            Icon(Icons.Default.Gavel, null, modifier = Modifier.size(14.dp), tint = Color(0xFFDC2626))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("📜 Legal Notice", fontSize = 11.sp, fontWeight = FontWeight.ExtraBold, color = Color(0xFF991B1B))
                        }
                    }

                    // Row 3: AI Diagnosis & Safety Score
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedButton(
                            onClick = { onReviewTransaction(item.id) },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp),
                            contentPadding = PaddingValues(horizontal = 6.dp, vertical = 6.dp)
                        ) {
                            Icon(Icons.Default.FindInPage, null, modifier = Modifier.size(14.dp), tint = PrimaryBlue)
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("AI Diagnosis", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = PrimaryBlue)
                        }

                        OutlinedButton(
                            onClick = { onOpenBorrowerProfile(item.customerName.ifBlank { "Dinesh" }) },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp),
                            contentPadding = PaddingValues(horizontal = 6.dp, vertical = 6.dp)
                        ) {
                            Icon(Icons.Default.AccountCircle, null, modifier = Modifier.size(14.dp), tint = PrimaryBlue)
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Safety Score", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = PrimaryBlue)
                        }
                    }

                    // Row 4: Dedicated Full-Width View Digital Loan Agreement Button (ON NEXT LINE)
                    Button(
                        onClick = {
                            val loanItem = LoanItem(
                                id = item.id,
                                borrowerName = item.customerName.ifBlank { "Vikram Singh" },
                                borrowerUpiId = "${item.customerName.ifBlank { "vikram" }.lowercase().replace(" ", "")}@upi",
                                lenderName = "PayPulse Merchant (Lender)",
                                principalAmount = item.amount,
                                principalAmountFormatted = item.amountFormatted,
                                amountRemaining = item.amount,
                                amountRemainingFormatted = item.amountFormatted,
                                repaymentDeadline = item.repaymentDeadline.ifBlank { "2026-10-02" },
                                repaymentTerms = "Single lump sum payment on or before due date via UPI. 0% interest personal loan."
                            )
                            onOpenLoanAgreement(loanItem)
                        },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0F172A)),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Description,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp),
                            tint = Color.White
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("📄 View Digital Loan Agreement", fontSize = 12.sp, fontWeight = FontWeight.ExtraBold, color = Color.White)
                    }
                }
            }
        }
    }
}

@Composable
private fun BatchSelectionBar(selectedItems: List<TransactionItem>, onReviewBatch: () -> Unit, onExecuteBatch: () -> Unit, onClear: () -> Unit) {
    val expectedRecovery = selectedItems.sumOf { it.expectedRecovery }
    val autoEligible = selectedItems.count { it.policyStatus == "AUTO-ELIGIBLE" }
    val needsReview = selectedItems.count { it.policyStatus == "NEEDS REVIEW" }
    val blocked = selectedItems.count { it.policyStatus == "BLOCKED" }
    val totalAmount = selectedItems.sumOf { it.amount }

    Surface(shape = RoundedCornerShape(18.dp), color = NavyDark, shadowElevation = 4.dp, modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Column {
                    Text("${selectedItems.size} Selected", fontSize = 14.sp, fontWeight = FontWeight.ExtraBold, color = Color.White)
                    Text("${formatInr(expectedRecovery)} Expected Recovery", fontSize = 12.sp, color = Color(0xFFBAE6FD))
                }
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    OutlinedButton(onClick = onClear, contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp)) {
                        Text("Clear", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.White)
                    }
                    Button(onClick = onExecuteBatch, colors = ButtonDefaults.buttonColors(containerColor = EmeraldGreen), contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp)) {
                        Text("Recover (${selectedItems.size})", fontSize = 11.sp, fontWeight = FontWeight.ExtraBold)
                    }
                }
            }
            Text(
                "Total ${formatInr(totalAmount)} • $autoEligible auto-eligible • $needsReview review • $blocked blocked",
                fontSize = 11.sp,
                color = Color.White.copy(alpha = 0.78f)
            )
        }
    }
}

@Composable
private fun QueueInfoLine(label: String, value: String) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(label, fontSize = 11.sp, color = TextMuted)
        Text(value, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = NavyDark)
    }
}

@Composable
private fun DetailRow(label: String, value: String) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(label, fontSize = 12.sp, color = TextMuted)
        Text(value, fontSize = 12.sp, fontWeight = FontWeight.ExtraBold, color = NavyDark)
    }
}

@Composable
private fun QueueEmptyState(title: String, message: String, onRetry: (() -> Unit)?) {
    Surface(shape = RoundedCornerShape(18.dp), color = Color.White, shadowElevation = 1.dp, modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier.padding(22.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Icon(imageVector = Icons.Default.SearchOff, contentDescription = null, tint = TextMuted, modifier = Modifier.size(28.dp))
            Text(title, fontSize = 15.sp, fontWeight = FontWeight.ExtraBold, color = NavyDark)
            Text(message, fontSize = 12.sp, color = TextMuted)
            if (onRetry != null) {
                OutlinedButton(onClick = onRetry, shape = RoundedCornerShape(12.dp)) {
                    Text("Reset Filters", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

private data class FilterOption(val key: String, val label: String)

private val filterOptions = listOf(
    FilterOption("all", "All"),
    FilterOption("recoverable", "Recoverable"),
    FilterOption("auto_eligible", "Auto-Eligible"),
    FilterOption("needs_review", "Needs Review"),
    FilterOption("blocked", "Blocked"),
    FilterOption("recovered", "Recovered"),
    FilterOption("upi_timeout", "UPI Timeout"),
    FilterOption("gateway_timeout", "Gateway Failure"),
    FilterOption("checkout_abandonment", "Checkout Abandonment"),
    FilterOption("mandate_failed", "Mandate Failure"),
    FilterOption("insufficient_funds", "Insufficient Funds"),
    FilterOption("bank_decline", "Bank Decline"),
    FilterOption("upi", "UPI"),
    FilterOption("card", "Card"),
    FilterOption("netbanking", "NetBanking"),
    FilterOption("mandate", "Mandate"),
    FilterOption("wallet", "Wallet"),
    FilterOption("high", "High"),
    FilterOption("medium", "Medium"),
    FilterOption("low", "Low")
)

private val sortOptions = listOf(
    FilterOption("expected_recovery", "Expected Recovery"),
    FilterOption("probability", "Recovery Probability"),
    FilterOption("amount", "Transaction Amount"),
    FilterOption("failure_time", "Failure Time"),
    FilterOption("priority", "Priority")
)

private fun TransactionItem.matchesSearch(query: String): Boolean {
    if (query.isBlank()) return true
    return id.contains(query, ignoreCase = true) ||
        customerName.contains(query, ignoreCase = true) ||
        failureReason.contains(query, ignoreCase = true) ||
        paymentMethod.contains(query, ignoreCase = true) ||
        gateway.contains(query, ignoreCase = true) ||
        recommendedStrategy.contains(query, ignoreCase = true)
}

private fun TransactionItem.matchesFilter(filter: String): Boolean {
    return when (filter) {
        "all" -> true
        "recoverable" -> expectedRecovery > 0.0 && policyStatus != "BLOCKED"
        "auto_eligible" -> policyStatus == "AUTO-ELIGIBLE"
        "needs_review" -> policyStatus == "NEEDS REVIEW"
        "blocked" -> policyStatus == "BLOCKED"
        "recovered" -> statusLabel.equals("Recovered", ignoreCase = true)
        "payment_failures" -> category == "payment_failures" || category == "upi_degradation"
        "subscriptions" -> category == "subscriptions"
        "checkout_abandonment" -> category == "checkout_abandonment" || failureReason.contains("Checkout", ignoreCase = true)
        "upi_timeout" -> failureReason.contains("UPI", ignoreCase = true) && failureReason.contains("Timeout", ignoreCase = true)
        "gateway_timeout" -> failureReason.contains("Gateway", ignoreCase = true)
        "mandate_failed" -> failureReason.contains("Mandate", ignoreCase = true) || paymentMethod.equals("Mandate", ignoreCase = true)
        "insufficient_funds" -> failureReason.contains("Insufficient", ignoreCase = true)
        "bank_decline" -> failureReason.contains("Bank Decline", ignoreCase = true)
        "upi", "card", "netbanking", "mandate", "wallet" -> paymentMethod.equals(filter, ignoreCase = true)
        "high", "medium", "low" -> priority.equals(filter, ignoreCase = true)
        else -> true
    }
}

private fun sortLabel(sortMode: String): String {
    return "Sort: " + (sortOptions.firstOrNull { it.key == sortMode }?.label ?: "Expected Recovery")
}

private fun priorityRank(priority: String): Int {
    return when (priority) {
        "HIGH" -> 0
        "MEDIUM" -> 1
        else -> 2
    }
}

private fun priorityBg(priority: String): Color {
    return when (priority) {
        "HIGH" -> Color(0xFFFEF3C7)
        "LOW" -> Color(0xFFF1F5F9)
        else -> Color(0xFFE0F2FE)
    }
}

private fun priorityText(priority: String): Color {
    return when (priority) {
        "HIGH" -> Color(0xFFB45309)
        "LOW" -> TextMuted
        else -> PrimaryBlue
    }
}

private fun actionWithDelay(item: TransactionItem): String {
    return if (item.actionDelay.isBlank()) item.recommendedStrategy else "${item.recommendedStrategy} • ${item.actionDelay}"
}

private fun formatInr(amount: Double): String {
    return when {
        amount >= 100000 -> "₹%.1fL".format(amount / 100000)
        amount >= 1000 -> "₹%,.0f".format(amount)
        else -> "₹%.0f".format(amount)
    }
}
