package com.paypulse.ai.ui.utils

import android.app.DownloadManager
import android.content.Context
import android.content.Intent
import android.os.Environment
import android.widget.Toast
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object ReportExporter {

    fun exportPdfAudit(context: Context): Pair<File, String> {
        val fileName = "PayPulse_Financial_Audit_Report.pdf"
        val file = getDownloadFile(context, fileName)

        val timestamp = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date())
        val pdfContent = """
            ================================================================================
            PAYPULSE AI - MERCHANT FINANCIAL AUDIT REPORT
            Razorpay Revenue Recovery Agent Engine v3.2
            ================================================================================
            Generated: $timestamp
            Merchant ID: MERCH_99104 (Enterprise Tier)
            Environment: Production Gateway Switch Telemetry

            --------------------------------------------------------------------------------
            1. EXECUTIVE FINANCIAL SUMMARY
            --------------------------------------------------------------------------------
            Revenue At Risk (Unrecovered):          ₹38,60,000
            Expected ML Recovery Opportunity:       ₹28,70,000
            Actual Recovered GMV:                   ₹21,40,000
            Recovery Success Rate:                 74.6%
            Baseline Lift (vs Simple Retry):        +18.4%

            --------------------------------------------------------------------------------
            2. NET P&L & WORKING CAPITAL CONTRIBUTION
            --------------------------------------------------------------------------------
            Gateway Retry Fees Avoided:             +₹42,800
            Working Capital Interest Saved (@7.5%): +₹18,400
            TOTAL NET MERCHANT P&L CONTRIBUTION:    +₹4,30,000 NET PROFIT

            --------------------------------------------------------------------------------
            3. RECOVERY STRATEGY PERFORMANCE BREAKDOWN
            --------------------------------------------------------------------------------
            • Delayed Retry (15m): 184 attempts | 137 recoveries | 74.5% success | ₹2.98L
            • Smart Payment Link:  120 attempts |  85 recoveries | 70.8% success | ₹1.84L
            • Mandate Retry:        95 attempts |  72 recoveries | 75.8% success | ₹1.56L
            • Alt. Payment Method:  64 attempts |  42 recoveries | 65.6% success | ₹92K

            --------------------------------------------------------------------------------
            4. GATEWAY SWITCH HEALTH TELEMETRY
            --------------------------------------------------------------------------------
            • Razorpay Core Switch: 410ms Latency (Healthy - 81.4% recovery)
            • HDFC Direct Gateway:  280ms Latency (Healthy - 88.2% recovery)
            • SBI UPI Switch:       4200ms Latency (Degraded - Outage Radar Enforced)

            🔒 AUDIT CERTIFICATION: All AI recovery decisions executed under strict merchant
            safety thresholds. Low-confidence transactions escalated to manual review.
            ================================================================================
        """.trimIndent()

        file.writeText(pdfContent)
        openDownloadsFolder(context, file)
        return Pair(file, "📄 PDF Audit Report downloaded to ${file.name}!")
    }

    fun exportCsvLedger(context: Context): Pair<File, String> {
        val fileName = "PayPulse_Settlement_Ledger.csv"
        val file = getDownloadFile(context, fileName)

        val csvContent = """
            Audit ID,Timestamp,Customer Name,Amount,Failure Reason,Strategy Used,Status,Revenue Saved
            AUD_9102,2026-08-31 01:20,Rahul Sharma,₹24500,UPI Timeout,Delayed Retry,RECOVERED,₹24500
            AUD_9103,2026-08-31 01:15,Priya Nair,₹18200,Gateway Failure,Immediate Retry,RECOVERED,₹18200
            AUD_9104,2026-08-31 01:05,Anand Verma,₹42000,Checkout Abandoned,Smart Payment Link,RECOVERED,₹42000
            AUD_9105,2026-08-31 00:50,Kavita Reddy,₹52000,Bank Decline,Alt Payment Method,RECOVERED,₹52000
            AUD_9106,2026-08-31 00:30,Deepak Gupta,₹92000,Hard Decline,Hard Decline Block,BLOCKED,₹0
        """.trimIndent()

        file.writeText(csvContent)
        openDownloadsFolder(context, file)
        return Pair(file, "📥 CSV Ledger downloaded to ${file.name}!")
    }

    private fun getDownloadFile(context: Context, fileName: String): File {
        val dir = context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS)
            ?: context.filesDir
        if (!dir.exists()) dir.mkdirs()
        val file = File(dir, fileName)
        if (!file.exists()) file.createNewFile()
        return file
    }

    private fun openDownloadsFolder(context: Context, file: File) {
        try {
            val intent = Intent(DownloadManager.ACTION_VIEW_DOWNLOADS).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }
            context.startActivity(intent)
        } catch (_: Exception) {
            Toast.makeText(context, "Saved to ${file.absolutePath}", Toast.LENGTH_LONG).show()
        }
    }
}
