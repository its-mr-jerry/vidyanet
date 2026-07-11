package com.kastack.vidyanet.commonUi.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kastack.vidyanet.data.DatabaseManager
import com.kastack.vidyanet.data.repositories.AuthRepository
import com.kastack.vidyanet.models.auth.LoginRequest
import com.kastack.vidyanet.models.auth.VerifyOtpRequest
import com.kastack.vidyanet.models.user.UserType
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class LoginViewModel(
    private val authRepository: AuthRepository,
    private val databaseManager: DatabaseManager
) : ViewModel() {

    private val _uiState = MutableStateFlow<LoginUiState>(LoginUiState.InputPhone)
    val uiState = _uiState.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error = _error.asStateFlow()

    private val _phone = MutableStateFlow("")
    val phone = _phone.asStateFlow()

    private val _otp = MutableStateFlow("")
    val otp = _otp.asStateFlow()

    fun onPhoneChanged(newPhone: String) {
        if (newPhone.length <= 10 && newPhone.all { it.isDigit() }) {
            _phone.value = newPhone
        }
    }

    fun onOtpChanged(newOtp: String) {
        if (newOtp.length <= 6 && newOtp.all { it.isDigit() }) {
            _otp.value = newOtp
        }
    }

    fun sendOtp() {
        if (_phone.value.length < 10) {
            _error.value = "Enter a valid 10-digit phone number"
            return
        }

        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            authRepository.sendOtp(LoginRequest(_phone.value, UserType.PLATFORM_OWNER))
                .onSuccess {
                    _uiState.value = LoginUiState.InputOtp
                }
                .onFailure {
                    _error.value = it.message ?: "Failed to send OTP"
                }
            _isLoading.value = false
        }
    }

    private val _loginSuccess = MutableStateFlow(false)
    val loginSuccess = _loginSuccess.asStateFlow()

    fun verifyOtp() {
        if (_otp.value.length != 6) {
            _error.value = "Enter a valid 6-digit OTP"
            return
        }

        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            authRepository.verifyOtp(VerifyOtpRequest(_phone.value, _otp.value))
                .onSuccess { response ->
                    databaseManager.saveString("auth_token", response.token)
                    _loginSuccess.value = true
                }
                .onFailure {
                    _error.value = it.message ?: "Invalid or expired OTP"
                }
            _isLoading.value = false
        }
    }

    fun backToPhone() {
        _uiState.value = LoginUiState.InputPhone
        _otp.value = ""
    }

}

sealed class LoginUiState {
    data object InputPhone : LoginUiState()
    data object InputOtp : LoginUiState()
}
