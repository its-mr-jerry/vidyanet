package com.kastack.vidyanet.commonUi.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kastack.vidyanet.data.DatabaseManager
import com.kastack.vidyanet.data.repositories.UserRepository
import com.kastack.vidyanet.models.user.UserType
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class SplashViewModel(
    private val databaseManager: DatabaseManager,
    private val userRepository: UserRepository
) : ViewModel() {

    private val _isLoggedIn = MutableStateFlow<Boolean?>(null)
    val isLoggedIn = _isLoggedIn.asStateFlow()

    private val _isSuperAdmin = MutableStateFlow<Boolean>(false)
    val isSuperAdmin = _isSuperAdmin.asStateFlow()

    init {
        checkAuth()
    }

    private fun checkAuth() {
        viewModelScope.launch {
            val token = databaseManager.getString("auth_token", "")
            if (token.isEmpty()) {
                delay(2000) // Ensure splash is visible
                _isLoggedIn.value = false
                return@launch
            }

            // Token exists, verify with server and update local data
            userRepository.getMe().onSuccess { user ->
                _isSuperAdmin.value = user.userType == UserType.PLATFORM_OWNER
                _isLoggedIn.value = true
            }.onFailure {
                // Token might be invalid or expired
                databaseManager.remove("auth_token")
                _isLoggedIn.value = false
            }
        }
    }
}
