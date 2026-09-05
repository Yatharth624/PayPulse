package com.paypulse.ai.data.api

import com.paypulse.ai.BuildConfig
import com.paypulse.ai.data.model.*
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.Path
import retrofit2.http.POST
import retrofit2.http.Query
import java.util.concurrent.TimeUnit

interface PayPulseApiService {

    // ─── Dashboard ───────────────────────────────────────────────────────────
    @GET("dashboard/summary")
    suspend fun getDashboardSummary(): Response<DashboardSummaryResponse>

    // ─── Recovery Queue ───────────────────────────────────────────────────────
    @GET("recovery-queue")
    suspend fun getRecoveryQueue(@Query("limit") limit: Int = 50): Response<RecoveryQueueResponse>

    // ─── Transaction Detail & Decision ───────────────────────────────────────
    @GET("transactions/{transaction_id}")
    suspend fun getTransaction(@Path("transaction_id") transactionId: String): Response<TransactionDto>

    @GET("transactions/{transaction_id}/decision")
    suspend fun getTransactionDecision(@Path("transaction_id") transactionId: String): Response<DecisionResponse>

    // ─── Recovery Execution ───────────────────────────────────────────────────
    @POST("recovery/{transaction_id}/execute")
    suspend fun executeRecovery(
        @Path("transaction_id") transactionId: String,
        @Body request: ExecuteRecoveryRequest
    ): Response<ExecuteRecoveryResponse>

    // ─── Automation Policies ──────────────────────────────────────────────────
    @GET("automation/policies")
    suspend fun getAutomationPolicies(): Response<AutomationPolicyDto>

    @PATCH("automation/policies")
    suspend fun updateAutomationPolicies(@Body request: PolicyUpdateRequest): Response<AutomationPolicyDto>

    @POST("policies/preview")
    suspend fun previewPolicyImpact(@Body request: PolicyPreviewRequest): Response<PolicyPreviewResponse>

    @GET("recovery/workflows")
    suspend fun getDetailedWorkflows(): Response<Map<String, List<WorkflowDetailDto>>>

    @GET("automation/status")
    suspend fun getAutomationStatus(@Query("enabled") enabled: Boolean = true): Response<AutomationStatusDto>

    @GET("transactions/{transaction_id}/intelligence")
    suspend fun getTransactionIntelligence(@Path("transaction_id") transactionId: String): Response<TransactionIntelligenceDto>

    @POST("transactions/{transaction_id}/simulate")
    suspend fun simulateRecoveryStrategies(@Path("transaction_id") transactionId: String): Response<SimulationResponseDto>

    @POST("recovery/{transaction_id}/confirm-execute")
    suspend fun confirmAndExecuteRecoveryAction(
        @Path("transaction_id") transactionId: String,
        @Query("action_override") actionOverride: String? = null
    ): Response<ExecutionOutcomeDto>

    @POST("demo/trigger-failure")
    suspend fun triggerDemoFailure(): Response<DemoTriggerResponseDto>

    @GET("telemetry/outage-radar")
    suspend fun getOutageRadar(): Response<List<OutageRadarItemDto>>

    @POST("recovery/batch-execute")
    suspend fun batchExecuteRecovery(@Body request: BatchRecoveryRequestDto): Response<BatchRecoveryResponseDto>

    // ─── Audit Log ────────────────────────────────────────────────────────────
    @GET("audit-log")
    suspend fun getAuditLog(@Query("limit") limit: Int = 100): Response<AuditLogResponse>

    // ─── Legacy / Synthetic endpoints ────────────────────────────────────────
    @GET("overview")
    suspend fun getOverview(): Response<OverviewResponse>

    // Upgraded endpoint for Insights screen
    @GET("insights")
    suspend fun getInsights(@Query("time_range") timeRange: String = "24h"): Response<InsightsResponseDto>

    @GET("queue")
    suspend fun getQueue(@Query("category") category: String? = null): Response<Map<String, Any>>

    @POST("recovery/execute")
    suspend fun executeLegacyRecovery(@Body request: RecoveryRequest): Response<Map<String, Any>>

    companion object {
        fun create(): PayPulseApiService {
            val logging = HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BASIC
            }
            val client = OkHttpClient.Builder()
                .addInterceptor(logging)
                .connectTimeout(3, TimeUnit.SECONDS)
                .readTimeout(3, TimeUnit.SECONDS)
                .writeTimeout(3, TimeUnit.SECONDS)
                .build()

            return Retrofit.Builder()
                .baseUrl(BuildConfig.PAYPULSE_API_BASE_URL)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(PayPulseApiService::class.java)
        }
    }
}
