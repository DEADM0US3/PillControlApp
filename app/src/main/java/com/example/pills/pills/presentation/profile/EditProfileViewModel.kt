package com.example.pills.pills.presentation.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pills.pills.domain.repository.ProfileRepository
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
    object SaveProfile : EditProfileEvent()
    object LoadUserProfile : EditProfileEvent()
    object ResetState : EditProfileEvent()
}

class EditProfileViewModel(
    private val profileRepository: ProfileRepository
) : ViewModel() {

    private val _state = MutableStateFlow(EditProfileState())
    val state: StateFlow<EditProfileState> = _state.asStateFlow()

    fun onEvent(event: EditProfileEvent) {
        when (event) {
            is EditProfileEvent.UpdateName -> _state.update { it.copy(name = event.name, errorMessage = null, isSuccess = false) }
            is EditProfileEvent.UpdateEmail -> _state.update { it.copy(email = event.email, errorMessage = null, isSuccess = false) }
            is EditProfileEvent.UpdatePhone -> _state.update { it.copy(phone = event.phone, errorMessage = null, isSuccess = false) }
            is EditProfileEvent.UpdateAge -> _state.update { it.copy(age = event.age, errorMessage = null, isSuccess = false) }
            EditProfileEvent.SaveProfile -> saveProfile()
            EditProfileEvent.LoadUserProfile -> loadUserProfile()
            EditProfileEvent.ResetState -> _state.value = EditProfileState()
        }
    }

    private fun loadUserProfile() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, errorMessage = null) }
            val result = profileRepository.getUserProfile()
            if (result.isSuccess) {
                result.getOrNull()?.let { profile ->
                    _state.update {
                        it.copy(
                            name = profile.fullName ?: "",
                            email = profile.email,
                            phone = profile.phone ?: "",
                            age = profile.age ?: "",
                            profileImageUri = profile.profileImageUrl,
                            isLoading = false
                        )
                    }
                }
            } else {
                _state.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = result.exceptionOrNull()?.message ?: "Error al cargar perfil"
                    )
                }
            }
        }
    }

    private fun saveProfile() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, errorMessage = null, isSuccess = false) }
            val currentState = _state.value
            val result = profileRepository.updateUserProfile(
                fullName = currentState.name,
                email = currentState.email,
                phone = currentState.phone.takeIf { it.isNotBlank() },
                age = currentState.age.takeIf { it.isNotBlank() }
            )
            if (result.isSuccess) {
                _state.update {
                    it.copy(isLoading = false, isSuccess = true)
                }
            } else {
                _state.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = result.exceptionOrNull()?.message ?: "Error al guardar perfil"
                    )
                }
            }
        }
    }
}
