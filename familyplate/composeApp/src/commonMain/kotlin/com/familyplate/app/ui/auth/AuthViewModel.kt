package com.familyplate.app.ui.auth

import com.familyplate.app.domain.repository.AuthRepository
import com.familyplate.app.domain.repository.FamilyRepository
import com.familyplate.app.util.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class AuthUiState(
    val email: String = "",
    val password: String = "",
    val displayName: String = "",
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val isSignInMode: Boolean = true
)

class AuthViewModel(
    private val authRepository: AuthRepository,
    private val familyRepository: FamilyRepository
) : ViewModel() {

    private val _state = MutableStateFlow(AuthUiState())
    val state: StateFlow<AuthUiState> = _state.asStateFlow()

    fun onEmailChange(email: String) {
        _state.value = _state.value.copy(email = email)
    }

    fun onPasswordChange(password: String) {
        _state.value = _state.value.copy(password = password)
    }

    fun onDisplayNameChange(name: String) {
        _state.value = _state.value.copy(displayName = name)
    }

    fun clearError() {
        _state.value = _state.value.copy(errorMessage = null)
    }

    fun signIn(onSuccess: () -> Unit) {
        val email = _state.value.email.trim()
        val password = _state.value.password

        if (email.isBlank() || password.isBlank()) {
            _state.value = _state.value.copy(errorMessage = "Please enter email and password")
            return
        }

        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, errorMessage = null)
            authRepository.signIn(email, password)
                .onSuccess {
                    _state.value = _state.value.copy(isLoading = false)
                    onSuccess()
                }
                .onFailure { e ->
                    _state.value = _state.value.copy(
                        isLoading = false,
                        errorMessage = e.message ?: "Sign in failed"
                    )
                }
        }
    }

    fun signUp(onSuccess: () -> Unit) {
        val email = _state.value.email.trim()
        val password = _state.value.password
        val displayName = _state.value.displayName.trim()

        if (email.isBlank() || password.isBlank() || displayName.isBlank()) {
            _state.value = _state.value.copy(
                errorMessage = "Please fill in all fields"
            )
            return
        }

        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, errorMessage = null)
            authRepository.signUp(email, password, displayName)
                .onSuccess { user ->
                    familyRepository.createUserProfile(user)
                    _state.value = _state.value.copy(isLoading = false)
                    onSuccess()
                }
                .onFailure { e ->
                    _state.value = _state.value.copy(
                        isLoading = false,
                        errorMessage = e.message ?: "Sign up failed"
                    )
                }
        }
    }
}
