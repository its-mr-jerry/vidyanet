@file:Suppress("DEPRECATION")

package com.kastack.vidyanet.school.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kastack.vidyanet.data.repositories.UserRepository
import com.kastack.vidyanet.models.user.UserStatus
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlin.time.Duration.Companion.milliseconds

data class UserUiModel(
    val id: Long,
    val name: String,
    val email: String,
    val role: String,
    val lastLogin: String,
    val status: UserStatus,
    val avatarUrl: String? = null
)

data class UserManagementUiState(
    val users: List<UserUiModel> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val totalUsers: Int = 0,
    val currentPage: Int = 1,
    val rowsPerPage: Int = 10,
    val pendingInvites: List<PendingInvite> = emptyList()
)

data class PendingInvite(
    val email: String,
    val role: String,
    val sentAt: String
)

class UserManagementViewModel(
    private val userRepository: UserRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(UserManagementUiState())
    val uiState: StateFlow<UserManagementUiState> = _uiState.asStateFlow()

    init {
        loadUsers()
    }

    fun loadUsers() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            delay(500.milliseconds) // Simulate network
            
            val mockUsers = listOf(
                UserUiModel(1, "Sarah Jenkins", "s.jenkins@educore.edu", "TEACHER", "2 hours ago", UserStatus.ACTIVE),
                UserUiModel(2, "Marcus Wright", "m.wright@educore.edu", "ADMIN", "10 mins ago", UserStatus.ACTIVE),
                UserUiModel(3, "David Chen", "d.chen@educore.edu", "STAFF", "Yesterday, 4:30 PM", UserStatus.INACTIVE)
            )

            val mockInvites = listOf(
                PendingInvite("lisa.monroe@edu.com", "Teacher", "Sent 2 days ago"),
                PendingInvite("james.p@staff.edu.com", "Staff", "Sent 5 hours ago")
            )

            _uiState.value = _uiState.value.copy(
                users = mockUsers,
                totalUsers = 124,
                pendingInvites = mockInvites,
                isLoading = false
            )
        }
    }

    fun onPageChanged(page: Int) {
        _uiState.value = _uiState.value.copy(currentPage = page)
        loadUsers()
    }

    fun onRowsPerPageChanged(rows: Int) {
        _uiState.value = _uiState.value.copy(rowsPerPage = rows, currentPage = 1)
        loadUsers()
    }

    fun deactivateUser(userId: Long) {
        // Implementation for deactivating user
    }

    fun activateUser(userId: Long) {
        // Implementation for activating user
    }

    fun resendInvite(email: String) {
        // Implementation for resending invite
    }
}
