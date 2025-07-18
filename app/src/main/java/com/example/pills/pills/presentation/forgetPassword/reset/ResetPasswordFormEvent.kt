package com.example.pills.pills.presentation.forgetPassword.reset

sealed class ForgetPasswordFormEvent {
    data class EmailChanged(val email: String) : ForgetPasswordFormEvent()
    object Submit : ForgetPasswordFormEvent()
}