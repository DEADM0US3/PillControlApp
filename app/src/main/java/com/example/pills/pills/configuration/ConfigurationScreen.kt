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
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
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
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
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
        Manifest.permission.BLUETOOTH_SCAN,
        Manifest.permission.BLUETOOTH_CONNECT
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
        if (permissionsGranted) viewModel.startScan(devices)
        else Log.w("ConfigScreen", "Permisos BLE no otorgados")
    }

    LaunchedEffect(Unit) {
        permissionsGranted = checkBlePermissions(context)
        if (permissionsGranted) viewModel.startScan(devices)
        else permissionsLauncher.launch(
            arrayOf(
                Manifest.permission.BLUETOOTH_SCAN,
                Manifest.permission.BLUETOOTH_CONNECT,
                Manifest.permission.ACCESS_FINE_LOCATION
            )
        )
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Brush.verticalGradient(primaryGradientColors))
                .padding(16.dp)
        ) {
            ModernHeader(onBackPressed)

            Spacer(Modifier.height(24.dp))

            Text(
                text = "Dispositivos Encontrados",
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
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    LoadingPill()
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        "Escaneando dispositivos...",
                        style = MaterialTheme.typography.bodyMedium.copy(color = Color.White)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    "Estado: $connectionState",
                    style = MaterialTheme.typography.bodyMedium.copy(color = Color.White)
                )

                Spacer(modifier = Modifier.height(12.dp))

                LazyColumn(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    contentPadding = PaddingValues(bottom = 16.dp)
                ) {
                    itemsIndexed(devices) { _, device ->
                        PillDeviceItem(device = device, onConnectClick = { viewModel.connectToDevice(it) })
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Recuerda tomar tu pastilla a tiempo 💊",
                    color = accentColor.copy(alpha = 0.9f),
                    style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))


            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Controla tu ciclo y mantén el ritmo perfecto con tu pastilla diaria.",
                color = Color.White.copy(alpha = 0.7f),
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 32.dp, vertical = 8.dp)
            )
        }
    }
}

@Composable
fun ModernHeader(onBackPressed: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .statusBarsPadding(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White.copy(alpha = 0.15f)
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
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
fun PillDeviceItem(device: BluetoothDevice, onConnectClick: (BluetoothDevice) -> Unit = {}) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(70.dp)
            .shadow(4.dp, RoundedCornerShape(16.dp)),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.95f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp, 40.dp)
                    .shadow(2.dp, RoundedCornerShape(20.dp))
                    .background(
                        brush = Brush.horizontalGradient(
                            colors = listOf(accentColor, Color.White)
                        ),
                        shape = RoundedCornerShape(20.dp)
                    )
            )

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = device.name ?: "Dispositivo desconocido",
                    fontWeight = FontWeight.Medium,
                    style = MaterialTheme.typography.titleMedium,
                    color = Color(0xFF5A3E47)
                )
                Text(
                    text = device.address,
                    style = MaterialTheme.typography.bodySmall,
                    color = Color(0xFF957D85)
                )
            }

            Button(
                onClick = { onConnectClick(device) },
                shape = RoundedCornerShape(20.dp),
                colors = ButtonDefaults.buttonColors(containerColor = accentColor),
                modifier = Modifier.height(36.dp).width(100.dp),
                elevation = ButtonDefaults.buttonElevation(defaultElevation = 6.dp)
            ) {
                Text(
                    text = "Conectar",
                    color = Color.White,
                    fontWeight = FontWeight.Medium,
                    style = MaterialTheme.typography.labelLarge
                )
            }
        }
    }
}

@Composable
fun LoadingPill(modifier: Modifier = Modifier) {
    val infiniteTransition = rememberInfiniteTransition()
    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1500, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        )
    )

    androidx.compose.foundation.Canvas(
        modifier = modifier
            .size(60.dp)
            .padding(4.dp)
    ) {
        withTransform({
            rotate(rotation, pivot = center)
        }) {
            val pillWidth = size.width
            val pillHeight = size.height * 0.6f
            val pillRect = Rect(
                left = 0f,
                top = (size.height - pillHeight) / 2,
                right = pillWidth,
                bottom = (size.height + pillHeight) / 2
            )
            // Mitad izquierda rosa fuerte
            drawRoundRect(
                color = accentColor,
                topLeft = pillRect.topLeft,
                size = pillRect.size.copy(width = pillRect.width / 2, height = pillRect.height),
                cornerRadius = CornerRadius(pillHeight / 2, pillHeight / 2),
                style = Fill
            )
            // Mitad derecha blanco
            drawRoundRect(
                color = Color.White,
                topLeft = pillRect.topLeft.copy(x = pillRect.left + pillRect.width / 2),
                size = pillRect.size.copy(width = pillRect.width / 2, height = pillRect.height),
                cornerRadius = CornerRadius(pillHeight / 2, pillHeight / 2),
                style = Stroke(width = 2f)
            )
            // Borde negro sutil
            drawRoundRect(
                color = Color.Black.copy(alpha = 0.1f),
                topLeft = pillRect.topLeft,
                size = pillRect.size,
                cornerRadius = CornerRadius(pillHeight / 2, pillHeight / 2),
                style = Stroke(width = 2f)
            )
        }
    }
}

@RequiresApi(Build.VERSION_CODES.S)
fun checkBlePermissions(context: Context): Boolean {
    return ContextCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH_SCAN) == PackageManager.PERMISSION_GRANTED &&
            ContextCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH_CONNECT) == PackageManager.PERMISSION_GRANTED &&
            ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
}
