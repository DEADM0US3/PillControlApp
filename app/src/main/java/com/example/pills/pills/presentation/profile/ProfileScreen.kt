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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.pills.R

@Composable
fun ProfileScreen(
    userName: String = "Laura Torres",
    onEditProfile: () -> Unit = {},
    onHelp: () -> Unit = {},
    onSettings: () -> Unit = {},
    onLogout: () -> Unit = {},
    isPetActive: Boolean = true,
    onPetActiveChange: (Boolean) -> Unit = {}
) {
    var petActive by remember { mutableStateOf(isPetActive) }
    Column(
        modifier = Modifier
            .fillMaxSize()
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
            // Puedes reemplazar painterResource(R.drawable.ic_profile) por tu imagen
            Image(
                painter = painterResource(id = R.drawable.ic_launcher_foreground),
                contentDescription = "Foto de perfil",
                modifier = Modifier.size(100.dp)
            )
        }
        Spacer(modifier = Modifier.height(16.dp))
        // Nombre
        Surface(
            color = Color(0xFFF48FB1),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text(
                text = userName,
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp,
                modifier = Modifier.padding(horizontal = 24.dp, vertical = 6.dp)
            )
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
            Text("Mascota Activa", color = Color.Black)
        }
        Spacer(modifier = Modifier.height(32.dp))
        // Botón cerrar sesión
        Button(
            onClick = onLogout,
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF48FB1)),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Cerrar sesión", color = Color.White, fontWeight = FontWeight.Bold)
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
            color = Color.Black,
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