package com.paypulse.ai.ui.screens

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
import com.paypulse.ai.data.model.LoanItem
import com.paypulse.ai.data.model.TransactionItem
import com.paypulse.ai.ui.theme.*
import com.paypulse.ai.ui.utils.SmartAlertEngine
import com.paypulse.ai.ui.utils.SmartAlertStage
import com.paypulse.ai.ui.utils.UpiPaymentLauncher
import java.util.*

private data class PaymentCalendarEvent(
    val dayOfMonth: Int,
    val month: Int, // 1-12
    val year: Int,
    val amount: Double,
    val amountFormatted: String,
    val title: String,
    val subTitle: String,
    val categoryTag: String,
    val status: String, // PAID, UPCOMING, OVERDUE
    val loan: LoanItem? = null,
    val transaction: TransactionItem? = null
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CalendarScreen(
    loans: List<LoanItem> = emptyList(),
    transactions: List<TransactionItem> = emptyList(),
    onOpenAddLoan: () -> Unit = {},
    onOpenRepayLoan: (LoanItem) -> Unit = {},
    onOpenBorrowerProfile: (String) -> Unit = {},
    onReviewTransaction: (String) -> Unit = {},
    onOpenRazorpayCheckout: (id: String, amount: Double, title: String) -> Unit = { _, _, _ -> }
) {
    val context = LocalContext.current
    val todayCalendar = Calendar.getInstance()
    val currentTodayDay = todayCalendar.get(Calendar.DAY_OF_MONTH)

    var selectedYear by remember { mutableStateOf(2026) }
    var selectedMonth by remember { mutableStateOf(9) } // September (9)
    var selectedDay by remember { mutableStateOf(currentTodayDay) }

    val monthNames = listOf("January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December")
    val selectedMonthName = monthNames.getOrElse(selectedMonth - 1) { "September" }

    // DYNAMIC PARSING: Combines BOTH Loans AND Queue Screen Transactions automatically into Calendar Events
    val paymentEventsMap = remember(loans, transactions, selectedMonth, selectedYear) {
        val map = mutableMapOf<Int, MutableList<PaymentCalendarEvent>>()

        // 1. Process Loans List
        loans.forEach { loan ->
            try {
                val parts = loan.repaymentDeadline.split("-")
                if (parts.size >= 3) {
                    val y = parts[0].toIntOrNull() ?: 2026
                    val m = parts[1].toIntOrNull() ?: 9
                    val d = parts[2].toIntOrNull() ?: 7

                    if (y == selectedYear && m == selectedMonth) {
                        val eventStatus = when {
                            loan.status == "REPAID" -> "PAID"
                            loan.status == "OVERDUE" || (loan.repaymentDeadline < "2026-09-02" && loan.status != "REPAID") -> "OVERDUE"
                            else -> "UPCOMING"
                        }
                        val amt = if (loan.amountRemaining > 0) loan.amountRemaining else loan.principalAmount
                        val event = PaymentCalendarEvent(
                            dayOfMonth = d,
                            month = m,
                            year = y,
                            amount = amt,
                            amountFormatted = "₹%,.0f".format(amt),
                            title = loan.borrowerName,
                            subTitle = "Loan Record · ${loan.notes.ifBlank { "Personal Loan" }}",
                            categoryTag = "Personal Debt",
                            status = eventStatus,
                            loan = loan
                        )
                        map.getOrPut(d) { mutableListOf() }.add(event)
                    }
                }
            } catch (_: Exception) {}
        }

        // 2. Process Queue Screen Transactions Automatically
        transactions.forEach { txn ->
            try {
                val dateStr = txn.repaymentDeadline.ifBlank { "2026-09-12" }
                val parts = dateStr.split("-")
                if (parts.size >= 3) {
                    val y = parts[0].toIntOrNull() ?: 2026
                    val m = parts[1].toIntOrNull() ?: 9
                    val d = parts[2].toIntOrNull() ?: 12

                    if (y == selectedYear && m == selectedMonth) {
                        val eventStatus = when {
                            txn.statusType == "recovered" || txn.statusLabel.contains("RECOVERED", ignoreCase = true) -> "PAID"
                            dateStr < "2026-09-02" || txn.statusLabel.contains("OVERDUE", ignoreCase = true) -> "OVERDUE"
                            else -> "UPCOMING"
                        }
                        val event = PaymentCalendarEvent(
                            dayOfMonth = d,
                            month = m,
                            year = y,
                            amount = txn.amount,
                            amountFormatted = txn.amountFormatted.ifBlank { "₹%,.0f".format(txn.amount) },
                            title = "${txn.customerName} (${txn.id})",
                            subTitle = "Queue Txn · ${txn.failureReason.ifBlank { "UPI Timeout" }} (${txn.gateway})",
                            categoryTag = "Queue Recovery",
                            status = eventStatus,
                            transaction = txn
                        )
                        map.getOrPut(d) { mutableListOf() }.add(event)
                    }
                }
            } catch (_: Exception) {}
        }

        map
    }

    // Monthly Financial Summary
    val allEvents = paymentEventsMap.values.flatten()
    val monthlyPaid = allEvents.filter { it.status == "PAID" }.sumOf { it.amount }
    val monthlyUpcoming = allEvents.filter { it.status == "UPCOMING" }.sumOf { it.amount }
    val monthlyOverdue = allEvents.filter { it.status == "OVERDUE" }.sumOf { it.amount }

    // Selected Date Events
    val selectedDayEvents = paymentEventsMap[selectedDay] ?: emptyList()
    val overdueEvents = allEvents.filter { it.status == "OVERDUE" }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundSlate)
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // ─── 1. SCREEN HEADER & SMART ALERTS SUMMARY ─────────────────────────
        val smartAlertsList = remember(loans, transactions) {
            SmartAlertEngine.generateAllAlerts(loans, transactions)
        }
        val overdueCount = remember(smartAlertsList) { smartAlertsList.count { it.stage == SmartAlertStage.OVERDUE } }
        val dueTodayCount = remember(smartAlertsList) { smartAlertsList.count { it.stage == SmartAlertStage.DUE_TODAY } }
        val reminder3dCount = remember(smartAlertsList) { smartAlertsList.count { it.stage == SmartAlertStage.REMINDER_3D } }
        val upcoming7dCount = remember(smartAlertsList) { smartAlertsList.count { it.stage == SmartAlertStage.UPCOMING_7D } }

        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(
                text = "Calendar",
                fontSize = 28.sp,
                fontWeight = FontWeight.ExtraBold,
                color = NavyDark
            )
            Text(
                text = "Your loan & queue payments timeline with Due Soon Smart Alerts",
                fontSize = 13.sp,
                color = TextMuted,
                fontWeight = FontWeight.Medium
            )

            // Smart Alerts Summary Pills
            Surface(
                color = Color.White,
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, Color(0xFFE2E8F0), RoundedCornerShape(14.dp))
            ) {
                Row(
                    modifier = Modifier.padding(10.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    AlertPillBadge("🚨 $overdueCount Overdue", Color(0xFFFEF2F2), Color(0xFFDC2626))
                    AlertPillBadge("⚡ $dueTodayCount Today", Color(0xFFFFFBEB), Color(0xFFB45309))
                    AlertPillBadge("⏰ $reminder3dCount (3D)", Color(0xFFFEF3C7), Color(0xFFD97706))
                    AlertPillBadge("🗓️ $upcoming7dCount (7D)", Color(0xFFEFF6FF), PrimaryBlue)
                }
            }
        }

        // ─── 2. MONTH SELECTOR BAR ───────────────────────────────────────────
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = Color.White,
            shadowElevation = 2.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 10.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedButton(
                    onClick = {
                        if (selectedMonth == 1) {
                            selectedMonth = 12
                            selectedYear -= 1
                        } else {
                            selectedMonth -= 1
                        }
                    },
                    shape = RoundedCornerShape(10.dp),
                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Icon(Icons.Default.ChevronLeft, null, modifier = Modifier.size(16.dp), tint = PrimaryBlue)
                    Text("Prev", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = PrimaryBlue)
                }

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "$selectedMonthName $selectedYear",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = NavyDark
                    )
                    Text(
                        text = "Today is Sept 2, 2026",
                        fontSize = 10.sp,
                        color = TextMuted,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                OutlinedButton(
                    onClick = {
                        if (selectedMonth == 12) {
                            selectedMonth = 1
                            selectedYear += 1
                        } else {
                            selectedMonth += 1
                        }
                    },
                    shape = RoundedCornerShape(10.dp),
                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Text("Next", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = PrimaryBlue)
                    Icon(Icons.Default.ChevronRight, null, modifier = Modifier.size(16.dp), tint = PrimaryBlue)
                }
            }
        }

        // ─── 3. MONTHLY CALENDAR GRID ────────────────────────────────────────
        Surface(
            shape = RoundedCornerShape(20.dp),
            color = Color.White,
            shadowElevation = 2.dp,
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, Color(0xFFE2E8F0), RoundedCornerShape(20.dp))
        ) {
            Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                // Day Names Header (Mon to Sun)
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceAround) {
                    listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun").forEach { dayName ->
                        Text(
                            text = dayName,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = TextMuted,
                            modifier = Modifier.weight(1f),
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )
                    }
                }

                HorizontalDivider(color = Color(0xFFF1F5F9))

                // Days Grid Math for September 2026 (Starts on Tuesday = Offset 1)
                val daysInMonth = if (selectedMonth == 2) 28 else if (listOf(4, 6, 9, 11).contains(selectedMonth)) 30 else 31
                val firstDayOffset = if (selectedMonth == 9) 1 else 0

                val totalCells = daysInMonth + firstDayOffset
                val numRows = (totalCells + 6) / 7

                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    for (row in 0 until numRows) {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceAround) {
                            for (col in 0..6) {
                                val cellIndex = row * 7 + col
                                val dayNum = cellIndex - firstDayOffset + 1

                                if (dayNum in 1..daysInMonth) {
                                    val isToday = (dayNum == 2 && selectedMonth == 9 && selectedYear == 2026)
                                    val isSelected = (dayNum == selectedDay)
                                    val dayEvents = paymentEventsMap[dayNum] ?: emptyList()

                                    val hasPaid = dayEvents.any { it.status == "PAID" }
                                    val hasOverdue = dayEvents.any { it.status == "OVERDUE" }
                                    val hasUpcoming = dayEvents.any { it.status == "UPCOMING" }

                                    val statusDotColor = when {
                                        hasOverdue -> Color(0xFFEF4444)
                                        hasUpcoming -> Color(0xFFF59E0B)
                                        hasPaid -> EmeraldGreen
                                        else -> Color.Transparent
                                    }

                                    Surface(
                                        color = when {
                                            isSelected -> Color(0xFFEFF6FF)
                                            isToday -> Color(0xFFFEF3C7)
                                            else -> Color(0xFFFAFAFA)
                                        },
                                        shape = RoundedCornerShape(10.dp),
                                        modifier = Modifier
                                            .weight(1f)
                                            .height(48.dp)
                                            .padding(2.dp)
                                            .border(
                                                width = if (isSelected) 2.dp else if (isToday) 1.5.dp else 0.5.dp,
                                                color = if (isSelected) PrimaryBlue else if (isToday) Color(0xFFD97706) else Color(0xFFE2E8F0),
                                                shape = RoundedCornerShape(10.dp)
                                            )
                                            .clickable { selectedDay = dayNum }
                                    ) {
                                        Column(
                                            modifier = Modifier.padding(2.dp),
                                            horizontalAlignment = Alignment.CenterHorizontally,
                                            verticalArrangement = Arrangement.SpaceBetween
                                        ) {
                                            Text(
                                                text = "$dayNum",
                                                fontSize = 12.sp,
                                                fontWeight = if (isToday || isSelected) FontWeight.ExtraBold else FontWeight.Bold,
                                                color = if (isSelected) PrimaryBlue else NavyDark
                                            )

                                            if (dayEvents.isNotEmpty()) {
                                                Row(
                                                    verticalAlignment = Alignment.CenterVertically,
                                                    horizontalArrangement = Arrangement.Center
                                                ) {
                                                    Surface(color = statusDotColor, shape = CircleShape, modifier = Modifier.size(6.dp)) {}
                                                    if (dayEvents.size > 1) {
                                                        Text(
                                                            text = "+${dayEvents.size}",
                                                            fontSize = 8.sp,
                                                            fontWeight = FontWeight.ExtraBold,
                                                            color = NavyDark,
                                                            modifier = Modifier.padding(start = 2.dp)
                                                        )
                                                    }
                                                }
                                            } else {
                                                Spacer(modifier = Modifier.height(6.dp))
                                            }
                                        }
                                    }
                                } else {
                                    Spacer(modifier = Modifier.weight(1f).height(48.dp))
                                }
                            }
                        }
                    }
                }

                HorizontalDivider(color = Color(0xFFF1F5F9))

                // LEGEND BAR
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    LegendItem(label = "Paid", color = EmeraldGreen)
                    LegendItem(label = "Upcoming", color = Color(0xFFF59E0B))
                    LegendItem(label = "Overdue", color = Color(0xFFEF4444))
                }
            }
        }

        // ─── 4. SELECTED DATE PAYMENT DETAILS ────────────────────────────────
        Surface(
            shape = RoundedCornerShape(20.dp),
            color = Color.White,
            shadowElevation = 2.dp,
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, PrimaryBlue.copy(alpha = 0.4f), RoundedCornerShape(20.dp))
        ) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Event, null, tint = PrimaryBlue, modifier = Modifier.size(22.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "$selectedMonthName $selectedDay, $selectedYear",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = NavyDark
                        )
                    }

                    if (selectedDayEvents.isNotEmpty()) {
                        Surface(
                            color = Color(0xFFEFF6FF),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(
                                text = "Total Scheduled: ₹%,.0f".format(selectedDayEvents.sumOf { it.amount }),
                                fontSize = 12.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = PrimaryBlue,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                            )
                        }
                    }
                }

                HorizontalDivider(color = Color(0xFFF1F5F9))

                if (selectedDayEvents.isEmpty()) {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(Icons.Default.CheckCircle, null, tint = EmeraldGreen, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "No payments scheduled for this date",
                            fontSize = 13.sp,
                            color = TextMuted,
                            fontWeight = FontWeight.Medium
                        )
                    }
                } else {
                    selectedDayEvents.forEach { event ->
                        Surface(
                            color = Color(0xFFFAFAFA),
                            shape = RoundedCornerShape(16.dp),
                            shadowElevation = 1.dp,
                            modifier = Modifier
                                .fillMaxWidth()
                                .border(1.dp, Color(0xFFE2E8F0), RoundedCornerShape(16.dp))
                        ) {
                            Column(
                                modifier = Modifier.padding(14.dp),
                                verticalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                // Header: Title + Category + Status Pill
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.Top
                                ) {
                                    Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                        Text(
                                            text = event.title,
                                            fontSize = 16.sp,
                                            fontWeight = FontWeight.ExtraBold,
                                            color = NavyDark
                                        )

                                        // Category Badge Tag on Next Line
                                        Surface(
                                            color = Color(0xFFEFF6FF),
                                            shape = RoundedCornerShape(6.dp)
                                        ) {
                                            Text(
                                                text = event.categoryTag,
                                                fontSize = 10.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = PrimaryBlue,
                                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                            )
                                        }

                                        Text(
                                            text = event.subTitle,
                                            fontSize = 12.sp,
                                            color = TextMuted,
                                            lineHeight = 16.sp,
                                            fontWeight = FontWeight.Medium
                                        )
                                    }

                                    Column(horizontalAlignment = Alignment.End) {
                                        Text(
                                            text = event.amountFormatted,
                                            fontSize = 18.sp,
                                            fontWeight = FontWeight.ExtraBold,
                                            color = PrimaryBlue
                                        )
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Surface(
                                            color = when (event.status) {
                                                "PAID" -> Color(0xFFDCFCE7)
                                                "OVERDUE" -> Color(0xFFFEE2E2)
                                                else -> Color(0xFFFEF3C7)
                                            },
                                            shape = RoundedCornerShape(6.dp)
                                        ) {
                                            Text(
                                                text = when (event.status) {
                                                    "PAID" -> "🟢 Paid"
                                                    "OVERDUE" -> "🔴 Overdue"
                                                    else -> "🟡 Due"
                                                },
                                                fontSize = 11.sp,
                                                fontWeight = FontWeight.ExtraBold,
                                                color = when (event.status) {
                                                    "PAID" -> EmeraldGreen
                                                    "OVERDUE" -> Color(0xFFDC2626)
                                                    else -> Color(0xFFB45309)
                                                },
                                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                            )
                                        }
                                    }
                                }

                                HorizontalDivider(color = Color(0xFFF1F5F9))

                                // Action Buttons Row
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    // Primary CTA: Pay Now / View Details
                                    Button(
                                        onClick = {
                                            if (event.loan != null) {
                                                if (event.status == "PAID") onOpenRepayLoan(event.loan)
                                                else onOpenRazorpayCheckout(event.loan.id, event.amount, event.title)
                                            } else if (event.transaction != null) {
                                                onOpenRazorpayCheckout(event.transaction.id, event.amount, event.transaction.customerName)
                                            }
                                        },
                                        modifier = Modifier
                                            .weight(1f)
                                            .height(46.dp),
                                        shape = RoundedCornerShape(12.dp),
                                        colors = ButtonDefaults.buttonColors(containerColor = EmeraldGreen),
                                        elevation = ButtonDefaults.buttonElevation(defaultElevation = 2.dp)
                                    ) {
                                        Icon(Icons.Default.Payments, null, tint = Color.White, modifier = Modifier.size(18.dp))
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(
                                            text = if (event.status == "PAID") "View Details" else "Pay Now",
                                            fontSize = 13.sp,
                                            fontWeight = FontWeight.ExtraBold,
                                            color = Color.White
                                        )
                                    }

                                    // Secondary CTA: Review Intel / Request Extension
                                    OutlinedButton(
                                        onClick = {
                                            if (event.loan != null) {
                                                onOpenRepayLoan(event.loan)
                                            } else if (event.transaction != null) {
                                                onReviewTransaction(event.transaction.id)
                                            }
                                        },
                                        modifier = Modifier
                                            .weight(1f)
                                            .height(46.dp),
                                        shape = RoundedCornerShape(12.dp)
                                    ) {
                                        Icon(
                                            imageVector = if (event.transaction != null) Icons.Default.AutoAwesome else Icons.Default.Schedule,
                                            contentDescription = null,
                                            tint = PrimaryBlue,
                                            modifier = Modifier.size(18.dp)
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(
                                            text = if (event.transaction != null) "Review Intel" else "Request Extension",
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.ExtraBold,
                                            color = PrimaryBlue
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // ─── 5. MONTHLY SUMMARY GRID ─────────────────────────────────────────
        Text("$selectedMonthName Financial Summary", fontSize = 16.sp, fontWeight = FontWeight.ExtraBold, color = NavyDark)

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            SummaryMetricCard("Paid", "₹%,.0f".format(monthlyPaid), EmeraldGreen, Modifier.weight(1f))
            SummaryMetricCard("Upcoming", "₹%,.0f".format(monthlyUpcoming), Color(0xFFD97706), Modifier.weight(1f))
            SummaryMetricCard("Overdue", "₹%,.0f".format(monthlyOverdue), Color(0xFFDC2626), Modifier.weight(1f))
        }

        // ─── 6. NEEDS ATTENTION / OVERDUE SECTION ────────────────────────────
        Text("⚠️ Needs Attention", fontSize = 16.sp, fontWeight = FontWeight.ExtraBold, color = NavyDark)

        if (overdueEvents.isNotEmpty()) {
            Surface(
                color = Color(0xFFFEF2F2),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth().border(1.dp, Color(0xFFFCA5A5), RoundedCornerShape(16.dp))
            ) {
                Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Warning, null, tint = Color(0xFFDC2626), modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "₹%,.0f Overdue Payments".format(monthlyOverdue),
                            fontSize = 15.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color(0xFF991B1B)
                        )
                    }

                    Text(
                        text = "You have ${overdueEvents.size} overdue payment(s) requiring immediate resolution.",
                        fontSize = 12.sp,
                        color = NavyDark
                    )

                    Button(
                        onClick = {
                            overdueEvents.firstOrNull()?.let { ev ->
                                UpiPaymentLauncher.launchUpiPayment(context, ev.loan?.id ?: ev.transaction?.id ?: "PAY_OVERDUE", ev.amount, ev.title)
                            }
                        },
                        modifier = Modifier.fillMaxWidth().height(44.dp),
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFDC2626))
                    ) {
                        Text("Pay Overdue Item Now via UPI", fontSize = 13.sp, fontWeight = FontWeight.ExtraBold, color = Color.White)
                    }
                }
            }
        } else {
            Surface(
                color = Color(0xFFECFDF5),
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier.fillMaxWidth().border(1.dp, Color(0xFFA7F3D0), RoundedCornerShape(14.dp))
            ) {
                Row(modifier = Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.CheckCircle, null, tint = EmeraldGreen, modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(10.dp))
                    Text("✓ You're up to date! No overdue payments for $selectedMonthName.", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = EmeraldGreen)
                }
            }
        }

        // ─── 7. SMART PAYPULSE INSIGHT ───────────────────────────────────────
        Surface(
            color = Color(0xFFEFF6FF),
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier.fillMaxWidth().border(1.dp, Color(0xFFBFDBFE), RoundedCornerShape(16.dp))
        ) {
            Row(modifier = Modifier.padding(14.dp), verticalAlignment = Alignment.Top) {
                Icon(Icons.Default.Lightbulb, null, tint = PrimaryBlue, modifier = Modifier.size(22.dp))
                Spacer(modifier = Modifier.width(10.dp))
                Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                    Text("PayPulse Calendar Insight", fontSize = 13.sp, fontWeight = FontWeight.ExtraBold, color = PrimaryBlue)
                    Text(
                        text = if (monthlyUpcoming > 0)
                            "💡 You have ₹%,.0f in upcoming payments scheduled this month across loans and recovery transactions. Settling them on time maintains your 87/100 Repayment Health Score.".format(monthlyUpcoming)
                        else
                            "✓ Excellent cash flow! All scheduled repayments for $selectedMonthName are fully settled.",
                        fontSize = 12.sp,
                        color = NavyDark,
                        lineHeight = 16.sp
                    )
                }
            }
        }

        // ─── 8. EMPTY STATE CTA ───────────────────────────────────────────────
        if (loans.isEmpty() && transactions.isEmpty()) {
            Surface(
                color = Color.White,
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text("No payments yet", fontSize = 16.sp, fontWeight = FontWeight.ExtraBold, color = NavyDark)
                    Text("You don't have any loan or queue payments scheduled in your calendar.", fontSize = 12.sp, color = TextMuted)
                    Button(onClick = onOpenAddLoan, shape = RoundedCornerShape(10.dp)) {
                        Icon(Icons.Default.Add, null)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Add a Loan")
                    }
                }
            }
        }
    }
}

@Composable
private fun LegendItem(label: String, color: Color) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Surface(color = color, shape = CircleShape, modifier = Modifier.size(8.dp)) {}
        Spacer(modifier = Modifier.width(6.dp))
        Text(label, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = TextMuted)
    }
}

@Composable
private fun SummaryMetricCard(label: String, amount: String, color: Color, modifier: Modifier = Modifier) {
    Surface(
        color = Color.White,
        shape = RoundedCornerShape(14.dp),
        shadowElevation = 1.dp,
        modifier = modifier
    ) {
        Column(modifier = Modifier.padding(10.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Text(amount, fontSize = 15.sp, fontWeight = FontWeight.ExtraBold, color = color)
            Text(label, fontSize = 10.sp, fontWeight = FontWeight.Bold, color = TextMuted)
        }
    }
}

@Composable
private fun AlertPillBadge(label: String, bgColor: Color, textColor: Color) {
    Surface(
        color = bgColor,
        shape = RoundedCornerShape(8.dp)
    ) {
        Text(
            text = label,
            fontSize = 11.sp,
            fontWeight = FontWeight.ExtraBold,
            color = textColor,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
        )
    }
}
