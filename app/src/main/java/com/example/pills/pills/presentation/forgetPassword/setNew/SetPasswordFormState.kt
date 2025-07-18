package com.example.pills.pills.presentation.forgetPassword.setNew

data class SetPasswordFormState(
    val password: String = "",
    val passwordError: String? = null,

    val confirmPassword: String = "",
    val confirmPasswordError: String? = null,

    val isLoading: Boolean = false
)
