package com.example.pills.pills.navigation

sealed class Screen(val route: String) {
    object Login : Screen("login")
    object SignUp : Screen("signup")

    object ResetPassword : Screen("resetPassword")
    object SetNewPassword : Screen("setNewPassword")

    object OtpVerification: Screen("otpVerify")
    object HomeScreen: Screen("homeScreen")
}
