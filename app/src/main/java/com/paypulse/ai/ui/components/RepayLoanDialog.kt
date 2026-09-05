package com.paypulse.ai.ui.components

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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.paypulse.ai.data.model.LoanItem
import com.paypulse.ai.ui.theme.*

@Composable
fun RepayLoanDialog(
    loan: LoanItem,
    onDismiss: () -> Unit,
    onRecordRepayment: (repaymentAmount: Double) -> Unit
) {
    var repaymentText by remember { mutableStateOf("%.0f".format(loan.amountRemaining)) }
    val enteredRepayment = repaymentText.toDoubleOrNull() ?: loan.amountRemaining
    val newRemaining = (loan.amountRemaining - enteredRepayment).coerceAtLeast(0.0)
    val isFullRepayment = newRemaining <= 0.0

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            shape = RoundedCornerShape(24.dp),
            color = Color.White,
            shadowElevation = 12.dp,
            modifier = Modifier
                .fillMaxWidth(0.92f)
                .padding(vertical = 12.dp)
        ) {
            Column(modifier = Modifier.fillMaxWidth()) {
                
                // Content Body
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
                                color = Color(0xFFECFDF5),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.size(44.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(
                                        imageVector = Icons.Default.Payments,
                                        contentDescription = null,
                                        tint = EmeraldGreen,
                                        modifier = Modifier.size(24.dp)
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text("Record Loan Repayment", fontSize = 20.sp, fontWeight = FontWeight.ExtraBold, color = NavyDark)
                                Text("${loan.borrowerName} (${loan.id})", fontSize = 12.sp, color = TextMuted)
                            }
                        }
                        IconButton(onClick = onDismiss) {
                            Icon(imageVector = Icons.Default.Close, contentDescription = "Close", tint = TextMuted)
                        }
                    }

                    HorizontalDivider(color = Color(0xFFF1F5F9))

                    // Financial Summary Box
                    Surface(
                        color = Color(0xFFF8FAFC),
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(1.dp, Color(0xFFE2E8F0), RoundedCornerShape(14.dp))
                    ) {
                        Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text("Total Loan Principal:", fontSize = 13.sp, color = TextMuted)
                                Text(loan.principalAmountFormatted, fontSize = 13.sp, fontWeight = FontWeight.ExtraBold, color = NavyDark)
                            }
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text("Already Repaid:", fontSize = 13.sp, color = TextMuted)
                                Text(loan.amountRepaidFormatted, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = EmeraldGreen)
                            }
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text("Current Owed Balance:", fontSize = 13.sp, color = TextMuted)
                                Text(loan.amountRemainingFormatted, fontSize = 14.sp, fontWeight = FontWeight.ExtraBold, color = PrimaryBlue)
                            }
                        }
                    }

                    OutlinedTextField(
                        value = repaymentText,
                        onValueChange = { repaymentText = it },
                        label = { Text("Repayment Amount Received (₹)") },
                        leadingIcon = { Icon(Icons.Default.CurrencyRupee, null, tint = EmeraldGreen) },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        singleLine = true
                    )

                    // Live Outcome Box
                    Surface(
                        color = if (isFullRepayment) Color(0xFFDCFCE7) else Color(0xFFFEF3C7),
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Text(
                                text = if (isFullRepayment) "✓ FULL REPAYMENT: Loan will be marked REPAID" else "PARTIAL REPAYMENT DETECTED",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = if (isFullRepayment) EmeraldGreen else Color(0xFFB45309)
                            )
                            Text(
                                text = "Received: ₹%,.0f  |  New Balance Remaining: ₹%,.0f".format(enteredRepayment, newRemaining),
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = NavyDark
                            )
                        }
                    }
                }

                HorizontalDivider(color = Color(0xFFE2E8F0))

                // FIXED PERMANENT BOTTOM ACTION BAR
                Surface(
                    color = Color(0xFFF8FAFC),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Box(modifier = Modifier.padding(horizontal = 16.dp, vertical = 14.dp)) {
                        Button(
                            onClick = {
                                onRecordRepayment(enteredRepayment)
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(56.dp),
                            shape = RoundedCornerShape(16.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = EmeraldGreen,
                                contentColor = Color.White
                            ),
                            elevation = ButtonDefaults.buttonElevation(defaultElevation = 8.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center
                            ) {
                                Surface(
                                    color = Color.White.copy(alpha = 0.25f),
                                    shape = CircleShape,
                                    modifier = Modifier.size(32.dp)
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Icon(
                                            imageVector = Icons.Default.Check,
                                            contentDescription = null,
                                            tint = Color.White,
                                            modifier = Modifier.size(20.dp)
                                        )
                                    }
                                }
                                Spacer(modifier = Modifier.width(10.dp))
                                Text(
                                    text = if (isFullRepayment) "Confirm Full Payment (₹%,.0f)".format(enteredRepayment)
                                    else "Record Partial Payment (₹%,.0f)".format(enteredRepayment),
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = Color.White
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
