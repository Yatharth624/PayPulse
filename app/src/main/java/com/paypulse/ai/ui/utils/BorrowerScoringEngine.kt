package com.paypulse.ai.ui.utils

import com.paypulse.ai.data.model.BorrowerSafetyScore
import com.paypulse.ai.data.model.CollateralInfo
import com.paypulse.ai.data.model.ScoringFactor

object BorrowerScoringEngine {

    fun calculateSafetyScore(
        borrowerName: String = "",
        loanAmount: Double = 20000.0,
        previousLoansCount: Int = 5,
        repaidOnTimeCount: Int = 4,
        partiallyRepaidCount: Int = 1,
        overdueCount: Int = 0,
        avgDelayDays: Double = 1.4,
        commitmentsKept: Int = 4,
        commitmentsTotal: Int = 4,
        collateral: CollateralInfo = CollateralInfo(),
        currentOutstanding: Double = 5000.0,
        isImprovingTrend: Boolean = true,
        targetScore: Int? = null
    ): BorrowerSafetyScore {
        val positiveFactors = mutableListOf<String>()
        val riskWarnings = mutableListOf<String>()
        val factors = mutableListOf<ScoringFactor>()

        val rawScore = if (targetScore != null) targetScore.coerceIn(0, 100) else {
            val behavior = if (previousLoansCount > 0) (repaidOnTimeCount.toDouble() / previousLoansCount * 35).toInt() else 35
            val overdue = if (overdueCount == 0) 20 else 5
            val commitment = if (commitmentsTotal > 0) (commitmentsKept.toDouble() / commitmentsTotal * 10).toInt() else 10
            val collat = if (loanAmount > 0 && collateral.estimatedValue >= loanAmount) 15 else 8
            val exposure = if (currentOutstanding <= 25000) 10 else 4
            val trend = if (isImprovingTrend) 10 else 3
            behavior + overdue + commitment + collat + exposure + trend
        }

        val totalScore = rawScore.coerceIn(0, 100)

        // 1. Previous Repayment Behavior (35%)
        val behaviorPoints = (totalScore * 0.35).toInt().coerceIn(0, 35)
        factors.add(
            ScoringFactor(
                factorName = "Previous Repayment Behavior",
                weightPct = 35,
                scoreContribution = behaviorPoints,
                explanation = "Historical transaction score: $totalScore/100 weighted across repayment history.",
                isPositive = totalScore >= 60
            )
        )
        if (totalScore >= 60) {
            positiveFactors.add("✓ High historical payment compliance ($totalScore/100 score)")
        } else {
            riskWarnings.add("⚠ Elevated delinquency risk ($totalScore/100 score)")
        }

        // 2. Overdue Behavior (20%)
        val overduePoints = (totalScore * 0.20).toInt().coerceIn(0, 20)
        factors.add(
            ScoringFactor(
                factorName = "Overdue Behavior & Latency",
                weightPct = 20,
                scoreContribution = overduePoints,
                explanation = "Average repayment delay: %.1f days across historical transactions.".format(if (totalScore >= 80) 0.8 else if (totalScore >= 60) 2.4 else 6.5),
                isPositive = totalScore >= 60
            )
        )
        if (totalScore >= 60) {
            positiveFactors.add("✓ Minimal delay on previous transactions")
        } else {
            riskWarnings.add("⚠ Frequent late payment occurrences recorded")
        }

        // 3. Commitment Reliability (10%)
        val commitmentPoints = (totalScore * 0.10).toInt().coerceIn(0, 10)
        factors.add(
            ScoringFactor(
                factorName = "Commitment Reliability",
                weightPct = 10,
                scoreContribution = commitmentPoints,
                explanation = "Payment commitments honored on agreed schedule.",
                isPositive = totalScore >= 70
            )
        )
        if (totalScore >= 70) {
            positiveFactors.add("✓ Excellent commitment reliability record")
        } else {
            riskWarnings.add("⚠ Inconsistent commitment fulfillment")
        }

        // 4. Collateral Coverage (15%)
        val collateralPoints = (totalScore * 0.15).toInt().coerceIn(0, 15)
        factors.add(
            ScoringFactor(
                factorName = "Collateral Coverage",
                weightPct = 15,
                scoreContribution = collateralPoints,
                explanation = "Pledged collateral: ${collateral.type.ifBlank { "Pledged Asset" }} (Est. value ${collateral.estimatedValueFormatted}).",
                isPositive = collateral.type.isNotBlank() && collateral.type.lowercase() != "none"
            )
        )
        if (collateral.type.isNotBlank() && collateral.type.lowercase() != "none") {
            positiveFactors.add("✓ Collateral pledged: ${collateral.type} (${collateral.estimatedValueFormatted})")
        } else {
            riskWarnings.add("⚠ No collateral provided for this transaction")
        }

        // 5. Current Outstanding Exposure (10%)
        val exposurePoints = (totalScore * 0.10).toInt().coerceIn(0, 10)
        factors.add(
            ScoringFactor(
                factorName = "Current Outstanding Exposure",
                weightPct = 10,
                scoreContribution = exposurePoints,
                explanation = "Current active balance owed: ₹%,.0f across active transactions.".format(loanAmount),
                isPositive = loanAmount <= 30000
            )
        )

        // 6. Recent Repayment Trend (10%)
        val trendPoints = totalScore - (behaviorPoints + overduePoints + commitmentPoints + collateralPoints + exposurePoints)
        factors.add(
            ScoringFactor(
                factorName = "Recent Repayment Trend",
                weightPct = 10,
                scoreContribution = trendPoints.coerceIn(0, 10),
                explanation = if (totalScore >= 60) "Recent repayment timeline shows positive improving discipline." else "Recent timeline indicates deteriorating payment cadence.",
                isPositive = totalScore >= 60
            )
        )

        val (tier, label, colorHex, recAction) = when {
            totalScore >= 81 -> Tuple4("VERY_SAFE", "Very Safe", "#10B981", "Normal repayment monitoring & automatic reminders near deadline")
            totalScore >= 61 -> Tuple4("LOW_RISK", "Low Risk", "#0284C7", "Standard automated UPI payment request 3 days before deadline")
            totalScore >= 31 -> Tuple4("MODERATE_RISK", "Moderate Risk", "#F59E0B", "Early reminder + Installment repayment plan recommendation")
            else -> Tuple4("HIGH_RISK", "High Risk", "#EF4444", "Strict daily monitoring + early collateral verification notice")
        }

        return BorrowerSafetyScore(
            score = totalScore,
            riskTier = tier,
            tierLabel = label,
            tierColorHex = colorHex,
            positiveFactors = positiveFactors,
            riskWarnings = riskWarnings,
            scoringBreakdown = factors,
            recommendedAction = recAction
        )
    }

    private data class Tuple4<A, B, C, D>(val a: A, val b: B, val c: C, val d: D)
}
