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
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.paypulse.ai.data.model.*
import com.paypulse.ai.ui.theme.*

@Composable
fun OverviewScreen(
    overview: OverviewScreenData,
    onNavigateToQueue: () -> Unit,
    onSelectTransaction: (String) -> Unit,
    onOpenIncidentModal: (String?) -> Unit,
    onOpenAffordabilityCheck: () -> Unit = {},
    onOpenBorrowerProfile: (String) -> Unit = {}
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF2F5FC))
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // ─── Header: Greeting & Recovery Pulse Title ────────────────────────
        Column(verticalArrangement = Arrangement.spacedBy(3.dp)) {
            Text(
                text = "Good Morning, Rahul",
                fontSize = 15.sp,
                color = Color(0xFF64748B),
                fontWeight = FontWeight.Medium
            )
            Text(
                text = "Here is your recovery pulse",
                fontSize = 26.sp,
                fontWeight = FontWeight.ExtraBold,
                color = Color(0xFF0F172A)
            )
        }

        // ─── HERO CARD: REVENUE AT RISK (ENHANCED GRADIENT DESIGN) ─────────────
        Surface(
            shape = RoundedCornerShape(24.dp),
            color = Color.Transparent,
            shadowElevation = 3.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            Box(
                modifier = Modifier
                    .background(
                        brush = Brush.linearGradient(
                            colors = listOf(
                                Color(0xFFFFF7ED),
                                Color(0xFFFEF3C7),
                                Color(0xFFFDE68A)
                            )
                        )
                    )
                    .border(1.dp, Color(0xFFF59E0B).copy(alpha = 0.4f), RoundedCornerShape(24.dp))
                    .padding(20.dp)
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Surface(
                                color = Color(0xFFFEF3C7),
                                shape = CircleShape,
                                modifier = Modifier.size(32.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(
                                        imageVector = Icons.Default.Warning,
                                        contentDescription = null,
                                        tint = Color(0xFFD97706),
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Revenue at Risk",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = Color(0xFF92400E)
                            )
                        }

                        Surface(
                            color = Color(0xFFFEE2E2),
                            shape = RoundedCornerShape(20.dp)
                        ) {
                            Text(
                                text = "⚠️ Action Needed",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFFDC2626),
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                            )
                        }
                    }

                    Text(
                        text = overview.recoveryPulse.revenueAtRiskFormatted,
                        fontSize = 38.sp,
                        fontWeight = FontWeight.Black,
                        color = Color(0xFFB45309)
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            color = Color.White.copy(alpha = 0.7f),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.ArrowUpward,
                                    contentDescription = null,
                                    tint = Color(0xFFD97706),
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "${overview.recoveryPulse.riskChangePct.toInt()}% from yesterday",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = Color(0xFF92400E)
                                )
                            }
                        }

                        Text(
                            text = "⚡ Auto-Escalation Active",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFD97706)
                        )
                    }
                }
            }
        }

        // ─── SECONDARY METRIC CARDS ROW (RECOVERABLE & RECOVERED) ────────────
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // RECOVERABLE Card (Soft Sky Blue)
            Surface(
                shape = RoundedCornerShape(20.dp),
                color = Color.Transparent,
                shadowElevation = 2.dp,
                modifier = Modifier
                    .weight(1f)
                    .border(1.dp, Color(0xFFBAE6FD), RoundedCornerShape(20.dp))
            ) {
                Box(
                    modifier = Modifier
                        .background(
                            brush = Brush.verticalGradient(
                                colors = listOf(Color(0xFFF0F9FF), Color(0xFFE0F2FE))
                            )
                        )
                        .padding(16.dp)
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "RECOVERABLE",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = Color(0xFF0369A1),
                                letterSpacing = 0.5.sp
                            )
                            Icon(
                                imageVector = Icons.Default.TrendingUp,
                                contentDescription = null,
                                tint = PrimaryBlue,
                                modifier = Modifier.size(14.dp)
                            )
                        }
                        Text(
                            text = overview.recoveryPulse.expectedRecoveryFormatted,
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Black,
                            color = Color(0xFF0284C7)
                        )
                        Text(
                            text = "${overview.recoveryPulse.expectedPctOfRisk}% of at-risk revenue",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF0369A1)
                        )
                    }
                }
            }

            // RECOVERED Card (Soft Mint Emerald)
            Surface(
                shape = RoundedCornerShape(20.dp),
                color = Color.Transparent,
                shadowElevation = 2.dp,
                modifier = Modifier
                    .weight(1f)
                    .border(1.dp, Color(0xFFA7F3D0), RoundedCornerShape(20.dp))
            ) {
                Box(
                    modifier = Modifier
                        .background(
                            brush = Brush.verticalGradient(
                                colors = listOf(Color(0xFFF0FDF4), Color(0xFFDCFCE7))
                            )
                        )
                        .padding(16.dp)
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "RECOVERED",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = Color(0xFF047857),
                                letterSpacing = 0.5.sp
                            )
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = null,
                                tint = EmeraldGreen,
                                modifier = Modifier.size(14.dp)
                            )
                        }
                        Text(
                            text = overview.recoveryPulse.recoveredAmountFormatted,
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Black,
                            color = NavyDark
                        )
                        Text(
                            text = "+8.2% this week",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = EmeraldGreen
                        )
                    }
                }
            }
        }

        // ─── RECOVERY RATE CARD (WITH RING INDICATOR) ────────────────────────
        Surface(
            shape = RoundedCornerShape(24.dp),
            color = Color.White,
            shadowElevation = 1.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(
                        text = "Recovery Rate",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF64748B)
                    )
                    Text(
                        text = overview.recoveryPulse.recoveryRateFormatted,
                        fontSize = 28.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color(0xFF0F172A)
                    )
                }

                Box(
                    modifier = Modifier
                        .size(56.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFE0F2FE)),
                    contentAlignment = Alignment.Center
                ) {
                    Box(
                        modifier = Modifier
                            .size(42.dp)
                            .clip(CircleShape)
                            .background(Color.White),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "+2.4%",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color(0xFF0284C7)
                        )
                    }
                }
            }
        }

        // ─── PREMIUM AFFORDABILITY & BORROWING CAPACITY HERO CARD ───────────────
        Surface(
            shape = RoundedCornerShape(24.dp),
            color = Color.White,
            shadowElevation = 1.dp,
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onOpenAffordabilityCheck() }
        ) {
            Box(
                modifier = Modifier
                    .background(
                        brush = Brush.verticalGradient(
                            colors = listOf(Color.White, Color(0xFFF0FDF4))
                        )
                    )
                    .padding(18.dp)
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    // Header Row: Icon + Title + Score Pill
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                            Surface(
                                color = Color(0xFFECFDF5),
                                shape = CircleShape,
                                modifier = Modifier.size(38.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(
                                        imageVector = Icons.Default.Calculate,
                                        contentDescription = null,
                                        tint = EmeraldGreen,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = "Affordability & Capacity",
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = NavyDark
                                )
                                Text(
                                    text = "Evaluate loan limits & cash flow",
                                    fontSize = 11.sp,
                                    color = TextMuted,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }

                        Surface(
                            color = Color(0xFFDCFCE7),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(
                                text = "78/100 🟢",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = EmeraldGreen,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                            )
                        }
                    }

                    HorizontalDivider(color = Color(0xFFE2E8F0).copy(alpha = 0.6f))

                    Text(
                        text = "Healthy monthly cash flow — repayments fit comfortably within available disposable income.",
                        fontSize = 12.sp,
                        color = NavyDark,
                        lineHeight = 16.sp,
                        fontWeight = FontWeight.Medium
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.clickable { onOpenAffordabilityCheck() }
                        ) {
                            Text(
                                text = "Check Loan Affordability",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = PrimaryBlue
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Icon(
                                imageVector = Icons.Default.ArrowForward,
                                contentDescription = null,
                                tint = PrimaryBlue,
                                modifier = Modifier.size(14.dp)
                            )
                        }
                    }
                }
            }
        }

        // ─── SECTION 1: PROFIT & LOSS & FINANCIAL ANALYTICS DASHBOARD (ENHANCED DESIGN) ──────────────
        Surface(
            shape = RoundedCornerShape(24.dp),
            color = Color.White,
            shadowElevation = 2.dp,
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, Color(0xFFE2E8F0), RoundedCornerShape(24.dp))
        ) {
            Box(
                modifier = Modifier
                    .background(
                        brush = Brush.verticalGradient(
                            colors = listOf(Color.White, Color(0xFFF8FAFC))
                        )
                    )
                    .padding(20.dp)
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(18.dp)) {
                    // Header Row: Section Icon, Title & Shifted ROI Badge
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Surface(
                                color = Color(0xFFECFDF5),
                                shape = CircleShape,
                                modifier = Modifier.size(40.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(
                                        imageVector = Icons.Default.TrendingUp,
                                        contentDescription = null,
                                        tint = EmeraldGreen,
                                        modifier = Modifier.size(22.dp)
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = "Profit & Loss Analytics",
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = NavyDark
                                )
                                Text(
                                    text = "Real-Time Revenue, Cost Savings & Financial Yield",
                                    fontSize = 11.sp,
                                    color = TextMuted,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }

                        // Shifted ROI Pill on Next Line with Soft Gradient
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = Color.Transparent,
                            modifier = Modifier.border(1.dp, Color(0xFFA7F3D0), RoundedCornerShape(12.dp))
                        ) {
                            Box(
                                modifier = Modifier
                                    .background(
                                        brush = Brush.horizontalGradient(
                                            colors = listOf(Color(0xFFECFDF5), Color(0xFFD1FAE5))
                                        )
                                    )
                                    .padding(horizontal = 12.dp, vertical = 6.dp)
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.Bolt,
                                        contentDescription = null,
                                        tint = EmeraldGreen,
                                        modifier = Modifier.size(15.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "${overview.profitLoss.netRecoveryRoiPct.toInt()}% ROI Operating Return",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.ExtraBold,
                                        color = Color(0xFF047857)
                                    )
                                }
                            }
                        }
                    }

                    // Main Net Profit Hero Banner (Premium Emerald Gradient Card)
                    Surface(
                        shape = RoundedCornerShape(20.dp),
                        color = Color.Transparent,
                        shadowElevation = 4.dp,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Box(
                            modifier = Modifier
                                .background(
                                    brush = Brush.linearGradient(
                                        colors = listOf(
                                            Color(0xFF064E3B),
                                            Color(0xFF047857),
                                            Color(0xFF059669)
                                        )
                                    )
                                )
                                .padding(18.dp)
                        ) {
                            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                                // Banner Top Row: Tag Badge & Live Indicator
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            imageVector = Icons.Default.MonetizationOn,
                                            contentDescription = null,
                                            tint = Color(0xFF6EE7B7),
                                            modifier = Modifier.size(16.dp)
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(
                                            text = "NET RECOVERED PROFIT",
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.ExtraBold,
                                            color = Color(0xFFA7F3D0),
                                            letterSpacing = 0.5.sp
                                        )
                                    }

                                    Surface(
                                        color = Color.White.copy(alpha = 0.15f),
                                        shape = RoundedCornerShape(20.dp)
                                    ) {
                                        Text(
                                            text = "🟢 Live P&L",
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color.White,
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                        )
                                    }
                                }

                                // Prominent Bold Net Profit Figure
                                Text(
                                    text = overview.profitLoss.netProfitFormatted,
                                    fontSize = 34.sp,
                                    fontWeight = FontWeight.Black,
                                    color = Color.White
                                )

                                // Subtitle with Bullet Point
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(
                                        modifier = Modifier
                                            .size(6.dp)
                                            .clip(CircleShape)
                                            .background(Color(0xFF6EE7B7))
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "Net gain after automated gateway fee deductions & retry costs",
                                        fontSize = 11.sp,
                                        color = Color(0xFFECFDF5),
                                        fontWeight = FontWeight.Medium
                                    )
                                }

                                HorizontalDivider(
                                    modifier = Modifier.padding(vertical = 2.dp),
                                    color = Color.White.copy(alpha = 0.2f),
                                    thickness = 1.dp
                                )

                                // Footer Key Highlights
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            imageVector = Icons.Default.CheckCircle,
                                            contentDescription = null,
                                            tint = Color(0xFF6EE7B7),
                                            modifier = Modifier.size(14.dp)
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(
                                            text = "14,280 retries saved",
                                            fontSize = 11.sp,
                                            color = Color(0xFFECFDF5),
                                            fontWeight = FontWeight.Medium
                                        )
                                    }
                                    Text(text = "•", fontSize = 11.sp, color = Color.White.copy(alpha = 0.5f))
                                    Text(
                                        text = "⚡ Real-time Settlement",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White
                                    )
                                }
                            }
                        }
                    }

                    // 4-Grid Financial Metric Cards Matrix (Refined Soft Tinted Surfaces)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        // Card 1: Gross Revenue at Risk (Amber)
                        Surface(
                            shape = RoundedCornerShape(16.dp),
                            color = Color.Transparent,
                            modifier = Modifier
                                .weight(1f)
                                .border(1.dp, Color(0xFFFDE68A), RoundedCornerShape(16.dp))
                        ) {
                            Box(
                                modifier = Modifier
                                    .background(
                                        brush = Brush.verticalGradient(
                                            colors = listOf(Color(0xFFFFFBEB), Color(0xFFFEF3C7))
                                        )
                                    )
                                    .padding(12.dp)
                            ) {
                                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text("GROSS AT RISK", fontSize = 10.sp, fontWeight = FontWeight.ExtraBold, color = Color(0xFFB45309))
                                        Icon(Icons.Default.Warning, contentDescription = null, tint = AmberGold, modifier = Modifier.size(14.dp))
                                    }
                                    Text(
                                        text = overview.profitLoss.grossRevenueAtRiskFormatted,
                                        fontSize = 18.sp,
                                        fontWeight = FontWeight.ExtraBold,
                                        color = AmberRiskText
                                    )
                                    Text(
                                        text = "Unrecovered: ${overview.profitLoss.unrecoveredRiskFormatted}",
                                        fontSize = 10.sp,
                                        color = Color(0xFFB91C1C),
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }

                        // Card 2: Net Recovered Revenue (Emerald)
                        Surface(
                            shape = RoundedCornerShape(16.dp),
                            color = Color.Transparent,
                            modifier = Modifier
                                .weight(1f)
                                .border(1.dp, Color(0xFFA7F3D0), RoundedCornerShape(16.dp))
                        ) {
                            Box(
                                modifier = Modifier
                                    .background(
                                        brush = Brush.verticalGradient(
                                            colors = listOf(Color(0xFFF0FDF4), Color(0xFFDCFCE7))
                                        )
                                    )
                                    .padding(12.dp)
                            ) {
                                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text("NET RECOVERED", fontSize = 10.sp, fontWeight = FontWeight.ExtraBold, color = Color(0xFF047857))
                                        Icon(Icons.Default.CheckCircle, contentDescription = null, tint = EmeraldGreen, modifier = Modifier.size(14.dp))
                                    }
                                    Text(
                                        text = overview.profitLoss.netRecoveredRevenueFormatted,
                                        fontSize = 18.sp,
                                        fontWeight = FontWeight.ExtraBold,
                                        color = NavyDark
                                    )
                                    Text(
                                        text = "${overview.recoveryPulse.recoveryRateFormatted} Rate",
                                        fontSize = 10.sp,
                                        color = EmeraldGreen,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        // Card 3: Gateway Savings (Sky Blue)
                        Surface(
                            shape = RoundedCornerShape(16.dp),
                            color = Color.Transparent,
                            modifier = Modifier
                                .weight(1f)
                                .border(1.dp, Color(0xFFBAE6FD), RoundedCornerShape(16.dp))
                        ) {
                            Box(
                                modifier = Modifier
                                    .background(
                                        brush = Brush.verticalGradient(
                                            colors = listOf(Color(0xFFF0F9FF), Color(0xFFE0F2FE))
                                        )
                                    )
                                    .padding(12.dp)
                            ) {
                                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text("GATEWAY SAVINGS", fontSize = 10.sp, fontWeight = FontWeight.ExtraBold, color = Color(0xFF0369A1))
                                        Icon(Icons.Default.Savings, contentDescription = null, tint = PrimaryBlue, modifier = Modifier.size(14.dp))
                                    }
                                    Text(
                                        text = overview.profitLoss.gatewayFeeSavingsFormatted,
                                        fontSize = 18.sp,
                                        fontWeight = FontWeight.ExtraBold,
                                        color = PrimaryBlue
                                    )
                                    Text(
                                        text = "Retry Cost Avoided",
                                        fontSize = 10.sp,
                                        color = Color(0xFF0284C7),
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }

                        // Card 4: AI Operating Cost (Violet)
                        Surface(
                            shape = RoundedCornerShape(16.dp),
                            color = Color.Transparent,
                            modifier = Modifier
                                .weight(1f)
                                .border(1.dp, Color(0xFFDDD6FE), RoundedCornerShape(16.dp))
                        ) {
                            Box(
                                modifier = Modifier
                                    .background(
                                        brush = Brush.verticalGradient(
                                            colors = listOf(Color(0xFFF5F3FF), Color(0xFFEDE9FE))
                                        )
                                    )
                                    .padding(12.dp)
                            ) {
                                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text("AI OVERHEAD", fontSize = 10.sp, fontWeight = FontWeight.ExtraBold, color = Color(0xFF6D28D9))
                                        Icon(Icons.Default.Psychology, contentDescription = null, tint = Color(0xFF7C3AED), modifier = Modifier.size(14.dp))
                                    }
                                    Text(
                                        text = overview.profitLoss.aiOperatingCostFormatted,
                                        fontSize = 18.sp,
                                        fontWeight = FontWeight.ExtraBold,
                                        color = Color(0xFF5B21B6)
                                    )
                                    Text(
                                        text = "Compute & Orchestration",
                                        fontSize = 10.sp,
                                        color = Color(0xFF7C3AED),
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                    }

                    // Progress Bar: Recovered vs Unrecovered Split Breakdown
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Surface(color = EmeraldGreen, shape = CircleShape, modifier = Modifier.size(8.dp)) {}
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "Recovered: ${overview.recoveryPulse.recoveredAmountFormatted} (${overview.recoveryPulse.recoveryRateFormatted})",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = NavyDark
                                )
                            }
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Surface(color = Color(0xFFF43F5E), shape = CircleShape, modifier = Modifier.size(8.dp)) {}
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "Unrecovered: ${overview.profitLoss.unrecoveredRiskFormatted}",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = Color(0xFFBE123C)
                                )
                            }
                        }

                        // Gradient Dual Track Progress Bar
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(10.dp)
                                .clip(RoundedCornerShape(6.dp))
                                .background(Color(0xFFFFE4E6))
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxHeight()
                                    .fillMaxWidth(fraction = (overview.recoveryPulse.recoveryRateValue / 100f).coerceIn(0.1f, 1f))
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(Brush.horizontalGradient(listOf(Color(0xFF059669), Color(0xFF10B981))))
                            )
                        }
                    }

                    HorizontalDivider(color = Color(0xFFE2E8F0))

                    // Channel Recovery Efficiency Section
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Text(
                            text = "Channel Recovery Efficiency",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = NavyDark
                        )
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            // Auto Retries Pill Card
                            Surface(
                                color = Color(0xFFE0F2FE),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.weight(1f)
                            ) {
                                Column(
                                    modifier = Modifier.padding(vertical = 10.dp, horizontal = 10.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Text("Auto Retries", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color(0xFF0369A1))
                                    Text("${overview.profitLoss.autoRetryPct}%", fontSize = 14.sp, fontWeight = FontWeight.ExtraBold, color = PrimaryBlue)
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth(0.8f)
                                            .height(3.dp)
                                            .clip(RoundedCornerShape(2.dp))
                                            .background(PrimaryBlue.copy(alpha = 0.3f))
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .fillMaxHeight()
                                                .fillMaxWidth(overview.profitLoss.autoRetryPct / 100f)
                                                .background(PrimaryBlue)
                                        )
                                    }
                                }
                            }

                            // Smart Links Pill Card
                            Surface(
                                color = Color(0xFFDCFCE7),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.weight(1f)
                            ) {
                                Column(
                                    modifier = Modifier.padding(vertical = 10.dp, horizontal = 10.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Text("Smart Links", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color(0xFF15803D))
                                    Text("${overview.profitLoss.smartLinkPct}%", fontSize = 14.sp, fontWeight = FontWeight.ExtraBold, color = EmeraldGreen)
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth(0.8f)
                                            .height(3.dp)
                                            .clip(RoundedCornerShape(2.dp))
                                            .background(EmeraldGreen.copy(alpha = 0.3f))
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .fillMaxHeight()
                                                .fillMaxWidth(overview.profitLoss.smartLinkPct / 100f)
                                                .background(EmeraldGreen)
                                        )
                                    }
                                }
                            }

                            // eMandate Pill Card
                            Surface(
                                color = Color(0xFFFEF3C7),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.weight(1f)
                            ) {
                                Column(
                                    modifier = Modifier.padding(vertical = 10.dp, horizontal = 10.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Text("eMandate", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color(0xFFB45309))
                                    Text("${overview.profitLoss.eMandatePct}%", fontSize = 14.sp, fontWeight = FontWeight.ExtraBold, color = AmberGold)
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth(0.8f)
                                            .height(3.dp)
                                            .clip(RoundedCornerShape(2.dp))
                                            .background(AmberGold.copy(alpha = 0.3f))
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .fillMaxHeight()
                                                .fillMaxWidth(overview.profitLoss.eMandatePct / 100f)
                                                .background(AmberGold)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // ─── SECTION 2: TODAY'S RECOVERY IMPACT ───────────────────────────────
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Text(
                text = "Today's Recovery Impact",
                fontSize = 16.sp,
                fontWeight = FontWeight.ExtraBold,
                color = NavyDark
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Metric 1: Recovered Today
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = Color.White,
                    shadowElevation = 1.dp,
                    modifier = Modifier.weight(1f)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text(overview.todayImpact.recoveredTodayFormatted, fontSize = 18.sp, fontWeight = FontWeight.ExtraBold, color = PrimaryBlue)
                        Text("Recovered Today", fontSize = 11.sp, color = TextMuted, fontWeight = FontWeight.SemiBold)
                    }
                }

                // Metric 2: Successful Recoveries
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = Color.White,
                    shadowElevation = 1.dp,
                    modifier = Modifier.weight(1f)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text("${overview.todayImpact.successfulRecoveriesToday}", fontSize = 18.sp, fontWeight = FontWeight.ExtraBold, color = NavyDark)
                        Text("Recoveries Made", fontSize = 11.sp, color = TextMuted, fontWeight = FontWeight.SemiBold)
                    }
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Metric 3: Recovery Rate Today
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = Color.White,
                    shadowElevation = 1.dp,
                    modifier = Modifier.weight(1f)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text(overview.todayImpact.recoveryRateTodayFormatted, fontSize = 18.sp, fontWeight = FontWeight.ExtraBold, color = EmeraldGreen)
                        Text(overview.todayImpact.changeVsPreviousPeriod, fontSize = 11.sp, color = TextMuted, fontWeight = FontWeight.SemiBold)
                    }
                }

                // Metric 4: Retry Cost Saved
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = Color.White,
                    shadowElevation = 1.dp,
                    modifier = Modifier.weight(1f)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text(overview.todayImpact.retryCostAvoidedFormatted, fontSize = 18.sp, fontWeight = FontWeight.ExtraBold, color = AmberRiskText)
                        Text("Retry Cost Saved", fontSize = 11.sp, color = TextMuted, fontWeight = FontWeight.SemiBold)
                    }
                }
            }
        }

        // ─── SECTION 3: CIBIL CREDIT SCORE & RISK INTELLIGENCE DASHBOARD (ENHANCED DESIGN) ───────
        Surface(
            shape = RoundedCornerShape(24.dp),
            color = Color.White,
            shadowElevation = 2.dp,
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, Color(0xFFE2E8F0), RoundedCornerShape(24.dp))
        ) {
            Box(
                modifier = Modifier
                    .background(
                        brush = Brush.verticalGradient(
                            colors = listOf(Color.White, Color(0xFFF8FAFC))
                        )
                    )
                    .padding(20.dp)
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(18.dp)) {
                    // Header Row: Shield Icon & Title
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Surface(
                                color = Color(0xFFECFDF5),
                                shape = CircleShape,
                                modifier = Modifier.size(40.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(
                                        imageVector = Icons.Default.Shield,
                                        contentDescription = null,
                                        tint = EmeraldGreen,
                                        modifier = Modifier.size(22.dp)
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = "CIBIL Credit Intelligence",
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = NavyDark
                                )
                                Text(
                                    text = "Real-Time Bureau Analytics & Borrower Scoring",
                                    fontSize = 11.sp,
                                    color = TextMuted,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                    }

                    // Main CIBIL Portfolio Score Hero Banner (Sleek Dark Slate / Emerald Glow Hero)
                    Surface(
                        shape = RoundedCornerShape(20.dp),
                        color = Color.Transparent,
                        shadowElevation = 4.dp,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Box(
                            modifier = Modifier
                                .background(
                                    brush = Brush.linearGradient(
                                        colors = listOf(
                                            Color(0xFF0F172A),
                                            Color(0xFF1E293B),
                                            Color(0xFF0F766E)
                                        )
                                    )
                                )
                                .padding(18.dp)
                        ) {
                            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                                // Top Header Line: Tag + Sync Count
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            imageVector = Icons.Default.VerifiedUser,
                                            contentDescription = null,
                                            tint = Color(0xFF6EE7B7),
                                            modifier = Modifier.size(14.dp)
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(
                                            text = "PORTFOLIO AVERAGE CIBIL",
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.ExtraBold,
                                            color = Color(0xFF6EE7B7),
                                            letterSpacing = 0.5.sp
                                        )
                                    }
                                    Text(
                                        text = "${overview.cibilSummary.borrowersAnalyzedCount} Borrowers Sync'd",
                                        fontSize = 10.sp,
                                        color = Color(0xFFCBD5E1),
                                        fontWeight = FontWeight.Medium
                                    )
                                }

                                // Main Score Figure
                                Row(verticalAlignment = Alignment.Bottom) {
                                    Text(
                                        text = "${overview.cibilSummary.avgCreditScore}",
                                        fontSize = 36.sp,
                                        fontWeight = FontWeight.Black,
                                        color = Color.White
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "/ 900",
                                        fontSize = 15.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFFA7F3D0),
                                        modifier = Modifier.padding(bottom = 6.dp)
                                    )
                                }

                                // Shifted EXCELLENT (Low Risk) Status Pill on Next Line
                                Surface(
                                    color = Color(0xFF047857),
                                    shape = RoundedCornerShape(10.dp),
                                    modifier = Modifier.border(1.dp, Color(0xFF6EE7B7).copy(alpha = 0.4f), RoundedCornerShape(10.dp))
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = "🟢 ${overview.cibilSummary.scoreStatusLabel} (Low Risk)",
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.ExtraBold,
                                            color = Color.White
                                        )
                                    }
                                }
                            }
                        }
                    }

                    // Risk Distribution Segmented Bar
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("🟢 Low Risk (750+): ${overview.cibilSummary.lowRiskPct}%", fontSize = 10.sp, fontWeight = FontWeight.ExtraBold, color = EmeraldGreen)
                            Text("🟡 Moderate (650-749): ${overview.cibilSummary.mediumRiskPct}%", fontSize = 10.sp, fontWeight = FontWeight.ExtraBold, color = AmberGold)
                        }
                        Text("🔴 High Risk (<650): ${overview.cibilSummary.highRiskPct}%", fontSize = 10.sp, fontWeight = FontWeight.ExtraBold, color = Color(0xFFDC2626))

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(10.dp)
                                .clip(RoundedCornerShape(5.dp))
                        ) {
                            Box(modifier = Modifier.weight(overview.cibilSummary.lowRiskPct.toFloat()).fillMaxHeight().background(EmeraldGreen))
                            Spacer(modifier = Modifier.width(2.dp))
                            Box(modifier = Modifier.weight(overview.cibilSummary.mediumRiskPct.toFloat()).fillMaxHeight().background(AmberGold))
                            Spacer(modifier = Modifier.width(2.dp))
                            Box(modifier = Modifier.weight(overview.cibilSummary.highRiskPct.toFloat()).fillMaxHeight().background(Color(0xFFDC2626)))
                        }
                    }

                    HorizontalDivider(color = Color(0xFFE2E8F0))

                    // Individual Borrower CIBIL Cards
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Borrower CIBIL Profiles",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = NavyDark
                            )
                            Text(
                                text = "Tap for Full Report →",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = PrimaryBlue
                            )
                        }

                        overview.borrowerCreditList.forEach { borrower ->
                            val scoreBg = when {
                                borrower.cibilScore >= 750 -> Color(0xFFECFDF5)
                                borrower.cibilScore >= 650 -> Color(0xFFFFFBEB)
                                else -> Color(0xFFFEF2F2)
                            }
                            val scoreFg = when {
                                borrower.cibilScore >= 750 -> EmeraldGreen
                                borrower.cibilScore >= 650 -> AmberGold
                                else -> Color(0xFFDC2626)
                            }

                            Surface(
                                shape = RoundedCornerShape(16.dp),
                                color = Color.White,
                                shadowElevation = 1.dp,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .border(1.dp, Color(0xFFE2E8F0), RoundedCornerShape(16.dp))
                                    .clickable { onOpenBorrowerProfile(borrower.borrowerName) }
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(14.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                                        Surface(
                                            color = scoreBg,
                                            shape = CircleShape,
                                            modifier = Modifier.size(42.dp)
                                        ) {
                                            Box(contentAlignment = Alignment.Center) {
                                                Text(
                                                    text = "${borrower.cibilScore}",
                                                    fontSize = 12.sp,
                                                    fontWeight = FontWeight.Black,
                                                    color = scoreFg
                                                )
                                            }
                                        }

                                        Spacer(modifier = Modifier.width(12.dp))

                                        Column {
                                            Text(
                                                text = borrower.borrowerName,
                                                fontSize = 14.sp,
                                                fontWeight = FontWeight.ExtraBold,
                                                color = NavyDark
                                            )
                                            Text(
                                                text = "${borrower.totalBorrowedFormatted} • ${borrower.activeLoansCount} active loans",
                                                fontSize = 11.sp,
                                                color = TextMuted,
                                                fontWeight = FontWeight.Medium
                                            )
                                        }
                                    }

                                    Column(horizontalAlignment = Alignment.End) {
                                        Surface(
                                            color = scoreBg,
                                            shape = RoundedCornerShape(8.dp)
                                        ) {
                                            Text(
                                                text = borrower.riskTier,
                                                fontSize = 10.sp,
                                                fontWeight = FontWeight.ExtraBold,
                                                color = scoreFg,
                                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                            )
                                        }
                                        Spacer(modifier = Modifier.height(3.dp))
                                        Text(
                                            text = "${borrower.onTimePaymentPct}% On-Time",
                                            fontSize = 10.sp,
                                            color = TextMuted,
                                            fontWeight = FontWeight.SemiBold
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // ─── SECTION 4: PRIORITY CTA ─────────────────────────────────────────
        Surface(
            shape = RoundedCornerShape(20.dp),
            color = PrimaryBlue,
            shadowElevation = 3.dp,
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onNavigateToQueue() }
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(18.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Review Recovery Opportunities",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color.White
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "${overview.transactionsRecoverableNow} transactions ready • ${overview.potentialRecoveryNowFormatted} expected recovery",
                        fontSize = 12.sp,
                        color = Color(0xFFBAE6FD),
                        fontWeight = FontWeight.Medium
                    )
                }

                Surface(
                    color = Color.White,
                    shape = CircleShape,
                    modifier = Modifier.size(36.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                            contentDescription = "Review Queue",
                            tint = PrimaryBlue,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }
        }

        // ─── SECTION 5: LIVE RECOVERY ACTIVITY ────────────────────────────────
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            color = Color(0xFFDCFCE7),
                            shape = CircleShape,
                            modifier = Modifier.size(10.dp)
                        ) {
                            Box(modifier = Modifier.background(EmeraldGreen))
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Live Recovery Feed",
                            fontSize = 17.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = NavyDark
                        )
                    }
                    Text(
                        text = "Real-time Razorpay telemetry & AI resolution events",
                        fontSize = 11.sp,
                        color = TextMuted,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            if (overview.recentEvents.isEmpty()) {
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = Color.White,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "Recovery activity will appear here as PayPulse processes failed payments.",
                        fontSize = 12.sp,
                        color = TextMuted,
                        modifier = Modifier.padding(16.dp)
                    )
                }
            } else {
                overview.recentEvents.forEach { event ->
                    val isRecovered = event.status.equals("Recovered", ignoreCase = true)
                    val receiverName = when (event.transactionId) {
                        "TXN_10482" -> "Rahul Sharma"
                        "TXN_10483" -> "Priya Nair"
                        "TXN_10484" -> "Ananya Iyer"
                        "TXN_10485" -> "Vikram Patel"
                        "TXN_10486" -> "Siddharth Verma"
                        else -> "Rahul Sharma"
                    }

                    val style = when (event.status) {
                        "Recovered" -> EventUiStyle(Color(0xFFDCFCE7), EmeraldGreen, Icons.Default.CheckCircle, Color(0xFFECFDF5))
                        "Policy Approved" -> EventUiStyle(Color(0xFFE0F2FE), PrimaryBlue, Icons.Default.Bolt, Color(0xFFEFF6FF))
                        "Retry Scheduled" -> EventUiStyle(Color(0xFFFEF3C7), Color(0xFFB45309), Icons.Default.Schedule, Color(0xFFFFFBEB))
                        else -> EventUiStyle(Color(0xFFF1F5F9), Color.Gray, Icons.Default.Sync, Color(0xFFF8FAFC))
                    }
                    val statusBg = style.statusBg
                    val statusFg = style.statusFg
                    val iconVector = style.iconVector
                    val iconBg = style.iconBg

                    Surface(
                        shape = RoundedCornerShape(18.dp),
                        color = Color.White,
                        shadowElevation = 1.dp,
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(1.dp, Color(0xFFE2E8F0), RoundedCornerShape(18.dp))
                            .clickable { onSelectTransaction(event.transactionId) }
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(14.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.weight(1f)
                            ) {
                                // Dynamic Circle Avatar Icon
                                Surface(
                                    color = iconBg,
                                    shape = CircleShape,
                                    modifier = Modifier.size(40.dp)
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Icon(
                                            imageVector = iconVector,
                                            contentDescription = null,
                                            tint = statusFg,
                                            modifier = Modifier.size(22.dp)
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.width(12.dp))

                                Column(verticalArrangement = Arrangement.spacedBy(3.dp)) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(
                                            text = receiverName,
                                            fontSize = 14.sp,
                                            fontWeight = FontWeight.ExtraBold,
                                            color = NavyDark
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(
                                            text = "(${event.transactionId})",
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = PrimaryBlue
                                        )
                                    }

                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Surface(
                                            color = Color(0xFFF1F5F9),
                                            shape = RoundedCornerShape(4.dp)
                                        ) {
                                            Text(
                                                text = event.failureReason,
                                                fontSize = 9.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = TextMuted,
                                                modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp)
                                            )
                                        }
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(
                                            text = "⚡ ${event.aiAction}",
                                            fontSize = 11.sp,
                                            color = TextMuted,
                                            fontWeight = FontWeight.Medium
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.width(8.dp))

                            Column(horizontalAlignment = Alignment.End, verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                Text(
                                    text = if (isRecovered) "+${event.amountFormatted}" else event.amountFormatted,
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = if (isRecovered) EmeraldGreen else NavyDark
                                )

                                Surface(color = statusBg, shape = RoundedCornerShape(8.dp)) {
                                    Text(
                                        text = event.status,
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.ExtraBold,
                                        color = statusFg,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                    )
                                }

                                Text(
                                    text = event.timestamp,
                                    fontSize = 10.sp,
                                    color = TextMuted,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                    }
                }
            }
        }

        // ─── SECTION 6: RAZORPAY SETTLEMENT & PAYOUT TRACKER ───────────────────
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Text(
                text = "Razorpay Settlement & Payout Tracker",
                fontSize = 20.sp,
                fontWeight = FontWeight.ExtraBold,
                color = NavyDark
            )

            Surface(
                shape = RoundedCornerShape(20.dp),
                color = Color.White,
                shadowElevation = 2.dp,
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, Color(0xFFBAE6FD), RoundedCornerShape(20.dp))
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.AccountBalance, contentDescription = null, tint = PrimaryBlue, modifier = Modifier.size(20.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Merchant Payout Status", fontSize = 16.sp, fontWeight = FontWeight.ExtraBold, color = NavyDark)
                        }
                    }

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        Surface(color = Color(0xFFF8FAFC), shape = RoundedCornerShape(12.dp), modifier = Modifier.weight(1f)) {
                            Column(modifier = Modifier.padding(10.dp)) {
                                Text("Today's Payout", fontSize = 11.sp, color = TextMuted, fontWeight = FontWeight.Medium)
                                Text("₹2.84L", fontSize = 18.sp, fontWeight = FontWeight.ExtraBold, color = EmeraldGreen)
                                Text("Settled to HDFC •8912", fontSize = 10.sp, color = TextMuted)
                            }
                        }
                        Surface(color = Color(0xFFF8FAFC), shape = RoundedCornerShape(12.dp), modifier = Modifier.weight(1f)) {
                            Column(modifier = Modifier.padding(10.dp)) {
                                Text("Next Settlement", fontSize = 11.sp, color = TextMuted, fontWeight = FontWeight.Medium)
                                Text("Tomorrow 9 AM", fontSize = 16.sp, fontWeight = FontWeight.ExtraBold, color = PrimaryBlue)
                                Text("T+1 Standard Cycle", fontSize = 10.sp, color = TextMuted)
                            }
                        }
                    }
                }
            }

            // Gateway Telemetry Cards Grid
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                overview.gatewayHealthList.take(2).forEach { gw ->
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = Color.White,
                        shadowElevation = 1.dp,
                        modifier = Modifier.weight(1f)
                    ) {
                        Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Text(gw.gateway, fontSize = 12.sp, fontWeight = FontWeight.ExtraBold, color = NavyDark, maxLines = 1)
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text("Health: ${gw.healthScore}", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = if (gw.healthScore >= 80) EmeraldGreen else AmberRiskText)
                                Text("${gw.latencyMs}ms", fontSize = 11.sp, color = TextMuted)
                            }
                            Text("Failures: ${gw.failureRateFormatted}", fontSize = 10.sp, color = TextMuted)
                        }
                    }
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                overview.gatewayHealthList.drop(2).take(2).forEach { gw ->
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = Color.White,
                        shadowElevation = 1.dp,
                        modifier = Modifier.weight(1f)
                    ) {
                        Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Text(gw.gateway, fontSize = 12.sp, fontWeight = FontWeight.ExtraBold, color = NavyDark, maxLines = 1)
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text("Health: ${gw.healthScore}", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = if (gw.healthScore >= 80) EmeraldGreen else AmberRiskText)
                                Text("${gw.latencyMs}ms", fontSize = 11.sp, color = TextMuted)
                            }
                            Text("Failures: ${gw.failureRateFormatted}", fontSize = 10.sp, color = TextMuted)
                        }
                    }
                }
            }
        }

        // ─── SECTION 7: AI INTELLIGENCE (OBSERVATIONS) ────────────────────────
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Text(
                text = "AI Intelligence",
                fontSize = 16.sp,
                fontWeight = FontWeight.ExtraBold,
                color = NavyDark
            )

            Surface(
                shape = RoundedCornerShape(18.dp),
                color = Color.White,
                shadowElevation = 1.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    overview.aiObservations.forEach { obs ->
                        Row(verticalAlignment = Alignment.Top) {
                            Icon(
                                imageVector = Icons.Default.AutoAwesome,
                                contentDescription = null,
                                tint = PrimaryBlue,
                                modifier = Modifier.size(16.dp).padding(top = 2.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = obs,
                                fontSize = 12.sp,
                                color = NavyDark,
                                lineHeight = 16.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}

private data class EventUiStyle(
    val statusBg: Color,
    val statusFg: Color,
    val iconVector: androidx.compose.ui.graphics.vector.ImageVector,
    val iconBg: Color
)
