package com.paypulse.ai.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.paypulse.ai.data.model.InsightsResponseDto
import com.paypulse.ai.data.repository.InsightsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * ViewModel responsible for fetching data for the upgraded Insights screen (Payment & Recovery Intelligence).
 * Uses InsightsRepository to query `/api/insights`.
 */
class InsightsViewModel(
    private val repository: InsightsRepository = InsightsRepository(com.paypulse.ai.data.api.PayPulseApiService.create())
) : ViewModel() {

    sealed class UiState {
        object Loading : UiState()
        data class Success(
            val data: InsightsResponseDto = InsightsResponseDto(),
            val selectedTimeRange: String = "24H"
        ) : UiState()
        data class Error(val message: String) : UiState()
    }

    private val _uiState = MutableStateFlow<UiState>(UiState.Success(InsightsResponseDto(), "24H"))
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    init {
        loadInsights("24h")
    }

    /** Load insights data from the backend */
    fun loadInsights(timeRange: String = "24h") {
        val currentRange = timeRange.uppercase()
        val currentData = (_uiState.value as? UiState.Success)?.data ?: InsightsResponseDto()
        _uiState.value = UiState.Loading
        viewModelScope.launch {
            try {
                val response = repository.fetchInsights(timeRange.lowercase())
                if (response.isSuccessful && response.body() != null) {
                    _uiState.value = UiState.Success(response.body()!!, currentRange)
                } else {
                    _uiState.value = UiState.Success(currentData.copy(timeRange = currentRange), currentRange)
                }
            } catch (e: Exception) {
                _uiState.value = UiState.Success(currentData.copy(timeRange = currentRange), currentRange)
            }
        }
    }

    fun selectTimeRange(timeRange: String) {
        loadInsights(timeRange)
    }

    /**
     * Navigate to the Recovery Queue screen applying a pre-selected category filter.
     */
    fun openQueueFilteredBy(category: String) {
        _navigationRequest.value = NavigationRequest.OpenQueueFiltered(category)
    }

    sealed class NavigationRequest {
        data class OpenQueueFiltered(val category: String) : NavigationRequest()
    }

    private val _navigationRequest = MutableStateFlow<NavigationRequest?>(null)
    val navigationRequest: StateFlow<NavigationRequest?> = _navigationRequest.asStateFlow()

    fun clearNavigationRequest() {
        _navigationRequest.value = null
    }
}
