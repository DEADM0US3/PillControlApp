package com.example.pills.pills.presentation.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import com.example.pills.pills.domain.use_case.GetUserProfile
import com.example.pills.pills.domain.use_case.UpdateUserProfile

data class EditProfileState(
    val name: String = "",
    val email: String = "",
    val imageUrl: String = "",
    val isLoading: Boolean = false,
    val error: String? = null,
    val errorMessage: String? = null,
    val isSuccess: Boolean = false
)

sealed class EditProfileEvent {
    data class UpdateName(val name: String) : EditProfileEvent()
    data class UpdateEmail(val email: String) : EditProfileEvent()
    data class UpdateProfileImage(val imageUri: String) : EditProfileEvent()
    object SaveProfile : EditProfileEvent()
    object ResetState : EditProfileEvent()
}

class EditProfileViewModel(
    private val getUserProfile: GetUserProfile,
    private val updateUserProfile: UpdateUserProfile
) : ViewModel() {

    private val _state = MutableStateFlow(EditProfileState())
    val state: StateFlow<EditProfileState> = _state.asStateFlow()

    fun onEvent(event: EditProfileEvent) {
        when (event) {
            is EditProfileEvent.UpdateName -> {
                _state.update { it.copy(name = event.name) }
            }
            is EditProfileEvent.UpdateEmail -> {
                _state.update { it.copy(email = event.email) }
            }
            is EditProfileEvent.UpdateProfileImage -> {
                _state.update { it.copy(imageUrl = event.imageUri) }
            }
            is EditProfileEvent.SaveProfile -> {
                saveProfile()
            }
            is EditProfileEvent.ResetState -> {
                _state.update { EditProfileState() }
            }
        }
    }

    private fun saveProfile() {
        viewModelScope.launch {
            val currentState = _state.value

            if (currentState.name.isBlank()) {
                _state.update { it.copy(errorMessage = "El nombre no puede estar vacío") }
                return@launch
            }

            if (currentState.email.isBlank()) {
                _state.update { it.copy(errorMessage = "El correo no puede estar vacío") }
                return@launch
            }

            _state.update { it.copy(isLoading = true, errorMessage = null, isSuccess = false) }

            val result = updateUserProfile(
                fullName = currentState.name,
                email = currentState.email
            )

            result.onSuccess {
                _state.update {
                    it.copy(
                        isLoading = false,
                        isSuccess = true
                    )
                }
            }.onFailure { e ->
                _state.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = e.message ?: "Error al guardar"
                    )
                }
            }
        }
    }

    fun loadUserProfile() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }

            val result = getUserProfile()

            result.onSuccess { user ->
                _state.update {
                    it.copy(
                        name = user.fullName ?: "",
                        email = user.email,
                        imageUrl = user.profileImageUrl ?: "",
                        isLoading = false
                    )
                }
            }.onFailure { e ->
                _state.update {
                    it.copy(
                        isLoading = false,
                        error = e.message ?: "Error al cargar perfil"
                    )
                }
            }
        }
    }
}