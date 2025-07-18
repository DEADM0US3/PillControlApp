package com.example.pills.homePage

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import android.widget.Toast
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.pillcontrolapp.components.BottomNavigationBar
import com.example.pills.homePage.presentation.BLE.BLEScreen
import com.example.pills.homePage.presentation.profile.ProfileScreen
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel

@Composable
fun HomeScreen(
    navigateToLogin: () -> Unit
) {
    val navController = rememberNavController()

    Scaffold(
        bottomBar = {
            BottomNavigationBar(navController = navController)
        }
    ) { innerPadding ->
        NavHost(
            navController,
            startDestination = "home",
            Modifier.padding(innerPadding)
        ) {
            composable("home") {
                HomeView(navigateToLogin)
            }
            composable("search") {
                BLEScreen()
            }
            composable("profile") {
                ProfileScreen(navigateToLogin)
            }
        }

    }

}

@Composable
fun HomeView(
    navigateToLogin: () -> Unit,
    homeViewModel: HomeViewModel = koinViewModel()
)
{

    val uiState by homeViewModel.uiState.collectAsState()
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current

    // If there's an error message, show a Toast (or display it in the UI)
    uiState.errorMessage?.let { error ->
        Toast.makeText(LocalContext.current, error, Toast.LENGTH_LONG).show()
    }

    // Check if we're in a loading state
    if (uiState.isLoading) {
        // Show a centered loading indicator
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFFAFAFA)),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
    } else {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFFAFAFA))
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Welcome Message
            Text(
                text = "Welcome, ${uiState.userName}! 🎉",
                fontSize = 24.sp,
                color = Color.Black,
                style = MaterialTheme.typography.headlineLarge
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Visual Debug: Display the stored tokens
            Text(
                text = "Access Token: ${uiState.accessToken}",
                style = MaterialTheme.typography.bodyMedium
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Refresh Token: ${uiState.refreshToken}",
                style = MaterialTheme.typography.bodyMedium
            )

            Spacer(modifier = Modifier.height(30.dp))

            //Logout Button
            Button(
                onClick = {
                    coroutineScope.launch {
                        homeViewModel.logout {
                            navigateToLogin()
                        }
                    }
                },
                colors = ButtonDefaults.buttonColors(MaterialTheme.colorScheme.secondary)
            ) {
                Text("Logout", color = Color.White)
            }
        }
    }
}