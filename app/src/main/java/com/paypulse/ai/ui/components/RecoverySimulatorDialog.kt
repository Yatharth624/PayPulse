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
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.paypulse.ai.data.model.SimulationResponseDto
import com.paypulse.ai.data.model.StrategySimulationItemDto
import com.paypulse.ai.ui.theme.*

@Composable
fun RecoverySimulatorDialog(
    simulation: SimulationResponseDto?,
    onDismiss: () -> Unit,
    onUseStrategy: (String, String) -> Unit = { _, _ -> }
) {
    val sim = simulation ?: buildFallbackSimulation()

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
                        Surface(color = Color(0xFFEFF6FF), shape = CircleShape, modifier = Modifier.size(38.dp)) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(imageVector = Icons.Default.Analytics, contentDescription = null, tint = PrimaryBlue, modifier = Modifier.size(20.dp))
                            }
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text("Compare Recovery Strategies", fontSize = 15.sp, fontWeight = FontWeight.ExtraBold, color = NavyDark)
                            Text("Simulated Options • ${sim.transactionId} (${sim.amountFormatted})", fontSize = 12.sp, color = TextMuted)
                        }
                    }

                    IconButton(onClick = onDismiss, modifier = Modifier.size(24.dp)) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = null, tint = TextMuted)
                    }
                }

                Surface(color = Color(0xFFF8FAFC), shape = RoundedCornerShape(12.dp), modifier = Modifier.fillMaxWidth()) {
                    Row(
                        modifier = Modifier.padding(10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(imageVector = Icons.Default.Info, contentDescription = null, tint = PrimaryBlue, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Simulation mode compares financial outcomes. Selecting 'Use This Strategy' routes to explicit Confirmation before execution.",
                            fontSize = 11.sp,
                            color = TextMuted
                        )
                    }
                }

                HorizontalDivider(color = Color(0xFFF1F5F9))

                Text("Compared Recovery Options", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = NavyDark)

                // Strategy List
                sim.options.forEach { opt ->
                    StrategyComparisonCard(opt = opt, onSelectStrategy = {
                        onUseStrategy(sim.transactionId, opt.name)
                        onDismiss()
                    })
                }

                Spacer(modifier = Modifier.height(4.dp))

                OutlinedButton(
                    onClick = onDismiss,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Close Simulator", fontWeight = FontWeight.Bold, color = TextMuted)
                }
            }
        }
    }
}

@Composable
private fun StrategyComparisonCard(opt: StrategySimulationItemDto, onSelectStrategy: () -> Unit) {
    val borderColor = if (opt.recommended) PrimaryBlue else if (opt.policyEligible) Color(0xFFE2E8F0) else Color(0xFFFECACA)
    val bgColor = if (opt.recommended) Color(0xFFF0F9FF) else Color.White

    Surface(
        shape = RoundedCornerShape(16.dp),
        color = bgColor,
        shadowElevation = 1.dp,
        modifier = Modifier
            .fillMaxWidth()
            .border(if (opt.recommended) 2.dp else 1.dp, borderColor, RoundedCornerShape(16.dp))
    ) {
        Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(opt.name, fontSize = 13.sp, fontWeight = FontWeight.ExtraBold, color = NavyDark)
                    if (opt.recommended) {
                        Spacer(modifier = Modifier.width(6.dp))
                        Surface(color = PrimaryBlue, shape = RoundedCornerShape(6.dp)) {
                            Text("RECOMMENDED", fontSize = 8.sp, fontWeight = FontWeight.ExtraBold, color = Color.White, modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp))
                        }
                    }
                }

                val pillBg = when (opt.policyStatusLabel) {
                    "RECOMMENDED" -> Color(0xFFE0F2FE)
                    "ELIGIBLE" -> Color(0xFFECFDF5)
                    "BLOCKED" -> Color(0xFFFEE2E2)
                    else -> Color(0xFFF1F5F9)
                }
                val pillText = when (opt.policyStatusLabel) {
                    "RECOMMENDED" -> PrimaryBlue
                    "ELIGIBLE" -> EmeraldGreen
                    "BLOCKED" -> Color(0xFFDC2626)
                    else -> TextMuted
                }

                Surface(color = pillBg, shape = RoundedCornerShape(6.dp)) {
                    Text(
                        text = opt.policyStatusLabel,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = pillText,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text("Recovery Prob.", fontSize = 10.sp, color = TextMuted)
                    Text("${opt.probabilityPct.toInt()}%", fontSize = 13.sp, fontWeight = FontWeight.ExtraBold, color = if (opt.probabilityPct >= 70) EmeraldGreen else NavyDark)
                }
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("Expected Recovery", fontSize = 10.sp, color = TextMuted)
                    Text(opt.expectedRecoveryFormatted, fontSize = 13.sp, fontWeight = FontWeight.ExtraBold, color = PrimaryBlue)
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text("Est. Gateway Cost", fontSize = 10.sp, color = TextMuted)
                    Text(opt.estimatedCostFormatted, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = TextMuted)
                }
            }

            Text(
                text = "Reason: ${opt.policyReason}",
                fontSize = 10.sp,
                color = TextMuted,
                fontWeight = FontWeight.Medium
            )

            if (opt.policyEligible && opt.type != "no_action") {
                Button(
                    onClick = onSelectStrategy,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue),
                    contentPadding = PaddingValues(vertical = 6.dp)
                ) {
                    Text("Use This Strategy", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.White)
                }
            }
        }
    }
}

private fun buildFallbackSimulation(): SimulationResponseDto {
    return SimulationResponseDto(
        transactionId = "RP_83922",
        amountFormatted = "₹24,500",
        failureReason = "UPI Timeout",
        currentRecommendedStrategy = "Delayed Retry (15 min)",
        options = listOf(
            StrategySimulationItemDto("1", "Delayed Retry (15 min)", "delayed_retry", 87.0, "₹21,315", "₹12", "₹21,303", true, "Passed 87% >= 70% min threshold", "RECOMMENDED", true),
            StrategySimulationItemDto("2", "Immediate Gateway Retry", "immediate_retry", 42.0, "₹10,290", "₹18", "₹10,272", false, "Blocked: Probability 42% < 70% threshold & gateway congested", "BLOCKED", false),
            StrategySimulationItemDto("3", "Smart Payment Link", "payment_link", 74.0, "₹18,130", "₹2.50", "₹18,127", true, "Communication policy active & customer conversion high", "ELIGIBLE", false),
            StrategySimulationItemDto("4", "Offer Alternative Payment Method", "alt_payment_method", 68.0, "₹16,660", "₹0", "₹16,660", true, "Safe fallback for checkout friction", "ELIGIBLE", false),
            StrategySimulationItemDto("5", "No Automated Action", "no_action", 0.0, "₹0", "₹0", "₹0", false, "Manual merchant oversight option (PayPulse will not attempt auto-recovery)", "MANUAL", false)
        )
    )
}
