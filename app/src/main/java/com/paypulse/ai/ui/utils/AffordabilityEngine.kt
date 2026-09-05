package com.paypulse.ai.ui.utils

import com.paypulse.ai.ui.theme.EmeraldGreen
import com.paypulse.ai.ui.theme.AmberGold
import androidx.compose.ui.graphics.Color

data class AffordabilityInput(
    val monthlyIncome: Double = 40000.0,
    val essentialExpenses: Double = 17000.0,
    val existingObligations: Double = 12000.0,
    val loanAmount: Double = 30000.0,
    val durationMonths: Int = 6
)

data class AffordabilityResult(
    val score: Int,
    val tier: String, // COMFORTABLE, MODERATE, HIGH_PRESSURE, INSUFFICIENT_DATA
    val tierLabel: String,
    val tierBadge: String,
    val tierColorHex: String,
    val monthlyIncome: Double,
    val essentialExpenses: Double,
    val existingObligations: Double,
    val estimatedMonthlyRepayment: Double,
    val disposableIncomeBeforeLoan: Double,
    val disposableIncomeAfterLoan: Double,
    val debtToIncomeRatioPct: Double,
    val summaryRecommendation: String,
    val recommendationTitle: String,
    val recommendationIcon: String,
    val whyScoreFactors: List<String>,
    val improvementTips: List<String>,
    val isHasSufficientData: Boolean = true
)

object AffordabilityEngine {

    fun calculateAffordability(input: AffordabilityInput): AffordabilityResult {
        if (input.monthlyIncome <= 0) {
            return AffordabilityResult(
                score = 0,
                tier = "INSUFFICIENT_DATA",
                tierLabel = "More Information Required",
                tierBadge = "Add Income Details ⚠️",
                tierColorHex = "#64748B",
                monthlyIncome = 0.0,
                essentialExpenses = input.essentialExpenses,
                existingObligations = input.existingObligations,
                estimatedMonthlyRepayment = if (input.durationMonths > 0) input.loanAmount / input.durationMonths else input.loanAmount,
                disposableIncomeBeforeLoan = 0.0,
                disposableIncomeAfterLoan = 0.0,
                debtToIncomeRatioPct = 0.0,
                summaryRecommendation = "Add your monthly income and existing obligations to calculate your affordability.",
                recommendationTitle = "More information required",
                recommendationIcon = "⚠️",
                whyScoreFactors = listOf("⚠️ Monthly income not specified"),
                improvementTips = listOf("Provide valid monthly income details to generate a score."),
                isHasSufficientData = false
            )
        }

        val estimatedMonthlyRepayment = if (input.durationMonths > 0) input.loanAmount / input.durationMonths else input.loanAmount
        val disposableIncomeBefore = (input.monthlyIncome - input.essentialExpenses - input.existingObligations).coerceAtLeast(0.0)
        val disposableIncomeAfter = disposableIncomeBefore - estimatedMonthlyRepayment
        val totalMonthlyDebt = input.existingObligations + estimatedMonthlyRepayment
        val dtiPct = (totalMonthlyDebt / input.monthlyIncome) * 100.0

        // Score calculation algorithm
        var scoreVal = 100

        // DTI Penalty
        when {
            dtiPct < 30.0 -> scoreVal -= 0
            dtiPct <= 45.0 -> scoreVal -= 12
            dtiPct <= 60.0 -> scoreVal -= 25
            dtiPct <= 75.0 -> scoreVal -= 40
            else -> scoreVal -= 60
        }

        // Disposable Buffer Penalty
        when {
            disposableIncomeAfter >= 12000.0 -> scoreVal += 0
            disposableIncomeAfter >= 6000.0 -> scoreVal -= 10
            disposableIncomeAfter >= 2000.0 -> scoreVal -= 25
            disposableIncomeAfter >= 0.0 -> scoreVal -= 40
            else -> scoreVal -= 65
        }

        // Duration Adjustment
        if (input.durationMonths >= 12) {
            scoreVal += 5
        } else if (input.durationMonths <= 2 && input.loanAmount > 20000.0) {
            scoreVal -= 10
        }

        val finalScore = scoreVal.coerceIn(0, 100)

        // Tier Assignment
        val (tier, tierLabel, tierBadge, colorHex, title, icon, summary) = when {
            finalScore >= 80 -> Tuple7(
                "COMFORTABLE",
                "80–100 🟢 Comfortable",
                "Comfortable 🟢",
                "#10B981",
                "Appears affordable",
                "🟢",
                "Based on the provided information, this loan appears manageable within your estimated monthly cash flow."
            )
            finalScore >= 60 -> Tuple7(
                "MODERATE",
                "60–79 🟡 Moderate",
                "Proceed with caution 🟡",
                "#F59E0B",
                "Proceed with caution",
                "🟡",
                "This loan may put additional pressure on your monthly cash flow. Consider reducing the amount or extending the duration."
            )
            else -> Tuple7(
                "HIGH_PRESSURE",
                "0–59 🔴 High pressure",
                "High financial pressure 🔴",
                "#EF4444",
                "High financial pressure",
                "🔴",
                "This repayment may leave limited disposable income after your estimated expenses and existing obligations."
            )
        }

        // Why Score Factors
        val whyFactors = mutableListOf<String>()
        if (input.monthlyIncome >= estimatedMonthlyRepayment * 3) {
            whyFactors.add("✓ Monthly income strongly supports the proposed repayment")
        } else if (disposableIncomeBefore > estimatedMonthlyRepayment) {
            whyFactors.add("✓ Disposable income buffer covers monthly EMI")
        }

        if (dtiPct <= 45.0) {
            whyFactors.add("✓ Total Debt-to-Income ratio (%.1f%%) is within healthy limits".format(dtiPct))
        } else {
            whyFactors.add("⚠️ Total Debt-to-Income ratio (%.1f%%) creates cash flow pressure".format(dtiPct))
        }

        if (disposableIncomeAfter < 5000.0 && disposableIncomeAfter >= 0) {
            whyFactors.add("⚠️ New repayment reduces post-loan disposable income to ₹%,.0f/mo".format(disposableIncomeAfter))
        } else if (disposableIncomeAfter < 0) {
            whyFactors.add("🔴 Deficit Alert: Repayment exceeds available monthly disposable income by ₹%,.0f".format(-disposableIncomeAfter))
        } else {
            whyFactors.add("✓ Healthy post-loan disposable income of ₹%,.0f/mo remaining".format(disposableIncomeAfter))
        }

        // Improvement Tips
        val tips = mutableListOf<String>()
        if (finalScore < 85) {
            tips.add("Request a smaller loan amount (e.g. ₹%,.0f)".format(input.loanAmount * 0.75))
            tips.add("Increase repayment duration to ${input.durationMonths + 6} months to lower monthly EMI")
            if (input.existingObligations > 5000.0) {
                tips.add("Reduce or consolidate existing monthly EMI obligations")
            }
        } else {
            tips.add("Maintain consistent repayment activity to boost your PayPulse Repayment Health score.")
        }

        return AffordabilityResult(
            score = finalScore,
            tier = tier,
            tierLabel = tierLabel,
            tierBadge = tierBadge,
            tierColorHex = colorHex,
            monthlyIncome = input.monthlyIncome,
            essentialExpenses = input.essentialExpenses,
            existingObligations = input.existingObligations,
            estimatedMonthlyRepayment = estimatedMonthlyRepayment,
            disposableIncomeBeforeLoan = disposableIncomeBefore,
            disposableIncomeAfterLoan = disposableIncomeAfter,
            debtToIncomeRatioPct = dtiPct,
            summaryRecommendation = summary,
            recommendationTitle = title,
            recommendationIcon = icon,
            whyScoreFactors = whyFactors,
            improvementTips = tips,
            isHasSufficientData = true
        )
    }

    private data class Tuple7<T1, T2, T3, T4, T5, T6, T7>(
        val v1: T1, val v2: T2, val v3: T3, val v4: T4, val v5: T5, val v6: T6, val v7: T7
    )
}
