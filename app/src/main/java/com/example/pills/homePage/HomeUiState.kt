package com.example.pills.homePage

import com.example.pills.pills.domain.repository.ProfileRepository
import com.example.pills.R

// UI state data class for the Home Screen
data class HomeUiState(
    val userId: String = "",
    val userName: String = "Guest",
    val userEmail: String = "",
    val userPhone: String? = null,
    val userAge: String? = null,
    val profileImageUrl: String? = null,
    val accessToken: String = "No Access Token",
    val refreshToken: String = "No Refresh Token",
    val errorMessage: String? = null,
    val isLoading: Boolean = true,
    val pillTime: String = "22:00", // Hora de toma por defecto
    val mascotImageRes: Int = R.drawable.mascot_happy,
    val mascotMessage: String = "¡Cuídate mucho hoy! 💖"
)