package com.paypulse.ai.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.paypulse.ai.data.model.BorrowerProfile
import com.paypulse.ai.ui.theme.*

@Composable
fun BorrowerProfileDialog(
    profile: BorrowerProfile,
    onDismiss: () -> Unit
) {
    val score = profile.safetyScore

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            shape = RoundedCornerShape(24.dp),
            color = Color.White,
            shadowElevation = 8.dp,
            modifier = Modifier
                .fillMaxWidth(0.94f)
                .padding(vertical = 16.dp)
        ) {
            Column(
                modifier = Modifier
                    .padding(20.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // Header & Close
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(color = Color(0xFFE0F2FE), shape = CircleShape, modifier = Modifier.size(42.dp)) {
                            Box(contentAlignment = Alignment.Center) {
                                Text(profile.name.take(1).uppercase(), fontSize = 18.sp, fontWeight = FontWeight.ExtraBold, color = PrimaryBlue)
                            }
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(profile.name, fontSize = 18.sp, fontWeight = FontWeight.ExtraBold, color = NavyDark)
                            Text(profile.upiId, fontSize = 12.sp, color = TextMuted, fontWeight = FontWeight.Bold)
                        }
                    }

                    IconButton(onClick = onDismiss) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = "Close", tint = TextMuted)
                    }
                }

                HorizontalDivider(color = Color(0xFFF1F5F9))

                // Safety Score Badge Box (0 - 100)
                Surface(
                    color = Color(0xFFECFDF5),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.5.dp, EmeraldGreen, RoundedCornerShape(16.dp))
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text("PayPulse Borrower Safety Score", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = TextMuted)
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text("${score.score}", fontSize = 28.sp, fontWeight = FontWeight.ExtraBold, color = EmeraldGreen)
                                Text(" / 100", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = TextMuted)
                                Spacer(modifier = Modifier.width(8.dp))
                                Surface(color = EmeraldGreen, shape = RoundedCornerShape(8.dp)) {
                                    Text(score.tierLabel.uppercase(), fontSize = 10.sp, fontWeight = FontWeight.ExtraBold, color = Color.White, modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp))
                                }
                            }
                        }

                        Icon(imageVector = Icons.Default.VerifiedUser, contentDescription = null, tint = EmeraldGreen, modifier = Modifier.size(34.dp))
                    }
                }

                // Financial Overview Metrics Grid
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    MetricBox(label = "Total Borrowed", value = profile.totalBorrowedFormatted, modifier = Modifier.weight(1f))
                    MetricBox(label = "Total Repaid", value = profile.totalRepaidFormatted, valueColor = EmeraldGreen, modifier = Modifier.weight(1f))
                    MetricBox(label = "Outstanding", value = profile.outstandingAmountFormatted, valueColor = PrimaryBlue, modifier = Modifier.weight(1f))
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    MetricBox(label = "On-time Repayment", value = "${profile.onTimeRepaymentPct}%", valueColor = EmeraldGreen, modifier = Modifier.weight(1f))
                    MetricBox(label = "Avg Repayment Delay", value = "%.1f days".format(profile.avgDelayDays), modifier = Modifier.weight(1f))
                    MetricBox(label = "Current Collateral", value = profile.collateralEstimatedValueFormatted, valueColor = PrimaryBlue, modifier = Modifier.weight(1f))
                }

                // Explainable AI — "Why this score?"
                Text("Why this score? (Explainable AI Analysis)", fontSize = 13.sp, fontWeight = FontWeight.ExtraBold, color = NavyDark)

                Surface(color = Color(0xFFF8FAFC), shape = RoundedCornerShape(14.dp), modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        score.positiveFactors.forEach { factor ->
                            Text(factor, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = EmeraldGreen)
                        }
                        score.riskWarnings.forEach { warning ->
                            Text(warning, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color(0xFFD97706))
                        }
                    }
                }

                // Scoring Factor Breakdown
                Text("6 Weighted Factor Breakdown", fontSize = 13.sp, fontWeight = FontWeight.ExtraBold, color = NavyDark)

                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    score.scoringBreakdown.forEach { factor ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(Color(0xFFF8FAFC), RoundedCornerShape(8.dp))
                                .padding(horizontal = 10.dp, vertical = 6.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text("${factor.factorName} (${factor.weightPct}%)", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = NavyDark)
                                Text(factor.explanation, fontSize = 10.sp, color = TextMuted)
                            }
                            Text("+${factor.scoreContribution} pts", fontSize = 12.sp, fontWeight = FontWeight.ExtraBold, color = if (factor.isPositive) EmeraldGreen else PrimaryBlue)
                        }
                    }
                }

                // Historical Repayment Table
                Text("Historical Loan Repayment Ledger", fontSize = 13.sp, fontWeight = FontWeight.ExtraBold, color = NavyDark)

                Surface(color = Color.White, shape = RoundedCornerShape(12.dp), modifier = Modifier.fillMaxWidth().border(1.dp, Color(0xFFE2E8F0), RoundedCornerShape(12.dp))) {
                    Column(modifier = Modifier.padding(10.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Loan", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = TextMuted, modifier = Modifier.weight(1f))
                            Text("Amount", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = TextMuted, modifier = Modifier.weight(1f))
                            Text("Due Date", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = TextMuted, modifier = Modifier.weight(1f))
                            Text("Status", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = TextMuted, modifier = Modifier.weight(1f))
                        }
                        HorizontalDivider(color = Color(0xFFF1F5F9))

                        profile.recentLoans.forEach { loan ->
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                                Text(loan.id, fontSize = 11.sp, fontWeight = FontWeight.ExtraBold, color = PrimaryBlue, modifier = Modifier.weight(1f))
                                Text(loan.principalAmountFormatted, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = NavyDark, modifier = Modifier.weight(1f))
                                Text(loan.repaymentDeadline, fontSize = 11.sp, color = TextMuted, modifier = Modifier.weight(1f))
                                Surface(
                                    color = if (loan.status == "REPAID" || loan.status == "CLOSED") Color(0xFFDCFCE7) else Color(0xFFFEF3C7),
                                    shape = RoundedCornerShape(6.dp)
                                ) {
                                    Text(
                                        text = loan.status,
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.ExtraBold,
                                        color = if (loan.status == "REPAID" || loan.status == "CLOSED") EmeraldGreen else Color(0xFFB45309),
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun MetricBox(label: String, value: String, valueColor: Color = NavyDark, modifier: Modifier = Modifier) {
    Surface(color = Color(0xFFF8FAFC), shape = RoundedCornerShape(10.dp), modifier = modifier) {
        Column(modifier = Modifier.padding(8.dp)) {
            Text(label, fontSize = 9.sp, fontWeight = FontWeight.Bold, color = TextMuted)
            Text(value, fontSize = 12.sp, fontWeight = FontWeight.ExtraBold, color = valueColor)
        }
    }
}
