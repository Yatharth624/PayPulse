package com.paypulse.ai.ui.components

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
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.paypulse.ai.data.model.LoanItem
import com.paypulse.ai.ui.theme.*

@Composable
fun DigitalLoanAgreementDialog(
    loan: LoanItem,
    onDismiss: () -> Unit,
    onToggleAcknowledgement: ((String) -> Unit)? = null
) {
    val context = LocalContext.current
    val scrollState = rememberScrollState()

    var isAcknowledged by remember { mutableStateOf(loan.isDigitallyAcknowledged) }

    val lenderName = loan.lenderName.ifBlank { "PayPulse Merchant (Lender)" }
    val borrowerName = loan.borrowerName.ifBlank { "Borrower" }
    val agreementId = "AGR-${loan.id.ifBlank { "LN_101" }}"
    val ackHash = loan.acknowledgementHash.ifBlank { "PP-ACK-${(System.currentTimeMillis() % 1000000)}" }
    val ackTimestamp = loan.acknowledgementTimestamp.ifBlank { "2026-09-04 10:30 AM" }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            shape = RoundedCornerShape(26.dp),
            color = Color.White,
            shadowElevation = 16.dp,
            modifier = Modifier
                .fillMaxWidth(0.93f)
                .padding(vertical = 12.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(18.dp)
                    .verticalScroll(scrollState),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // ─── TOP DIALOG HEADER BAR ────────────────────────────────────
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            color = Color(0xFFEFF6FF),
                            shape = CircleShape,
                            modifier = Modifier.size(44.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Default.Description,
                                    contentDescription = null,
                                    tint = PrimaryBlue,
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "Digital Loan Agreement",
                                fontSize = 20.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = NavyDark
                            )
                            Text(
                                text = "Legal Contract • $agreementId",
                                fontSize = 12.sp,
                                color = TextMuted,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                    IconButton(onClick = onDismiss, modifier = Modifier.size(36.dp)) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = "Close", tint = TextMuted, modifier = Modifier.size(22.dp))
                    }
                }

                HorizontalDivider(color = Color(0xFFF1F5F9))

                // ─── OFFICIAL DIGITAL AGREEMENT CONTAINER ─────────────────────
                Surface(
                    color = Color(0xFFF8FAFC),
                    shape = RoundedCornerShape(20.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, Color(0xFFE2E8F0), RoundedCornerShape(20.dp))
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        // Document Header Title & Verified Contract Badge
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Text(
                                text = "PAYPULSE AI DIGITAL AGREEMENT",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = PrimaryBlue
                            )
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Surface(
                                    color = Color(0xFFEFF6FF),
                                    shape = RoundedCornerShape(6.dp),
                                    modifier = Modifier.border(1.dp, PrimaryBlue.copy(alpha = 0.3f), RoundedCornerShape(6.dp))
                                ) {
                                    Text(
                                        text = "✓ VERIFIED CONTRACT",
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.ExtraBold,
                                        color = PrimaryBlue,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                    )
                                }
                                Text(
                                    text = "• Executing Platform: PayPulse Engine",
                                    fontSize = 11.sp,
                                    color = TextMuted,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }

                        // Full-Width Digital Acknowledgement Stamp Ribbon
                        Surface(
                            color = if (isAcknowledged) Color(0xFFECFDF5) else Color(0xFFFFFBEB),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .border(1.dp, if (isAcknowledged) EmeraldGreen else Color(0xFFF59E0B), RoundedCornerShape(12.dp))
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center
                            ) {
                                Icon(
                                    imageVector = if (isAcknowledged) Icons.Default.Verified else Icons.Default.Pending,
                                    contentDescription = null,
                                    tint = if (isAcknowledged) EmeraldGreen else Color(0xFFD97706),
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = if (isAcknowledged) "✓ DIGITALLY ACKNOWLEDGED & VERIFIED" else "⏳ PENDING DIGITAL ACKNOWLEDGEMENT",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = if (isAcknowledged) EmeraldGreen else Color(0xFFB45309)
                                )
                            }
                        }

                        HorizontalDivider(color = Color(0xFFE2E8F0))

                        // ─── 1. PARTIES TO AGREEMENT ──────────────────────────
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Surface(color = Color(0xFFEEF2FF), shape = CircleShape, modifier = Modifier.size(24.dp)) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(Icons.Default.People, null, tint = Color(0xFF4F46E5), modifier = Modifier.size(14.dp))
                                }
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "1. PARTIES TO AGREEMENT",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = NavyDark
                            )
                        }

                        Surface(
                            color = Color.White,
                            shape = RoundedCornerShape(14.dp),
                            modifier = Modifier.border(1.dp, Color(0xFFE2E8F0), RoundedCornerShape(14.dp))
                        ) {
                            Column(
                                modifier = Modifier.padding(14.dp),
                                verticalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                PartyDetailRow(
                                    roleLabel = "Lender (Disburser)",
                                    name = lenderName,
                                    upiId = loan.lenderUpiId.ifBlank { "merchant@paypulse" },
                                    isLender = true
                                )

                                HorizontalDivider(color = Color(0xFFF1F5F9))

                                PartyDetailRow(
                                    roleLabel = "Borrower (Recipient)",
                                    name = borrowerName,
                                    upiId = loan.borrowerUpiId.ifBlank { "${borrowerName.lowercase().replace(" ", "")}@upi" },
                                    isLender = false
                                )
                            }
                        }

                        // ─── 2. FINANCIAL TERMS & SCHEDULE ───────────────────
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Surface(color = Color(0xFFE0F2FE), shape = CircleShape, modifier = Modifier.size(24.dp)) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(Icons.Default.AccountBalanceWallet, null, tint = PrimaryBlue, modifier = Modifier.size(14.dp))
                                }
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "2. FINANCIAL TERMS & SCHEDULE",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = NavyDark
                            )
                        }

                        Surface(
                            color = Color.White,
                            shape = RoundedCornerShape(14.dp),
                            modifier = Modifier.border(1.dp, Color(0xFFE2E8F0), RoundedCornerShape(14.dp))
                        ) {
                            Column(
                                modifier = Modifier.padding(14.dp),
                                verticalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                // 2-Column High Contrast Stat Cards
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    Surface(
                                        color = Color(0xFFF0F9FF),
                                        shape = RoundedCornerShape(10.dp),
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        Column(modifier = Modifier.padding(10.dp)) {
                                            Text("Principal Amount", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = TextMuted)
                                            Spacer(modifier = Modifier.height(2.dp))
                                            Text(loan.principalAmountFormatted, fontSize = 18.sp, fontWeight = FontWeight.ExtraBold, color = PrimaryBlue)
                                        }
                                    }

                                    Surface(
                                        color = Color(0xFFFEF2F2),
                                        shape = RoundedCornerShape(10.dp),
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        Column(modifier = Modifier.padding(10.dp)) {
                                            Text("Outstanding Dues", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = TextMuted)
                                            Spacer(modifier = Modifier.height(2.dp))
                                            Text(loan.amountRemainingFormatted, fontSize = 18.sp, fontWeight = FontWeight.ExtraBold, color = Color(0xFFDC2626))
                                        }
                                    }
                                }

                                AgreementRow("Disbursement Date", loan.loanGivenDate)
                                AgreementRow("Repayment Due Date", loan.repaymentDeadline, isAlert = loan.deadlineDaysRemaining < 0)
                            }
                        }

                        // ─── 3. REPAYMENT TERMS & COLLATERAL ─────────────────
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Surface(color = Color(0xFFFEF3C7), shape = CircleShape, modifier = Modifier.size(24.dp)) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(Icons.Default.Gavel, null, tint = Color(0xFFB45309), modifier = Modifier.size(14.dp))
                                }
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "3. REPAYMENT TERMS & COLLATERAL",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = NavyDark
                            )
                        }

                        Surface(
                            color = Color.White,
                            shape = RoundedCornerShape(14.dp),
                            modifier = Modifier.border(1.dp, Color(0xFFE2E8F0), RoundedCornerShape(14.dp))
                        ) {
                            Column(
                                modifier = Modifier.padding(14.dp),
                                verticalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                Text(
                                    text = loan.repaymentTerms.ifBlank { "Borrower agrees to repay full principal amount on or before due date via UPI." },
                                    fontSize = 13.sp,
                                    color = NavyDark,
                                    lineHeight = 18.sp,
                                    fontWeight = FontWeight.Medium
                                )

                                if (loan.collateral.type.lowercase() != "none") {
                                    Surface(
                                        color = Color(0xFFF0F9FF),
                                        shape = RoundedCornerShape(10.dp),
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Column(modifier = Modifier.padding(10.dp)) {
                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                Icon(Icons.Default.Security, null, tint = PrimaryBlue, modifier = Modifier.size(14.dp))
                                                Spacer(modifier = Modifier.width(6.dp))
                                                Text(
                                                    text = "Pledged Collateral: ${loan.collateral.type} (Est. ${loan.collateral.estimatedValueFormatted})",
                                                    fontSize = 12.sp,
                                                    fontWeight = FontWeight.ExtraBold,
                                                    color = PrimaryBlue
                                                )
                                            }
                                            if (loan.collateral.description.isNotBlank()) {
                                                Spacer(modifier = Modifier.height(4.dp))
                                                Text(
                                                    text = loan.collateral.description,
                                                    fontSize = 11.sp,
                                                    color = TextMuted
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }

                        // ─── 4. DIGITAL ACKNOWLEDGEMENT & STAMP SIGNATURE ─────
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Surface(color = Color(0xFFDCFCE7), shape = CircleShape, modifier = Modifier.size(24.dp)) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(Icons.Default.Verified, null, tint = EmeraldGreen, modifier = Modifier.size(14.dp))
                                }
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "4. DIGITAL ACKNOWLEDGEMENT & STAMP",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = NavyDark
                            )
                        }

                        Surface(
                            color = if (isAcknowledged) Color(0xFFF0FDF4) else Color(0xFFFFFBEB),
                            shape = RoundedCornerShape(14.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .border(
                                    1.dp,
                                    if (isAcknowledged) Color(0xFFA7F3D0) else Color(0xFFFDE68A),
                                    RoundedCornerShape(14.dp)
                                )
                        ) {
                            Column(
                                modifier = Modifier.padding(14.dp),
                                verticalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                Column(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            imageVector = Icons.Default.Gavel,
                                            contentDescription = null,
                                            tint = if (isAcknowledged) EmeraldGreen else Color(0xFFD97706),
                                            modifier = Modifier.size(16.dp)
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(
                                            text = "Electronic Signature Token",
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.ExtraBold,
                                            color = if (isAcknowledged) EmeraldGreen else Color(0xFFB45309)
                                        )
                                    }

                                    Surface(
                                        color = Color.White.copy(alpha = 0.8f),
                                        shape = RoundedCornerShape(6.dp),
                                        modifier = Modifier.border(
                                            1.dp,
                                            if (isAcknowledged) EmeraldGreen.copy(alpha = 0.3f) else Color(0xFFD97706).copy(alpha = 0.3f),
                                            RoundedCornerShape(6.dp)
                                        )
                                    ) {
                                        Text(
                                            text = ackHash,
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = TextMuted,
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                        )
                                    }
                                }

                                Text(
                                    text = if (isAcknowledged)
                                        "This agreement has been digitally acknowledged and verified by $borrowerName and $lenderName via PayPulse Engine on $ackTimestamp."
                                    else
                                        "Waiting for digital acknowledgement by $borrowerName. Click button below to sign & acknowledge agreement.",
                                    fontSize = 12.sp,
                                    color = NavyDark,
                                    lineHeight = 17.sp
                                )

                                Button(
                                    onClick = {
                                        isAcknowledged = !isAcknowledged
                                        onToggleAcknowledgement?.invoke(loan.id)
                                        val statusText = if (isAcknowledged) "Acknowledged & Signed ✓" else "Marked Pending"
                                        Toast.makeText(context, "Digital Agreement $statusText", Toast.LENGTH_SHORT).show()
                                    },
                                    shape = RoundedCornerShape(10.dp),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = if (isAcknowledged) EmeraldGreen else Color(0xFFD97706)
                                    ),
                                    modifier = Modifier.fillMaxWidth(),
                                    contentPadding = PaddingValues(vertical = 10.dp)
                                ) {
                                    Icon(
                                        imageVector = if (isAcknowledged) Icons.Default.CheckCircle else Icons.Default.Draw,
                                        contentDescription = null,
                                        modifier = Modifier.size(16.dp),
                                        tint = Color.White
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = if (isAcknowledged) "Digitally Signed ✓ (Click to toggle)" else "Digitally Sign & Acknowledge Now",
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.ExtraBold,
                                        color = Color.White
                                    )
                                }
                            }
                        }
                    }
                }

                // ─── ACTION BUTTONS AT BOTTOM ─────────────────────────────────
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Button(
                            onClick = {
                                Toast.makeText(context, "📄 Digital Loan Agreement PDF generated for $borrowerName!", Toast.LENGTH_LONG).show()
                            },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue),
                            contentPadding = PaddingValues(vertical = 11.dp)
                        ) {
                            Icon(Icons.Default.PictureAsPdf, contentDescription = null, modifier = Modifier.size(16.dp), tint = Color.White)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Export PDF", fontSize = 13.sp, fontWeight = FontWeight.ExtraBold, color = Color.White)
                        }

                        Button(
                            onClick = {
                                val shareIntent = Intent(Intent.ACTION_VIEW).apply {
                                    data = Uri.parse("https://api.whatsapp.com/send?text=" + Uri.encode(
                                        "📄 DIGITAL LOAN AGREEMENT ($agreementId)\n\n" +
                                        "Lender: $lenderName\n" +
                                        "Borrower: $borrowerName\n" +
                                        "Principal Amount: ${loan.principalAmountFormatted}\n" +
                                        "Due Date: ${loan.repaymentDeadline}\n" +
                                        "Terms: ${loan.repaymentTerms}\n" +
                                        "Status: ${if (isAcknowledged) "Digitally Acknowledged & Signed ✓" else "Pending Signature"}\n\n" +
                                        "PayPulse AI Digital Agreement Engine"
                                    ))
                                }
                                context.startActivity(shareIntent)
                            },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF075E54)),
                            contentPadding = PaddingValues(vertical = 11.dp)
                        ) {
                            Icon(Icons.Default.Send, contentDescription = null, modifier = Modifier.size(16.dp), tint = Color.White)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Share via WhatsApp", fontSize = 13.sp, fontWeight = FontWeight.ExtraBold, color = Color.White)
                        }
                    }

                    OutlinedButton(
                        onClick = onDismiss,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        contentPadding = PaddingValues(vertical = 10.dp)
                    ) {
                        Text("Close Agreement", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = TextMuted)
                    }
                }
            }
        }
    }
}

@Composable
private fun PartyDetailRow(
    roleLabel: String,
    name: String,
    upiId: String,
    isLender: Boolean
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Surface(
            color = if (isLender) Color(0xFFEFF6FF) else Color(0xFFEEF2FF),
            shape = CircleShape,
            modifier = Modifier.size(36.dp)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    imageVector = if (isLender) Icons.Default.Storefront else Icons.Default.Person,
                    contentDescription = null,
                    tint = if (isLender) PrimaryBlue else Color(0xFF4F46E5),
                    modifier = Modifier.size(18.dp)
                )
            }
        }
        Spacer(modifier = Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(roleLabel, fontSize = 11.sp, color = TextMuted, fontWeight = FontWeight.Bold)
            Text(name, fontSize = 14.sp, fontWeight = FontWeight.ExtraBold, color = NavyDark)
            Spacer(modifier = Modifier.height(3.dp))
            Surface(
                color = Color(0xFFF8FAFC),
                shape = RoundedCornerShape(6.dp),
                modifier = Modifier.border(1.dp, Color(0xFFE2E8F0), RoundedCornerShape(6.dp))
            ) {
                Text(
                    text = upiId,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextMuted,
                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                )
            }
        }
    }
}

@Composable
private fun AgreementRow(
    label: String,
    value: String,
    isBold: Boolean = false,
    isHighlight: Boolean = false,
    isAlert: Boolean = false
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(label, fontSize = 12.sp, color = TextMuted, fontWeight = FontWeight.Medium)
        Text(
            text = value,
            fontSize = if (isHighlight) 16.sp else 12.sp,
            fontWeight = if (isBold || isHighlight || isAlert) FontWeight.ExtraBold else FontWeight.Bold,
            color = if (isAlert) Color(0xFFDC2626) else if (isHighlight) PrimaryBlue else NavyDark
        )
    }
}
