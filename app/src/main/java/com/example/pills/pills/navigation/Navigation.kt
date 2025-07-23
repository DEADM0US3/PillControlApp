package com.example.pills.pills.navigation

import androidx.annotation.RequiresPermission
import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.pills.pills.presentation.forgetPassword.reset.ForgetPasswordScreen
import com.example.pills.pills.presentation.forgetPassword.setNew.SetPasswordScreen
import com.example.pills.pills.presentation.login.LoginScreen
import com.example.pills.pills.presentation.otpVerification.OtpVerificationScreen
import com.example.pills.pills.presentation.signUp.SignUpScreen
import com.example.pills.pills.presentation.calendar.CalendarScreen
import com.example.pills.pills.presentation.components.BottomNavBar
import androidx.compose.runtime.getValue
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.compose.ui.Modifier
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import com.example.pills.pills.presentation.configuration.ConfigurationScreen
import com.example.pills.pills.presentation.friends.FriendScreen
import com.example.pills.pills.presentation.homePage.HomeScreen
import com.example.pills.pills.presentation.profile.ProfileScreen
import com.example.pills.pills.presentation.profile.EditProfileScreen
import com.example.pills.pills.presentation.profile.HelpScreen


@RequiresPermission(
    allOf = [
        android.Manifest.permission.BLUETOOTH_SCAN,
        android.Manifest.permission.BLUETOOTH_CONNECT
    ]
)
@Composable
fun AuthNavigation(
    startDestination: String
) {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    NavHost(navController = navController, startDestination = startDestination) {

        // Login Screen
        composable(
            route = Screen.Login.route
        ) {
            LoginScreen(
                navigateToSignUp = { navController.navigate(Screen.SignUp.route) },
                navigateToForgetPassword = { navController.navigate(Screen.ResetPassword.route) },
                navigateToHome = {
                    navController.navigate(Screen.HomeScreen.route) {
                        popUpTo(Screen.Login.route) {
                            inclusive = true
                        }
                    }
                },
                navigateToOtp = { email ->
                    navController.navigate("${Screen.OtpVerification.route}/$email/login")
                }
            )
        }

        // Sign-Up Screen
        composable(
            route = Screen.SignUp.route
        ) {
            SignUpScreen(
                navigateToLogin = { navController.navigate(Screen.Login.route) },
                navigateToOtp = { email ->
                    navController.navigate("${Screen.OtpVerification.route}/$email/signup")
                }
            )
        }

        // OTP Screen
        composable(
            route = "${Screen.OtpVerification.route}/{email}/{flow}",
            arguments = listOf(
                navArgument("email") { type = NavType.StringType },
                navArgument("flow") { type = NavType.StringType }
            )
        ) {backStackEntry ->
            val email = backStackEntry.arguments?.getString("email") ?: ""
            val flow = backStackEntry.arguments?.getString("flow") ?: "signup"

            OtpVerificationScreen(
                email = email,
                flow = flow,
                navigateAfterOtp = {
                    if (flow == "login") {
                        navController.navigate(Screen.HomeScreen.route) {
                            popUpTo(Screen.Login.route) { inclusive = true }
                        }
                    }
                    else if(flow == "reset"){
                        navController.navigate(Screen.SetNewPassword.route) {
                            popUpTo(Screen.ResetPassword.route) { inclusive = true }
                        }
                    }
                    else {
                        navController.navigate(Screen.Login.route) {
                            popUpTo(Screen.Login.route) { inclusive = true }
                        }
                    }
                },
                onBackPressed  = { navController.popBackStack() },
            )
        }

        // Reset Password Screen
        composable(
            route = Screen.ResetPassword.route
        ) {
            ForgetPasswordScreen(
                navigateToLogin = { navController.navigate(Screen.Login.route) },
                navigateToOtp = {email->
                    navController.navigate("${Screen.OtpVerification.route}/$email/reset")
                }
            )
        }

        // Set New Password Screen
        composable(
            route = Screen.SetNewPassword.route
        ){
            SetPasswordScreen(
                navigateToLogin = {
                    navController.navigate(Screen.Login.route) {
                        popUpTo(0)
                    }
                },
                navigateToReset = { navController.navigate(Screen.ResetPassword.route) }
            )
        }

        // Home Screen
        composable(
            route = Screen.HomeScreen.route
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                Box(
                    modifier = Modifier.weight(1f)
                ) {
                    HomeScreen(
                        navigateToLogin = {
                            navController.navigate(Screen.Login.route) {
                                popUpTo(0) // Clears the backstack if needed.
                            }
                        },
                        navigateToFriends = {
                            navController.navigate(Screen.FriendScreen.route) {
                                popUpTo(0) // Clears the backstack if needed.
                            }
                        }

                    )
                }
                BottomNavBar(currentRoute = currentRoute ?: "", onNavigate = { route ->
                    if (route != currentRoute) navController.navigate(route)
                })
            }
        }

        // Calendar Screen
        composable(
            route = Screen.CalendarScreen.route
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                Box(
                    modifier = Modifier.weight(1f)
                ) {
                    CalendarScreen(
                        onDayClick = { /* TODO: Navegar a detalles del día */ }
                    )
                }
                BottomNavBar(currentRoute = currentRoute ?: "", onNavigate = { route ->
                    if (route != currentRoute) navController.navigate(route)
                })
            }
        }

        composable(
            route = Screen.FriendScreen.route
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                Box(
                    modifier = Modifier.weight(1f)
                ) {
                    FriendScreen(
                        onBackPressed = { navController.navigate(Screen.HomeScreen.route)},
                    )
                }
                BottomNavBar(currentRoute = currentRoute ?: "", onNavigate = { route ->
                    if (route != currentRoute) navController.navigate(route)
                })
            }
        }

        // Profile Screen
        composable(
            route = Screen.ProfileScreen.route
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                Box(
                    modifier = Modifier.weight(1f)
                ) {
                    ProfileScreen(
                        onEditProfile = { navController.navigate(Screen.EditProfileScreen.route) },
                        onSettings = { navController.navigate(Screen.ConfigurationScreen.route) },
                        onHelp = { navController.navigate(Screen.HelpScreen.route) },
                        navigateToLogin = { navController.navigate(Screen.Login.route) },
                        )
                }
                BottomNavBar(currentRoute = currentRoute ?: "", onNavigate = { route ->
                    if (route != currentRoute) navController.navigate(route)
                })
            }
        }

        // Configuration Screen
        composable(
            route = Screen.ConfigurationScreen.route
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                Box(
                    modifier = Modifier.weight(1f)
                ) {
                    ConfigurationScreen(
                        onBackPressed = { navController.popBackStack() }
                    )
                }
            }
        }


        // Edit Profile Screen
        composable(
            route = Screen.EditProfileScreen.route
        ) {
            EditProfileScreen(
                onBackPressed = { navController.popBackStack() }
            )
        }

        // Help Screen
        composable(
            route = Screen.HelpScreen.route
        ) {
            HelpScreen(
                onBackPressed = { navController.popBackStack() }
            )
        }
    }
}