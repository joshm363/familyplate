package com.familyplate.app.ui.family

import com.familyplate.app.domain.model.Family
import com.familyplate.app.domain.repository.AuthRepository
import com.familyplate.app.domain.repository.FamilyRepository
import com.familyplate.app.util.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class FamilyUiState(
    val familyName: String = "",
    val inviteCode: String = "",
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val createdFamily: Family? = null
)

class FamilyViewModel(
    private val familyRepository: FamilyRepository,
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _state = MutableStateFlow(FamilyUiState())
    val state: StateFlow<FamilyUiState> = _state.asStateFlow()

    fun onFamilyNameChange(name: String) {
        _state.value = _state.value.copy(familyName = name)
    }

    fun onInviteCodeChange(code: String) {
        _state.value = _state.value.copy(inviteCode = code.uppercase().take(6))
    }

    fun clearError() {
        _state.value = _state.value.copy(errorMessage = null)
    }

    fun createFamily(onSuccess: () -> Unit) {
        val name = _state.value.familyName.trim()
        if (name.isBlank()) {
            _state.value = _state.value.copy(errorMessage = "Please enter a family name")
            return
        }

        val userId = authRepository.getCurrentUserId()
        if (userId == null) {
            _state.value = _state.value.copy(errorMessage = "You must be signed in to create a family")
            return
        }

        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, errorMessage = null)
            familyRepository.createFamily(name, userId)
                .onSuccess { family ->
                    familyRepository.updateUserFamily(userId, family.id)
                        .onSuccess {
                            _state.value = _state.value.copy(
                                isLoading = false,
                                createdFamily = family
                            )
                            // Don't call onSuccess here - UI shows invite code first, user taps Continue
                        }
                        .onFailure { e ->
                            _state.value = _state.value.copy(
                                isLoading = false,
                                errorMessage = e.message ?: "Failed to update user"
                            )
                        }
                }
                .onFailure { e ->
                    _state.value = _state.value.copy(
                        isLoading = false,
                        errorMessage = e.message ?: "Failed to create family"
                    )
                }
        }
    }

    fun joinFamily(onSuccess: () -> Unit) {
        val inviteCode = _state.value.inviteCode.trim()
        if (inviteCode.isBlank()) {
            _state.value = _state.value.copy(errorMessage = "Please enter an invite code")
            return
        }

        val userId = authRepository.getCurrentUserId()
        if (userId == null) {
            _state.value = _state.value.copy(errorMessage = "You must be signed in to join a family")
            return
        }

        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, errorMessage = null)
            familyRepository.joinFamily(inviteCode, userId)
                .onSuccess { family ->
                    familyRepository.updateUserFamily(userId, family.id)
                        .onSuccess {
                            _state.value = _state.value.copy(isLoading = false)
                            onSuccess()
                        }
                        .onFailure { e ->
                            _state.value = _state.value.copy(
                                isLoading = false,
                                errorMessage = e.message ?: "Failed to update user"
                            )
                        }
                }
                .onFailure { e ->
                    _state.value = _state.value.copy(
                        isLoading = false,
                        errorMessage = e.message ?: "Invalid invite code"
                    )
                }
        }
    }
}
