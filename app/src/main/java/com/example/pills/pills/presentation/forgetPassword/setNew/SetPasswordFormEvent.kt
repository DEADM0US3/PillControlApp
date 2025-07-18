package com.example.pills.pills.presentation.forgetPassword.setNew

sealed class SetPasswordFormEvent {
    data class PasswordChanged(val password: String) : SetPasswordFormEvent()
    data class ConfirmPasswordChanged(val confirmPassword: String) : SetPasswordFormEvent()
    object Submit : SetPasswordFormEvent()
}