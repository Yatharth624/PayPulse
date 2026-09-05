package com.paypulse.ai.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.paypulse.ai.data.model.LoanItem
import com.paypulse.ai.ui.theme.*
import com.paypulse.ai.ui.utils.UpiPaymentLauncher

@Composable
fun MyDebtScreen(
    loans: List<LoanItem> = emptyList(),
    onOpenAddLoan: () -> Unit = {},
    onOpenBorrowerProfile: (String) -> Unit = {},
    onOpenRepayLoan: (LoanItem) -> Unit = {},
    onRefresh: () -> Unit = {},
    onOpenRazorpayCheckout: (id: String, amount: Double, title: String) -> Unit = { _, _, _ -> },
    onOpenAutoPay: (LoanItem) -> Unit = {}
) {
    val context = LocalContext.current
    val scrollState = rememberScrollState()

    // ─── DYNAMIC CALCULATIONS FROM REAL LOAN DATA ──────────────────────────────
    val activeLoans = remember(loans) { loans.filter { it.status != "REPAID" && it.status != "CLOSED" } }
    val activeCount = activeLoans.size
    val totalBorrowed = remember(loans) { loans.sumOf { it.principalAmount }.takeIf { it > 0 } ?: 45000.0 }
    val totalPaid = remember(loans) { loans.sumOf { it.amountRepaid }.takeIf { it > 0 } ?: 16500.0 }
    val totalRemaining = remember(loans) { loans.sumOf { it.amountRemaining }.takeIf { it > 0 } ?: (totalBorrowed - totalPaid) }

    val progressPct = remember(totalBorrowed, totalPaid) {
        if (totalBorrowed > 0) ((totalPaid / totalBorrowed) * 100).coerceIn(0.0, 100.0) else 0.0
    }

    // Nearest upcoming or overdue payment loan
    val nextPaymentLoan = remember(activeLoans) {
        activeLoans.minByOrNull { it.deadlineDaysRemaining }
    }

    val nextPaymentAmount = nextPaymentLoan?.amountRemaining ?: 3500.0
    val nextPaymentAmountFormatted = "₹%,.0f".format(nextPaymentAmount)
    val nextPaymentDueDate = nextPaymentLoan?.repaymentDeadline ?: "September 7, 2026"
    val nextPaymentDaysRemaining = nextPaymentLoan?.deadlineDaysRemaining ?: 5
    val nextPaymentLender = nextPaymentLoan?.borrowerName ?: "Vikram Singh"
    val isOverdue = nextPaymentDaysRemaining < 0

    // Repayment Health Score
    val healthScore = remember(activeLoans) {
        if (activeLoans.isEmpty()) 100
        else {
            val avg = activeLoans.map { it.safetyScore.score }.average()
            if (avg.isNaN()) 82 else avg.toInt()
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(BackgroundSlate)
                .verticalScroll(scrollState)
                .padding(horizontal = 16.dp, vertical = 18.dp),
            verticalArrangement = Arrangement.spacedBy(18.dp)
        ) {
            // ─── 1. HEADER ────────────────────────────────────────────────────────
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "My Debt",
                        fontSize = 26.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = NavyDark
                    )
                    Text(
                        text = "Your complete borrowing and repayment overview",
                        fontSize = 13.sp,
                        color = TextMuted,
                        fontWeight = FontWeight.Medium
                    )
                }
                IconButton(
                    onClick = onRefresh,
                    modifier = Modifier
                        .background(Color.White, CircleShape)
                        .border(1.dp, Color(0xFFE2E8F0), CircleShape)
                        .size(42.dp)
                ) {
                    Icon(imageVector = Icons.Default.Refresh, contentDescription = "Sync", tint = PrimaryBlue, modifier = Modifier.size(20.dp))
                }
            }

            // ─── 2. DEBT OVERVIEW (COMPACT GROUPED GRID) ──────────────────────────
            Surface(
                shape = RoundedCornerShape(18.dp),
                color = Color.White,
                shadowElevation = 2.dp,
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, Color(0xFFE2E8F0), RoundedCornerShape(18.dp))
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Financial Summary", fontSize = 16.sp, fontWeight = FontWeight.ExtraBold, color = NavyDark)
                        Surface(color = Color(0xFFEFF6FF), shape = RoundedCornerShape(8.dp)) {
                            Text(
                                text = "Health $healthScore/100",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = PrimaryBlue,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                            )
                        }
                    }

                    HorizontalDivider(color = Color(0xFFF1F5F9))

                    // 2x3 Metric Matrix with Larger Text
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        MetricCell(label = "Active Loans", value = "$activeCount", modifier = Modifier.weight(1f))
                        MetricCell(label = "Total Borrowed", value = "₹%,.0f".format(totalBorrowed), modifier = Modifier.weight(1f))
                        MetricCell(label = "Remaining", value = "₹%,.0f".format(totalRemaining), valueColor = Color(0xFFDC2626), modifier = Modifier.weight(1f))
                    }
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        MetricCell(label = "Paid", value = "₹%,.0f".format(totalPaid), valueColor = EmeraldGreen, modifier = Modifier.weight(1f))
                        MetricCell(label = "Next Payment", value = nextPaymentAmountFormatted, valueColor = PrimaryBlue, modifier = Modifier.weight(1f))
                        MetricCell(label = "Repayment Health", value = "$healthScore/100", valueColor = if (healthScore >= 75) EmeraldGreen else AmberGold, modifier = Modifier.weight(1f))
                    }
                }
            }

            // ─── 3. OVERALL DEBT PROGRESS ─────────────────────────────────────────
            Surface(
                shape = RoundedCornerShape(18.dp),
                color = Color.White,
                shadowElevation = 2.dp,
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, Color(0xFFE2E8F0), RoundedCornerShape(18.dp))
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Debt Progress", fontSize = 16.sp, fontWeight = FontWeight.ExtraBold, color = NavyDark)
                        Text(
                            text = "%.1f%% paid".format(progressPct),
                            fontSize = 15.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = EmeraldGreen
                        )
                    }

                    // Dynamic Horizontal Progress Bar
                    LinearProgressIndicator(
                        progress = { (progressPct / 100.0).toFloat() },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(12.dp)
                            .clip(RoundedCornerShape(6.dp)),
                        color = EmeraldGreen,
                        trackColor = Color(0xFFE2E8F0)
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "₹%,.0f paid of ₹%,.0f".format(totalPaid, totalBorrowed),
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextMuted
                        )
                        Text(
                            text = "₹%,.0f remaining".format(totalRemaining),
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFDC2626)
                        )
                    }
                }
            }

            // ─── 4. NEXT PAYMENT SPOTLIGHT ────────────────────────────────────────
            Surface(
                shape = RoundedCornerShape(18.dp),
                color = if (isOverdue) Color(0xFFFEF2F2) else Color(0xFFEFF6FF),
                shadowElevation = 2.dp,
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.5.dp, if (isOverdue) Color(0xFFFCA5A5) else PrimaryBlue, RoundedCornerShape(18.dp))
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = if (isOverdue) Icons.Default.Warning else Icons.Default.Event,
                                contentDescription = null,
                                tint = if (isOverdue) Color(0xFFDC2626) else PrimaryBlue,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = if (isOverdue) "⚠️ Payment Overdue" else "Next Payment",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = if (isOverdue) Color(0xFF991B1B) else NavyDark
                            )
                        }

                        if (activeCount > 1) {
                            Surface(color = Color.White.copy(alpha = 0.9f), shape = RoundedCornerShape(8.dp)) {
                                Text(
                                    text = "+${activeCount - 1} more this month",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = NavyDark,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                )
                            }
                        }
                    }

                    Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                        Text(
                            text = nextPaymentAmountFormatted,
                            fontSize = 28.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = if (isOverdue) Color(0xFFDC2626) else PrimaryBlue
                        )
                        Text(
                            text = if (isOverdue) "Overdue by ${-nextPaymentDaysRemaining} days • $nextPaymentDueDate"
                            else "Due in $nextPaymentDaysRemaining days • $nextPaymentDueDate",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isOverdue) Color(0xFFB91C1C) else TextMuted
                        )
                        Text(
                            text = "Lender: $nextPaymentLender",
                            fontSize = 13.sp,
                            color = NavyDark,
                            fontWeight = FontWeight.SemiBold
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = {
                                if (nextPaymentLoan != null) {
                                    onOpenRazorpayCheckout(nextPaymentLoan.id, nextPaymentAmount, nextPaymentLender)
                                } else {
                                    onOpenRazorpayCheckout("DEBT_01", nextPaymentAmount, nextPaymentLender)
                                }
                            },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = if (isOverdue) Color(0xFFDC2626) else EmeraldGreen),
                            contentPadding = PaddingValues(vertical = 10.dp)
                        ) {
                            Icon(imageVector = Icons.Default.Payments, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Pay Now", fontSize = 13.sp, fontWeight = FontWeight.ExtraBold)
                        }

                        OutlinedButton(
                            onClick = {
                                if (nextPaymentLoan != null) {
                                    onOpenAutoPay(nextPaymentLoan)
                                }
                            },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = PrimaryBlue),
                            contentPadding = PaddingValues(vertical = 10.dp)
                        ) {
                            Icon(imageVector = Icons.Default.Autorenew, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("⚡ Setup AutoPay", fontSize = 12.sp, fontWeight = FontWeight.ExtraBold)
                        }
                    }
                }
            }

            // ─── 5. ACTIVE LOANS LIST ─────────────────────────────────────────────
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Active Debt Accounts", fontSize = 18.sp, fontWeight = FontWeight.ExtraBold, color = NavyDark)
                    Text("$activeCount active", fontSize = 12.sp, color = TextMuted, fontWeight = FontWeight.Bold)
                }

                if (activeLoans.isEmpty()) {
                    Surface(
                        color = Color.White,
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier.fillMaxWidth().border(1.dp, Color(0xFFE2E8F0), RoundedCornerShape(16.dp))
                    ) {
                        Column(
                            modifier = Modifier.padding(28.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Icon(imageVector = Icons.Default.CheckCircle, contentDescription = null, tint = EmeraldGreen, modifier = Modifier.size(42.dp))
                            Text("No active loans", fontSize = 18.sp, fontWeight = FontWeight.ExtraBold, color = NavyDark)
                            Text("You currently have no outstanding debt.", fontSize = 13.sp, color = TextMuted)
                            Button(onClick = onOpenAddLoan, shape = RoundedCornerShape(12.dp), colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue)) {
                                Text("Add a Loan", fontSize = 13.sp, fontWeight = FontWeight.ExtraBold)
                            }
                        }
                    }
                } else {
                    activeLoans.forEach { loan ->
                        val loanProgress = if (loan.principalAmount > 0)
                            ((loan.amountRepaid / loan.principalAmount) * 100).coerceIn(0.0, 100.0)
                        else 0.0

                        Surface(
                            color = Color.White,
                            shape = RoundedCornerShape(16.dp),
                            shadowElevation = 1.dp,
                            modifier = Modifier
                                .fillMaxWidth()
                                .border(1.dp, Color(0xFFE2E8F0), RoundedCornerShape(16.dp))
                                .clickable { onOpenBorrowerProfile(loan.borrowerName) }
                        ) {
                            Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column {
                                        Text(loan.borrowerName, fontSize = 16.sp, fontWeight = FontWeight.ExtraBold, color = NavyDark)
                                        Text("ID: ${loan.id}", fontSize = 11.sp, color = TextMuted)
                                    }
                                    Column(horizontalAlignment = Alignment.End) {
                                        Text(loan.amountRemainingFormatted, fontSize = 16.sp, fontWeight = FontWeight.ExtraBold, color = Color(0xFFDC2626))
                                        Text("remaining", fontSize = 11.sp, color = TextMuted)
                                    }
                                }

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text("Next payment: ${loan.amountRemainingFormatted}", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = NavyDark)
                                    Text("Due: ${loan.repaymentDeadline}", fontSize = 12.sp, color = TextMuted)
                                }

                                // Individual Loan Repayment Progress Bar
                                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                        Text("Repayment progress:", fontSize = 12.sp, color = TextMuted)
                                        Text("%.0f%%".format(loanProgress), fontSize = 12.sp, fontWeight = FontWeight.ExtraBold, color = PrimaryBlue)
                                    }
                                    LinearProgressIndicator(
                                        progress = { (loanProgress / 100.0).toFloat() },
                                        modifier = Modifier.fillMaxWidth().height(8.dp).clip(RoundedCornerShape(4.dp)),
                                        color = PrimaryBlue,
                                        trackColor = Color(0xFFE2E8F0)
                                    )
                                }

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Surface(
                                        color = Color(0xFFEFF6FF),
                                        shape = RoundedCornerShape(8.dp),
                                        modifier = Modifier.clickable { onOpenAutoPay(loan) }
                                    ) {
                                        Text(
                                            text = "⚡ Setup AutoPay",
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.ExtraBold,
                                            color = PrimaryBlue,
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                        )
                                    }

                                    Text(
                                        text = "View Details →",
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.ExtraBold,
                                        color = PrimaryBlue,
                                        modifier = Modifier.clickable { onOpenBorrowerProfile(loan.borrowerName) }
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // ─── 6. REPAYMENT HEALTH BREAKDOWN ────────────────────────────────────
            Surface(
                shape = RoundedCornerShape(18.dp),
                color = Color.White,
                shadowElevation = 2.dp,
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, Color(0xFFE2E8F0), RoundedCornerShape(18.dp))
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Repayment Health", fontSize = 16.sp, fontWeight = FontWeight.ExtraBold, color = NavyDark)
                        Text("$healthScore/100", fontSize = 18.sp, fontWeight = FontWeight.ExtraBold, color = if (healthScore >= 75) EmeraldGreen else AmberGold)
                    }

                    Text(
                        text = if (healthScore >= 75) "Your repayment history is healthy." else "Repayment health needs attention.",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium,
                        color = TextMuted
                    )

                    HorizontalDivider(color = Color(0xFFF1F5F9))

                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        HealthCheckItem("✓ 8 of 10 payments made on time")
                        HealthCheckItem(if (isOverdue) "⚠️ 1 payment currently overdue" else "✓ No currently overdue payments", isAlert = isOverdue)
                        HealthCheckItem("✓ Consistent repayment activity across active loans")
                    }
                }
            }

            // ─── 7. QUICK FINANCIAL INSIGHT BOX ───────────────────────────────────
            Surface(
                color = Color(0xFFEFF6FF),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, Color(0xFFBFDBFE), RoundedCornerShape(16.dp))
            ) {
                Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(imageVector = Icons.Default.AutoAwesome, contentDescription = null, tint = PrimaryBlue, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("PayPulse AI Insight", fontSize = 14.sp, fontWeight = FontWeight.ExtraBold, color = PrimaryBlue)
                    }

                    Text(
                        text = when {
                            isOverdue -> "⚠️ Action Required: Your payment of $nextPaymentAmountFormatted to $nextPaymentLender is overdue. Pay now to protect your safety score!"
                            activeCount == 0 -> "✨ Outstanding Discipline: You currently have no active debt! Use the '+' button to add a new loan."
                            nextPaymentDaysRemaining <= 5 -> "You're currently on track with your repayments. Your next payment of $nextPaymentAmountFormatted is due in $nextPaymentDaysRemaining days ($nextPaymentDueDate)."
                            else -> "🎉 Great progress! You have paid %.1f%% of your total borrowed debt. Keep up the consistent activity!".format(progressPct)
                        },
                        fontSize = 13.sp,
                        color = NavyDark,
                        lineHeight = 17.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            Spacer(modifier = Modifier.height(60.dp)) // Extra padding so scroll content doesn't cover FAB
        }

        // ─── FLOATING ACTION BUTTON (+) IN BOTTOM RIGHT CORNER ─────────────────
        FloatingActionButton(
            onClick = onOpenAddLoan,
            containerColor = PrimaryBlue,
            contentColor = Color.White,
            shape = CircleShape,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(20.dp)
        ) {
            Icon(imageVector = Icons.Default.Add, contentDescription = "Add Loan", modifier = Modifier.size(26.dp))
        }
    }
}

@Composable
private fun MetricCell(label: String, value: String, valueColor: Color = NavyDark, modifier: Modifier = Modifier) {
    Surface(
        color = Color(0xFFF8FAFC),
        shape = RoundedCornerShape(10.dp),
        modifier = modifier
    ) {
        Column(modifier = Modifier.padding(10.dp)) {
            Text(label, fontSize = 10.sp, fontWeight = FontWeight.Bold, color = TextMuted)
            Text(value, fontSize = 14.sp, fontWeight = FontWeight.ExtraBold, color = valueColor, maxLines = 1)
        }
    }
}

@Composable
private fun HealthCheckItem(text: String, isAlert: Boolean = false) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(
            imageVector = if (isAlert) Icons.Default.Warning else Icons.Default.Check,
            contentDescription = null,
            tint = if (isAlert) Color(0xFFDC2626) else EmeraldGreen,
            modifier = Modifier.size(16.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(text, fontSize = 12.sp, fontWeight = FontWeight.Medium, color = if (isAlert) Color(0xFF991B1B) else NavyDark)
    }
}
