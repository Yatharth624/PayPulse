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
import androidx.compose.material.icons.automirrored.filled.AltRoute
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.platform.LocalContext
import kotlin.math.pow
import com.paypulse.ai.data.model.*
import com.paypulse.ai.ui.theme.*
import com.paypulse.ai.ui.utils.ReportExporter

@Composable
fun InsightsScreen(
    insightsData: InsightsResponseDto = InsightsResponseDto(),
    loans: List<LoanItem> = emptyList(),
    selectedTimeRange: String = "24H",
    onTimeRangeSelected: (String) -> Unit = {},
    onNavigateToQueueFiltered: (String) -> Unit = {},
    onOpenDecisionModal: () -> Unit = {},
    onOpenWhatsAppSimulator: () -> Unit = {},
    onOpenAddLoan: () -> Unit = {},
    onOpenBorrowerProfile: (String) -> Unit = {},
    onOpenRepayLoan: (LoanItem) -> Unit = {},
    onRefresh: () -> Unit = {},
    onOpenRazorpayCheckout: (id: String, amount: Double, title: String) -> Unit = { _, _, _ -> },
    onOpenAutoPay: (LoanItem) -> Unit = {}
) {
    var activeSubTab by remember { mutableStateOf("debt") } // "debt" or "analytics"

    Column(modifier = Modifier.fillMaxSize()) {
        // TOP INSIGHTS SEGMENTED BAR
        Surface(
            color = Color.White,
            tonalElevation = 2.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 10.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Surface(
                    color = if (activeSubTab == "debt") PrimaryBlue else Color(0xFFF1F5F9),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .weight(1f)
                        .clickable { activeSubTab = "debt" }
                ) {
                    Row(
                        modifier = Modifier.padding(vertical = 10.dp),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.AccountBalanceWallet,
                            contentDescription = null,
                            tint = if (activeSubTab == "debt") Color.White else NavyDark,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "My Debt Ledger",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = if (activeSubTab == "debt") Color.White else NavyDark
                        )
                    }
                }

                Surface(
                    color = if (activeSubTab == "analytics") PrimaryBlue else Color(0xFFF1F5F9),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .weight(1f)
                        .clickable { activeSubTab = "analytics" }
                ) {
                    Row(
                        modifier = Modifier.padding(vertical = 10.dp),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.AutoAwesome,
                            contentDescription = null,
                            tint = if (activeSubTab == "analytics") Color.White else NavyDark,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Recovery Analytics",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = if (activeSubTab == "analytics") Color.White else NavyDark
                        )
                    }
                }
            }
        }

        if (activeSubTab == "debt") {
            MyDebtScreen(
                loans = loans,
                onOpenAddLoan = onOpenAddLoan,
                onOpenBorrowerProfile = onOpenBorrowerProfile,
                onOpenRepayLoan = onOpenRepayLoan,
                onRefresh = onRefresh,
                onOpenRazorpayCheckout = onOpenRazorpayCheckout,
                onOpenAutoPay = onOpenAutoPay
            )
        } else {
            val scrollState = rememberScrollState()
            val biz = insightsData.businessMetrics

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundSlate)
            .verticalScroll(scrollState)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // ─── 1. SCREEN HEADER & TIME RANGE SELECTOR ─────────────────────────
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Payment & Recovery Intelligence",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = NavyDark
                    )
                    Text(
                        text = "Understand payment channel volume, failure breakdowns, and recovery forecasts.",
                        fontSize = 12.sp,
                        color = TextMuted,
                        modifier = Modifier.padding(top = 2.dp),
                        lineHeight = 16.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Time Range Tabs
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                listOf("TODAY", "24H", "7D", "1H").forEach { range ->
                    val isSelected = range.equals(selectedTimeRange, ignoreCase = true)
                    Surface(
                        color = if (isSelected) PrimaryBlue else Color.White,
                        shape = RoundedCornerShape(12.dp),
                        shadowElevation = if (isSelected) 2.dp else 0.dp,
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .border(1.dp, if (isSelected) PrimaryBlue else Color(0xFFE2E8F0), RoundedCornerShape(12.dp))
                            .clickable { onTimeRangeSelected(range) }
                    ) {
                        Text(
                            text = range,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isSelected) Color.White else NavyDark,
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp)
                        )
                    }
                }
            }
        }

        // ─── 2. PAYMENT CHANNEL PERFORMANCE & VOLUME ─────────────────────────
        Surface(
            shape = RoundedCornerShape(20.dp),
            color = Color.White,
            shadowElevation = 1.dp,
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, Color(0xFFE2E8F0), RoundedCornerShape(20.dp))
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.CreditCard, contentDescription = null, tint = PrimaryBlue, modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Payment Channel Volume & Conversion",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = NavyDark
                    )
                }

                ChannelPerformanceRow(name = "UPI Intent (GPay, PhonePe, CRED)", gmv = "₹18.4L GMV", rate = "84.2% Success", color = Color(0xFF0284C7))
                ChannelPerformanceRow(name = "UPI AutoPay / eMandate", gmv = "₹12.2L GMV", rate = "72.6% Success", color = EmeraldGreen)
                ChannelPerformanceRow(name = "Cards & NetBanking", gmv = "₹8.0L GMV", rate = "91.0% Success", color = Color(0xFF7C3AED))
            }
        }

        // ─── 3. CASH FLOW RECOVERY FORECAST ──────────────────────────────────
        Surface(
            shape = RoundedCornerShape(20.dp),
            color = Color(0xFFF0F9FF),
            shadowElevation = 1.dp,
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, Color(0xFFBAE6FD), RoundedCornerShape(20.dp))
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.TrendingUp, contentDescription = null, tint = PrimaryBlue, modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Cash Flow Recovery Forecast",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = NavyDark
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    ForecastCard(period = "Next 7 Days", amount = "₹1.85L", label = "High Certainty 🟢", modifier = Modifier.weight(1f))
                    ForecastCard(period = "Next 14 Days", amount = "₹3.20L", label = "Moderate 🟡", modifier = Modifier.weight(1f))
                    ForecastCard(period = "Next 30 Days", amount = "₹4.50L", label = "Total Forecast 🔵", modifier = Modifier.weight(1f))
                }
            }
        }

        // ─── 4. RECOVERY BUSINESS IMPACT ─────────────────────────────────────
        Surface(
            shape = RoundedCornerShape(20.dp),
            color = Color.White,
            shadowElevation = 1.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(bottom = 12.dp)
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.TrendingUp,
                        contentDescription = null,
                        tint = EmeraldGreen,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Recovery Business Impact",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = NavyDark
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    BizImpactCard(
                        label = "Expected Recovery",
                        value = biz.expectedRecoveryFormatted,
                        subtitle = "Expected Target",
                        modifier = Modifier.weight(1f)
                    )
                    BizImpactCard(
                        label = "Actual Recovery",
                        value = biz.recoveredAmountFormatted,
                        subtitle = "Confirmed Recovered",
                        valueColor = EmeraldGreen,
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    BizImpactCard(
                        label = "Recovery Rate",
                        value = biz.recoveryRateFormatted,
                        subtitle = "Actual / Opportunity",
                        valueColor = PrimaryBlue,
                        modifier = Modifier.weight(1f)
                    )
                    BizImpactCard(
                        label = "Recovery Lift",
                        value = biz.recoveryLiftFormatted,
                        subtitle = "vs Simple Retry Baseline",
                        valueColor = AmberGold,
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                Spacer(modifier = Modifier.height(14.dp))

                // 🤖 MULTI-STAGE AUTOMATED RECOVERY WORKFLOW ENGINE SECTION
                MultiStageRecoveryWorkflowSection(onOpenWhatsAppSimulator = onOpenWhatsAppSimulator)

                Spacer(modifier = Modifier.height(14.dp))

                // ACTION BUTTONS IN INSIGHTS SCREEN
                val context = LocalContext.current
                var exportStatusMessage by remember { mutableStateOf<String?>(null) }

                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    // 💬 TALK VIA WHATSAPP SIMULATOR BUTTON
                    Button(
                        onClick = { onOpenWhatsAppSimulator() },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF075E54))
                    ) {
                        Icon(imageVector = Icons.Default.Chat, contentDescription = null, modifier = Modifier.size(16.dp), tint = Color.White)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("💬 Talk via WhatsApp Simulator", fontSize = 12.sp, fontWeight = FontWeight.ExtraBold, color = Color.White)
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Button(
                            onClick = {
                                val (_, msg) = ReportExporter.exportPdfAudit(context)
                                exportStatusMessage = msg
                            },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue)
                        ) {
                            Icon(imageVector = Icons.Default.PictureAsPdf, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Download PDF Audit", fontSize = 12.sp, fontWeight = FontWeight.ExtraBold)
                        }

                        OutlinedButton(
                            onClick = {
                                val (_, msg) = ReportExporter.exportCsvLedger(context)
                                exportStatusMessage = msg
                            },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Icon(imageVector = Icons.Default.Download, contentDescription = null, modifier = Modifier.size(16.dp), tint = PrimaryBlue)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Export CSV Ledger", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = PrimaryBlue)
                        }
                    }

                    exportStatusMessage?.let { msg ->
                        Surface(
                            color = Color(0xFFECFDF5),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(10.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(imageVector = Icons.Default.CheckCircle, contentDescription = null, tint = EmeraldGreen, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(msg, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = EmeraldGreen)
                            }
                        }
                    }
                }
            }
        }

        // ─── 5. PAYMENT FAILURE INTELLIGENCE ────────────────────────────────
        Surface(
            shape = RoundedCornerShape(20.dp),
            color = Color.White,
            shadowElevation = 1.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                    Text(
                        text = "Payment Failure Intelligence",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = NavyDark
                    )
                    Surface(
                        color = Color(0xFFF1F5F9),
                        shape = RoundedCornerShape(6.dp),
                        modifier = Modifier.wrapContentWidth()
                    ) {
                        Text(
                            text = "Sorted by Financial Impact",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextMuted,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Failure Breakdown Cards - Multi-line visual hierarchy (like Active Workflows)
                insightsData.failureBreakdown.forEach { item ->
                    Surface(
                        color = Color(0xFFF8FAFC),
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 5.dp)
                            .border(1.dp, Color(0xFFE2E8F0), RoundedCornerShape(16.dp))
                            .clickable { onNavigateToQueueFiltered(item.category) }
                    ) {
                        Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                            // Line 1: Category Name + Expected Recovery Badge
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Surface(color = Color(0xFFEFF6FF), shape = CircleShape, modifier = Modifier.size(32.dp)) {
                                        Box(contentAlignment = Alignment.Center) {
                                            Icon(imageVector = Icons.Default.Analytics, contentDescription = null, tint = PrimaryBlue, modifier = Modifier.size(16.dp))
                                        }
                                    }
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Text(
                                        text = item.categoryLabel,
                                        fontSize = 15.sp,
                                        fontWeight = FontWeight.ExtraBold,
                                        color = NavyDark
                                    )
                                }

                                Surface(color = Color(0xFFECFDF5), shape = RoundedCornerShape(8.dp)) {
                                    Text(
                                        text = "${item.expectedRecoveryFormatted} Expected",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.ExtraBold,
                                        color = EmeraldGreen,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                    )
                                }
                            }

                            HorizontalDivider(color = Color(0xFFE2E8F0))

                            // Line 2: Transaction Volume & Financial Value Box
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Column {
                                    Text("Transaction Volume", fontSize = 12.sp, color = TextMuted, fontWeight = FontWeight.Medium)
                                    Text("${item.transactionCount} failed payments", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = NavyDark)
                                }
                                Column(horizontalAlignment = Alignment.End) {
                                    Text("Total GMV Value", fontSize = 12.sp, color = TextMuted, fontWeight = FontWeight.Medium)
                                    Text(item.transactionValueFormatted, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = NavyDark)
                                }
                            }

                            // Line 3: AI Recovery Strategy & Historical Recovery Rate on Next Line
                            Surface(
                                color = Color(0xFFF0F9FF),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(
                                    modifier = Modifier.padding(12.dp),
                                    verticalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(imageVector = Icons.Default.AutoAwesome, contentDescription = null, tint = PrimaryBlue, modifier = Modifier.size(16.dp))
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text("AI Action: ${item.recommendedStrategy}", fontSize = 13.sp, fontWeight = FontWeight.ExtraBold, color = PrimaryBlue)
                                    }
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(imageVector = Icons.AutoMirrored.Filled.TrendingUp, contentDescription = null, tint = EmeraldGreen, modifier = Modifier.size(16.dp))
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text("Historical Recovery Rate: ${item.recoveryRateFormatted}", fontSize = 13.sp, fontWeight = FontWeight.ExtraBold, color = EmeraldGreen)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // ─── 6. FAILURE TREND ────────────────────────────────────────────────
        if (insightsData.failureTrends.isNotEmpty()) {
            Surface(
                shape = RoundedCornerShape(20.dp),
                color = Color.White,
                shadowElevation = 1.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text(
                            text = "Failure Volume & Impact Trend",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = NavyDark
                        )
                        Text(
                            text = "Time-series recovery potential ($selectedTimeRange)",
                            fontSize = 13.sp,
                            color = TextMuted
                        )
                        Surface(
                            color = Color(0xFFEFF6FF),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.wrapContentWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(8.dp)
                                        .clip(CircleShape)
                                        .background(PrimaryBlue)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "Expected Recovery Indicator",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = PrimaryBlue
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // GRAPH CANVAS CONTAINER
                    Surface(
                        color = Color(0xFFF8FAFC),
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(1.dp, Color(0xFFE2E8F0), RoundedCornerShape(16.dp))
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(180.dp),
                                horizontalArrangement = Arrangement.SpaceAround,
                                verticalAlignment = Alignment.Bottom
                            ) {
                                val maxVal = (insightsData.failureTrends.maxOfOrNull { it.failureCount } ?: 1).toFloat()
                                insightsData.failureTrends.forEach { pt ->
                                    val heightFraction = (pt.failureCount / maxVal).coerceIn(0.18f, 1f)
                                    Column(
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                        modifier = Modifier.fillMaxHeight(),
                                        verticalArrangement = Arrangement.Bottom
                                    ) {
                                        Surface(
                                            color = Color(0xFFDBEAFE),
                                            shape = RoundedCornerShape(6.dp),
                                            modifier = Modifier.padding(bottom = 4.dp)
                                        ) {
                                            Text(
                                                text = pt.expectedRecoveryFormatted,
                                                fontSize = 9.sp,
                                                fontWeight = FontWeight.ExtraBold,
                                                color = PrimaryBlue,
                                                modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                                            )
                                        }

                                        Box(
                                            modifier = Modifier
                                                .width(38.dp)
                                                .fillMaxHeight(heightFraction * 0.72f)
                                                .clip(RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp))
                                                .background(PrimaryBlue)
                                        )

                                        Spacer(modifier = Modifier.height(6.dp))

                                        Text(
                                            text = pt.timestampLabel,
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = NavyDark
                                        )
                                        Text(
                                            text = "${pt.failureCount} txns",
                                            fontSize = 9.sp,
                                            color = TextMuted
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // ─── 7. RAZORPAYX NEOBANKING VIRTUAL ACCOUNT & SMART COLLECT ─────────
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = Color.White,
            shadowElevation = 1.dp,
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, Color(0xFFE2E8F0), RoundedCornerShape(16.dp))
        ) {
            Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        color = Color(0xFF0F172A),
                        shape = CircleShape,
                        modifier = Modifier.size(30.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Default.AccountBalance,
                                contentDescription = null,
                                tint = Color(0xFF38BDF8),
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text(
                            text = "RazorpayX Smart Collect (VAN)",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = NavyDark
                        )
                        Text(
                            text = "Virtual Accounts for >₹50k Transfers",
                            fontSize = 10.sp,
                            color = TextMuted
                        )
                    }
                }

                // Compact Virtual Account Card Box (Navy Gradient)
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(
                            brush = Brush.horizontalGradient(
                                colors = listOf(Color(0xFF0F172A), Color(0xFF1E293B))
                            )
                        )
                        .padding(10.dp)
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "VAN: 2223330091823",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = Color.White,
                                letterSpacing = 0.5.sp
                            )
                            Text(
                                text = "IFSC: RAZR0000001",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF38BDF8)
                            )
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Escrow: PayPulse Settlement",
                                fontSize = 10.sp,
                                color = Color.LightGray
                            )
                            Text(
                                text = "NEFT / RTGS / IMPS",
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.LightGray
                            )
                        }
                    }
                }

                // Interactive Buttons: Copy Bank Details & Show B2B QR Code
                var copiedFeedback by remember { mutableStateOf(false) }
                var showQrCode by remember { mutableStateOf(false) }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedButton(
                        onClick = { copiedFeedback = true },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = PrimaryBlue),
                        contentPadding = PaddingValues(vertical = 6.dp)
                    ) {
                        Icon(
                            imageVector = if (copiedFeedback) Icons.Default.Check else Icons.Default.ContentCopy,
                            contentDescription = null,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = if (copiedFeedback) "Copied!" else "Copy Details",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.ExtraBold
                        )
                    }

                    Button(
                        onClick = { showQrCode = !showQrCode },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue),
                        contentPadding = PaddingValues(vertical = 6.dp)
                    ) {
                        Icon(imageVector = Icons.Default.QrCode2, contentDescription = null, modifier = Modifier.size(15.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = if (showQrCode) "Hide QR" else "B2B Payment QR",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.ExtraBold
                        )
                    }
                }

                // Expandable B2B Payment QR Code View
                AnimatedVisibility(visible = showQrCode) {
                    Surface(
                        color = Color(0xFFF8FAFC),
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(1.dp, Color(0xFFE2E8F0), RoundedCornerShape(16.dp))
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Surface(
                                color = Color.White,
                                shape = RoundedCornerShape(12.dp),
                                shadowElevation = 2.dp,
                                modifier = Modifier.padding(8.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.QrCodeScanner,
                                    contentDescription = "RazorpayX B2B Smart Collect QR",
                                    tint = NavyDark,
                                    modifier = Modifier
                                        .size(120.dp)
                                        .padding(12.dp)
                                )
                            }

                            Text(
                                text = "Scan to Pay via Corporate Banking / UPI",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = NavyDark
                            )
                            Text(
                                text = "Instant auto-reconciliation linked to PayPulse Escrow Account",
                                fontSize = 11.sp,
                                color = TextMuted
                            )
                        }
                    }
                }
            }
        }



        // ─── 9. AI INTELLIGENCE ─────────────────────────────────────────────
        Surface(
            shape = RoundedCornerShape(20.dp),
            color = Color(0xFFF8FAFC),
            shadowElevation = 1.dp,
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, Color(0xFFE2E8F0), RoundedCornerShape(20.dp))
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Lightbulb,
                        contentDescription = null,
                        tint = PrimaryBlue,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "AI Intelligence",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = NavyDark
                    )
                }

                if (insightsData.aiInsights.isEmpty()) {
                    Text(
                        text = "PayPulse is still collecting enough recovery data to generate reliable insights.",
                        fontSize = 13.sp,
                        color = TextMuted
                    )
                } else {
                    insightsData.aiInsights.forEach { insight ->
                        val (pillBg, pillText) = when (insight.severity.uppercase()) {
                            "CRITICAL", "WARNING" -> Pair(Color(0xFFFEF2F2), Color(0xFFDC2626))
                            else -> Pair(Color(0xFFEFF6FF), PrimaryBlue)
                        }

                        Surface(
                            shape = RoundedCornerShape(14.dp),
                            color = Color.White,
                            shadowElevation = 1.dp,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Surface(
                                        color = pillBg,
                                        shape = RoundedCornerShape(6.dp)
                                    ) {
                                        Text(
                                            text = insight.severity.uppercase(),
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.ExtraBold,
                                            color = pillText,
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                        )
                                    }

                                    Text(
                                        text = insight.createdAt,
                                        fontSize = 12.sp,
                                        color = TextMuted
                                    )
                                }

                                Spacer(modifier = Modifier.height(6.dp))

                                Text(
                                    text = insight.title,
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = NavyDark
                                )

                                Text(
                                    text = insight.message,
                                    fontSize = 13.sp,
                                    color = TextMuted,
                                    lineHeight = 17.sp,
                                    modifier = Modifier.padding(top = 4.dp)
                                )

                                if (insight.financialImpactFormatted.isNotBlank()) {
                                    Text(
                                        text = insight.financialImpactFormatted,
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = EmeraldGreen,
                                        modifier = Modifier.padding(top = 4.dp)
                                    )
                                }

                                Spacer(modifier = Modifier.height(8.dp))

                                Column(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Text(
                                        text = "Rec: ${insight.recommendedAction}",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = NavyDark
                                    )

                                    Surface(
                                        color = Color(0xFFF1F5F9),
                                        shape = RoundedCornerShape(8.dp),
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clickable {
                                                onNavigateToQueueFiltered(insight.relatedFilter.ifBlank { "all" })
                                            }
                                    ) {
                                        Row(
                                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 7.dp),
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.Center
                                        ) {
                                            Text(
                                                text = "Review affected payments",
                                                fontSize = 12.sp,
                                                fontWeight = FontWeight.ExtraBold,
                                                color = PrimaryBlue
                                            )
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Icon(
                                                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                                                contentDescription = null,
                                                tint = PrimaryBlue,
                                                modifier = Modifier.size(14.dp)
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}
}
}

@Composable
private fun MultiStageRecoveryWorkflowSection(
    onOpenWhatsAppSimulator: () -> Unit = {}
) {
    Surface(
        shape = RoundedCornerShape(18.dp),
        color = Color.White,
        shadowElevation = 2.dp,
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, Color(0xFFE2E8F0), RoundedCornerShape(18.dp))
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Section Header
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        color = Color(0xFFEFF6FF),
                        shape = CircleShape,
                        modifier = Modifier.size(36.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Default.AccountTree,
                                contentDescription = null,
                                tint = PrimaryBlue,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = "Multi-Stage Recovery Engine",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = NavyDark
                    )
                }

                Text(
                    text = "Automated recovery cadences driven by ML Risk Scores",
                    fontSize = 13.sp,
                    color = TextMuted,
                    fontWeight = FontWeight.Medium
                )
            }

            HorizontalDivider(color = Color(0xFFF1F5F9))

            // Stage 1
            WorkflowCadenceStageItem(
                stageNum = "1",
                dayLabel = "Day 0 (Instant Failure)",
                title = "Silent Gateway Smart Retry",
                description = "Automatic background gateway retry during low-latency banking settlement windows (Zero friction).",
                badgeText = "⚡ Zero Friction · 74.6% Instant Recovery",
                badgeBg = Color(0xFFECFDF5),
                badgeTextColor = EmeraldGreen,
                icon = Icons.Default.Bolt
            )

            // Stage 2
            WorkflowCadenceStageItem(
                stageNum = "2",
                dayLabel = "Day 1 (24 Hours)",
                title = "Friendly WhatsApp UPI Request Link",
                description = "Deep-linked 1-click Razorpay UPI Payment Link sent via WhatsApp Business API with polite reminder.",
                badgeText = "📱 WhatsApp / SMS · Click to Test Flow",
                badgeBg = Color(0xFFEFF6FF),
                badgeTextColor = PrimaryBlue,
                icon = Icons.Default.QrCodeScanner,
                onClick = onOpenWhatsAppSimulator
            )

            // Stage 3
            WorkflowCadenceStageItem(
                stageNum = "3",
                dayLabel = "Day 3 (Overdue Nudge)",
                title = "Soft Automated Voice Call & IVR Nudge",
                description = "Automated outbound voice call offering flexible installment repayment options or date extensions.",
                badgeText = "📞 Voice IVR Call · +6.8% Lift",
                badgeBg = Color(0xFFFEF3C7),
                badgeTextColor = Color(0xFFD97706),
                icon = Icons.Default.PhoneCallback
            )

            // Stage 4
            WorkflowCadenceStageItem(
                stageNum = "4",
                dayLabel = "Day 7+ (Default)",
                title = "Formal Collateral Notice & Demand Notice",
                description = "Automated generation & dispatch of Section 138/172 Legal Demand Notice with pledged collateral verification.",
                badgeText = "📜 Legal & Collateral · High Risk Escalation",
                badgeBg = Color(0xFFFEF2F2),
                badgeTextColor = Color(0xFFDC2626),
                icon = Icons.Default.Gavel
            )
        }
    }
}

@Composable
private fun WorkflowCadenceStageItem(
    stageNum: String,
    dayLabel: String,
    title: String,
    description: String,
    badgeText: String,
    badgeBg: Color,
    badgeTextColor: Color,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    onClick: (() -> Unit)? = null
) {
    Surface(
        color = Color(0xFFFAFAFA),
        shape = RoundedCornerShape(14.dp),
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, Color(0xFFE2E8F0), RoundedCornerShape(14.dp))
            .let { if (onClick != null) it.clickable { onClick() } else it }
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.Top
        ) {
            Surface(
                color = PrimaryBlue,
                shape = CircleShape,
                modifier = Modifier.size(26.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(stageNum, fontSize = 13.sp, fontWeight = FontWeight.ExtraBold, color = Color.White)
                }
            }
            Spacer(modifier = Modifier.width(10.dp))
            Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text(title, fontSize = 15.sp, fontWeight = FontWeight.ExtraBold, color = NavyDark)

                Surface(
                    color = Color(0xFFEFF6FF),
                    shape = RoundedCornerShape(6.dp)
                ) {
                    Text(
                        text = "📅 $dayLabel",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = PrimaryBlue,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                    )
                }

                Text(description, fontSize = 13.sp, color = TextMuted, lineHeight = 17.sp, fontWeight = FontWeight.Medium)

                Surface(
                    color = badgeBg,
                    shape = RoundedCornerShape(6.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(imageVector = icon, contentDescription = null, tint = badgeTextColor, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(5.dp))
                        Text(badgeText, fontSize = 11.sp, fontWeight = FontWeight.ExtraBold, color = badgeTextColor)
                    }
                }
            }
        }
    }
}

@Composable
private fun ChannelPerformanceRow(name: String, gmv: String, rate: String, color: Color) {
    Surface(
        color = Color(0xFFFAFAFA),
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier.fillMaxWidth().border(1.dp, Color(0xFFE2E8F0), RoundedCornerShape(12.dp))
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(name, fontSize = 13.sp, fontWeight = FontWeight.ExtraBold, color = NavyDark)
                Text(gmv, fontSize = 11.sp, color = TextMuted, fontWeight = FontWeight.SemiBold)
            }
            Surface(color = color.copy(alpha = 0.12f), shape = RoundedCornerShape(6.dp)) {
                Text(rate, fontSize = 12.sp, fontWeight = FontWeight.ExtraBold, color = color, modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp))
            }
        }
    }
}

@Composable
private fun BizImpactCard(label: String, value: String, subtitle: String, valueColor: Color = NavyDark, modifier: Modifier = Modifier) {
    Surface(
        color = Color(0xFFF8FAFC),
        shape = RoundedCornerShape(12.dp),
        modifier = modifier
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(label, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = TextMuted)
            Text(value, fontSize = 17.sp, fontWeight = FontWeight.ExtraBold, color = valueColor, modifier = Modifier.padding(vertical = 2.dp))
            Text(subtitle, fontSize = 10.sp, color = TextMuted)
        }
    }
}

@Composable
private fun ForecastCard(period: String, amount: String, label: String, modifier: Modifier = Modifier) {
    Surface(
        color = Color.White,
        shape = RoundedCornerShape(14.dp),
        shadowElevation = 1.dp,
        modifier = modifier
    ) {
        Column(modifier = Modifier.padding(10.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Text(amount, fontSize = 16.sp, fontWeight = FontWeight.ExtraBold, color = PrimaryBlue)
            Text(period, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = NavyDark)
            Text(label, fontSize = 9.sp, fontWeight = FontWeight.SemiBold, color = TextMuted)
        }
    }
}


