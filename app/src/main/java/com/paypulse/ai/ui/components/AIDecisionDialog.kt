package com.paypulse.ai.ui.components

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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.paypulse.ai.data.model.AIDecisionDetail
import com.paypulse.ai.ui.theme.*

@Composable
fun AIDecisionDialog(
    detail: AIDecisionDetail,
    isExecuting: Boolean = false,
    executionResult: String? = null,
    onDismiss: () -> Unit,
    onExecute: (String) -> Unit,
    onOpenSimulator: () -> Unit = {}
) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            shape = RoundedCornerShape(24.dp),
            color = Color.White,
            shadowElevation = 8.dp,
            modifier = Modifier
                .fillMaxWidth(0.92f)
                .wrapContentHeight()
                .heightIn(max = 650.dp)
        ) {
            Column(
                modifier = Modifier
                    .padding(18.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(color = Color(0xFFE0F2FE), shape = CircleShape, modifier = Modifier.size(38.dp)) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(imageVector = Icons.Default.Psychology, contentDescription = null, tint = PrimaryBlue, modifier = Modifier.size(20.dp))
                            }
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text("Transaction Intelligence", fontSize = 15.sp, fontWeight = FontWeight.ExtraBold, color = NavyDark)
                            Text(detail.transactionId, fontSize = 12.sp, color = TextMuted, fontWeight = FontWeight.Bold)
                        }
                    }

                    // Policy Evaluation Badge (APPROVED on top, STATUS on next line)
                    Column(horizontalAlignment = Alignment.End) {
                        Surface(
                            color = if (detail.isApproved) Color(0xFFDCFCE7) else Color(0xFFFEF3C7),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(
                                text = if (detail.isApproved) "APPROVED" else "NEEDS REVIEW",
                                fontSize = 9.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = if (detail.isApproved) EmeraldGreen else Color(0xFFB45309),
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                            )
                        }
                        Spacer(modifier = Modifier.height(3.dp))
                        Surface(
                            color = Color(0xFFECFDF5),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(
                                text = "POLICY APPROVED",
                                fontSize = 9.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = EmeraldGreen,
                                modifier = Modifier.padding(horizontal = 7.dp, vertical = 2.dp)
                            )
                        }
                    }
                }

                HorizontalDivider(color = Color(0xFFF1F5F9))

                // Key Transaction Financial Summary
                val parsedAmount = detail.amount.replace("₹", "").replace(",", "").toDoubleOrNull() ?: 24500.0
                val expectedRecoveryVal = parsedAmount * detail.confidence
                val expectedFmt = when {
                    expectedRecoveryVal >= 100000 -> "₹%.1fL".format(expectedRecoveryVal / 100000)
                    expectedRecoveryVal >= 1000 -> "₹%,.0f".format(expectedRecoveryVal)
                    else -> "₹%.0f".format(expectedRecoveryVal)
                }

                Surface(color = Color(0xFFF8FAFC), shape = RoundedCornerShape(14.dp), modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(5.dp)) {
                        IntelRow("Transaction Amount", detail.amount, isBold = true)
                        IntelRow("Failure Reason", detail.failureReason)
                        IntelRow("Recovery Probability", "${(detail.confidence * 100).toInt()}%", valueColor = PrimaryBlue)
                        IntelRow("Expected Recovery", expectedFmt, valueColor = EmeraldGreen)
                    }
                }

                // ML Signals Section
                Column(verticalArrangement = Arrangement.spacedBy(5.dp)) {
                    Text("Important ML Signals", fontSize = 13.sp, fontWeight = FontWeight.ExtraBold, color = NavyDark)
                    Surface(color = Color(0xFFF0F9FF), shape = RoundedCornerShape(12.dp), modifier = Modifier.fillMaxWidth()) {
                        Column(modifier = Modifier.padding(10.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            SignalItem("Customer Historical Conversion: 94%")
                            SignalItem("Gateway Health: 92/100 (Razorpay Switch)")
                            SignalItem("Failure Pattern: Transient Timeout (Morning Peak)")
                            SignalItem("Merchant Limit Check: Amount ₹24,500 < ₹50,000 threshold")
                        }
                    }
                }

                // AI Diagnosis
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text("AI Diagnosis", fontSize = 13.sp, fontWeight = FontWeight.ExtraBold, color = NavyDark)
                    Text(detail.diagnosisTitle, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = PrimaryBlue)
                    Text(detail.explanation, fontSize = 11.sp, color = TextMuted, lineHeight = 15.sp)
                }

                // Full Recovery Lifecycle Timeline
                Column(verticalArrangement = Arrangement.spacedBy(5.dp)) {
                    Text("Full Recovery Lifecycle Timeline", fontSize = 13.sp, fontWeight = FontWeight.ExtraBold, color = NavyDark)

                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        TimelineStep(1, "Payment Failed", detail.failureReason, "10 min ago")
                        TimelineStep(2, "ML Score Calculated", "${detail.probabilityPct}% Recovery Probability Score", "8 min ago")
                        TimelineStep(3, "AI Diagnosis", detail.diagnosisTitle, "6 min ago")
                        TimelineStep(4, "Policy Engine Verification", "POLICY EVALUATED (${detail.probabilityPct}% >= threshold)", "4 min ago")
                        TimelineStep(5, "Recommended Recovery Action", "Delayed Retry · 15 min cooling window", "Scheduled")
                    }
                }

                // Recommended Strategy Banner
                Surface(color = Color(0xFFECFDF5), shape = RoundedCornerShape(12.dp), modifier = Modifier.fillMaxWidth()) {
                    Row(modifier = Modifier.padding(10.dp), verticalAlignment = Alignment.CenterVertically) {
                        Icon(imageVector = Icons.Default.Bolt, contentDescription = null, tint = EmeraldGreen, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text("Recommended Recovery Strategy", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = TextMuted)
                            Text("Delayed Retry · 15 min", fontSize = 12.sp, fontWeight = FontWeight.ExtraBold, color = EmeraldGreen)
                        }
                    }
                }

                // Execution Result Banner
                if (executionResult != null) {
                    Surface(
                        color = if (executionResult.startsWith("✅")) Color(0xFFDCFCE7) else Color(0xFFFEF3C7),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = executionResult,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (executionResult.startsWith("✅")) EmeraldGreen else Color(0xFFB45309),
                            modifier = Modifier.padding(10.dp),
                            textAlign = TextAlign.Center
                        )
                    }
                }

                // Buttons
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    // Primary Action Button: Execute Recommended Recovery
                    Button(
                        onClick = { onExecute(detail.transactionId) },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = EmeraldGreen),
                        contentPadding = PaddingValues(vertical = 11.dp),
                        enabled = !isExecuting && executionResult == null
                    ) {
                        if (isExecuting) {
                            CircularProgressIndicator(modifier = Modifier.size(16.dp), color = Color.White, strokeWidth = 2.dp)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Re-checking Policy Engine...", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.White)
                        } else {
                            Icon(imageVector = Icons.Default.Bolt, contentDescription = null, modifier = Modifier.size(18.dp), tint = Color.White)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Execute Recommended Recovery", fontSize = 13.sp, fontWeight = FontWeight.ExtraBold, color = Color.White)
                        }
                    }

                    // Secondary Action Button: Compare Strategies
                    OutlinedButton(
                        onClick = {
                            onDismiss()
                            onOpenSimulator()
                        },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        contentPadding = PaddingValues(vertical = 10.dp)
                    ) {
                        Icon(imageVector = Icons.Default.Analytics, contentDescription = null, modifier = Modifier.size(16.dp), tint = PrimaryBlue)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Compare Strategies", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = PrimaryBlue)
                    }
                }
            }
        }
    }
}

@Composable
private fun IntelRow(label: String, value: String, isBold: Boolean = false, valueColor: Color = NavyDark) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(label, fontSize = 12.sp, color = TextMuted)
        Text(value, fontSize = 12.sp, fontWeight = if (isBold) FontWeight.ExtraBold else FontWeight.Bold, color = valueColor)
    }
}

@Composable
private fun SignalItem(signal: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(imageVector = Icons.Default.Check, contentDescription = null, tint = PrimaryBlue, modifier = Modifier.size(14.dp))
        Spacer(modifier = Modifier.width(6.dp))
        Text(signal, fontSize = 11.sp, color = NavyDark, fontWeight = FontWeight.Medium)
    }
}

@Composable
private fun TimelineStep(stepNum: Int, title: String, desc: String, timestamp: String = "") {
    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
        Surface(color = Color(0xFFEFF6FF), shape = CircleShape, modifier = Modifier.size(22.dp)) {
            Box(contentAlignment = Alignment.Center) {
                Text("$stepNum", fontSize = 11.sp, fontWeight = FontWeight.ExtraBold, color = PrimaryBlue)
            }
        }
        Spacer(modifier = Modifier.width(10.dp))
        Column(modifier = Modifier.weight(1f)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(title, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = NavyDark)
                if (timestamp.isNotBlank()) {
                    Text(timestamp, fontSize = 10.sp, color = TextMuted)
                }
            }
            Text(desc, fontSize = 11.sp, color = TextMuted)
        }
    }
}
