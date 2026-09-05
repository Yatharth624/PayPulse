package com.paypulse.ai.ui.utils

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast

object UpiPaymentLauncher {

    fun launchUpiPayment(context: Context, transactionId: String, amount: Double, borrowerName: String = "Borrower") {
        val merchantVpa = "razorpay.paypulse@icici"
        val merchantName = "PayPulse AI Loan Recovery"
        val formattedAmount = String.format(java.util.Locale.US, "%.2f", amount)
        val note = "Loan Recovery Collection Request for $transactionId ($borrowerName)"

        // NPCI standard UPI Uri for requesting / collecting money into merchant VPA
        val upiUriString = "upi://pay?pa=$merchantVpa&pn=${Uri.encode(merchantName)}&am=$formattedAmount&cu=INR&tn=${Uri.encode(note)}"
        val uri = Uri.parse(upiUriString)

        val upiIntent = Intent(Intent.ACTION_VIEW, uri)
        val chooser = Intent.createChooser(upiIntent, "Request Loan Payment from $borrowerName via (GPay, PhonePe, Paytm)")
        chooser.flags = Intent.FLAG_ACTIVITY_NEW_TASK

        try {
            context.startActivity(chooser)
        } catch (_: Exception) {
            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                type = "text/plain"
                putExtra(Intent.EXTRA_TEXT, "Hi $borrowerName, please complete your loan recovery payment of ₹$formattedAmount for $transactionId using this Razorpay link: $upiUriString")
            }
            val shareChooser = Intent.createChooser(shareIntent, "Send Payment Request Link to $borrowerName via WhatsApp/SMS")
            shareChooser.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            try {
                context.startActivity(shareChooser)
            } catch (e: Exception) {
                Toast.makeText(
                    context,
                    "📲 Collection Request Link generated for ₹$formattedAmount from $borrowerName ($transactionId)",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }
}
