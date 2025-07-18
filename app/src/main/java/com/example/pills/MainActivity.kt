package com.example.pills

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.pills.pills.navigation.AuthNavigation
import com.example.pills.pills.presentation.main.MainViewModel
import com.example.pills.ui.theme.AuthTheme
import org.koin.androidx.viewmodel.ext.android.viewModel

class MainActivity : ComponentActivity() {
    // Inject the MainViewModel via Koin.
    private val mainViewModel: MainViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AuthTheme(darkTheme = false, dynamicColor = false) {
                // Collect the UI state exposed by the ViewModel.
                val uiState by mainViewModel.uiState.collectAsState()

                ToastDebug(mainViewModel = mainViewModel)

                if (uiState.isLoading) {
                    LoadingScreen()
                } else {
                    AuthNavigation(startDestination = uiState.startDestination)
                }
            }
        }
    }
}



@Composable
fun LoadingScreen() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFD56A83)), // Fondo rosa similar al de tu imagen
        contentAlignment = Alignment.Center
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                painter = painterResource(id = R.drawable.pillcontrollogo), // Tu ícono de la izquierda
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(340.dp) // Ajusta según lo necesites
            )
        }
    }
}


/**
 * Composable that listens to the ViewModel’s toast events and shows a Toast message.
 */
@Composable
fun ToastDebug(mainViewModel: MainViewModel) {
    val context = LocalContext.current
    LaunchedEffect(Unit) {
        mainViewModel.toastMessageFlow.collect { message ->
            Toast.makeText(context, message, Toast.LENGTH_LONG).show()
        }
    }
}


