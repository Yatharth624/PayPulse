package com.paypulse.ai.data.repository

import com.paypulse.ai.data.api.PayPulseApiService
import com.paypulse.ai.data.model.InsightsResponseDto
import retrofit2.Response

/**
 * Repository for fetching Insights data from the backend.
 * Uses `/api/insights` with optional time_range filtering.
 */
class InsightsRepository(private val apiService: PayPulseApiService) {
    suspend fun fetchInsights(timeRange: String = "24h"): Response<InsightsResponseDto> {
        return apiService.getInsights(timeRange)
    }
}
