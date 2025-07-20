package com.example.pills.pills.presentation.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pills.Logger
import com.example.pills.pills.domain.use_case.GetUserProfile
import com.example.pills.pills.domain.use_case.UpdateUserProfile
import com.example.pills.pills.domain.use_case.UpdateProfileImage
import com.example.pills.pills.domain.use_case.UploadProfileImage
import com.example.pills.pills.domain.use_case.DeleteProfileImage
import com.example.pills.pills.domain.use_case.ValidateEmail
import com.example.pills.pills.domain.use_case.ValidateName
import com.example.pills.pills.domain.use_case.ValidatePhone
import com.example.pills.pills.domain.use_case.ValidateAge
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
    val isSuccess: Boolean = false,
    val nameError: String? = null,
    val emailError: String? = null,
    val phoneError: String? = null,
    val ageError: String? = null
)

sealed class EditProfileEvent {
    data class UpdateName(val name: String) : EditProfileEvent()
    data class UpdateEmail(val email: String) : EditProfileEvent()
    data class UpdatePhone(val phone: String) : EditProfileEvent()
    data class UpdateAge(val age: String) : EditProfileEvent()
    data class UpdateProfileImage(val imageUri: String) : EditProfileEvent()
    data class UploadImage(val imageUri: String) : EditProfileEvent()
    object SaveProfile : EditProfileEvent()
    object ResetState : EditProfileEvent()
    object LoadUserProfile : EditProfileEvent()
}

class EditProfileViewModel(
    private val getUserProfile: GetUserProfile,
    private val updateUserProfile: UpdateUserProfile,
    private val updateProfileImage: UpdateProfileImage,
    private val uploadProfileImage: UploadProfileImage,
    private val deleteProfileImage: DeleteProfileImage,
    private val validateName: ValidateName,
    private val validateEmail: ValidateEmail,
    private val validatePhone: ValidatePhone,
    private val validateAge: ValidateAge
) : ViewModel() {
    
    private val _state = MutableStateFlow(EditProfileState())
    val state: StateFlow<EditProfileState> = _state.asStateFlow()

    fun onEvent(event: EditProfileEvent) {
        when (event) {
            is EditProfileEvent.UpdateName -> {
                _state.update { it.copy(name = event.name, nameError = null) }
            }
            is EditProfileEvent.UpdateEmail -> {
                _state.update { it.copy(email = event.email, emailError = null) }
            }
            is EditProfileEvent.UpdatePhone -> {
                _state.update { it.copy(phone = event.phone, phoneError = null) }
            }
            is EditProfileEvent.UpdateAge -> {
                _state.update { it.copy(age = event.age, ageError = null) }
            }
            is EditProfileEvent.UpdateProfileImage -> {
                _state.update { it.copy(profileImageUri = event.imageUri) }
            }
            is EditProfileEvent.UploadImage -> {
                uploadImage(event.imageUri)
            }
            is EditProfileEvent.SaveProfile -> {
                saveProfile()
            }
            is EditProfileEvent.ResetState -> {
                _state.update { EditProfileState() }
            }
            is EditProfileEvent.LoadUserProfile -> {
                loadUserProfileFromDatabase()
            }
        }
    }

    private fun saveProfile() {
        viewModelScope.launch {
            try {
                Logger.d("EditProfileViewModel", "Saving profile...")
                
                _state.update { 
                    it.copy(
                        isLoading = true, 
                        errorMessage = null,
                        nameError = null,
                        emailError = null,
                        phoneError = null,
                        ageError = null
                    ) 
                }
                
                val currentState = _state.value
                
                // Validar todos los campos usando los casos de uso
                val nameResult = validateName.execute(currentState.name)
                val emailResult = validateEmail.execute(currentState.email)
                val phoneResult = validatePhone.execute(currentState.phone)
                val ageResult = validateAge.execute(currentState.age)
                
                // Verificar si hay errores de validación
                val hasValidationErrors = listOf(
                    nameResult, emailResult, phoneResult, ageResult
                ).any { !it.successful }
                
                if (hasValidationErrors) {
                    _state.update { 
                        it.copy(
                            isLoading = false,
                            nameError = nameResult.errorMessage,
                            emailError = emailResult.errorMessage,
                            phoneError = phoneResult.errorMessage,
                            ageError = ageResult.errorMessage
                        ) 
                    }
                    return@launch
                }
                
                // Actualizar perfil en la base de datos
                val updateResult = updateUserProfile(
                    fullName = currentState.name,
                    email = currentState.email,
                    phone = currentState.phone.takeIf { it.isNotBlank() },
                    age = currentState.age.takeIf { it.isNotBlank() }
                )
                
                if (updateResult.isSuccess) {
                    // Si hay una nueva imagen de perfil, actualizarla también
                    currentState.profileImageUri?.let { imageUri ->
                        if (imageUri.isNotBlank()) {
                            updateProfileImage(imageUri)
                        }
                    }
                    
                    _state.update { 
                        it.copy(
                            isLoading = false, 
                            isSuccess = true
                        ) 
                    }
                } else {
                    val error = updateResult.exceptionOrNull()?.message ?: "Error desconocido al guardar"
                    Logger.e("EditProfileViewModel", "Error saving profile: $error")
                    _state.update { 
                        it.copy(
                            isLoading = false, 
                            errorMessage = error
                        ) 
                    }
                }
                
            } catch (e: Exception) {
                Logger.e("EditProfileViewModel", "Exception saving profile: ${e.message}")
                _state.update { 
                    it.copy(
                        isLoading = false, 
                        errorMessage = "Error al guardar: ${e.message ?: "Error desconocido"}"
                    ) 
                }
            }
        }
    }

    private fun loadUserProfileFromDatabase() {
        viewModelScope.launch {
            try {
                Logger.d("EditProfileViewModel", "Loading user profile...")
                _state.update { it.copy(isLoading = true, errorMessage = null) }
                
                val profileResult = getUserProfile()
                
                if (profileResult.isSuccess) {
                    val profile = profileResult.getOrNull()
                    profile?.let { userProfile ->
                        _state.update { 
                            it.copy(
                                name = userProfile.fullName ?: "",
                                email = userProfile.email ?: "",
                                phone = userProfile.phone ?: "",
                                age = userProfile.age ?: "",
                                profileImageUri = userProfile.profileImageUrl,
                                isLoading = false
                            ) 
                        }
                    } ?: run {
                        // Si el perfil es null, establecer valores por defecto
                        _state.update { 
                            it.copy(
                                name = "",
                                email = "",
                                phone = "",
                                age = "",
                                profileImageUri = null,
                                isLoading = false
                            ) 
                        }
                    }
                } else {
                    val error = profileResult.exceptionOrNull()?.message ?: "Error al cargar el perfil"
                    Logger.e("EditProfileViewModel", "Error loading profile: $error")
                    _state.update { 
                        it.copy(
                            isLoading = false, 
                            errorMessage = error
                        ) 
                    }
                }
                
            } catch (e: Exception) {
                Logger.e("EditProfileViewModel", "Exception loading profile: ${e.message}")
                _state.update { 
                    it.copy(
                        isLoading = false, 
                        errorMessage = "Error al cargar el perfil: ${e.message ?: "Error desconocido"}"
                    ) 
                }
            }
        }
    }

    private fun uploadImage(imageUri: String) {
        viewModelScope.launch {
            try {
                Logger.d("EditProfileViewModel", "Uploading image: $imageUri")
                _state.update { it.copy(isLoading = true, errorMessage = null) }
                
                // Upload image to Supabase Storage
                val uploadResult = uploadProfileImage(imageUri)
                
                if (uploadResult.isSuccess) {
                    val publicUrl = uploadResult.getOrNull()
                    Logger.d("EditProfileViewModel", "Image uploaded successfully: $publicUrl")
                    
                    // Update profile image URL in database
                    val updateResult = updateProfileImage(publicUrl ?: "")
                    
                    if (updateResult.isSuccess) {
                        _state.update { 
                            it.copy(
                                profileImageUri = publicUrl,
                                isLoading = false
                            ) 
                        }
                    } else {
                        val error = updateResult.exceptionOrNull()?.message ?: "Error updating profile image"
                        Logger.e("EditProfileViewModel", "Error updating profile image: $error")
                        _state.update { 
                            it.copy(
                                isLoading = false,
                                errorMessage = "Error updating profile image: $error"
                            ) 
                        }
                    }
                } else {
                    val error = uploadResult.exceptionOrNull()?.message ?: "Error uploading image"
                    Logger.e("EditProfileViewModel", "Error uploading image: $error")
                    _state.update { 
                        it.copy(
                            isLoading = false,
                            errorMessage = "Error uploading image: $error"
                        ) 
                    }
                }
                
            } catch (e: Exception) {
                Logger.e("EditProfileViewModel", "Exception uploading image: ${e.message}")
                _state.update { 
                    it.copy(
                        isLoading = false,
                        errorMessage = "Error uploading image: ${e.message ?: "Error desconocido"}"
                    ) 
                }
            }
        }
    }

    fun loadUserProfile(userName: String, userEmail: String, userPhone: String, userAge: String) {
        try {
            _state.update { 
                it.copy(
                    name = userName,
                    email = userEmail,
                    phone = userPhone,
                    age = userAge
                ) 
            }
        } catch (e: Exception) {
            Logger.e("EditProfileViewModel", "Error loading user profile from parameters: ${e.message}")
        }
    }
} 