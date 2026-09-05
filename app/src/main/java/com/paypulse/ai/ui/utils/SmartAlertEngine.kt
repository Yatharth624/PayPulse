package com.paypulse.ai.ui.utils

import com.paypulse.ai.data.model.LoanItem
import com.paypulse.ai.data.model.TransactionItem
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

enum class SmartAlertStage {
    OVERDUE,     // < 0 days (After due date)
    DUE_TODAY,   // == 0 days (Due date)
    REMINDER_3D, // 1 to 3 days before
    UPCOMING_7D, // 4 to 7 days before
    NORMAL       // > 7 days before or already paid
}

data class SmartAlertItem(
    val id: String,
    val title: String,
    val message: String,
    val daysDifference: Int,
    val stage: SmartAlertStage,
    val timestamp: String,
    val customerName: String,
    val amountFormatted: String,
    val loan: LoanItem? = null,
    val transaction: TransactionItem? = null
)

object SmartAlertEngine {

    /**
     * Evaluates a LoanItem and returns a SmartAlertItem if it falls into one of the 4 alert stages:
     * 7 days before → Upcoming
     * 3 days before → Reminder
     * Due date → Due today
     * After due date → Overdue
     */
    fun evaluateLoanAlert(loan: LoanItem, currentDateStr: String = "2026-09-04"): SmartAlertItem? {
        if (loan.status == "REPAID") return null

        val daysDiff = calculateDaysDifference(currentDateStr, loan.repaymentDeadline)
        val stage = when {
            daysDiff < 0 -> SmartAlertStage.OVERDUE
            daysDiff == 0 -> SmartAlertStage.DUE_TODAY
            daysDiff in 1..3 -> SmartAlertStage.REMINDER_3D
            daysDiff in 4..7 -> SmartAlertStage.UPCOMING_7D
            else -> return null
        }

        val name = loan.borrowerName.ifBlank { "Borrower" }
        val amount = loan.amountRemainingFormatted.ifBlank { loan.principalAmountFormatted }

        val title = when (stage) {
            SmartAlertStage.OVERDUE -> "🚨 OVERDUE: $name (${Math.abs(daysDiff)}d Late)"
            SmartAlertStage.DUE_TODAY -> "⚡ DUE TODAY: $name ($amount)"
            SmartAlertStage.REMINDER_3D -> "⏰ 3-Day Reminder: $name"
            SmartAlertStage.UPCOMING_7D -> "🗓️ 7-Day Upcoming: $name"
            SmartAlertStage.NORMAL -> ""
        }

        val message = when (stage) {
            SmartAlertStage.OVERDUE -> "$name's loan payment of $amount was due on ${loan.repaymentDeadline}. Immediate action required."
            SmartAlertStage.DUE_TODAY -> "$name's loan repayment of $amount is due TODAY. Tap to collect via UPI or send reminder."
            SmartAlertStage.REMINDER_3D -> "$name has a repayment of $amount due in $daysDiff days (${loan.repaymentDeadline})."
            SmartAlertStage.UPCOMING_7D -> "Advance Alert: $name has a repayment of $amount scheduled for ${loan.repaymentDeadline} ($daysDiff days away)."
            SmartAlertStage.NORMAL -> ""
        }

        val timestamp = when (stage) {
            SmartAlertStage.OVERDUE -> "${Math.abs(daysDiff)} days overdue"
            SmartAlertStage.DUE_TODAY -> "Today"
            SmartAlertStage.REMINDER_3D -> "Due in $daysDiff days"
            SmartAlertStage.UPCOMING_7D -> "Due in $daysDiff days"
            SmartAlertStage.NORMAL -> ""
        }

        return SmartAlertItem(
            id = "ALERT_LOAN_${loan.id}",
            title = title,
            message = message,
            daysDifference = daysDiff,
            stage = stage,
            timestamp = timestamp,
            customerName = name,
            amountFormatted = amount,
            loan = loan
        )
    }

    /**
     * Evaluates a TransactionItem from the Recovery Queue.
     */
    fun evaluateTransactionAlert(transaction: TransactionItem, currentDateStr: String = "2026-09-04"): SmartAlertItem? {
        if (transaction.statusType == "recovered" || transaction.statusLabel.contains("RECOVERED", ignoreCase = true)) {
            return null
        }

        val deadline = transaction.repaymentDeadline.ifBlank { "2026-09-12" }
        val daysDiff = calculateDaysDifference(currentDateStr, deadline)

        val stage = when {
            daysDiff < 0 || transaction.statusLabel.contains("OVERDUE", ignoreCase = true) -> SmartAlertStage.OVERDUE
            daysDiff == 0 -> SmartAlertStage.DUE_TODAY
            daysDiff in 1..3 -> SmartAlertStage.REMINDER_3D
            daysDiff in 4..7 -> SmartAlertStage.UPCOMING_7D
            else -> return null
        }

        val name = transaction.customerName.ifBlank { "Customer" }
        val amount = transaction.amountFormatted

        val title = when (stage) {
            SmartAlertStage.OVERDUE -> "🚨 OVERDUE QUEUE TXN: $name (${transaction.id})"
            SmartAlertStage.DUE_TODAY -> "⚡ DUE TODAY: $name ($amount)"
            SmartAlertStage.REMINDER_3D -> "⏰ 3-Day Recovery Reminder: $name"
            SmartAlertStage.UPCOMING_7D -> "🗓️ 7-Day Upcoming Recovery: $name"
            SmartAlertStage.NORMAL -> ""
        }

        val message = when (stage) {
            SmartAlertStage.OVERDUE -> "Queue item for $name ($amount) failed via ${transaction.gateway} and is overdue. Tap to trigger AI recovery."
            SmartAlertStage.DUE_TODAY -> "Queue payment for $name ($amount) is due TODAY. AI engine recommends ${transaction.recommendedStrategy}."
            SmartAlertStage.REMINDER_3D -> "Queue transaction for $name ($amount) is due in $daysDiff days ($deadline)."
            SmartAlertStage.UPCOMING_7D -> "Upcoming recovery for $name ($amount) scheduled for $deadline."
            SmartAlertStage.NORMAL -> ""
        }

        val timestamp = when (stage) {
            SmartAlertStage.OVERDUE -> "Overdue"
            SmartAlertStage.DUE_TODAY -> "Today"
            else -> "In $daysDiff days"
        }

        return SmartAlertItem(
            id = "ALERT_TXN_${transaction.id}",
            title = title,
            message = message,
            daysDifference = daysDiff,
            stage = stage,
            timestamp = timestamp,
            customerName = name,
            amountFormatted = amount,
            transaction = transaction
        )
    }

    /**
     * Generates all active Smart Alerts sorted by urgency: Overdue -> Due Today -> 3D Reminder -> 7D Upcoming.
     */
    fun generateAllAlerts(
        loans: List<LoanItem>,
        transactions: List<TransactionItem>,
        currentDateStr: String = "2026-09-04"
    ): List<SmartAlertItem> {
        val loanAlerts = loans.mapNotNull { evaluateLoanAlert(it, currentDateStr) }
        val txnAlerts = transactions.mapNotNull { evaluateTransactionAlert(it, currentDateStr) }

        val combined = loanAlerts + txnAlerts
        return combined.sortedWith(
            compareBy<SmartAlertItem> { stagePriority(it.stage) }
                .thenBy { it.daysDifference }
        )
    }

    private fun stagePriority(stage: SmartAlertStage): Int {
        return when (stage) {
            SmartAlertStage.OVERDUE -> 1
            SmartAlertStage.DUE_TODAY -> 2
            SmartAlertStage.REMINDER_3D -> 3
            SmartAlertStage.UPCOMING_7D -> 4
            SmartAlertStage.NORMAL -> 5
        }
    }

    private fun calculateDaysDifference(currentDateStr: String, targetDateStr: String): Int {
        return try {
            val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.US)
            val currentDate = sdf.parse(currentDateStr) ?: return 0
            val targetDate = sdf.parse(targetDateStr) ?: return 0

            val diffMillis = targetDate.time - currentDate.time
            (diffMillis / (1000 * 60 * 60 * 24)).toInt()
        } catch (_: Exception) {
            0
        }
    }
}
