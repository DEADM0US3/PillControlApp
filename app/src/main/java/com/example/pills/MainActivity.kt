package com.example.pills

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresPermission
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.example.pills.pills.navigation.AuthNavigation
import com.example.pills.pills.infrastructure.ViewModel.MainViewModel
import com.example.pills.ui.theme.AuthTheme
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.util.concurrent.TimeUnit


fun Context.hasInternetConnection(): Boolean {
    val connectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    val activeNetwork = connectivityManager.activeNetwork ?: return false
    val capabilities = connectivityManager.getNetworkCapabilities(activeNetwork) ?: return false
    return capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
}

class MainActivity : ComponentActivity() {
    private val mainViewModel: MainViewModel by viewModel()


    @RequiresPermission(
        allOf = [
            android.Manifest.permission.BLUETOOTH_SCAN,
            android.Manifest.permission.BLUETOOTH_CONNECT
        ]
    )


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                    1001
                )
            }
        }

        if (hasInternetConnection()) {
            setContent {
                AuthTheme(darkTheme = false, dynamicColor = false) {
                    val uiState by mainViewModel.uiState.collectAsState()

                    ToastDebug(mainViewModel = mainViewModel)

                    if (uiState.isLoading) {
                        LoadingScreen()
                    } else {
                        AuthNavigation(startDestination = uiState.startDestination)
                    }
                }
            }
        } else {
            setContent {
                AuthTheme(darkTheme = false, dynamicColor = false) {
                    NoConnectionScreen()
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


@Composable
fun NoConnectionScreen() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFD56A83)), // Fondo rosa
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                painter = painterResource(id = R.drawable.pillcontrollogo),
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(300.dp) // Ajusta tamaño si necesitas
            )
            Spacer(modifier = Modifier.height(24.dp))
            Text(
                text = "Sin conexión a internet",
                color = Color.White,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold
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


