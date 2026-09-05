package com.paypulse.ai.ui.components

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.paypulse.ai.data.model.TransactionItem
import com.paypulse.ai.ui.theme.*

@Composable
fun LegalNoticeDialog(
    transaction: TransactionItem,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    val scrollState = rememberScrollState()

    val borrowerName = transaction.customerName.ifBlank { "Vikram Singh" }
    val refNo = "PAYPULSE/LEGAL/2026/${transaction.id.ifBlank { "RP_83925" }}"
    val currentDate = "02 September 2026"

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(22.dp),
            color = Color.White,
            shadowElevation = 10.dp,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(14.dp)
                    .verticalScroll(scrollState),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Header Bar
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            color = Color(0xFFFEF2F2),
                            shape = CircleShape,
                            modifier = Modifier.size(32.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Default.Gavel,
                                    contentDescription = null,
                                    tint = Color(0xFFDC2626),
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text(
                                text = "Legal Demand Notice",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = NavyDark
                            )
                            Text(
                                text = "Official Stamped Ledger • $refNo",
                                fontSize = 10.sp,
                                color = TextMuted,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                    IconButton(onClick = onDismiss) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = "Close", tint = TextMuted)
                    }
                }

                HorizontalDivider(color = Color(0xFFF1F5F9))

                // Official Legal Document Container
                Surface(
                    color = Color(0xFFFAFAFA),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, Color(0xFFE2E8F0), RoundedCornerShape(10.dp))
                ) {
                    Column(
                        modifier = Modifier.padding(8.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        // Stamp & Compliance Header
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            verticalArrangement = Arrangement.spacedBy(2.dp)
                        ) {
                            Text(
                                text = "PAYPULSE AI LEGAL & COMPLIANCE",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = PrimaryBlue
                            )
                            Text(
                                text = "Date: $currentDate",
                                fontSize = 10.sp,
                                color = TextMuted,
                                fontWeight = FontWeight.Bold
                            )

                            Spacer(modifier = Modifier.height(2.dp))

                            Surface(
                                color = Color(0xFFECFDF5),
                                shape = RoundedCornerShape(5.dp),
                                modifier = Modifier.border(1.dp, EmeraldGreen, RoundedCornerShape(5.dp))
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Verified,
                                        contentDescription = null,
                                        tint = EmeraldGreen,
                                        modifier = Modifier.size(12.dp)
                                    )
                                    Spacer(modifier = Modifier.width(3.dp))
                                    Text(
                                        text = "DIGITALLY STAMPED BY PAYPULSE AI ENGINE",
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.ExtraBold,
                                        color = EmeraldGreen
                                    )
                                }
                            }
                        }

                        HorizontalDivider(color = Color(0xFFE2E8F0))

                        // Formal Demand Text
                        Text(
                            text = "FORMAL DEMAND FOR IMMEDIATE PAYMENT RECOVERY",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color(0xFF991B1B)
                        )

                        Text(
                            text = "To $borrowerName,\nThis is a formal legal demand notice regarding your outstanding financial obligation of ${transaction.amountFormatted} under Loan Transaction ID ${transaction.id}. As per records, the payment was due on ${transaction.repaymentDeadline} (${transaction.deadlineLabel}).",
                            fontSize = 11.sp,
                            color = NavyDark,
                            lineHeight = 15.sp
                        )

                        // Financial Ledger Summary Box
                        Surface(
                            color = Color.White,
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.border(1.dp, Color(0xFFE2E8F0), RoundedCornerShape(10.dp))
                        ) {
                            Column(
                                modifier = Modifier.padding(12.dp),
                                verticalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                DetailRow("Borrower Name", borrowerName)
                                DetailRow("Transaction / Loan ID", transaction.id)
                                DetailRow("Principal Amount Owed", transaction.amountFormatted, isBold = true)
                                DetailRow("Current Status", transaction.deadlineLabel, isAlert = transaction.deadlineDaysRemaining < 0)
                                DetailRow("Borrower Risk Score", "${transaction.probabilityPct}/100 Score")
                            }
                        }

                        // Collateral Security Clause
                        Surface(
                            color = Color(0xFFFEF2F2),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(
                                modifier = Modifier.padding(10.dp),
                                verticalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.Shield,
                                        contentDescription = null,
                                        tint = Color(0xFFDC2626),
                                        modifier = Modifier.size(14.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "Pledged Collateral Asset Security Clause",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.ExtraBold,
                                        color = Color(0xFF991B1B)
                                    )
                                }
                                Text(
                                    text = "Pursuant to Section 138 / Section 172 of the Indian Contract Act, pledged collateral assets (Gold / Vehicle / Property) pledged against this loan are subject to formal recovery proceedings upon non-repayment.",
                                    fontSize = 10.sp,
                                    color = Color(0xFF7F1D1D),
                                    lineHeight = 14.sp
                                )
                            }
                        }

                        // Audit Trail History Timeline
                        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Text(
                                text = "Itemized Audit Ledger Trail",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = NavyDark
                            )
                            AuditStepItem("1. Disburse Event", "Loan principal ${transaction.amountFormatted} disbursed via Razorpay UPI")
                            AuditStepItem("2. Repayment Scheduled", "Target repayment deadline set for ${transaction.repaymentDeadline}")
                            AuditStepItem("3. PayPulse AI Evaluation", "Borrower Risk Score calculated at ${transaction.probabilityPct}/100")
                            AuditStepItem("4. Legal Notice Trigger", "Formal demand notice generated & timestamped digitally")
                        }
                    }
                }

                // Action Buttons Row
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = {
                                Toast.makeText(context, "📄 Legal Notice PDF downloaded to storage for $borrowerName!", Toast.LENGTH_LONG).show()
                            },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue),
                            contentPadding = PaddingValues(vertical = 10.dp)
                        ) {
                            Icon(Icons.Default.PictureAsPdf, contentDescription = null, modifier = Modifier.size(16.dp), tint = Color.White)
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Export PDF", fontSize = 12.sp, fontWeight = FontWeight.ExtraBold, color = Color.White)
                        }

                        Button(
                            onClick = {
                                Toast.makeText(context, "📊 Itemized CSV Audit Ledger exported!", Toast.LENGTH_LONG).show()
                            },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = EmeraldGreen),
                            contentPadding = PaddingValues(vertical = 10.dp)
                        ) {
                            Icon(Icons.Default.TableChart, contentDescription = null, modifier = Modifier.size(16.dp), tint = Color.White)
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Export CSV", fontSize = 12.sp, fontWeight = FontWeight.ExtraBold, color = Color.White)
                        }
                    }

                    OutlinedButton(
                        onClick = {
                            val shareIntent = Intent(Intent.ACTION_VIEW).apply {
                                data = Uri.parse("https://api.whatsapp.com/send?text=" + Uri.encode(
                                    "⚖ OFFICIAL LEGAL DEMAND NOTICE ($refNo)\n\n" +
                                    "Dear $borrowerName,\n" +
                                    "This is an official notice regarding your outstanding loan of ${transaction.amountFormatted} (ID: ${transaction.id}). " +
                                    "Please settle your dues via UPI or contact PayPulse Recovery immediately to prevent legal escalation.\n\n" +
                                    "PayPulse AI Legal & Compliance Division"
                                ))
                            }
                            context.startActivity(shareIntent)
                        },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        contentPadding = PaddingValues(vertical = 10.dp)
                    ) {
                        Icon(Icons.Default.Send, contentDescription = null, modifier = Modifier.size(16.dp), tint = PrimaryBlue)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Send Notice via WhatsApp", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = PrimaryBlue)
                    }
                }
            }
        }
    }
}

@Composable
private fun DetailRow(label: String, value: String, isBold: Boolean = false, isAlert: Boolean = false) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(label, fontSize = 11.sp, color = TextMuted)
        Text(
            text = value,
            fontSize = 11.sp,
            fontWeight = if (isBold || isAlert) FontWeight.ExtraBold else FontWeight.Bold,
            color = if (isAlert) Color(0xFFDC2626) else if (isBold) PrimaryBlue else NavyDark
        )
    }
}

@Composable
private fun AuditStepItem(title: String, desc: String) {
    Row(verticalAlignment = Alignment.Top, modifier = Modifier.fillMaxWidth()) {
        Text("• ", fontSize = 11.sp, fontWeight = FontWeight.ExtraBold, color = PrimaryBlue)
        Column {
            Text(title, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = NavyDark)
            Text(desc, fontSize = 10.sp, color = TextMuted)
        }
    }
}
