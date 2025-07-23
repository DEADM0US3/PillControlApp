package com.example.pills.pills.navigation

import androidx.annotation.RequiresPermission
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.pills.homePage.HomeScreen
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
import com.example.pills.pills.presentation.profile.ProfileScreen
import com.example.pills.pills.presentation.profile.EditProfileScreen
import com.example.pills.pills.presentation.profile.HelpScreen
import com.example.pills.pills.presentation.loading.LoadingScreen
import kotlinx.coroutines.delay

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

        composable(route = Screen.Login.route) {
            LoginScreen(
                navigateToSignUp = { navController.navigate("${Screen.Loading.route}/${Screen.SignUp.route}") },
                navigateToForgetPassword = { navController.navigate("${Screen.Loading.route}/${Screen.ResetPassword.route}") },
                navigateToHome = {
                    navController.navigate("${Screen.Loading.route}/${Screen.HomeScreen.route}") {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                },
                navigateToOtp = { email ->
                    navController.navigate("${Screen.Loading.route}/${Screen.OtpVerification.route}/$email/login")
                }
            )
        }

        composable(route = Screen.SignUp.route) {
            SignUpScreen(
                navigateToLogin = { navController.navigate("${Screen.Loading.route}/${Screen.Login.route}") },
                navigateToOtp = { email ->
                    navController.navigate("${Screen.Loading.route}/${Screen.OtpVerification.route}/$email/signup")
                }
            )
        }

        composable(
            route = "${Screen.Loading.route}/{destination}",
            arguments = listOf(navArgument("destination") { type = NavType.StringType })
        ) { backStackEntry ->
            val destination = backStackEntry.arguments?.getString("destination") ?: Screen.Login.route

            LaunchedEffect(Unit) {
                delay(1000)
                navController.navigate(destination) {
                    popUpTo(Screen.Loading.route) { inclusive = true }
                }
            }
            LoadingScreen()
        }

        composable(
            route = "${Screen.OtpVerification.route}/{email}/{flow}",
            arguments = listOf(
                navArgument("email") { type = NavType.StringType },
                navArgument("flow") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val email = backStackEntry.arguments?.getString("email") ?: ""
            val flow = backStackEntry.arguments?.getString("flow") ?: "signup"

            OtpVerificationScreen(
                email = email,
                flow = flow,
                navigateAfterOtp = {
                    when (flow) {
                        "login" -> navController.navigate("${Screen.Loading.route}/${Screen.HomeScreen.route}") {
                            popUpTo(Screen.Login.route) { inclusive = true }
                        }
                        "reset" -> navController.navigate("${Screen.Loading.route}/${Screen.SetNewPassword.route}") {
                            popUpTo(Screen.ResetPassword.route) { inclusive = true }
                        }
                        else -> navController.navigate("${Screen.Loading.route}/${Screen.Login.route}") {
                            popUpTo(Screen.Login.route) { inclusive = true }
                        }
                    }
                },
                onBackPressed = { navController.popBackStack() }
            )
        }

        composable(route = Screen.ResetPassword.route) {
            ForgetPasswordScreen(
                navigateToLogin = { navController.navigate("${Screen.Loading.route}/${Screen.Login.route}") },
                navigateToOtp = { email ->
                    navController.navigate("${Screen.Loading.route}/${Screen.OtpVerification.route}/$email/reset")
                }
            )
        }

        composable(route = Screen.SetNewPassword.route) {
            SetPasswordScreen(
                navigateToLogin = {
                    navController.navigate("${Screen.Loading.route}/${Screen.Login.route}") {
                        popUpTo(0)
                    }
                },
                navigateToReset = { navController.navigate("${Screen.Loading.route}/${Screen.ResetPassword.route}") }
            )
        }

        composable(route = Screen.HomeScreen.route) {
            Column(modifier = Modifier.fillMaxSize()) {
                Box(modifier = Modifier.weight(1f)) {
                    HomeScreen(
                        navigateToLogin = {
                            navController.navigate("${Screen.Loading.route}/${Screen.Login.route}") {
                                popUpTo(0)
                            }
                        },
                        navigateToFriends = {
                            navController.navigate("${Screen.Loading.route}/${Screen.FriendScreen.route}") {
                                popUpTo(0)
                            }
                        }
                    )
                }
                BottomNavBar(
                    currentRoute = currentRoute ?: "",
                    onNavigate = { route ->
                        if (route != currentRoute) {
                            navController.navigate("${Screen.Loading.route}/$route") {
                                launchSingleTop = true
                            }
                        }
                    }
                )
            }
        }

        composable(route = Screen.CalendarScreen.route) {
            Column(modifier = Modifier.fillMaxSize()) {
                Box(modifier = Modifier.weight(1f)) {
                    CalendarScreen(onDayClick = { })
                }
                BottomNavBar(
                    currentRoute = currentRoute ?: "",
                    onNavigate = { route ->
                        if (route != currentRoute) {
                            navController.navigate("${Screen.Loading.route}/$route") {
                                launchSingleTop = true
                            }
                        }
                    }
                )
            }
        }

        composable(route = Screen.FriendScreen.route) {
            Column(modifier = Modifier.fillMaxSize()) {
                Box(modifier = Modifier.weight(1f)) {
                    FriendScreen(onBackPressed = { navController.navigate(Screen.HomeScreen.route) })
                }
                BottomNavBar(
                    currentRoute = currentRoute ?: "",
                    onNavigate = { route ->
                        if (route != currentRoute) {
                            navController.navigate("${Screen.Loading.route}/$route") {
                                launchSingleTop = true
                            }
                        }
                    }
                )
            }
        }

        composable(route = Screen.ProfileScreen.route) {
            Column(modifier = Modifier.fillMaxSize()) {
                Box(modifier = Modifier.weight(1f)) {
                    ProfileScreen(
                        onEditProfile = { navController.navigate(Screen.EditProfileScreen.route) },
                        onSettings = { navController.navigate(Screen.ConfigurationScreen.route) },
                        onHelp = { navController.navigate(Screen.HelpScreen.route) },
                        navigateToLogin = { navController.navigate("${Screen.Loading.route}/${Screen.Login.route}") }
                    )
                }
                BottomNavBar(
                    currentRoute = currentRoute ?: "",
                    onNavigate = { route ->
                        if (route != currentRoute) {
                            navController.navigate("${Screen.Loading.route}/$route") {
                                launchSingleTop = true
                            }
                        }
                    }
                )
            }
        }

        composable(route = Screen.ConfigurationScreen.route) {
            Column(modifier = Modifier.fillMaxSize()) {
                Box(modifier = Modifier.weight(1f)) {
                    ConfigurationScreen(onBackPressed = { navController.popBackStack() })
                }
            }
        }

        composable(route = Screen.EditProfileScreen.route) {
            EditProfileScreen(onBackPressed = { navController.popBackStack() })
        }

        composable(route = Screen.HelpScreen.route) {
            HelpScreen(onBackPressed = { navController.popBackStack() })
        }
    }
}
