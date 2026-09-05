package com.paypulse.ai.ui.components

import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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
import com.paypulse.ai.data.model.LoanItem
import com.paypulse.ai.data.model.TransactionItem
import com.paypulse.ai.ui.theme.*
import com.paypulse.ai.ui.utils.NotificationHelper
import com.paypulse.ai.ui.utils.UpiPaymentLauncher

enum class NotificationCategory {
    PAYMENT, LOAN, SMART, AI, SECURITY
}

enum class NotificationGroupFilter {
    ALL, PAYMENT, LOAN, SMART, AI, SECURITY
}

data class RichNotificationItem(
    val id: String,
    val category: NotificationCategory,
    val iconEmoji: String,
    val title: String,
    val message: String,
    val categoryTag: String,
    val timestamp: String,
    val bgTint: Color,
    val borderColor: Color,
    val titleColor: Color,
    val recipientName: String = "Rahul",
    val amount: String = "₹3,500"
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationsDialog(
    loans: List<LoanItem> = emptyList(),
    transactions: List<TransactionItem> = emptyList(),
    onDismiss: () -> Unit,
    onOpenRepayLoan: (LoanItem) -> Unit = {},
    onOpenWhatsAppSimulator: (String) -> Unit = {},
    onOpenRazorpayCheckout: (id: String, amount: Double, title: String) -> Unit = { _, _, _ -> }
) {
    val context = LocalContext.current
    var selectedGroup by remember { mutableStateOf(NotificationGroupFilter.ALL) }

    // Build all requested Notifications across 5 Categories
    val allNotifications = remember {
        listOf(
            // 🔔 Payment Notifications
            RichNotificationItem(
                id = "N_PAY_1",
                category = NotificationCategory.PAYMENT,
                iconEmoji = "🟡",
                title = "Payment due in 3 days",
                message = "₹3,500 is due to Rahul on September 8.",
                categoryTag = "Payment Due Soon",
                timestamp = "3 days left",
                bgTint = Color(0xFFFFFBEB),
                borderColor = Color(0xFFFDE68A),
                titleColor = Color(0xFFB45309)
            ),
            RichNotificationItem(
                id = "N_PAY_2",
                category = NotificationCategory.PAYMENT,
                iconEmoji = "🔔",
                title = "Payment due today",
                message = "₹3,500 is due to Rahul today.",
                categoryTag = "Due Today",
                timestamp = "Due Today",
                bgTint = Color(0xFFFEF3C7),
                borderColor = Color(0xFFFCD34D),
                titleColor = Color(0xFFD97706)
            ),
            RichNotificationItem(
                id = "N_PAY_3",
                category = NotificationCategory.PAYMENT,
                iconEmoji = "🔴",
                title = "Payment overdue",
                message = "₹3,500 was due to Rahul yesterday. Mark as paid or send a reminder.",
                categoryTag = "Overdue",
                timestamp = "Yesterday",
                bgTint = Color(0xFFFEF2F2),
                borderColor = Color(0xFFFCA5A5),
                titleColor = Color(0xFFDC2626)
            ),
            RichNotificationItem(
                id = "N_PAY_4",
                category = NotificationCategory.PAYMENT,
                iconEmoji = "💰",
                title = "Payment received",
                message = "You received ₹2,000 from Rahul. Remaining balance: ₹3,000.",
                categoryTag = "Payment Received",
                timestamp = "10 min ago",
                bgTint = Color(0xFFECFDF5),
                borderColor = Color(0xFFA7F3D0),
                titleColor = Color(0xFF059669)
            ),
            RichNotificationItem(
                id = "N_PAY_5",
                category = NotificationCategory.PAYMENT,
                iconEmoji = "✅",
                title = "Payment successful",
                message = "₹2,000 was paid to Rahul.",
                categoryTag = "Payment Sent",
                timestamp = "1 hr ago",
                bgTint = Color(0xFFF0FDF4),
                borderColor = Color(0xFFBBF7D0),
                titleColor = Color(0xFF047857)
            ),
            RichNotificationItem(
                id = "N_PAY_6",
                category = NotificationCategory.PAYMENT,
                iconEmoji = "💸",
                title = "Partial payment received",
                message = "Rahul paid ₹2,000 toward the ₹5,000 loan. ₹3,000 remaining.",
                categoryTag = "Partial Payment",
                timestamp = "2 hrs ago",
                bgTint = Color(0xFFEFF6FF),
                borderColor = Color(0xFFBFDBFE),
                titleColor = PrimaryBlue
            ),

            // 👥 Loan Notifications
            RichNotificationItem(
                id = "N_LOAN_1",
                category = NotificationCategory.LOAN,
                iconEmoji = "📩",
                title = "New loan request",
                message = "Rahul has requested ₹5,000 from you.",
                categoryTag = "New Loan Request",
                timestamp = "Just now",
                bgTint = Color(0xFFF5F3FF),
                borderColor = Color(0xFFDDD6FE),
                titleColor = Color(0xFF7C3AED)
            ),
            RichNotificationItem(
                id = "N_LOAN_2",
                category = NotificationCategory.LOAN,
                iconEmoji = "✅",
                title = "Loan request accepted",
                message = "Rahul accepted your ₹5,000 loan request.",
                categoryTag = "Loan Request Accepted",
                timestamp = "15 min ago",
                bgTint = Color(0xFFECFDF5),
                borderColor = Color(0xFFA7F3D0),
                titleColor = Color(0xFF059669)
            ),
            RichNotificationItem(
                id = "N_LOAN_3",
                category = NotificationCategory.LOAN,
                iconEmoji = "❌",
                title = "Loan request declined",
                message = "Rahul declined your ₹5,000 loan request.",
                categoryTag = "Loan Request Rejected",
                timestamp = "30 min ago",
                bgTint = Color(0xFFFEF2F2),
                borderColor = Color(0xFFFCA5A5),
                titleColor = Color(0xFFDC2626)
            ),
            RichNotificationItem(
                id = "N_LOAN_4",
                category = NotificationCategory.LOAN,
                iconEmoji = "🤝",
                title = "Loan recorded",
                message = "Your ₹10,000 loan with Rahul has been added to PayPulse.",
                categoryTag = "Loan Created",
                timestamp = "2 hrs ago",
                bgTint = Color(0xFFF0F9FF),
                borderColor = Color(0xFFBAE6FD),
                titleColor = Color(0xFF0284C7)
            ),
            RichNotificationItem(
                id = "N_LOAN_5",
                category = NotificationCategory.LOAN,
                iconEmoji = "🎉",
                title = "Repayment plan accepted",
                message = "Rahul agreed to repay ₹5,000 per month for 4 months.",
                categoryTag = "Repayment Plan Accepted",
                timestamp = "Yesterday",
                bgTint = Color(0xFFECFDF5),
                borderColor = Color(0xFFA7F3D0),
                titleColor = Color(0xFF059669)
            ),
            RichNotificationItem(
                id = "N_LOAN_6",
                category = NotificationCategory.LOAN,
                iconEmoji = "🔄",
                title = "Repayment plan updated",
                message = "Rahul proposed a new repayment schedule. Review it now.",
                categoryTag = "Repayment Plan Changed",
                timestamp = "2 days ago",
                bgTint = Color(0xFFFFFBEB),
                borderColor = Color(0xFFFDE68A),
                titleColor = Color(0xFFB45309)
            ),

            // 🧠 Smart PayPulse Notifications
            RichNotificationItem(
                id = "N_SMART_1",
                category = NotificationCategory.SMART,
                iconEmoji = "📊",
                title = "Your PayPulse Weekly",
                message = "₹8,500 received • ₹4,000 paid • 2 payments due next week.",
                categoryTag = "Weekly Summary",
                timestamp = "Weekly",
                bgTint = Color(0xFFEFF6FF),
                borderColor = Color(0xFFBFDBFE),
                titleColor = PrimaryBlue
            ),
            RichNotificationItem(
                id = "N_SMART_2",
                category = NotificationCategory.SMART,
                iconEmoji = "💡",
                title = "Your debt health improved",
                message = "You cleared 2 overdue payments this week.",
                categoryTag = "Debt Health",
                timestamp = "Today",
                bgTint = Color(0xFFECFDF5),
                borderColor = Color(0xFFA7F3D0),
                titleColor = Color(0xFF059669)
            ),
            RichNotificationItem(
                id = "N_SMART_3",
                category = NotificationCategory.SMART,
                iconEmoji = "📅",
                title = "Busy payment week ahead",
                message = "You're expected to receive ₹12,500 and pay ₹7,000 this week.",
                categoryTag = "Upcoming Cash Flow",
                timestamp = "This Week",
                bgTint = Color(0xFFF0F9FF),
                borderColor = Color(0xFFBAE6FD),
                titleColor = Color(0xFF0284C7)
            ),
            RichNotificationItem(
                id = "N_SMART_4",
                category = NotificationCategory.SMART,
                iconEmoji = "⚠️",
                title = "3 payments need attention",
                message = "₹8,500 is currently overdue.",
                categoryTag = "Overdue Alert",
                timestamp = "Urgent",
                bgTint = Color(0xFFFEF2F2),
                borderColor = Color(0xFFFCA5A5),
                titleColor = Color(0xFFDC2626)
            ),
            RichNotificationItem(
                id = "N_SMART_5",
                category = NotificationCategory.SMART,
                iconEmoji = "🎯",
                title = "You're getting closer",
                message = "You've cleared 72% of your outstanding debt.",
                categoryTag = "Debt-Free Progress",
                timestamp = "Milestone",
                bgTint = Color(0xFFF5F3FF),
                borderColor = Color(0xFFDDD6FE),
                titleColor = Color(0xFF7C3AED)
            ),

            // 🤖 AI Notifications
            RichNotificationItem(
                id = "N_AI_1",
                category = NotificationCategory.AI,
                iconEmoji = "🤖",
                title = "PayPulse noticed something",
                message = "You have ₹6,000 due within the next 5 days. Want help planning your repayments?",
                categoryTag = "AI Insight",
                timestamp = "AI Pulse",
                bgTint = Color(0xFFEFF6FF),
                borderColor = Color(0xFFBFDBFE),
                titleColor = PrimaryBlue
            ),
            RichNotificationItem(
                id = "N_AI_2",
                category = NotificationCategory.AI,
                iconEmoji = "✍️",
                title = "Need to remind Rahul?",
                message = "PayPulse can write a friendly reminder for you.",
                categoryTag = "AI Reminder Suggestion",
                timestamp = "Suggestion",
                bgTint = Color(0xFFECFDF5),
                borderColor = Color(0xFFA7F3D0),
                titleColor = Color(0xFF059669)
            ),
            RichNotificationItem(
                id = "N_AI_3",
                category = NotificationCategory.AI,
                iconEmoji = "💡",
                title = "Quick insight",
                message = "Most of your outstanding money is due within the next 2 weeks.",
                categoryTag = "AI Financial Insight",
                timestamp = "Analytics",
                bgTint = Color(0xFFFFFBEB),
                borderColor = Color(0xFFFDE68A),
                titleColor = Color(0xFFB45309)
            ),

            // 🛡️ Trust & Security Notifications
            RichNotificationItem(
                id = "N_SEC_1",
                category = NotificationCategory.SECURITY,
                iconEmoji = "🔐",
                title = "New login detected",
                message = "Your PayPulse account was accessed from a new device.",
                categoryTag = "New Device Login",
                timestamp = "Security",
                bgTint = Color(0xFFFEF2F2),
                borderColor = Color(0xFFFCA5A5),
                titleColor = Color(0xFFDC2626)
            ),
            RichNotificationItem(
                id = "N_SEC_2",
                category = NotificationCategory.SECURITY,
                iconEmoji = "🔒",
                title = "Confirm payment",
                message = "You're about to send ₹10,000 to Rahul. Please verify the details.",
                categoryTag = "Payment Confirmation",
                timestamp = "Verification",
                bgTint = Color(0xFFFFFBEB),
                borderColor = Color(0xFFFDE68A),
                titleColor = Color(0xFFB45309)
            ),
            RichNotificationItem(
                id = "N_SEC_3",
                category = NotificationCategory.SECURITY,
                iconEmoji = "📜",
                title = "Agreement confirmed",
                message = "Both parties have acknowledged the ₹25,000 loan agreement.",
                categoryTag = "Agreement Accepted",
                timestamp = "Legal Shield",
                bgTint = Color(0xFFECFDF5),
                borderColor = Color(0xFFA7F3D0),
                titleColor = Color(0xFF059669)
            )
        )
    }

    val filteredNotifications = remember(allNotifications, selectedGroup) {
        when (selectedGroup) {
            NotificationGroupFilter.PAYMENT -> allNotifications.filter { it.category == NotificationCategory.PAYMENT }
            NotificationGroupFilter.LOAN -> allNotifications.filter { it.category == NotificationCategory.LOAN }
            NotificationGroupFilter.SMART -> allNotifications.filter { it.category == NotificationCategory.SMART }
            NotificationGroupFilter.AI -> allNotifications.filter { it.category == NotificationCategory.AI }
            NotificationGroupFilter.SECURITY -> allNotifications.filter { it.category == NotificationCategory.SECURITY }
            else -> allNotifications
        }
    }

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
                .wrapContentHeight()
                .heightIn(max = 660.dp)
        ) {
            Column(
                modifier = Modifier.padding(18.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // Header Bar
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            color = Color(0xFFEFF6FF),
                            shape = CircleShape,
                            modifier = Modifier.size(40.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Default.Notifications,
                                    contentDescription = null,
                                    tint = PrimaryBlue,
                                    modifier = Modifier.size(22.dp)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text("Notifications", fontSize = 18.sp, fontWeight = FontWeight.ExtraBold, color = NavyDark)
                            Text("${allNotifications.size} active updates & alerts", fontSize = 11.sp, color = TextMuted)
                        }
                    }
                    IconButton(onClick = onDismiss) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = "Close", tint = TextMuted, modifier = Modifier.size(20.dp))
                    }
                }

                // Filter Chips Bar
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    listOf(
                        NotificationGroupFilter.ALL to "All (${allNotifications.size})",
                        NotificationGroupFilter.PAYMENT to "🔔 Payments (${allNotifications.count { it.category == NotificationCategory.PAYMENT }})",
                        NotificationGroupFilter.LOAN to "👥 Loans (${allNotifications.count { it.category == NotificationCategory.LOAN }})",
                        NotificationGroupFilter.SMART to "🧠 Smart (${allNotifications.count { it.category == NotificationCategory.SMART }})",
                        NotificationGroupFilter.AI to "🤖 AI (${allNotifications.count { it.category == NotificationCategory.AI }})",
                        NotificationGroupFilter.SECURITY to "🛡️ Security (${allNotifications.count { it.category == NotificationCategory.SECURITY }})"
                    ).forEach { (group, label) ->
                        FilterChip(
                            selected = selectedGroup == group,
                            onClick = { selectedGroup = group },
                            label = { Text(label, fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                            shape = RoundedCornerShape(16.dp),
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = PrimaryBlue,
                                selectedLabelColor = Color.White,
                                containerColor = Color(0xFFF1F5F9),
                                labelColor = TextMuted
                            )
                        )
                    }
                }

                HorizontalDivider(color = Color(0xFFF1F5F9))

                // Notifications List
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.weight(1f, fill = false)
                ) {
                    items(filteredNotifications, key = { it.id }) { item ->
                        RichNotificationCard(
                            item = item,
                            onPayUpi = {
                                UpiPaymentLauncher.launchUpiPayment(context, item.id, 3500.0, item.recipientName)
                            },
                            onWhatsApp = {
                                val shareIntent = Intent(Intent.ACTION_VIEW).apply {
                                    data = Uri.parse("https://api.whatsapp.com/send?text=" + Uri.encode(
                                        "🔔 PAYPULSE: ${item.title} - ${item.message}"
                                    ))
                                }
                                context.startActivity(shareIntent)
                            }
                        )
                    }
                }

                // System Push Notification Trigger Buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = {
                            val pushItems = filteredNotifications.map { Pair("${it.iconEmoji} ${it.title}", it.message) }
                            NotificationHelper.triggerAppNotifications(context, pushItems)
                            Toast.makeText(context, "🔔 System notifications sent to phone status bar!", Toast.LENGTH_SHORT).show()
                        },
                        modifier = Modifier.weight(1.2f),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0284C7)),
                        shape = RoundedCornerShape(12.dp),
                        contentPadding = PaddingValues(vertical = 10.dp)
                    ) {
                        Icon(Icons.Default.NotificationsActive, null, modifier = Modifier.size(15.dp), tint = Color.White)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Push to Phone", fontWeight = FontWeight.ExtraBold, fontSize = 12.sp)
                    }

                    OutlinedButton(
                        onClick = {
                            NotificationHelper.sendTestNotification(context)
                            Toast.makeText(context, "🔔 Test notification sent to phone!", Toast.LENGTH_SHORT).show()
                        },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp),
                        contentPadding = PaddingValues(vertical = 10.dp)
                    ) {
                        Icon(Icons.Default.Vibration, null, modifier = Modifier.size(15.dp), tint = PrimaryBlue)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Test Push", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = PrimaryBlue)
                    }
                }

                Button(
                    onClick = onDismiss,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue),
                    shape = RoundedCornerShape(12.dp),
                    contentPadding = PaddingValues(vertical = 10.dp)
                ) {
                    Text("Close Notifications", fontWeight = FontWeight.ExtraBold, fontSize = 13.sp)
                }
            }
        }
    }
}

@Composable
private fun RichNotificationCard(
    item: RichNotificationItem,
    onPayUpi: () -> Unit,
    onWhatsApp: () -> Unit
) {
    Surface(
        color = item.bgTint,
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, item.borderColor, RoundedCornerShape(16.dp))
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Row(modifier = Modifier.weight(1f), verticalAlignment = Alignment.Top) {
                    Text(item.iconEmoji, fontSize = 18.sp)
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(item.title, fontSize = 13.sp, fontWeight = FontWeight.ExtraBold, color = item.titleColor)
                        }
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(item.message, fontSize = 11.sp, color = NavyDark, lineHeight = 15.sp)
                    }
                }
                Spacer(modifier = Modifier.width(6.dp))
                Surface(
                    color = Color.White.copy(alpha = 0.85f),
                    shape = RoundedCornerShape(6.dp)
                ) {
                    Text(
                        text = item.timestamp,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = item.titleColor,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }

            HorizontalDivider(color = item.borderColor.copy(alpha = 0.5f))

            // Contextual Action Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                if (item.category == NotificationCategory.PAYMENT) {
                    Button(
                        onClick = onPayUpi,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = item.titleColor),
                        contentPadding = PaddingValues(vertical = 5.dp)
                    ) {
                        Icon(Icons.Default.QrCodeScanner, null, modifier = Modifier.size(13.dp), tint = Color.White)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Pay via UPI", fontSize = 11.sp, fontWeight = FontWeight.ExtraBold, color = Color.White)
                    }
                }

                Button(
                    onClick = onWhatsApp,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF075E54)),
                    contentPadding = PaddingValues(vertical = 5.dp)
                ) {
                    Icon(Icons.Default.Chat, null, modifier = Modifier.size(13.dp), tint = Color.White)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Reminder", fontSize = 11.sp, fontWeight = FontWeight.ExtraBold, color = Color.White)
                }
            }
        }
    }
}
