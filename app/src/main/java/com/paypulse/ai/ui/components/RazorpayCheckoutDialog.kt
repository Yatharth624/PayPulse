package com.paypulse.ai.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
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
import com.paypulse.ai.ui.theme.*
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RazorpayCheckoutDialog(
    targetId: String = "LOAN_101",
    amount: Double = 3500.0,
    title: String = "Rahul (Personal Loan)",
    onDismiss: () -> Unit,
    onPaymentSuccess: (targetId: String, amount: Double) -> Unit
) {
    var selectedTab by remember { mutableStateOf(0) } // 0: UPI Intent, 1: Magic Checkout, 2: EMI
    var customUpiVpa by remember { mutableStateOf("rahul@okaxis") }
    var selectedEmiOption by remember { mutableStateOf(3) } // 3 months or 6 months

    // Processing State Simulation
    var isProcessing by remember { mutableStateOf(false) }
    var processingStep by remember { mutableStateOf(0) } // 0: Handshake, 1: Switch, 2: Success
    var paymentRefId by remember { mutableStateOf("") }

    LaunchedEffect(isProcessing) {
        if (isProcessing) {
            processingStep = 0
            delay(1000)
            processingStep = 1
            delay(1200)
            paymentRefId = "pay_Rzp${System.currentTimeMillis() % 100000000}"
            processingStep = 2
            delay(1400)
            onPaymentSuccess(targetId, amount)
            onDismiss()
        }
    }

    Dialog(
        onDismissRequest = { if (!isProcessing) onDismiss() },
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            shape = RoundedCornerShape(24.dp),
            color = Color.White,
            shadowElevation = 16.dp,
            modifier = Modifier
                .fillMaxWidth(0.94f)
                .padding(vertical = 12.dp)
        ) {
            Column(modifier = Modifier.fillMaxWidth()) {
                
                // ─── 1. RAZORPAY BRANDED NAVY TOP BAR ────────────────────────────────
                Surface(
                    color = Color(0xFF0C2340), // Razorpay Navy Dark
                    shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(18.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Surface(
                                    color = Color(0xFF0284C7),
                                    shape = RoundedCornerShape(8.dp),
                                    modifier = Modifier.padding(end = 8.dp)
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(Icons.Default.Lock, null, tint = Color.White, modifier = Modifier.size(12.dp))
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text("RAZORPAY SECURE", fontSize = 10.sp, fontWeight = FontWeight.ExtraBold, color = Color.White)
                                    }
                                }
                            }

                            if (!isProcessing) {
                                IconButton(onClick = onDismiss, modifier = Modifier.size(28.dp)) {
                                    Icon(Icons.Default.Close, contentDescription = "Close", tint = Color.White.copy(alpha = 0.8f))
                                }
                            }
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.Bottom
                        ) {
                            Column {
                                Text("Paying Merchant / Borrower", fontSize = 11.sp, color = Color.White.copy(alpha = 0.7f), fontWeight = FontWeight.Medium)
                                Text(title, fontSize = 17.sp, fontWeight = FontWeight.ExtraBold, color = Color.White)
                            }

                            Column(horizontalAlignment = Alignment.End) {
                                Text("Amount Due", fontSize = 11.sp, color = Color.White.copy(alpha = 0.7f), fontWeight = FontWeight.Medium)
                                Text("₹%,.0f".format(amount), fontSize = 24.sp, fontWeight = FontWeight.ExtraBold, color = Color(0xFF38BDF8))
                            }
                        }
                    }
                }

                // ─── 2. LIVE PAYMENT PROCESSING OVERLAY ─────────────────────────────
                if (isProcessing) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(340.dp)
                            .background(Color.White),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(16.dp),
                            modifier = Modifier.padding(24.dp)
                        ) {
                            when (processingStep) {
                                0 -> {
                                    CircularProgressIndicator(color = Color(0xFF0284C7), strokeWidth = 4.dp, modifier = Modifier.size(54.dp))
                                    Text("Connecting to Razorpay Banking Gateway...", fontSize = 16.sp, fontWeight = FontWeight.ExtraBold, color = NavyDark)
                                    Text("Securing SSL Handshake with NPCI Switch", fontSize = 12.sp, color = TextMuted)
                                }
                                1 -> {
                                    CircularProgressIndicator(color = EmeraldGreen, strokeWidth = 4.dp, modifier = Modifier.size(54.dp))
                                    Text("Authorizing UPI Intent Payment...", fontSize = 16.sp, fontWeight = FontWeight.ExtraBold, color = NavyDark)
                                    Text("Processing transaction with HDFC / ICICI Switch", fontSize = 12.sp, color = TextMuted)
                                }
                                2 -> {
                                    Surface(color = Color(0xFFDCFCE7), shape = CircleShape, modifier = Modifier.size(64.dp)) {
                                        Box(contentAlignment = Alignment.Center) {
                                            Icon(Icons.Default.CheckCircle, null, tint = EmeraldGreen, modifier = Modifier.size(44.dp))
                                        }
                                    }
                                    Text("Payment Successful!", fontSize = 22.sp, fontWeight = FontWeight.ExtraBold, color = EmeraldGreen)
                                    Surface(color = Color(0xFFEFF6FF), shape = RoundedCornerShape(8.dp)) {
                                        Text(
                                            text = "Ref: $paymentRefId",
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = PrimaryBlue,
                                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                                        )
                                    }
                                    Text("Updating PayPulse loan ledger across screens...", fontSize = 12.sp, color = TextMuted)
                                }
                            }
                        }
                    }
                } else {
                    // ─── 3. TAB SELECTOR BAR ─────────────────────────────────────────
                    Surface(color = Color(0xFFF8FAFC), modifier = Modifier.fillMaxWidth()) {
                        Row(modifier = Modifier.padding(6.dp), horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            TabPill("⚡ UPI Intent", isSelected = selectedTab == 0, onClick = { selectedTab = 0 }, modifier = Modifier.weight(1f))
                            TabPill("🪄 Magic Pay", isSelected = selectedTab == 1, onClick = { selectedTab = 1 }, modifier = Modifier.weight(1f))
                            TabPill("💳 EMI Options", isSelected = selectedTab == 2, onClick = { selectedTab = 2 }, modifier = Modifier.weight(1f))
                        }
                    }

                    HorizontalDivider(color = Color(0xFFE2E8F0))

                    // ─── 4. TAB CONTENTS ─────────────────────────────────────────────
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(max = 380.dp)
                            .verticalScroll(rememberScrollState())
                            .padding(18.dp),
                        verticalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        when (selectedTab) {
                            0 -> {
                                // TAB 1: UPI INTENT & 1-CLICK APPS
                                Text("1-Click Instant UPI Apps", fontSize = 13.sp, fontWeight = FontWeight.ExtraBold, color = NavyDark)

                                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                                    UpiAppCard(name = "Google Pay", color = Color(0xFF4285F4), icon = Icons.Default.AccountBalanceWallet, modifier = Modifier.weight(1f), onClick = { isProcessing = true })
                                    UpiAppCard(name = "PhonePe", color = Color(0xFF5F259F), icon = Icons.Default.Smartphone, modifier = Modifier.weight(1f), onClick = { isProcessing = true })
                                }

                                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                                    UpiAppCard(name = "Paytm UPI", color = Color(0xFF00B9F1), icon = Icons.Default.QrCode, modifier = Modifier.weight(1f), onClick = { isProcessing = true })
                                    UpiAppCard(name = "CRED UPI", color = Color(0xFF1E293B), icon = Icons.Default.CreditCard, modifier = Modifier.weight(1f), onClick = { isProcessing = true })
                                }

                                HorizontalDivider(color = Color(0xFFF1F5F9))

                                Text("Or Enter VPA / UPI ID", fontSize = 13.sp, fontWeight = FontWeight.ExtraBold, color = NavyDark)

                                OutlinedTextField(
                                    value = customUpiVpa,
                                    onValueChange = { customUpiVpa = it },
                                    label = { Text("UPI ID (e.g. username@upi)") },
                                    leadingIcon = { Icon(Icons.Default.QrCodeScanner, null, tint = PrimaryBlue) },
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(12.dp),
                                    singleLine = true
                                )

                                Button(
                                    onClick = { isProcessing = true },
                                    modifier = Modifier.fillMaxWidth().height(52.dp),
                                    shape = RoundedCornerShape(14.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0284C7)),
                                    elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp)
                                ) {
                                    Icon(Icons.Default.Bolt, null, tint = Color.White)
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("Pay ₹%,.0f via Razorpay UPI".format(amount), fontSize = 15.sp, fontWeight = FontWeight.ExtraBold, color = Color.White)
                                }
                            }

                            1 -> {
                                // TAB 2: RAZORPAY MAGIC CHECKOUT
                                Surface(
                                    color = Color(0xFFF0F9FF),
                                    shape = RoundedCornerShape(14.dp),
                                    modifier = Modifier.fillMaxWidth().border(1.dp, Color(0xFFBAE6FD), RoundedCornerShape(14.dp))
                                ) {
                                    Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Icon(Icons.Default.AutoAwesome, null, tint = Color(0xFF0284C7), modifier = Modifier.size(20.dp))
                                            Spacer(modifier = Modifier.width(8.dp))
                                            Text("Razorpay Magic 1-Click Profile", fontSize = 14.sp, fontWeight = FontWeight.ExtraBold, color = Color(0xFF0369A1))
                                        }
                                        Text("Pre-verified 1-click checkout active for Rahul Sharma", fontSize = 12.sp, color = NavyDark)
                                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                            Surface(color = Color(0xFFDCFCE7), shape = RoundedCornerShape(6.dp)) {
                                                Text("Saved VPA: rahul.sharma@okaxis", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = EmeraldGreen, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
                                            }
                                        }
                                    }
                                }

                                Button(
                                    onClick = { isProcessing = true },
                                    modifier = Modifier.fillMaxWidth().height(54.dp),
                                    shape = RoundedCornerShape(14.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0284C7)),
                                    elevation = ButtonDefaults.buttonElevation(defaultElevation = 6.dp)
                                ) {
                                    Icon(Icons.Default.AutoAwesome, null, tint = Color.White)
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("Magic 1-Tap Pay ₹%,.0f".format(amount), fontSize = 16.sp, fontWeight = FontWeight.ExtraBold, color = Color.White)
                                }
                            }

                            2 -> {
                                // TAB 3: FLEXIBLE EMI INSTALLMENTS
                                Text("Flexible Razorpay EMI Plans", fontSize = 13.sp, fontWeight = FontWeight.ExtraBold, color = NavyDark)

                                EmiOptionCard(
                                    months = 3,
                                    monthlyEmi = amount / 3.0,
                                    totalAmount = amount,
                                    isSelected = selectedEmiOption == 3,
                                    onClick = { selectedEmiOption = 3 }
                                )

                                EmiOptionCard(
                                    months = 6,
                                    monthlyEmi = (amount / 6.0) * 1.04,
                                    totalAmount = amount * 1.04,
                                    isSelected = selectedEmiOption == 6,
                                    onClick = { selectedEmiOption = 6 }
                                )

                                Button(
                                    onClick = { isProcessing = true },
                                    modifier = Modifier.fillMaxWidth().height(52.dp),
                                    shape = RoundedCornerShape(14.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0284C7))
                                ) {
                                    Icon(Icons.Default.CreditCard, null, tint = Color.White)
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("Start ${selectedEmiOption}-Month EMI Plan", fontSize = 15.sp, fontWeight = FontWeight.ExtraBold, color = Color.White)
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
private fun TabPill(text: String, isSelected: Boolean, onClick: () -> Unit, modifier: Modifier = Modifier) {
    Surface(
        color = if (isSelected) Color(0xFF0C2340) else Color.Transparent,
        shape = RoundedCornerShape(10.dp),
        modifier = modifier.clickable { onClick() }
    ) {
        Box(modifier = Modifier.padding(vertical = 10.dp), contentAlignment = Alignment.Center) {
            Text(
                text = text,
                fontSize = 12.sp,
                fontWeight = FontWeight.ExtraBold,
                color = if (isSelected) Color.White else NavyDark
            )
        }
    }
}

@Composable
private fun UpiAppCard(name: String, color: Color, icon: androidx.compose.ui.graphics.vector.ImageVector, onClick: () -> Unit, modifier: Modifier = Modifier) {
    Surface(
        color = color.copy(alpha = 0.08f),
        shape = RoundedCornerShape(12.dp),
        modifier = modifier
            .border(1.dp, color.copy(alpha = 0.3f), RoundedCornerShape(12.dp))
            .clickable { onClick() }
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(color = color, shape = CircleShape, modifier = Modifier.size(24.dp)) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(icon, null, tint = Color.White, modifier = Modifier.size(14.dp))
                }
            }
            Spacer(modifier = Modifier.width(8.dp))
            Text(name, fontSize = 13.sp, fontWeight = FontWeight.ExtraBold, color = NavyDark)
        }
    }
}

@Composable
private fun EmiOptionCard(months: Int, monthlyEmi: Double, totalAmount: Double, isSelected: Boolean, onClick: () -> Unit) {
    Surface(
        color = if (isSelected) Color(0xFFEFF6FF) else Color(0xFFFAFAFA),
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier
            .fillMaxWidth()
            .border(if (isSelected) 2.dp else 1.dp, if (isSelected) PrimaryBlue else Color(0xFFE2E8F0), RoundedCornerShape(12.dp))
            .clickable { onClick() }
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text("${months} Months Plan", fontSize = 14.sp, fontWeight = FontWeight.ExtraBold, color = NavyDark)
                Text("Total: ₹%,.0f".format(totalAmount), fontSize = 11.sp, color = TextMuted)
            }
            Column(horizontalAlignment = Alignment.End) {
                Text("₹%,.0f / mo".format(monthlyEmi), fontSize = 15.sp, fontWeight = FontWeight.ExtraBold, color = PrimaryBlue)
                Text(if (months == 3) "0% No-Cost EMI" else "Standard EMI", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = EmeraldGreen)
            }
        }
    }
}
