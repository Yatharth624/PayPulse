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
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.paypulse.ai.data.model.TransactionItem
import com.paypulse.ai.ui.theme.*

@Composable
fun WhatsAppSimulatorDialog(
    transaction: TransactionItem,
    onDismiss: () -> Unit
) {
    val scrollState = rememberScrollState()
    var selectedOption by remember { mutableStateOf<String?>(null) }
    var isPaymentCompleted by remember { mutableStateOf(false) }

    val borrowerName = transaction.customerName.ifBlank { "Vikram Singh" }
    val originalAmount = transaction.amountFormatted.ifBlank { "₹92,000" }
    val discountedAmount = "₹" + "%,.0f".format(transaction.amount * 0.95)
    val installmentAmount = "₹" + "%,.0f".format(transaction.amount / 2)

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(22.dp),
            color = Color(0xFFE5DDD5), // WhatsApp Background Chat Color
            shadowElevation = 10.dp,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxWidth()
            ) {
                // WhatsApp Header Bar (Dark Teal #075E54)
                Surface(
                    color = Color(0xFF075E54),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp, vertical = 10.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Surface(
                                color = PrimaryBlue,
                                shape = CircleShape,
                                modifier = Modifier.size(36.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Text(
                                        text = borrowerName.take(1).uppercase(),
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.ExtraBold,
                                        color = Color.White
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = borrowerName,
                                        fontSize = 15.sp,
                                        fontWeight = FontWeight.ExtraBold,
                                        color = Color.White
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Icon(
                                        imageVector = Icons.Default.Verified,
                                        contentDescription = null,
                                        tint = Color(0xFF25D366),
                                        modifier = Modifier.size(14.dp)
                                    )
                                }
                                Text(
                                    text = "PayPulse AI Verified Business",
                                    fontSize = 10.sp,
                                    color = Color(0xFFDCF8C6),
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        IconButton(onClick = onDismiss) {
                            Icon(imageVector = Icons.Default.Close, contentDescription = "Close", tint = Color.White)
                        }
                    }
                }

                // Chat Messages Body
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp)
                        .verticalScroll(scrollState),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // Date Separator Pill
                    Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                        Surface(
                            color = Color(0xFFDCF8C6),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(
                                text = "TODAY • PAYPULSE AI AUTOMATED RECOVERY",
                                fontSize = 9.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = Color(0xFF075E54),
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                            )
                        }
                    }

                    // Message 1 (Outbound PayPulse AI Business - Left/Right)
                    OutboundMessageBubble(
                        time = "10:30 AM",
                        message = "Hi $borrowerName! 👋 This is PayPulse AI on behalf of your lender.\nYour repayment of $originalAmount was scheduled for ${transaction.repaymentDeadline} (${transaction.deadlineLabel}).\n\nPay securely in 1-click via Razorpay UPI with 0 fees."
                    )

                    // Message 2 (Inbound Borrower - Right)
                    InboundMessageBubble(
                        time = "10:32 AM",
                        message = "Hey! Money has been a bit tight for me this week due to unexpected expenses. Can I get a small discount or pay in installments?"
                    )

                    // Message 3 (Outbound PayPulse AI Auto-Offer)
                    OutboundMessageBubble(
                        time = "10:33 AM",
                        message = "We completely understand, $borrowerName! 💙 Based on your good safety score of ${transaction.probabilityPct}/100, PayPulse AI has unlocked 2 exclusive settlement options for you:"
                    )

                    // Interactive Option Selector Buttons
                    if (!isPaymentCompleted) {
                        Surface(
                            color = Color.White,
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .border(1.dp, Color(0xFFCBD5E1), RoundedCornerShape(12.dp))
                        ) {
                            Column(
                                modifier = Modifier.padding(10.dp),
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Text(
                                    text = "SELECT REPAYMENT OPTION:",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = Color(0xFF075E54)
                                )

                                // Option 1: 5% Waiver
                                Button(
                                    onClick = {
                                        selectedOption = "DISCOUNT"
                                        isPaymentCompleted = true
                                    },
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(10.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = EmeraldGreen),
                                    contentPadding = PaddingValues(vertical = 8.dp, horizontal = 10.dp)
                                ) {
                                    Icon(imageVector = Icons.Default.Bolt, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("⚡ Pay $discountedAmount (5% Discount)", fontSize = 11.sp, fontWeight = FontWeight.ExtraBold)
                                }

                                // Option 2: 2 Installments
                                OutlinedButton(
                                    onClick = {
                                        selectedOption = "INSTALLMENT"
                                        isPaymentCompleted = true
                                    },
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(10.dp),
                                    colors = ButtonDefaults.outlinedButtonColors(contentColor = PrimaryBlue),
                                    contentPadding = PaddingValues(vertical = 8.dp, horizontal = 10.dp)
                                ) {
                                    Icon(imageVector = Icons.Default.Payments, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("📅 Split into 2 x $installmentAmount", fontSize = 11.sp, fontWeight = FontWeight.ExtraBold)
                                }
                            }
                        }
                    } else {
                        // Payment Completed Chat Message
                        InboundMessageBubble(
                            time = "10:35 AM",
                            message = if (selectedOption == "DISCOUNT")
                                "Selected 5% Discount option! Paying $discountedAmount via Razorpay UPI now..."
                            else
                                "Selected 2 Installments option! Paying 1st installment of $installmentAmount via Razorpay UPI now..."
                        )

                        // Payment Confirmation Receipt Bubble
                        Surface(
                            color = Color(0xFFDCF8C6),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .border(1.dp, Color(0xFF86EFAC), RoundedCornerShape(12.dp))
                        ) {
                            Column(
                                modifier = Modifier.padding(12.dp),
                                verticalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(imageVector = Icons.Default.CheckCircle, contentDescription = null, tint = EmeraldGreen, modifier = Modifier.size(18.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("Payment Confirmed via Razorpay UPI", fontSize = 12.sp, fontWeight = FontWeight.ExtraBold, color = Color(0xFF065F46))
                                }
                                Text(
                                    text = "Transaction Ref: WZP_938472 • Status: SUCCESS\nAmount Settled: ${if (selectedOption == "DISCOUNT") discountedAmount else installmentAmount}",
                                    fontSize = 10.sp,
                                    color = Color(0xFF047857),
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        OutboundMessageBubble(
                            time = "10:36 AM",
                            message = "Thank you $borrowerName! 🎉 Your payment has been received and verified. Your borrower safety score has been updated to 98/100!"
                        )

                        Button(
                            onClick = onDismiss,
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue)
                        ) {
                            Text("Done & Close Simulator", fontSize = 12.sp, fontWeight = FontWeight.ExtraBold)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun OutboundMessageBubble(time: String, message: String) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Start) {
        Surface(
            color = Color.White,
            shape = RoundedCornerShape(topStart = 0.dp, topEnd = 12.dp, bottomStart = 12.dp, bottomEnd = 12.dp),
            shadowElevation = 1.dp,
            modifier = Modifier.widthIn(max = 280.dp)
        ) {
            Column(modifier = Modifier.padding(10.dp)) {
                Text(message, fontSize = 11.sp, color = NavyDark, lineHeight = 15.sp)
                Spacer(modifier = Modifier.height(2.dp))
                Text(time, fontSize = 9.sp, color = TextMuted, modifier = Modifier.align(Alignment.End))
            }
        }
    }
}

@Composable
private fun InboundMessageBubble(time: String, message: String) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
        Surface(
            color = Color(0xFFDCF8C6),
            shape = RoundedCornerShape(topStart = 12.dp, topEnd = 0.dp, bottomStart = 12.dp, bottomEnd = 12.dp),
            shadowElevation = 1.dp,
            modifier = Modifier.widthIn(max = 280.dp)
        ) {
            Column(modifier = Modifier.padding(10.dp)) {
                Text(message, fontSize = 11.sp, color = NavyDark, lineHeight = 15.sp)
                Spacer(modifier = Modifier.height(2.dp))
                Text(time, fontSize = 9.sp, color = Color(0xFF075E54), modifier = Modifier.align(Alignment.End))
            }
        }
    }
}
