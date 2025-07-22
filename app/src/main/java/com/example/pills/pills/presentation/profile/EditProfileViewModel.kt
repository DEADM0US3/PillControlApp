package com.example.pills.pills.presentation.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class EditProfileState(
    val name: String = "",
    val email: String = "",
    val phone: String = "",
    val age: String = "",
    val profileImageUri: String? = null,
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val isSuccess: Boolean = false
)

sealed class EditProfileEvent {
    data class UpdateName(val name: String) : EditProfileEvent()
    data class UpdateEmail(val email: String) : EditProfileEvent()
    data class UpdatePhone(val phone: String) : EditProfileEvent()
    data class UpdateAge(val age: String) : EditProfileEvent()
    data class UpdateProfileImage(val imageUri: String) : EditProfileEvent()
    object SaveProfile : EditProfileEvent()
    object ResetState : EditProfileEvent()
}

class EditProfileViewModel : ViewModel() {

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
            is EditProfileEvent.UpdatePhone -> {
                _state.update { it.copy(phone = event.phone) }
            }
            is EditProfileEvent.UpdateAge -> {
                _state.update { it.copy(age = event.age) }
            }
            is EditProfileEvent.UpdateProfileImage -> {
                _state.update { it.copy(profileImageUri = event.imageUri) }
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
            try {
                _state.update { it.copy(isLoading = true, errorMessage = null) }

                // Validar datos
                val currentState = _state.value
                if (currentState.name.isBlank()) {
                    _state.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = "El nombre no puede estar vacío"
                        )
                    }
                    return@launch
                }

                if (currentState.email.isBlank()) {
                    _state.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = "El email no puede estar vacío"
                        )
                    }
                    return@launch
                }

                // TODO: Implementar guardado en Supabase
                // Por ahora simulamos un guardado exitoso
                kotlinx.coroutines.delay(1000)

                _state.update {
                    it.copy(
                        isLoading = false,
                        isSuccess = true
                    )
                }

            } catch (e: Exception) {
                _state.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = "Error al guardar: ${e.message ?: "Error desconocido"}"
                    )
                }
            }
        }
    }

    fun loadUserProfile(userName: String, userEmail: String, userPhone: String, userAge: String) {
        _state.update {
            it.copy(
                name = userName,
                email = userEmail,
                phone = userPhone,
                age = userAge
            )
        }
    }
}