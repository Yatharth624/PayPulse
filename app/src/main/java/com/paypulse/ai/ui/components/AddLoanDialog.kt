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
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.paypulse.ai.data.model.CollateralInfo
import com.paypulse.ai.ui.theme.*
import com.paypulse.ai.ui.utils.UpiPaymentLauncher

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddLoanDialog(
    onDismiss: () -> Unit,
    onAddLoan: (
        borrowerName: String,
        borrowerUpiId: String,
        amount: Double,
        repaymentDeadline: String,
        collateral: CollateralInfo,
        notes: String,
        lenderName: String,
        repaymentTerms: String
    ) -> Unit
) {
    val context = LocalContext.current

    var loanType by remember { mutableStateOf("GIVE") } // "GIVE" (Giving a Loan) or "TAKE" (Taking a Loan)
    var borrowerName by remember { mutableStateOf("Dinesh") }
    var borrowerUpiId by remember { mutableStateOf("dinesh@upi") }
    var lenderName by remember { mutableStateOf("PayPulse Merchant (Lender)") }
    var amountText by remember { mutableStateOf("20000") }
    var deadlineText by remember { mutableStateOf("2026-10-02") }
    var repaymentTerms by remember { mutableStateOf("Single lump sum payment on or before repayment due date via UPI. 0% interest personal loan.") }
    var collateralType by remember { mutableStateOf("Gold") }
    var collateralValueText by remember { mutableStateOf("25000") }
    var collateralDescription by remember { mutableStateOf("Gold chain collateral pledged for safety") }
    var notes by remember { mutableStateOf("Personal business working capital") }
    var showAgreementPreview by remember { mutableStateOf(false) }

    val amountVal = amountText.toDoubleOrNull() ?: 20000.0
    val collateralVal = collateralValueText.toDoubleOrNull() ?: 25000.0

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            shape = RoundedCornerShape(24.dp),
            color = Color.White,
            shadowElevation = 12.dp,
            modifier = Modifier
                .fillMaxWidth(0.94f)
                .padding(vertical = 12.dp)
        ) {
            Column(modifier = Modifier.fillMaxWidth()) {
                
                // ─── 1. FIXED TOP HEADER & TOGGLE ───────────────────────────────────
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Surface(
                                color = if (loanType == "GIVE") Color(0xFFEFF6FF) else Color(0xFFECFDF5),
                                shape = RoundedCornerShape(14.dp),
                                modifier = Modifier.size(44.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(
                                        imageVector = if (loanType == "GIVE") Icons.Default.CallMade else Icons.Default.CallReceived,
                                        contentDescription = null,
                                        tint = if (loanType == "GIVE") PrimaryBlue else Color(0xFF059669),
                                        modifier = Modifier.size(24.dp)
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = if (loanType == "GIVE") "Give a Loan" else "Take a Loan",
                                    fontSize = 22.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = NavyDark
                                )
                                Text(
                                    text = if (loanType == "GIVE") "Disburse money & track repayment" else "Record money borrowed from lender",
                                    fontSize = 12.sp,
                                    color = TextMuted
                                )
                            }
                        }

                        IconButton(onClick = onDismiss) {
                            Icon(imageVector = Icons.Default.Close, contentDescription = "Close", tint = TextMuted)
                        }
                    }

                    // SIMPLIFIED SEGMENTED TOGGLE (Give Loan vs Take Loan)
                    Surface(
                        color = Color(0xFFF1F5F9),
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(4.dp),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Surface(
                                color = if (loanType == "GIVE") PrimaryBlue else Color.Transparent,
                                shape = RoundedCornerShape(11.dp),
                                modifier = Modifier
                                    .weight(1f)
                                    .clickable {
                                        loanType = "GIVE"
                                        if (borrowerName == "Ramesh Kumar (Lender)") borrowerName = "Dinesh"
                                        if (borrowerUpiId == "ramesh@upi") borrowerUpiId = "dinesh@upi"
                                    }
                            ) {
                                Row(
                                    modifier = Modifier.padding(vertical = 11.dp),
                                    horizontalArrangement = Arrangement.Center,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.CallMade,
                                        contentDescription = null,
                                        tint = if (loanType == "GIVE") Color.White else NavyDark,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "Give Loan (Lend)",
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.ExtraBold,
                                        color = if (loanType == "GIVE") Color.White else NavyDark
                                    )
                                }
                            }

                            Surface(
                                color = if (loanType == "TAKE") Color(0xFF059669) else Color.Transparent,
                                shape = RoundedCornerShape(11.dp),
                                modifier = Modifier
                                    .weight(1f)
                                    .clickable {
                                        loanType = "TAKE"
                                        if (borrowerName == "Dinesh") borrowerName = "Ramesh Kumar (Lender)"
                                        if (borrowerUpiId == "dinesh@upi") borrowerUpiId = "ramesh@upi"
                                    }
                            ) {
                                Row(
                                    modifier = Modifier.padding(vertical = 11.dp),
                                    horizontalArrangement = Arrangement.Center,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.CallReceived,
                                        contentDescription = null,
                                        tint = if (loanType == "TAKE") Color.White else NavyDark,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "Take Loan (Borrow)",
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.ExtraBold,
                                        color = if (loanType == "TAKE") Color.White else NavyDark
                                    )
                                }
                            }
                        }
                    }
                }

                HorizontalDivider(color = Color(0xFFF1F5F9))

                // ─── 2. SCROLLABLE FORM FIELDS ───────────────────────────────────────
                Column(
                    modifier = Modifier
                        .weight(1f, fill = false)
                        .verticalScroll(rememberScrollState())
                        .padding(horizontal = 20.dp, vertical = 12.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    // Section 1: Person Details (Borrower vs Lender)
                    Text(
                        text = if (loanType == "GIVE") "1. Borrower Details (Person receiving loan)" else "1. Lender Details (Person giving loan)",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = if (loanType == "GIVE") PrimaryBlue else Color(0xFF059669)
                    )

                    OutlinedTextField(
                        value = borrowerName,
                        onValueChange = { borrowerName = it },
                        label = { Text(if (loanType == "GIVE") "Borrower Name" else "Lender Name") },
                        leadingIcon = { Icon(Icons.Default.Person, null, tint = if (loanType == "GIVE") PrimaryBlue else Color(0xFF059669)) },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        singleLine = true
                    )

                    OutlinedTextField(
                        value = borrowerUpiId,
                        onValueChange = { borrowerUpiId = it },
                        label = { Text(if (loanType == "GIVE") "Borrower UPI ID / VPA" else "Lender UPI ID / VPA") },
                        leadingIcon = { Icon(Icons.Default.QrCodeScanner, null, tint = if (loanType == "GIVE") PrimaryBlue else Color(0xFF059669)) },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        singleLine = true
                    )

                    // Section 2: Loan Financials
                    Text("2. Loan Financials & Terms", fontSize = 14.sp, fontWeight = FontWeight.ExtraBold, color = NavyDark)

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        OutlinedTextField(
                            value = amountText,
                            onValueChange = { amountText = it },
                            label = { Text("Amount (₹)") },
                            leadingIcon = { Icon(Icons.Default.CurrencyRupee, null, tint = NavyDark) },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp),
                            singleLine = true
                        )

                        OutlinedTextField(
                            value = deadlineText,
                            onValueChange = { deadlineText = it },
                            label = { Text("Repayment Due Date") },
                            leadingIcon = { Icon(Icons.Default.Event, null, tint = NavyDark) },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp),
                            singleLine = true
                        )
                    }

                    // Section 3: Collateral / Security
                    Text("3. Collateral & Security (Optional)", fontSize = 14.sp, fontWeight = FontWeight.ExtraBold, color = NavyDark)

                    var collateralDropdownExpanded by remember { mutableStateOf(false) }
                    val collateralOptions = listOf("Gold / Jewelry", "Real Estate / Property", "Electronics / Laptop", "Vehicle / Automobile", "Fixed Deposit / Stocks", "Other")
                    var customOtherCollateral by remember { mutableStateOf("") }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Box(modifier = Modifier.weight(1f)) {
                            OutlinedTextField(
                                value = collateralType,
                                onValueChange = {},
                                readOnly = true,
                                label = { Text("Collateral Type") },
                                leadingIcon = { Icon(Icons.Default.Security, null, tint = NavyDark) },
                                trailingIcon = { Icon(Icons.Default.ArrowDropDown, null, modifier = Modifier.clickable { collateralDropdownExpanded = true }) },
                                modifier = Modifier.fillMaxWidth().clickable { collateralDropdownExpanded = true },
                                shape = RoundedCornerShape(12.dp),
                                singleLine = true
                            )

                            DropdownMenu(
                                expanded = collateralDropdownExpanded,
                                onDismissRequest = { collateralDropdownExpanded = false }
                            ) {
                                collateralOptions.forEach { option ->
                                    DropdownMenuItem(
                                        text = { Text(option, fontSize = 13.sp, fontWeight = FontWeight.Bold) },
                                        onClick = {
                                            collateralType = option
                                            collateralDropdownExpanded = false
                                        }
                                    )
                                }
                            }
                        }

                        OutlinedTextField(
                            value = collateralValueText,
                            onValueChange = { collateralValueText = it },
                            label = { Text("Est. Value (₹)") },
                            leadingIcon = { Icon(Icons.Default.Savings, null, tint = NavyDark) },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp),
                            singleLine = true
                        )
                    }

                    if (collateralType == "Other") {
                        OutlinedTextField(
                            value = customOtherCollateral,
                            onValueChange = { customOtherCollateral = it },
                            label = { Text("Specify Custom Collateral Type") },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            singleLine = true
                        )
                    }

                    OutlinedTextField(
                        value = collateralDescription,
                        onValueChange = { collateralDescription = it },
                        label = { Text("Collateral Description & Notes") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        maxLines = 2
                    )

                    // ─── DUAL METRIC EVALUATION (AFFORDABILITY VS RISK) ───────────────
                    var isAffordabilityCalculatorOpen by remember { mutableStateOf(false) }
                    var affordabilityScoreVal by remember { mutableStateOf(78) }

                    if (isAffordabilityCalculatorOpen) {
                        AffordabilityDialog(
                            initialLoanAmount = amountVal,
                            initialDurationMonths = 6,
                            onDismiss = { isAffordabilityCalculatorOpen = false },
                            onApplyAssessment = { score, _ ->
                                affordabilityScoreVal = score
                                isAffordabilityCalculatorOpen = false
                            }
                        )
                    }

                    Surface(
                        color = Color(0xFFF8FAFC),
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(1.dp, Color(0xFFE2E8F0), RoundedCornerShape(16.dp))
                    ) {
                        Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("PayPulse AI Dual Evaluation", fontSize = 13.sp, fontWeight = FontWeight.ExtraBold, color = NavyDark)
                                OutlinedButton(
                                    onClick = { isAffordabilityCalculatorOpen = true },
                                    shape = RoundedCornerShape(8.dp),
                                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp)
                                ) {
                                    Icon(Icons.Default.Calculate, null, modifier = Modifier.size(14.dp), tint = PrimaryBlue)
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Check Affordability", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = PrimaryBlue)
                                }
                            }

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                // Metric 1: Affordability Score
                                Surface(
                                    color = Color(0xFFECFDF5),
                                    shape = RoundedCornerShape(12.dp),
                                    modifier = Modifier.weight(1f).border(1.dp, Color(0xFFA7F3D0), RoundedCornerShape(12.dp))
                                ) {
                                    Column(modifier = Modifier.padding(10.dp)) {
                                        Text("Affordability Score", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = TextMuted)
                                        Text("$affordabilityScoreVal / 100 🟢", fontSize = 15.sp, fontWeight = FontWeight.ExtraBold, color = EmeraldGreen)
                                        Text("Can borrower handle repayment?", fontSize = 9.sp, color = TextMuted)
                                    }
                                }

                                // Metric 2: Repayment Risk Score
                                Surface(
                                    color = Color(0xFFEFF6FF),
                                    shape = RoundedCornerShape(12.dp),
                                    modifier = Modifier.weight(1f).border(1.dp, Color(0xFFBFDBFE), RoundedCornerShape(12.dp))
                                ) {
                                    Column(modifier = Modifier.padding(10.dp)) {
                                        Text("Repayment Risk Score", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = TextMuted)
                                        Text("87 / 100 🟢", fontSize = 15.sp, fontWeight = FontWeight.ExtraBold, color = PrimaryBlue)
                                        Text("Historical repayment reliability", fontSize = 9.sp, color = TextMuted)
                                    }
                                }
                            }
                        }
                    }

                    // Section 4: Digital Loan Agreement & Terms
                    Text("4. Digital Loan Agreement & Acknowledgement", fontSize = 14.sp, fontWeight = FontWeight.ExtraBold, color = NavyDark)

                    OutlinedTextField(
                        value = lenderName,
                        onValueChange = { lenderName = it },
                        label = { Text("Lender Name (Disburser)") },
                        leadingIcon = { Icon(Icons.Default.AccountBalance, null, tint = NavyDark) },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        singleLine = true
                    )

                    OutlinedTextField(
                        value = repaymentTerms,
                        onValueChange = { repaymentTerms = it },
                        label = { Text("Repayment Terms & Agreement Clauses") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        maxLines = 3
                    )

                    OutlinedButton(
                        onClick = { showAgreementPreview = true },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp)
                    ) {
                        Icon(Icons.Default.Description, null, modifier = Modifier.size(16.dp), tint = PrimaryBlue)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("📄 Preview Digital Loan Agreement", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = PrimaryBlue)
                    }
                }

                HorizontalDivider(color = Color(0xFFE2E8F0))

                // ─── 3. FIXED PERMANENT BOTTOM ACTION BAR ───────────────────────────
                Surface(
                    color = Color(0xFFF8FAFC),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Box(modifier = Modifier.padding(horizontal = 16.dp, vertical = 14.dp)) {
                        Button(
                            onClick = {
                                val finalName = borrowerName.ifBlank { if (loanType == "GIVE") "Dinesh" else "Ramesh Kumar (Lender)" }
                                val finalUpi = borrowerUpiId.ifBlank { if (loanType == "GIVE") "dinesh@upi" else "ramesh@upi" }

                                val collateralObj = CollateralInfo(
                                    type = collateralType.ifBlank { "None" },
                                    estimatedValue = collateralVal,
                                    estimatedValueFormatted = "₹%,.0f".format(collateralVal),
                                    description = collateralDescription
                                )
                                onAddLoan(
                                    finalName,
                                    finalUpi,
                                    amountVal,
                                    deadlineText,
                                    collateralObj,
                                    notes,
                                    lenderName.ifBlank { "PayPulse Merchant (Lender)" },
                                    repaymentTerms.ifBlank { "Single lump sum payment on or before due date via UPI. 0% interest personal loan." }
                                )
                                if (loanType == "GIVE") {
                                    UpiPaymentLauncher.launchUpiPayment(context, "DISBURSE_${System.currentTimeMillis() % 10000}", amountVal, finalName)
                                }
                                onDismiss()
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(56.dp),
                            shape = RoundedCornerShape(16.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (loanType == "GIVE") PrimaryBlue else Color(0xFF059669)
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
                                            imageVector = if (loanType == "GIVE") Icons.Default.Send else Icons.Default.Check,
                                            contentDescription = null,
                                            tint = Color.White,
                                            modifier = Modifier.size(20.dp)
                                        )
                                    }
                                }
                                Spacer(modifier = Modifier.width(10.dp))
                                Text(
                                    text = if (loanType == "GIVE") "Disburse ₹%,.0f via UPI".format(amountVal)
                                    else "Save & Record ₹%,.0f Borrowed Loan".format(amountVal),
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

        if (showAgreementPreview) {
            val previewLoan = com.paypulse.ai.data.model.LoanItem(
                borrowerName = borrowerName.ifBlank { "Dinesh" },
                borrowerUpiId = borrowerUpiId.ifBlank { "dinesh@upi" },
                lenderName = lenderName.ifBlank { "PayPulse Merchant (Lender)" },
                principalAmount = amountVal,
                principalAmountFormatted = "₹%,.0f".format(amountVal),
                amountRemaining = amountVal,
                amountRemainingFormatted = "₹%,.0f".format(amountVal),
                repaymentDeadline = deadlineText,
                repaymentTerms = repaymentTerms,
                collateral = CollateralInfo(
                    type = collateralType,
                    estimatedValue = collateralVal,
                    estimatedValueFormatted = "₹%,.0f".format(collateralVal),
                    description = collateralDescription
                )
            )
            DigitalLoanAgreementDialog(
                loan = previewLoan,
                onDismiss = { showAgreementPreview = false }
            )
        }
    }
}
