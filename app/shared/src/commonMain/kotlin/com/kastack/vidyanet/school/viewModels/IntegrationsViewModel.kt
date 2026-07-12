@file:Suppress("DEPRECATION")

package com.kastack.vidyanet.school.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kastack.vidyanet.models.settings.*
import com.kastack.vidyanet.utils.toKotlin
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlin.time.Duration.Companion.days
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.minutes

data class IntegrationsUiState(
    val integrations: List<IntegrationDto> = emptyList(),
    val apiKeys: List<ApiKeyDto> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val isSaving: Boolean = false,
    val actionSuccess: Boolean = false
)

class IntegrationsViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(IntegrationsUiState())
    val uiState: StateFlow<IntegrationsUiState> = _uiState.asStateFlow()

    init {
        loadData()
    }

    fun loadData() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            delay(500.milliseconds)
            
            val mockIntegrations = listOf(
                IntegrationDto("whatsapp", "WhatsApp", "Automated notifications and direct student communication via WhatsApp Business API.", IntegrationCategory.COMMUNICATION, true),
                IntegrationDto("twilio", "Twilio", "Global SMS gateway for emergency alerts, attendance updates, and OTP verification.", IntegrationCategory.COMMUNICATION, false),
                IntegrationDto("sendgrid", "SendGrid", "Transactional email engine for monthly newsletters and system-wide announcements.", IntegrationCategory.COMMUNICATION, true),
                IntegrationDto("stripe", "Stripe", "Enable global fee payments with credit cards, Apple Pay, and Google Pay.", IntegrationCategory.PAYMENTS, true),
                IntegrationDto("razorpay", "Razorpay", "Accept domestic Indian payments via UPI, NetBanking, and localized wallets.", IntegrationCategory.PAYMENTS, false)
            )
            
            val now = Clock.System.now().toKotlin()
            val mockApiKeys = listOf(
                ApiKeyDto("1", "Mobile App Prod", now.minus(12.days), now.minus(2.minutes), "Read/Write"),
                ApiKeyDto("2", "Custom LMS Sync", now.minus(60.days), now.minus(1.days), "Read Only")
            )
            
            _uiState.value = _uiState.value.copy(
                integrations = mockIntegrations,
                apiKeys = mockApiKeys,
                isLoading = false
            )
        }
    }

    fun connectIntegration(id: String) {
        // Implementation
    }

    fun configureIntegration(id: String) {
        // Implementation
    }

    fun generateNewKey() {
        // Implementation
    }

    fun revokeKey(id: String) {
        // Implementation
    }
}
