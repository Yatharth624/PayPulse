package com.paypulse.ai.data.model

data class CollateralInfo(
    val type: String = "None",
    val estimatedValue: Double = 0.0,
    val estimatedValueFormatted: String = "₹0",
    val description: String = "",
    val documentUri: String? = null
)

data class ScoringFactor(
    val factorName: String,
    val weightPct: Int,
    val scoreContribution: Int,
    val explanation: String,
    val isPositive: Boolean
)

data class BorrowerSafetyScore(
    val score: Int = 87, // 0 to 100
    val riskTier: String = "VERY_SAFE", // VERY_SAFE (81-100), LOW_RISK (61-80), MODERATE_RISK (31-60), HIGH_RISK (0-30)
    val tierLabel: String = "Very Safe",
    val tierColorHex: String = "#10B981", // Emerald Green
    val positiveFactors: List<String> = emptyList(),
    val riskWarnings: List<String> = emptyList(),
    val scoringBreakdown: List<ScoringFactor> = emptyList(),
    val recommendedAction: String = "Normal repayment monitoring"
)

data class RepaymentRecord(
    val id: String = "",
    val loanId: String = "",
    val amount: Double = 0.0,
    val amountFormatted: String = "",
    val date: String = "",
    val paymentMethod: String = "UPI",
    val upiRef: String = "",
    val status: String = "VERIFIED"
)

data class LoanItem(
    val id: String = "LN_101",
    val borrowerId: String = "BW_001",
    val borrowerName: String = "Dinesh",
    val borrowerUpiId: String = "dinesh@upi",
    val lenderName: String = "PayPulse Merchant (Lender)",
    val lenderUpiId: String = "merchant@paypulse",
    val principalAmount: Double = 20000.0,
    val principalAmountFormatted: String = "₹20,000",
    val amountRepaid: Double = 0.0,
    val amountRepaidFormatted: String = "₹0",
    val amountRemaining: Double = 20000.0,
    val amountRemainingFormatted: String = "₹20,000",
    val loanGivenDate: String = "2026-09-02",
    val repaymentDeadline: String = "2026-10-02",
    val deadlineDaysRemaining: Int = 30,
    val deadlineLabel: String = "30 days remaining",
    val status: String = "ACTIVE", // ACTIVE, DUE_SOON, DUE_TODAY, OVERDUE, PARTIALLY_REPAID, REPAID, CLOSED
    val collateral: CollateralInfo = CollateralInfo("Gold", 25000.0, "₹25,000", "Gold Chain collateral"),
    val notes: String = "",
    val repaymentTerms: String = "Single lump sum payment on or before repayment due date via UPI. 0% interest personal loan.",
    val isDigitallyAcknowledged: Boolean = true,
    val acknowledgementHash: String = "PP-ACK-9842-2026",
    val acknowledgementTimestamp: String = "2026-09-04 10:30 AM",
    val safetyScore: BorrowerSafetyScore = BorrowerSafetyScore(),
    val repaymentsHistory: List<RepaymentRecord> = emptyList()
)

data class BorrowerProfile(
    val borrowerId: String = "BW_001",
    val name: String = "Dinesh",
    val upiId: String = "dinesh@upi",
    val safetyScore: BorrowerSafetyScore = BorrowerSafetyScore(),
    val totalBorrowed: Double = 50000.0,
    val totalBorrowedFormatted: String = "₹50,000",
    val totalRepaid: Double = 45000.0,
    val totalRepaidFormatted: String = "₹45,000",
    val outstandingAmount: Double = 5000.0,
    val outstandingAmountFormatted: String = "₹5,000",
    val totalLoansCount: Int = 5,
    val onTimeRepaymentPct: Int = 80,
    val avgDelayDays: Double = 1.4,
    val collateralEstimatedValueFormatted: String = "₹30,000",
    val recentLoans: List<LoanItem> = emptyList()
)
