package com.paypulse.ai.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Shield
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
import com.paypulse.ai.ui.theme.*

@Composable
fun ConfirmRecoveryDialog(
    transactionId: String = "RP_83922",
    amountFormatted: String = "₹24,500",
    failureReason: String = "UPI Timeout",
    probabilityPct: Double = 87.0,
    expectedRecoveryFormatted: String = "₹21,315",
    strategyName: String = "Delayed Retry",
    delayFormatted: String = "15 minutes",
    policyStatus: String = "APPROVED",
    retryCountText: String = "0 / 2",
    isExecuting: Boolean = false,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
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
                modifier = Modifier.padding(18.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Header
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(color = Color(0xFFECFDF5), shape = RoundedCornerShape(10.dp), modifier = Modifier.size(38.dp)) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(imageVector = Icons.Default.Bolt, contentDescription = null, tint = EmeraldGreen, modifier = Modifier.size(20.dp))
                        }
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text("Confirm Recommended Recovery", fontSize = 15.sp, fontWeight = FontWeight.ExtraBold, color = NavyDark)
                        Text("Policy Check & Authoritative Execution", fontSize = 12.sp, color = TextMuted)
                    }
                }

                HorizontalDivider(color = Color(0xFFF1F5F9))

                // Detail Rows
                Surface(color = Color(0xFFF8FAFC), shape = RoundedCornerShape(14.dp), modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(5.dp)) {
                        ConfirmRow("Transaction ID", transactionId, isBold = true)
                        ConfirmRow("Transaction Amount", amountFormatted, isBold = true, valueColor = NavyDark)
                        ConfirmRow("Failure Reason", failureReason)
                        ConfirmRow("Recovery Probability", "${probabilityPct.toInt()}%", valueColor = PrimaryBlue)
                        ConfirmRow("Expected Recovery", expectedRecoveryFormatted, valueColor = EmeraldGreen)
                        ConfirmRow("Recommended Action", strategyName, isBold = true, valueColor = EmeraldGreen)
                        ConfirmRow("Cooling Window Delay", delayFormatted)
                        ConfirmRow("Policy Evaluation", policyStatus, valueColor = EmeraldGreen)
                        ConfirmRow("Attempt Count", retryCountText)
                    }
                }

                Surface(color = Color(0xFFF0F9FF), shape = RoundedCornerShape(12.dp), modifier = Modifier.fillMaxWidth()) {
                    Row(modifier = Modifier.padding(10.dp), verticalAlignment = Alignment.CenterVertically) {
                        Icon(imageVector = Icons.Default.Shield, contentDescription = null, tint = PrimaryBlue, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Backend Policy Engine will re-evaluate rules before execution.", fontSize = 11.sp, color = TextMuted)
                    }
                }

                // Buttons
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedButton(
                        onClick = onDismiss,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Cancel", fontWeight = FontWeight.Bold, color = TextMuted)
                    }

                    Button(
                        onClick = onConfirm,
                        modifier = Modifier.weight(1.5f),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = EmeraldGreen),
                        enabled = !isExecuting
                    ) {
                        if (isExecuting) {
                            CircularProgressIndicator(modifier = Modifier.size(14.dp), color = Color.White, strokeWidth = 2.dp)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Re-checking...", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.White)
                        } else {
                            Text("Confirm Recovery", fontSize = 12.sp, fontWeight = FontWeight.ExtraBold, color = Color.White)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ConfirmRow(label: String, value: String, isBold: Boolean = false, valueColor: Color = NavyDark) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(label, fontSize = 12.sp, color = TextMuted)
        Text(value, fontSize = 12.sp, fontWeight = if (isBold) FontWeight.ExtraBold else FontWeight.Bold, color = valueColor)
    }
}
