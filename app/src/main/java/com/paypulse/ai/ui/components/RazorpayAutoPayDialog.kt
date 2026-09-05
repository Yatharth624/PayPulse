package com.paypulse.ai.ui.components

import androidx.compose.animation.*
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RazorpayAutoPayDialog(
    borrowerName: String = "Rahul Sharma",
    monthlyAmount: Double = 5000.0,
    onDismiss: () -> Unit,
    onAutoPaySuccess: (upiApp: String, umrn: String) -> Unit
) {
    var selectedApp by remember { mutableStateOf("Google Pay") }
    var vpaInput by remember { mutableStateOf("rahul@okicici") }
    var isProcessing by remember { mutableStateOf(false) }
    var processingStep by remember { mutableStateOf(0) }
    var isCompleted by remember { mutableStateOf(false) }
    var generatedUmrn by remember { mutableStateOf("") }

    val razorpayNavy = Color(0xFF0C2340)
    val razorpayBlue = Color(0xFF0284C7)
    val emeraldGreen = Color(0xFF059669)

    // Animated telemetry sequence upon authorization
    LaunchedEffect(isProcessing) {
        if (isProcessing) {
            processingStep = 1 // NPCI Handshake
            delay(1200)
            processingStep = 2 // Sending Mandate Request
            delay(1400)
            processingStep = 3 // Awaiting Biometric / PIN Approval
            delay(1500)
            generatedUmrn = "UMRN" + (100000000000..999999999999).random().toString()
            processingStep = 4 // Mandate Registered
            isCompleted = true
            delay(1200)
            onAutoPaySuccess(selectedApp, generatedUmrn)
        }
    }

    BasicAlertDialog(
        onDismissRequest = { if (!isProcessing) onDismiss() },
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(24.dp))
            .background(Color.White)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
        ) {
            // ─── RAZORPAY BRANDED NAVY HEADER ───────────────────────────────
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        brush = Brush.verticalGradient(
                            colors = listOf(razorpayNavy, Color(0xFF1E293B))
                        )
                    )
                    .padding(20.dp)
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            color = Color(0xFF38BDF8).copy(alpha = 0.2f),
                            shape = CircleShape,
                            modifier = Modifier.size(36.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Default.Autorenew,
                                    contentDescription = null,
                                    tint = Color(0xFF38BDF8),
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "RAZORPAY AUTOPAY",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = Color(0xFF38BDF8),
                                letterSpacing = 1.sp
                            )
                            Text(
                                text = "NPCI eMandate Registration",
                                fontSize = 11.sp,
                                color = Color.LightGray
                            )
                        }
                    }

                    HorizontalDivider(color = Color.White.copy(alpha = 0.15f))

                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text(
                            text = "Max Monthly Debit Limit",
                            fontSize = 11.sp,
                            color = Color.LightGray
                        )
                        Text(
                            text = "₹${"%,.2f".format(monthlyAmount)}/mo",
                            fontSize = 24.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color.White
                        )
                        Surface(
                            color = Color.White.copy(alpha = 0.15f),
                            shape = RoundedCornerShape(6.dp),
                            modifier = Modifier.padding(top = 2.dp)
                        ) {
                            Text(
                                text = "Frequency: Monthly",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                            )
                        }
                    }
                }
            }

            // ─── MANDATE DETAILS & SELECTION ────────────────────────────────
            Column(
                modifier = Modifier.padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                if (!isProcessing && !isCompleted) {
                    // Mandate Summary Card
                    Surface(
                        color = Color(0xFFF8FAFC),
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(1.dp, Color(0xFFE2E8F0), RoundedCornerShape(16.dp))
                    ) {
                        Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                            Text(
                                text = "Mandate Parameters",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = Color(0xFF0F172A)
                            )

                            Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                                Text("Borrower / Account", fontSize = 11.sp, color = Color(0xFF64748B), fontWeight = FontWeight.Medium)
                                Text(borrowerName, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color(0xFF0F172A))
                            }

                            Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                                Text("Debit Schedule", fontSize = 11.sp, color = Color(0xFF64748B), fontWeight = FontWeight.Medium)
                                Text("5th of every month", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color(0xFF0F172A))
                            }

                            Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                                Text("AutoPay Guarantee", fontSize = 11.sp, color = Color(0xFF64748B), fontWeight = FontWeight.Medium)
                                Text("Zero Late Fees ⚡", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = emeraldGreen)
                            }
                        }
                    }

                    // Select UPI App
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text(
                            text = "Select UPI AutoPay App",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color(0xFF0F172A)
                        )

                        val upiApps = listOf(
                            Pair("Google Pay", "⚡ Recommended • Instant Mandate"),
                            Pair("PhonePe", "🪄 1-Tap eMandate Authorization"),
                            Pair("Paytm UPI", "💳 Paytm AutoPay Active"),
                            Pair("CRED UPI", "🔒 CRED Protect Secured")
                        )

                        upiApps.forEach { (appName, subtitle) ->
                            val isSelected = selectedApp == appName
                            Surface(
                                color = if (isSelected) Color(0xFFF0F9FF) else Color.White,
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .border(
                                        width = if (isSelected) 2.dp else 1.dp,
                                        color = if (isSelected) razorpayBlue else Color(0xFFE2E8F0),
                                        shape = RoundedCornerShape(12.dp)
                                    )
                                    .clickable { selectedApp = appName }
                            ) {
                                Row(
                                    modifier = Modifier.padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    RadioButton(
                                        selected = isSelected,
                                        onClick = { selectedApp = appName },
                                        colors = RadioButtonDefaults.colors(selectedColor = razorpayBlue)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Column {
                                        Text(
                                            text = appName,
                                            fontSize = 14.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color(0xFF0F172A)
                                        )
                                        Text(
                                            text = subtitle,
                                            fontSize = 11.sp,
                                            color = if (isSelected) razorpayBlue else Color(0xFF64748B)
                                        )
                                    }
                                }
                            }
                        }
                    }

                    // UPI ID / VPA Input
                    OutlinedTextField(
                        value = vpaInput,
                        onValueChange = { vpaInput = it },
                        label = { Text("UPI ID / VPA") },
                        placeholder = { Text("e.g. rahul@okicici") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = razorpayBlue,
                            focusedLabelColor = razorpayBlue
                        )
                    )

                    // Clear & Prominent Authorize Button
                    Button(
                        onClick = { isProcessing = true },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = razorpayBlue),
                        elevation = ButtonDefaults.buttonElevation(defaultElevation = 2.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Setup AutoPay",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color.White
                        )
                    }
                } else {
                    // ─── LIVE TELEMETRY SEQUENCE ─────────────────────────────────
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        if (!isCompleted) {
                            CircularProgressIndicator(
                                color = razorpayBlue,
                                strokeWidth = 4.dp,
                                modifier = Modifier.size(48.dp)
                            )
                        } else {
                            Surface(
                                color = Color(0xFFDCFCE7),
                                shape = CircleShape,
                                modifier = Modifier.size(56.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(
                                        imageVector = Icons.Default.CheckCircle,
                                        contentDescription = null,
                                        tint = emeraldGreen,
                                        modifier = Modifier.size(36.dp)
                                    )
                                }
                            }
                        }

                        Text(
                            text = when (processingStep) {
                                1 -> "Connecting to NPCI AutoPay Switch..."
                                2 -> "Sending Mandate Request to $selectedApp..."
                                3 -> "Awaiting Biometric / PIN Approval..."
                                4 -> "Mandate Registered Successfully!"
                                else -> "Processing..."
                            },
                            fontSize = 16.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color(0xFF0F172A)
                        )

                        if (generatedUmrn.isNotBlank()) {
                            Surface(
                                color = Color(0xFFF0FDF4),
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier.border(1.dp, emeraldGreen, RoundedCornerShape(10.dp))
                            ) {
                                Text(
                                    text = "UMRN: $generatedUmrn",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = emeraldGreen,
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
