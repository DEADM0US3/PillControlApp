package com.example.pills.pills.presentation.configuration

import android.Manifest
import android.bluetooth.BluetoothDevice
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.annotation.RequiresPermission
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.example.pills.pills.configuration.BluetoothViewModel
import org.koin.androidx.compose.koinViewModel

private val primaryGradientColors = listOf(Color(0xFFF48FB1), Color(0xFFFCE4EC))
private val accentColor = Color(0xFFFF6B9D)
private val errorBackground = Color(0xFFFF5252).copy(alpha = 0.1f)
private val errorTextColor = Color(0xFFFF5252)


@RequiresApi(Build.VERSION_CODES.S)
@RequiresPermission(
    allOf = [
        android.Manifest.permission.BLUETOOTH_SCAN,
        android.Manifest.permission.BLUETOOTH_CONNECT
    ]
)
@Composable
fun ConfigurationScreen(
    onBackPressed: () -> Unit,
    viewModel: BluetoothViewModel = koinViewModel()
) {
    val context = LocalContext.current

    val connectionState by viewModel.connectionState.collectAsState()

    var permissionsGranted by remember { mutableStateOf(false) }

    val devices = remember { mutableStateListOf<BluetoothDevice>() }

    val permissionsLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissionsMap ->
        permissionsGranted = permissionsMap.entries.all { it.value }
        if (!permissionsGranted) {
            Log.w("ConfigurationScreen", "Permisos BLE no otorgados")
        } else {
            viewModel.startScan(devices)
        }
    }

    LaunchedEffect(Unit) {
        permissionsGranted = checkBlePermissions(context)
        Log.d("ConfigurationScreen", "Permisos otorgados: $permissionsGranted")

        if (permissionsGranted) {
            Log.d("ConfigurationScreen", "Iniciando escaneo de dispositivos...")
            viewModel.startScan(devices)
        } else {
            Log.d("ConfigurationScreen", "Solicitando permisos...")
            permissionsLauncher.launch(
                arrayOf(
                    Manifest.permission.BLUETOOTH_SCAN,
                    Manifest.permission.BLUETOOTH_CONNECT,
                    Manifest.permission.ACCESS_FINE_LOCATION
                )
            )
        }
    }

    LaunchedEffect(devices.size) {
        Log.d("ConfigurationScreen", "Dispositivos actuales (${devices.size}):")
        devices.forEach {
            Log.d("ConfigurationScreen", " - ${it.name} - ${it.address}")
        }
    }


    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(primaryGradientColors)
                )
                .padding(16.dp)
        ) {

            ModernHeader(onBackPressed)

            Text(
                "Dispositivos Encontrados",
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                ),
                modifier = Modifier.padding(bottom = 12.dp)
            )

            AnimatedVisibility(
                visible = !permissionsGranted,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = errorBackground),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Text(
                        text = "Necesitas otorgar permisos para buscar dispositivos Bluetooth",
                        modifier = Modifier.padding(16.dp),
                        color = errorTextColor,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }

            AnimatedVisibility(
                visible = permissionsGranted && devices.isEmpty(),
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                Text(
                    "Escaneando dispositivos...",
                    style = MaterialTheme.typography.bodyMedium.copy(color = Color.White),
                    modifier = Modifier.padding(vertical = 16.dp)
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
                Text("Estado: $connectionState", style = MaterialTheme.typography.bodyMedium)

                Spacer(modifier = Modifier.height(8.dp))

                LazyColumn(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    itemsIndexed(devices) { _, device ->
                        DeviceItem(device = device, onConnectClick = {
                            viewModel.connectToDevice(it)
                        })
                    }
                }
            }
        }
    }
}


@Composable
fun ModernHeader(onBackPressed: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp) // Padding más equilibrado
            .statusBarsPadding(), // Asegura que no se superponga con la barra de estado
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White.copy(alpha = 0.15f)
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp), // Padding interno consistente
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = onBackPressed,
                modifier = Modifier
                    .size(48.dp)
                    .background(
                        Color.White.copy(alpha = 0.2f),
                        CircleShape
                    )
            ) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Regresar",
                    tint = Color.White,
                    modifier = Modifier.size(24.dp)
                )
            }
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                text = "Configuración",
                color = Color.White,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@RequiresPermission(
    allOf = [
        Manifest.permission.BLUETOOTH_SCAN,
        Manifest.permission.BLUETOOTH_CONNECT
    ]
)
@Composable
fun DeviceItem(device: BluetoothDevice, onConnectClick: (BluetoothDevice) -> Unit = {}) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(6.dp, RoundedCornerShape(16.dp)),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.85f))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = device.name ?: "Unknown",
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.bodyLarge,
                    color = Color.Black
                )
                Text(
                    text = device.address,
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.DarkGray
                )
            }
            Button(
                onClick = { onConnectClick(device) },
                shape = RoundedCornerShape(24.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFF48FB1) // color rosa suave
                ),
                modifier = Modifier.height(36.dp)
            ) {
                Text(
                    text = "Conectar",
                    color = Color.White,
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.S)
fun checkBlePermissions(context: Context): Boolean {
    return ContextCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH_SCAN) == PackageManager.PERMISSION_GRANTED &&
            ContextCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH_CONNECT) == PackageManager.PERMISSION_GRANTED &&
            ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
}
