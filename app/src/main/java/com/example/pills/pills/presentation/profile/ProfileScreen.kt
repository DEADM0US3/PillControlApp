package com.example.pills.pills.presentation.profile

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.platform.LocalContext
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.pills.R
import com.example.pills.homePage.HomeViewModel
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel

@Composable
fun ProfileScreen(
    onEditProfile: () -> Unit = {},
    onHelp: () -> Unit = {},
    onSettings: () -> Unit = {},
    navigateToLogin: () -> Unit = {},
    isPetActive: Boolean = true,
    onPetActiveChange: (Boolean) -> Unit = {},
    homeViewModel: HomeViewModel = koinViewModel()
) {
    val homeState by homeViewModel.uiState.collectAsState()
    var petActive by remember { mutableStateOf(isPetActive) }
    val coroutineScope = rememberCoroutineScope()

    // Manejo de errores
    LaunchedEffect(homeState.errorMessage) {
        homeState.errorMessage?.let { error ->
            // Aquí podrías mostrar un Toast o Snackbar con el error
            // Por ahora solo lo ignoramos para evitar crasheos
        }
    }

    if (homeState.isLoading) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color(0xFFF48FB1),
                            Color(0xFFFCE4EC)
                        )
                    )
                ),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator(
                color = Color.White,
                modifier = Modifier.size(48.dp)
            )
        }
    } else if (homeState.userName == "Guest" && homeState.userEmail.isEmpty()) {
        // Usuario no autenticado
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color(0xFFF48FB1),
                            Color(0xFFFCE4EC)
                        )
                    )
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "Usuario no autenticado",
                color = Color.White,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
        }
    } else {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color(0xFFF48FB1), // Mismo color que CalendarScreen
                            Color(0xFFFCE4EC)  // Mismo color que CalendarScreen
                        )
                    )
                )
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
        Spacer(modifier = Modifier.height(32.dp))
        // Imagen de perfil
        Box(
            modifier = Modifier
                .size(120.dp)
                .clip(CircleShape)
                .background(Color(0xFFB3E5FC)),
            contentAlignment = Alignment.Center
        ) {
            // Mostrar imagen de perfil real si existe, sino imagen por defecto
            if (!homeState.profileImageUrl.isNullOrBlank()) {
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(homeState.profileImageUrl)
                        .crossfade(true)
                        .build(),
                    contentDescription = "Foto de perfil",
                    modifier = Modifier.fillMaxSize(),
                    error = painterResource(id = R.drawable.ic_launcher_foreground),
                    onError = {
                        // Manejo de error de carga de imagen
                    }
                )
            } else {
                Image(
                    painter = painterResource(id = R.drawable.ic_launcher_foreground),
                    contentDescription = "Foto de perfil",
                    modifier = Modifier.size(100.dp)
                )
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
        // Nombre
        Surface(
            color = Color(0xFFF48FB1),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text(
                text = homeState.userName.trim('"'),
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp,
                modifier = Modifier.padding(horizontal = 24.dp, vertical = 6.dp)
            )
        }

        // Información adicional del usuario
        if (homeState.userEmail.isNotBlank() || !homeState.userPhone.isNullOrBlank() || !homeState.userAge.isNullOrBlank()) {
            Spacer(modifier = Modifier.height(16.dp))
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = Color.White.copy(alpha = 0.9f)
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    if (homeState.userEmail.isNotBlank()) {
                        UserInfoRow("Email", homeState.userEmail)
                    }
                    if (!homeState.userPhone.isNullOrBlank()) {
                        UserInfoRow("Teléfono", homeState.userPhone ?: "")
                    }
                    if (!homeState.userAge.isNullOrBlank()) {
                        UserInfoRow("Edad", "${homeState.userAge} años")
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
        // Opciones
        ProfileOption(text = "Editar perfil", onClick = onEditProfile)
        ProfileOption(text = "Ayuda", onClick = onHelp)
        ProfileOption(text = "Configuración", onClick = onSettings)
        Spacer(modifier = Modifier.height(24.dp))
        // Switch Mascota Activa
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Switch(
                checked = petActive,
                onCheckedChange = {
                    petActive = it
                    onPetActiveChange(it)
                },
                colors = SwitchDefaults.colors(
                    checkedThumbColor = Color(0xFFF48FB1),
                    checkedTrackColor = Color(0xFFF8BBD0)
                )
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text("Mascota Activa", color = Color.White)
        }
        Spacer(modifier = Modifier.height(32.dp))
        // Botón cerrar sesión
        Button(
            onClick = {
                coroutineScope.launch {
                    homeViewModel.logout {
                        navigateToLogin()
                    }
                }
            },
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF48FB1)),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Cerrar sesión", color = Color.White, fontWeight = FontWeight.Bold)
        }
    }
    }
}

@Composable
fun ProfileOption(text: String, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = text,
            color = Color.White,
            fontSize = 16.sp,
            modifier = Modifier.weight(1f)
        )
        Text(
            text = ">",
            color = Color.Gray,
            fontSize = 18.sp
        )
    }
    Divider(color = Color.LightGray, thickness = 1.dp)
}

@Composable
fun UserInfoRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "$label:",
            color = Color.Gray,
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.width(80.dp)
        )
        Text(
            text = value,
            color = Color.Black,
            fontSize = 14.sp,
            modifier = Modifier.weight(1f)
        )
    }
}