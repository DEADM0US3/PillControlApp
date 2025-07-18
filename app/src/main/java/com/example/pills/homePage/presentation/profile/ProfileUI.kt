package com.example.pills.homePage.presentation.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Icon
import androidx.compose.material.Switch
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Person
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun ProfileScreen(
//    navigateToEditProfile: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(Modifier.height(32.dp))

        // Imagen de perfil
        Box(
            modifier = Modifier
                .size(120.dp)
                .clip(CircleShape)
                .background(Color(0xFFE0E0E0)),
            contentAlignment = Alignment.Center
        ) {
            Icon(Icons.Default.Person, contentDescription = null, tint = Color.White, modifier = Modifier.size(60.dp))
        }

        Spacer(Modifier.height(16.dp))

        // Nombre de usuario
        Button(
            onClick = {},
            colors = ButtonDefaults.buttonColors(contentColor = Color(0xFFF36F9D)),
            shape = RoundedCornerShape(20.dp)
        ) {
            Text("Laura Torres", color = Color.White)
        }

        Spacer(Modifier.height(24.dp))

        // Opciones
//        ProfileOption("Editar perfil") { navigateToEditProfile() }
        ProfileOption("Ayuda") { /* Acción */ }
        ProfileOption("Configuración") { /* Acción */ }

        Spacer(Modifier.height(16.dp))

        // Switch
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Mascota Activa", modifier = Modifier.weight(1f))
            Switch(checked = true, onCheckedChange = {})
        }

        Spacer(Modifier.weight(1f))

        // Botón Cerrar sesión
        Button(
            onClick = { /* Logout */ },
            colors = ButtonDefaults.buttonColors(contentColor = Color(0xFFF36F9D)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Cerrar sesión", color = Color.White)
        }
    }
}

@Composable
fun ProfileOption(title: String, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .clickable{ onClick() },
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(title, modifier = Modifier.weight(1f))
        Icon(Icons.Default.KeyboardArrowRight, contentDescription = null)
    }
}
