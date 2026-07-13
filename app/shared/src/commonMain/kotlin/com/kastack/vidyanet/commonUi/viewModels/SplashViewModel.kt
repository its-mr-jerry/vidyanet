package com.kastack.vidyanet.commonUi.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kastack.vidyanet.core.GlobalStore
import com.kastack.vidyanet.models.user.UserType
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class SplashViewModel(
    private val globalStore: GlobalStore
) : ViewModel() {

    val isLoggedIn = globalStore.currentUser.map { it != null }.asStateFlow(viewModelScope, false)
    val isSuperAdmin = globalStore.currentUser.map { it?.userType == UserType.PLATFORM_OWNER }.asStateFlow(viewModelScope, false)
    val userSchoolId = globalStore.currentUser.map { it?.schoolId?.toString() }.asStateFlow(viewModelScope, null)

    init {
        // Initialization logic moved to App.kt to handle reloads correctly
    }
}

// Extension to convert Flow to StateFlow in ViewModelScope
private fun <T> Flow<T>.asStateFlow(scope: CoroutineScope, initialValue: T): StateFlow<T> {
    val flow = MutableStateFlow(initialValue)
    scope.launch {
        this@asStateFlow.collect { flow.value = it }
    }
    return flow.asStateFlow()
}
