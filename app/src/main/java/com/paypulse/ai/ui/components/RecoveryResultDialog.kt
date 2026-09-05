package com.paypulse.ai.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Dvr
import androidx.compose.material.icons.filled.CheckCircle
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
import com.paypulse.ai.data.model.ExecutionOutcomeDto
import com.paypulse.ai.ui.theme.*

@Composable
fun RecoveryResultDialog(
    outcome: ExecutionOutcomeDto?,
    onDismiss: () -> Unit
) {
    val data = outcome ?: buildFallbackOutcome()

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
                Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                    Surface(color = Color(0xFFDCFCE7), shape = CircleShape, modifier = Modifier.size(44.dp)) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(imageVector = Icons.Default.CheckCircle, contentDescription = null, tint = EmeraldGreen, modifier = Modifier.size(26.dp))
                        }
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Text("Payment Recovered", fontSize = 17.sp, fontWeight = FontWeight.ExtraBold, color = NavyDark)
                    Text(data.amountFormatted, fontSize = 26.sp, fontWeight = FontWeight.ExtraBold, color = EmeraldGreen)
                }

                Surface(color = Color(0xFFF1F5F9), shape = RoundedCornerShape(8.dp), modifier = Modifier.fillMaxWidth()) {
                    Row(modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.Center) {
                        Icon(imageVector = Icons.AutoMirrored.Filled.Dvr, contentDescription = null, tint = TextMuted, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(data.environment, fontSize = 10.sp, fontWeight = FontWeight.Bold, color = TextMuted)
                    }
                }

                HorizontalDivider(color = Color(0xFFF1F5F9))

                // Detail Rows
                Surface(color = Color(0xFFF8FAFC), shape = RoundedCornerShape(14.dp), modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(5.dp)) {
                        OutcomeRow("Transaction ID", data.transactionId)
                        OutcomeRow("Strategy Used", data.strategyUsed, isBold = true)
                        OutcomeRow("Attempts Count", "${data.attemptsCount}")
                        OutcomeRow("Expected Recovery", data.expectedRecoveryFormatted, valueColor = PrimaryBlue)
                        OutcomeRow("Actual Recovery", data.actualRecoveryFormatted, isBold = true, valueColor = EmeraldGreen)
                        OutcomeRow("Status", data.status, isBold = true, valueColor = EmeraldGreen)
                    }
                }

                // Execution Timeline
                Text("Recovery Execution Timeline", fontSize = 13.sp, fontWeight = FontWeight.ExtraBold, color = NavyDark)

                Column(verticalArrangement = Arrangement.spacedBy(5.dp)) {
                    TimelineItem("ML Prediction", "87% Recovery Probability calculated")
                    TimelineItem("AI Diagnosis", "Transient Gateway Failure identified")
                    TimelineItem("Policy Check", "POLICY APPROVED (87% >= 70% threshold)")
                    TimelineItem("Recovery Scheduled", "Delayed Retry (15 minutes window)")
                    TimelineItem("Recovery Attempt", "Attempt #1 via Gateway Switch")
                    TimelineItem("Payment Result", "Successful: ${data.amountFormatted} captured")
                }

                Button(
                    onClick = onDismiss,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue)
                ) {
                    Text("Done & Sync Telemetry", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
private fun OutcomeRow(label: String, value: String, isBold: Boolean = false, valueColor: Color = NavyDark) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(label, fontSize = 12.sp, color = TextMuted)
        Text(value, fontSize = 12.sp, fontWeight = if (isBold) FontWeight.ExtraBold else FontWeight.Bold, color = valueColor)
    }
}

@Composable
private fun TimelineItem(title: String, desc: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(imageVector = Icons.Default.CheckCircle, contentDescription = null, tint = EmeraldGreen, modifier = Modifier.size(15.dp))
        Spacer(modifier = Modifier.width(8.dp))
        Column {
            Text(title, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = NavyDark)
            Text(desc, fontSize = 10.sp, color = TextMuted)
        }
    }
}

private fun buildFallbackOutcome(): ExecutionOutcomeDto {
    return ExecutionOutcomeDto(
        executionId = "REC_83922",
        transactionId = "RP_83922",
        status = "RECOVERED",
        amountFormatted = "₹24,500",
        expectedRecoveryFormatted = "₹21,315",
        actualRecoveryFormatted = "₹24,500",
        strategyUsed = "Delayed Retry",
        attemptsCount = 1,
        policyApproved = true,
        environment = "Demo Payment Gateway Simulation"
    )
}
