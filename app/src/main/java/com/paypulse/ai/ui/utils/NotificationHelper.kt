package com.paypulse.ai.ui.utils

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.paypulse.ai.MainActivity
import com.paypulse.ai.R

object NotificationHelper {

    private const val CHANNEL_ID = "paypulse_smart_alerts"
    private const val CHANNEL_NAME = "PayPulse Smart Due Soon Alerts"
    private const val CHANNEL_DESC = "Real-time notifications for 7-day upcoming, 3-day reminder, due today, and overdue loan repayments"

    fun createNotificationChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel(CHANNEL_ID, CHANNEL_NAME, importance).apply {
                description = CHANNEL_DESC
                enableVibration(true)
            }
            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    fun sendSystemNotification(context: Context, title: String, message: String, notificationId: Int = (System.currentTimeMillis() % 10000).toInt()) {
        createNotificationChannel(context)

        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent = PendingIntent.getActivity(
            context,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val builder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle(title)
            .setContentText(message)
            .setStyle(NotificationCompat.BigTextStyle().bigText(message))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setDefaults(NotificationCompat.DEFAULT_ALL)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)

        try {
            val notificationManager = NotificationManagerCompat.from(context)
            notificationManager.notify(notificationId, builder.build())
        } catch (_: SecurityException) {
            // Permission not granted by user yet
        } catch (_: Exception) {
            // Graceful fallback
        }
    }

    fun triggerSmartAlertsNotifications(context: Context, alerts: List<SmartAlertItem>) {
        if (alerts.isEmpty()) {
            sendSystemNotification(
                context = context,
                title = "PayPulse Smart Alerts Active 🛡️",
                message = "All scheduled repayments are up to date! No overdue or due today actions required.",
                notificationId = 1000
            )
        } else {
            alerts.take(4).forEachIndexed { index, alert ->
                sendSystemNotification(
                    context = context,
                    title = "${getStageEmoji(alert.stage)} ${alert.title}",
                    message = "${alert.message} • Click to launch PayPulse 1-Tap UPI clearance.",
                    notificationId = 1000 + index
                )
            }
        }
    }

    fun triggerAppNotifications(context: Context, items: List<Pair<String, String>>) {
        items.take(5).forEachIndexed { index, (title, message) ->
            sendSystemNotification(
                context = context,
                title = title,
                message = message,
                notificationId = 2000 + index
            )
        }
    }

    fun sendTestNotification(context: Context) {
        sendSystemNotification(
            context = context,
            title = "🔔 PayPulse Push Notification Active",
            message = "Smart Payment Calendar & Due Soon push alerts are enabled on your device!",
            notificationId = 9999
        )
    }

    private fun getStageEmoji(stage: SmartAlertStage): String {
        return when (stage) {
            SmartAlertStage.OVERDUE -> "🚨 OVERDUE"
            SmartAlertStage.DUE_TODAY -> "⚡ DUE TODAY"
            SmartAlertStage.REMINDER_3D -> "⏰ REMINDER (3D)"
            SmartAlertStage.UPCOMING_7D -> "🗓️ UPCOMING (7D)"
            SmartAlertStage.NORMAL -> "ℹ️ ALERT"
        }
    }
}
