package com.paypulse.ai.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.paypulse.ai.data.model.TransactionItem
import com.paypulse.ai.ui.theme.*

@Composable
fun IncidentDialog(
    category: String?,
    transactions: List<TransactionItem>,
    isExecuting: Boolean = false,
    executionResult: String? = null,
    onDismiss: () -> Unit,
    onExecute: (String) -> Unit
) {
    val relevantItems = when (category) {
        "upi_degradation" -> transactions.filter { it.failureReason.contains("UPI", ignoreCase = true) || it.failureReason.contains("Timeout", ignoreCase = true) }
        "failed_subscriptions" -> transactions.filter { it.category == "subscriptions" }
        "checkout_abandonment" -> transactions.filter { it.category == "checkout_abandonment" }
        else -> transactions.take(3)
    }
    val topItem = relevantItems.firstOrNull() ?: transactions.firstOrNull()
    val totalAtRisk = relevantItems.sumOf { it.amount }

    val (title, subtitle, iconBg, icon) = when (category) {
        "upi_degradation" -> listOf(
            "UPI Degradation Incident",
            "Gateway health critical — smart retry recommended",
            Color(0xFFFEF3C7),
            Icons.Default.Warning
        )
        "failed_subscriptions" -> listOf(
            "Subscription Recovery",
            "Mandate failures — retry or alert customers",
            Color(0xFFE0F2FE),
            Icons.Default.Autorenew
        )
        "checkout_abandonment" -> listOf(
            "Checkout Abandonment Recovery",
            "Send payment links to recover abandoned carts",
            Color(0xFFDCFCE7),
            Icons.Default.ShoppingCart
        )
        else -> listOf(
            "Recovery Incident",
            "AI-recommended action available",
            Color(0xFFE0F2FE),
            Icons.Default.AutoAwesome
        )
    }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(24.dp),
            color = Color.White,
            shadowElevation = 8.dp,
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
        ) {
            Column(modifier = Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {

                // Header
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(color = iconBg as Color, shape = CircleShape, modifier = Modifier.size(40.dp)) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(imageVector = icon as androidx.compose.ui.graphics.vector.ImageVector, contentDescription = null, tint = PrimaryBlue, modifier = Modifier.size(20.dp))
                        }
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(title as String, fontSize = 15.sp, fontWeight = FontWeight.ExtraBold, color = NavyDark)
                        Text(subtitle as String, fontSize = 11.sp, color = TextMuted)
                    }
                }

                HorizontalDivider(color = Color(0xFFF1F5F9))

                // Summary Stats
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceAround) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("${relevantItems.size}", fontSize = 22.sp, fontWeight = FontWeight.ExtraBold, color = NavyDark)
                        Text("Transactions", fontSize = 11.sp, color = TextMuted)
                    }
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        val atRiskStr = if (totalAtRisk >= 100000) "₹%.1fL".format(totalAtRisk / 100000) else "₹%,.0f".format(totalAtRisk)
                        Text(atRiskStr, fontSize = 22.sp, fontWeight = FontWeight.ExtraBold, color = AmberRiskText)
                        Text("At Risk", fontSize = 11.sp, color = TextMuted)
                    }
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        val avgProb = if (relevantItems.isNotEmpty()) relevantItems.sumOf { it.probabilityPct.toDouble() } / relevantItems.size else 0.0
                        Text("${avgProb.toInt()}%", fontSize = 22.sp, fontWeight = FontWeight.ExtraBold, color = PrimaryBlue)
                        Text("Avg. Probability", fontSize = 11.sp, color = TextMuted)
                    }
                }

                // Top affected transaction
                if (topItem != null) {
                    Surface(color = Color(0xFFF8FAFC), shape = RoundedCornerShape(14.dp), modifier = Modifier.fillMaxWidth()) {
                        Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(5.dp)) {
                            Text("Top Priority Transaction", fontSize = 12.sp, fontWeight = FontWeight.ExtraBold, color = NavyDark)
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text(topItem.id, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = PrimaryBlue)
                                Text(topItem.amountFormatted, fontSize = 12.sp, fontWeight = FontWeight.ExtraBold, color = NavyDark)
                            }
                            Text("${topItem.failureReason} • Prob: ${topItem.probabilityPct}%", fontSize = 11.sp, color = TextMuted)
                            Text("Rec: ${topItem.recommendedStrategy}", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = NavyDark)
                        }
                    }
                }

                // AI Intervention Plan
                Surface(color = Color(0xFFEFF6FF), shape = RoundedCornerShape(14.dp), modifier = Modifier.fillMaxWidth(),) {
                    Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.Top) {
                        Icon(imageVector = Icons.Default.AutoAwesome, contentDescription = null, tint = PrimaryBlue, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Bounded AI Intervention: Execute recovery strategy for ${relevantItems.size} transaction${if (relevantItems.size != 1) "s" else ""} within merchant policy gates. All actions are logged and auditable.",
                            fontSize = 11.sp, color = NavyDark, lineHeight = 15.sp
                        )
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
                            modifier = Modifier.padding(12.dp),
                            textAlign = TextAlign.Center
                        )
                    }
                }

                // Buttons
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedButton(
                        onClick = onDismiss,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp),
                        contentPadding = PaddingValues(vertical = 10.dp)
                    ) {
                        Text("Cancel", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }

                    Button(
                        onClick = { topItem?.let { onExecute(it.id) } },
                        modifier = Modifier.weight(1.6f),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue),
                        contentPadding = PaddingValues(vertical = 10.dp),
                        enabled = !isExecuting && topItem != null && executionResult == null
                    ) {
                        if (isExecuting) {
                            CircularProgressIndicator(modifier = Modifier.size(14.dp), color = Color.White, strokeWidth = 2.dp)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Executing...", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.White)
                        } else {
                            Text("Execute Bounded AI", fontSize = 11.sp, fontWeight = FontWeight.ExtraBold, color = Color.White, maxLines = 1)
                        }
                    }
                }
            }
        }
    }
}
