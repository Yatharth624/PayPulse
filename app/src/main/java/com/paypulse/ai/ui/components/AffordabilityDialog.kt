package com.paypulse.ai.ui.components

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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.paypulse.ai.data.model.LoanItem
import com.paypulse.ai.ui.theme.*
import com.paypulse.ai.ui.utils.AffordabilityEngine
import com.paypulse.ai.ui.utils.AffordabilityInput

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AffordabilityDialog(
    initialLoanAmount: Double = 30000.0,
    initialDurationMonths: Int = 6,
    existingLoans: List<LoanItem> = emptyList(),
    onDismiss: () -> Unit,
    onApplyAssessment: (score: Int, resultSummary: String) -> Unit = { _, _ -> }
) {
    // Dynamic calculation from real active loans
    val activeLoansObligations = remember(existingLoans) {
        val active = existingLoans.filter { it.status != "REPAID" && it.status != "CLOSED" }
        if (active.isNotEmpty()) {
            active.sumOf { it.amountRemaining / 6.0 }
        } else 12000.0
    }

    var incomeText by remember { mutableStateOf("40000") }
    var expensesText by remember { mutableStateOf("17000") }
    var existingEmisText by remember { mutableStateOf("%.0f".format(activeLoansObligations)) }

    var loanAmount by remember { mutableStateOf(initialLoanAmount) }
    var selectedDurationMonths by remember { mutableStateOf(initialDurationMonths) }

    val monthlyIncome = incomeText.toDoubleOrNull() ?: 0.0
    val essentialExpenses = expensesText.toDoubleOrNull() ?: 0.0
    val existingObligations = existingEmisText.toDoubleOrNull() ?: 0.0

    // Dynamic Affordability Score Calculation
    val affordabilityResult = remember(monthlyIncome, essentialExpenses, existingObligations, loanAmount, selectedDurationMonths) {
        AffordabilityEngine.calculateAffordability(
            AffordabilityInput(
                monthlyIncome = monthlyIncome,
                essentialExpenses = essentialExpenses,
                existingObligations = existingObligations,
                loanAmount = loanAmount,
                durationMonths = selectedDurationMonths
            )
        )
    }

    val themeColor = Color(android.graphics.Color.parseColor(affordabilityResult.tierColorHex))

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            shape = RoundedCornerShape(24.dp),
            color = Color.White,
            shadowElevation = 12.dp,
            modifier = Modifier
                .fillMaxWidth(0.94f)
                .padding(vertical = 12.dp)
        ) {
            Column(modifier = Modifier.fillMaxWidth()) {
                
                // ─── 1. FIXED TOP HEADER WITH ENLARGED CLOSE CROSS ───────────────────
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            color = Color(0xFFEFF6FF),
                            shape = RoundedCornerShape(14.dp),
                            modifier = Modifier.size(44.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Default.Calculate,
                                    contentDescription = null,
                                    tint = PrimaryBlue,
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "Affordability Check",
                                fontSize = 22.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = NavyDark
                            )
                            Text(
                                text = "Test loan terms to see cash flow impact & score",
                                fontSize = 12.sp,
                                color = TextMuted
                            )
                        }
                    }

                    // ENLARGED CROSS ICON (28dp size)
                    IconButton(onClick = onDismiss) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close",
                            tint = TextMuted,
                            modifier = Modifier.size(28.dp)
                        )
                    }
                }

                HorizontalDivider(color = Color(0xFFF1F5F9))

                // ─── 2. SCROLLABLE FORM BODY ─────────────────────────────────────────
                Column(
                    modifier = Modifier
                        .weight(1f, fill = false)
                        .verticalScroll(rememberScrollState())
                        .padding(horizontal = 20.dp, vertical = 14.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // SECTION 1: FINANCIAL INPUTS
                    Text("1. Monthly Cash Flow Inputs", fontSize = 14.sp, fontWeight = FontWeight.ExtraBold, color = PrimaryBlue)

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        OutlinedTextField(
                            value = incomeText,
                            onValueChange = { incomeText = it },
                            label = { Text("Monthly Income") },
                            leadingIcon = { Icon(Icons.Default.CurrencyRupee, null, tint = EmeraldGreen) },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp),
                            singleLine = true
                        )

                        OutlinedTextField(
                            value = expensesText,
                            onValueChange = { expensesText = it },
                            label = { Text("Essential Expenses") },
                            leadingIcon = { Icon(Icons.Default.ShoppingCart, null, tint = AmberGold) },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp),
                            singleLine = true
                        )
                    }

                    OutlinedTextField(
                        value = existingEmisText,
                        onValueChange = { existingEmisText = it },
                        label = { Text("Current Loan/EMI Obligations (₹/mo)") },
                        leadingIcon = { Icon(Icons.Default.CreditScore, null, tint = PrimaryBlue) },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        singleLine = true
                    )

                    HorizontalDivider(color = Color(0xFFF1F5F9))

                    // SECTION 2: INTERACTIVE LOAN ADJUSTMENT SLIDERS
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("Adjust Loan Amount:", fontSize = 14.sp, fontWeight = FontWeight.ExtraBold, color = NavyDark)
                            Text("₹%,.0f".format(loanAmount), fontSize = 16.sp, fontWeight = FontWeight.ExtraBold, color = PrimaryBlue)
                        }

                        Slider(
                            value = loanAmount.toFloat(),
                            onValueChange = { loanAmount = (it / 1000).toInt() * 1000.0 },
                            valueRange = 5000f..150000f,
                            steps = 28,
                            colors = SliderDefaults.colors(thumbColor = PrimaryBlue, activeTrackColor = PrimaryBlue)
                        )

                        Text("Repayment Duration:", fontSize = 14.sp, fontWeight = FontWeight.ExtraBold, color = NavyDark)

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            listOf(1, 3, 6, 12, 18, 24).forEach { months ->
                                val isSelected = selectedDurationMonths == months
                                Surface(
                                    color = if (isSelected) PrimaryBlue else Color(0xFFF1F5F9),
                                    shape = RoundedCornerShape(10.dp),
                                    modifier = Modifier
                                        .weight(1f)
                                        .clickable { selectedDurationMonths = months }
                                ) {
                                    Box(
                                        modifier = Modifier.padding(vertical = 8.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = "${months}m",
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.ExtraBold,
                                            color = if (isSelected) Color.White else NavyDark
                                        )
                                    }
                                }
                            }
                        }
                    }

                    // SECTION 3: DYNAMIC AFFORDABILITY SCORE & CASH FLOW BREAKDOWN
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = Color.White,
                        shadowElevation = 2.dp,
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(1.5.dp, themeColor, RoundedCornerShape(16.dp))
                    ) {
                        Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                            // SCORE & STATUS BADGE ON DEDICATED NEXT LINE
                            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                Text("Affordability Score", fontSize = 13.sp, color = TextMuted, fontWeight = FontWeight.Bold)
                                Text(
                                    text = "${affordabilityResult.score} / 100",
                                    fontSize = 26.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = themeColor
                                )

                                // Dedicated line for status badge ("Proceed with caution 🟡")
                                Surface(color = themeColor.copy(alpha = 0.15f), shape = RoundedCornerShape(8.dp)) {
                                    Text(
                                        text = affordabilityResult.tierBadge,
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.ExtraBold,
                                        color = themeColor,
                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                                    )
                                }
                            }

                            HorizontalDivider(color = Color(0xFFF1F5F9))

                            // Breakdown Metrics
                            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                DataRow(label = "Monthly Income", value = "₹%,.0f".format(affordabilityResult.monthlyIncome))
                                DataRow(label = "Essential Expenses", value = "₹%,.0f".format(affordabilityResult.essentialExpenses))
                                DataRow(label = "Current Loan Obligations", value = "₹%,.0f".format(affordabilityResult.existingObligations))
                                DataRow(
                                    label = "New Loan Repayment (${selectedDurationMonths}m)",
                                    value = "₹%,.0f / mo".format(affordabilityResult.estimatedMonthlyRepayment),
                                    valueColor = PrimaryBlue,
                                    isBold = true
                                )
                                
                                // STACKED DEDICATED NEXT LINE FOR DISPOSABLE INCOME
                                DataRowStacked(
                                    label = "Estimated Disposable Income After New Loan",
                                    value = "₹%,.0f".format(affordabilityResult.disposableIncomeAfterLoan),
                                    valueColor = if (affordabilityResult.disposableIncomeAfterLoan >= 0) EmeraldGreen else Color(0xFFDC2626)
                                )
                            }
                        }
                    }

                    // SECTION 4: WHY THIS SCORE?
                    Surface(
                        shape = RoundedCornerShape(14.dp),
                        color = Color(0xFFF8FAFC),
                        modifier = Modifier.fillMaxWidth().border(1.dp, Color(0xFFE2E8F0), RoundedCornerShape(14.dp))
                    ) {
                        Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            Text("Why this score?", fontSize = 13.sp, fontWeight = FontWeight.ExtraBold, color = NavyDark)
                            affordabilityResult.whyScoreFactors.forEach { factor ->
                                Text(factor, fontSize = 12.sp, color = NavyDark, fontWeight = FontWeight.Medium)
                            }
                        }
                    }

                    // SECTION 5: HOW TO IMPROVE
                    Surface(
                        shape = RoundedCornerShape(14.dp),
                        color = Color(0xFFFEF3C7),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.TrendingUp, null, tint = Color(0xFFD97706), modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("How can I improve my affordability?", fontSize = 12.sp, fontWeight = FontWeight.ExtraBold, color = Color(0xFFB45309))
                            }
                            affordabilityResult.improvementTips.forEach { tip ->
                                Text("• $tip", fontSize = 11.sp, color = Color(0xFF78350F), fontWeight = FontWeight.Medium)
                            }
                        }
                    }

                    // SECTION 6: AI TIP BOX
                    Surface(
                        color = Color(0xFFEFF6FF),
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier.fillMaxWidth().border(1.dp, Color(0xFFBFDBFE), RoundedCornerShape(14.dp))
                    ) {
                        Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.Top) {
                            Icon(Icons.Default.Lightbulb, null, tint = PrimaryBlue, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "💡 Tip: Increasing the repayment period lowers your estimated monthly payment and improves affordability score.",
                                fontSize = 11.sp,
                                color = NavyDark,
                                lineHeight = 15.sp
                            )
                        }
                    }

                    // SECTION 7: RECOMMENDATION BOX
                    Surface(
                        color = themeColor.copy(alpha = 0.12f),
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Text(
                                text = "${affordabilityResult.recommendationIcon} ${affordabilityResult.recommendationTitle}",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = themeColor
                            )
                            Text(
                                text = affordabilityResult.summaryRecommendation,
                                fontSize = 11.sp,
                                color = NavyDark,
                                lineHeight = 15.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }

                HorizontalDivider(color = Color(0xFFE2E8F0))

                // ─── 3. FIXED BOTTOM ACTION BAR ──────────────────────────────────────
                Surface(
                    color = Color(0xFFF8FAFC),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Box(modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)) {
                        Button(
                            onClick = {
                                onApplyAssessment(affordabilityResult.score, affordabilityResult.summaryRecommendation)
                                onDismiss()
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(54.dp),
                            shape = RoundedCornerShape(16.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue),
                            elevation = ButtonDefaults.buttonElevation(defaultElevation = 6.dp)
                        ) {
                            Icon(Icons.Default.Check, null, tint = Color.White)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Apply Score (${affordabilityResult.score}/100)",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = Color.White
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun DataRow(label: String, value: String, valueColor: Color = NavyDark, isBold: Boolean = false) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(label, fontSize = 12.sp, color = TextMuted, fontWeight = if (isBold) FontWeight.Bold else FontWeight.Normal)
        Text(value, fontSize = 12.sp, fontWeight = if (isBold) FontWeight.ExtraBold else FontWeight.SemiBold, color = valueColor)
    }
}

@Composable
private fun DataRowStacked(label: String, value: String, valueColor: Color = NavyDark) {
    Column(
        modifier = Modifier.fillMaxWidth().padding(top = 2.dp),
        verticalArrangement = Arrangement.spacedBy(2.dp)
    ) {
        Text(label, fontSize = 12.sp, color = TextMuted, fontWeight = FontWeight.Bold)
        Text(value, fontSize = 16.sp, fontWeight = FontWeight.ExtraBold, color = valueColor)
    }
}
