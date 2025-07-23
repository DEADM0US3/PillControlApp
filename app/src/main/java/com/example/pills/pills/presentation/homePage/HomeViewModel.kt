package com.example.pills.pills.presentation.homePage

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pills.Logger
import com.example.pills.R
import com.example.pills.pills.domain.repository.LoginRepository
import com.example.pills.pills.domain.use_case.GetUserProfile
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.auth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.time.Duration
import java.time.LocalTime
import java.time.format.DateTimeFormatter

class HomeViewModel(
    private val loginRepository: LoginRepository,
    private val supabaseClient: SupabaseClient,
    private val getUserProfile: GetUserProfile
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState

    init {
        loadSessionData()
        updateMascotInfo()
    }

    fun loadSessionData() {
        _uiState.value = _uiState.value.copy(isLoading = true)
        viewModelScope.launch {
            try {
                Logger.d("HomeViewModel", "Loading session data...")

                // Get the current session from Supabase
                val session = supabaseClient.auth.currentSessionOrNull()
                val userId = supabaseClient.auth.currentUserOrNull()?.id.toString()
                val access = session?.accessToken ?: "No Access Token"
                val refresh = session?.refreshToken ?: "No Refresh Token"

                Logger.d("HomeViewModel", "Session found: ${session != null}")

                // Get complete user profile from database
                val profileResult = getUserProfile()

                if (profileResult.isSuccess) {
                    val userProfile = profileResult.getOrNull()
                    userProfile?.let { profile ->
                        _uiState.value = HomeUiState(
                            userId = userId, // Añade esto a tu HomeUiState
                            userName = profile.fullName ?: "Guest",
                            userEmail = profile.email,
                            userPhone = profile.phone,
                            userAge = profile.age,
                            profileImageUrl = profile.profileImageUrl,
                            accessToken = access,
                            refreshToken = refresh,
                            errorMessage = null,
                            isLoading = false
                        )
                    } ?: run {
                        val userResponse = supabaseClient.auth.retrieveUserForCurrentSession(updateSession = true)
                        val fullNameValue = userResponse.userMetadata?.get("full_name")
                        val userName = fullNameValue?.toString() ?: userResponse.email ?: "Guest"

                        _uiState.value = HomeUiState(
                            userId = userId, // Añade esto
                            userName = userName,
                            userEmail = userResponse.email ?: "",
                            accessToken = access,
                            refreshToken = refresh,
                            errorMessage = null,
                            isLoading = false
                        )
                    }
                } else {
                    Logger.d("HomeViewModel", "Profile fetch failed: ${profileResult.exceptionOrNull()?.message}")
                    // Fallback to basic auth data if profile fetch failed
                    val userResponse = supabaseClient.auth.retrieveUserForCurrentSession(updateSession = true)
                    val fullNameValue = userResponse.userMetadata?.get("full_name")
                    val userName = fullNameValue?.toString() ?: userResponse.email ?: "Guest"

                    _uiState.value = HomeUiState(
                        userId = userId, // Añade esto
                        userName = userName,
                        userEmail = userResponse.email ?: "",
                        accessToken = access,
                        refreshToken = refresh,
                        errorMessage = "Could not load complete profile: ${profileResult.exceptionOrNull()?.message}",
                        isLoading = false
                    )
                }
            } catch (e: Exception) {
                Logger.e("HomeViewModel", "Error loading session data: ${e.message}")
                val session = supabaseClient.auth.currentSessionOrNull()
                val access = session?.accessToken ?: "No Access Token"
                val refresh = session?.refreshToken ?: "No Refresh Token"
                _uiState.value = HomeUiState(
                    userName = "Guest",
                    accessToken = access,
                    refreshToken = refresh,
                    errorMessage = "Failed to fetch user details: ${e.message}",
                    isLoading = false
                )
            }
        }
    }

    fun logout(onLogoutSuccess: () -> Unit) {
        viewModelScope.launch {
            val result = loginRepository.logoutUser()
            if (result.isSuccess) {
                onLogoutSuccess()
            } else {
                _uiState.value = _uiState.value.copy(
                    errorMessage = "Logout failed. Please try again."
                )
            }
        }
    }

    /**
     * Refresh user profile data
     * Call this method when returning from profile editing
     */
    fun refreshUserProfile() {
        loadSessionData()
    }

    private fun updateMascotInfo() {
        // CONFIGURACIÓN PARA PRUEBAS
        val USE_STATIC_TIME = true // Cambia a false para usar hora real
        val STATIC_TIME_FOR_TESTING = "04:35" // Cambia para probar diferentes estados

        val currentTime = if (USE_STATIC_TIME) {
            LocalTime.parse(STATIC_TIME_FOR_TESTING, DateTimeFormatter.ofPattern("HH:mm"))
        } else {
            LocalTime.now()
        }

        val pillTime = LocalTime.parse(_uiState.value.pillTime, DateTimeFormatter.ofPattern("HH:mm"))

        // Calcular minutos hasta la toma
        val minutesUntilPill = if (currentTime.isBefore(pillTime)) {
            Duration.between(currentTime, pillTime).toMinutes()
        } else {
            Duration.between(currentTime, pillTime.plusHours(24)).toMinutes()
        }

        val (imageRes, message) = when {
            // Es hora de tomar la pastilla (±15 minutos)
            minutesUntilPill <= 15 -> Pair(
                R.drawable.mascot_excited,
                "¡Es hora de tu pastilla! 💊✨"
            )

            // Falta poco (16-60 minutos)
            minutesUntilPill in 16..60 -> Pair(
                R.drawable.mascot_happy,
                "¡Prepárate! Tu pastilla es en ${minutesUntilPill} minutos"
            )

            // Se olvidó tomar (pasó más de 2 horas después de la hora programada)
            minutesUntilPill > 120 && currentTime.isAfter(pillTime) -> Pair(
                R.drawable.mascot_worried,
                "¿Ya tomaste tu pastilla de hoy? 🤔"
            )

            // Por la mañana (6 AM - 12 PM)
            currentTime.hour in 6..11 -> Pair(
                R.drawable.mascot_happy,
                "¡Buenos días! Recuerda desayunar bien 🌅"
            )

            // Por la tarde (12 PM - 6 PM)
            currentTime.hour in 12..17 -> Pair(
                R.drawable.mascot_happy,
                "¡Mantente hidratada durante el día! 💧"
            )

            // Por la noche temprana (6 PM - 10 PM)
            currentTime.hour in 18..21 -> Pair(
                R.drawable.mascot_happy,
                "No olvides estar tomando agua 💧"
            )

            // Por la noche tardía (10 PM - 12 AM)
            currentTime.hour in 22..23 -> Pair(
                R.drawable.mascot_sleeping,
                "Es hora de descansar. ¡Dulces sueños! 🌙"
            )

            // Madrugada (12 AM - 6 AM)
            currentTime.hour in 0..5 -> Pair(
                R.drawable.mascot_sleeping,
                "Zzz... Es muy tarde, mejor descansa 😴"
            )

            // Caso por defecto
            else -> Pair(
                R.drawable.mascot_happy,
                "¡Cuídate mucho hoy! 💖"
            )
        }

        _uiState.value = _uiState.value.copy(
            mascotImageRes = imageRes,
            mascotMessage = message
        )
    }

    // Función para actualizar la hora de toma (para cuando se implemente la configuración)
    fun updatePillTime(newTime: String) {
        _uiState.value = _uiState.value.copy(pillTime = newTime)
        updateMascotInfo()
    }

    // Función para forzar actualización de mascota (útil para pruebas o refrescos)
    fun refreshMascot() {
        updateMascotInfo()
    }
}
