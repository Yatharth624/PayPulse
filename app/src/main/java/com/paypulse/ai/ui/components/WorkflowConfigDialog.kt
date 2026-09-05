package com.paypulse.ai.ui.components

import androidx.compose.foundation.background
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.paypulse.ai.data.model.WorkflowItem
import com.paypulse.ai.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WorkflowConfigDialog(
    workflow: WorkflowItem,
    onDismiss: () -> Unit,
    onSave: (WorkflowItem) -> Unit
) {
    var isEnabled by remember { mutableStateOf(workflow.status) }
    var maxRetries by remember { mutableStateOf(2f) }
    var coolingPeriodMinutes by remember { mutableStateOf(15f) }
    var notifyCustomer by remember { mutableStateOf(true) }
    var selectedChannel by remember { mutableStateOf("WhatsApp + SMS") }
    var isSaving by remember { mutableStateOf(false) }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(24.dp),
            color = Color.White,
            shadowElevation = 8.dp,
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
        ) {
            Column(
                modifier = Modifier
                    .padding(20.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            color = Color(0xFFE0F2FE),
                            shape = CircleShape,
                            modifier = Modifier.size(38.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Default.Tune,
                                    contentDescription = null,
                                    tint = PrimaryBlue,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "Configure Workflow",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = NavyDark
                            )
                            Text(
                                text = workflow.title,
                                fontSize = 12.sp,
                                color = TextMuted
                            )
                        }
                    }

                    IconButton(onClick = onDismiss) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = "Close", tint = TextMuted)
                    }
                }

                HorizontalDivider(color = Color(0xFFF1F5F9))

                // Workflow Enable Toggle
                Surface(
                    color = Color(0xFFF8FAFC),
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text("Workflow Automation", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = NavyDark)
                            Text(
                                if (isEnabled) "Active and autonomously recovering" else "Paused by merchant",
                                fontSize = 11.sp,
                                color = if (isEnabled) EmeraldGreen else Color.Gray
                            )
                        }
                        Switch(
                            checked = isEnabled,
                            onCheckedChange = { isEnabled = it },
                            colors = SwitchDefaults.colors(checkedThumbColor = PrimaryBlue, checkedTrackColor = Color(0xFFBAE6FD))
                        )
                    }
                }

                // Trigger and Current Strategy
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text("Trigger Condition", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = NavyDark)
                    Surface(
                        color = Color(0xFFEFF6FF),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = workflow.trigger,
                            fontSize = 12.sp,
                            color = PrimaryBlue,
                            fontWeight = FontWeight.SemiBold,
                            modifier = Modifier.padding(10.dp)
                        )
                    }
                }

                // Retry Policy Slider
                Column {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Max Auto-Retries", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = NavyDark)
                        Text("${maxRetries.toInt()} attempts", fontSize = 12.sp, fontWeight = FontWeight.ExtraBold, color = PrimaryBlue)
                    }
                    Slider(
                        value = maxRetries,
                        onValueChange = { maxRetries = it },
                        valueRange = 1f..5f,
                        steps = 3,
                        colors = SliderDefaults.colors(thumbColor = PrimaryBlue, activeTrackColor = PrimaryBlue)
                    )
                }

                // Cooling Window Slider
                Column {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Cooling-off Window", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = NavyDark)
                        Text("${coolingPeriodMinutes.toInt()} mins", fontSize = 12.sp, fontWeight = FontWeight.ExtraBold, color = PrimaryBlue)
                    }
                    Slider(
                        value = coolingPeriodMinutes,
                        onValueChange = { coolingPeriodMinutes = it },
                        valueRange = 5f..60f,
                        steps = 10,
                        colors = SliderDefaults.colors(thumbColor = PrimaryBlue, activeTrackColor = PrimaryBlue)
                    )
                }

                // Customer Notification Channel
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text("Dispatch Channel", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = NavyDark)
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        listOf("WhatsApp + SMS", "Smart Link", "In-App Prompt").forEach { channel ->
                            FilterChip(
                                selected = selectedChannel == channel,
                                onClick = { selectedChannel = channel },
                                label = { Text(channel, fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                                shape = RoundedCornerShape(10.dp),
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = PrimaryBlue,
                                    selectedLabelColor = Color.White
                                )
                            )
                        }
                    }
                }

                // Save Button
                Button(
                    onClick = {
                        isSaving = true
                        onSave(
                            workflow.copy(
                                status = isEnabled,
                                strategy = "${workflow.strategy.split("→")[0]} → $selectedChannel (${coolingPeriodMinutes.toInt()}m)"
                            )
                        )
                    },
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue),
                    modifier = Modifier.fillMaxWidth(),
                    contentPadding = PaddingValues(vertical = 12.dp)
                ) {
                    Icon(imageVector = Icons.Default.Check, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Save & Deploy Workflow", fontSize = 13.sp, fontWeight = FontWeight.ExtraBold, color = Color.White)
                }
            }
        }
    }
}
