package com.example.pills.pills.presentation.components

import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Person

@Composable
fun BottomNavBar(
    currentRoute: String,
    onNavigate: (String) -> Unit
) {
    NavigationBar {
        NavigationBarItem(
            selected = currentRoute == "homeScreen",
            onClick = { onNavigate("homeScreen") },
            icon = { Icon(Icons.Filled.Home, contentDescription = "Inicio") },
            label = { Text("Inicio") }
        )
        NavigationBarItem(
            selected = currentRoute == "calendarScreen",
            onClick = { onNavigate("calendarScreen") },
            icon = { Icon(Icons.Filled.CalendarToday, contentDescription = "Calendario") },
            label = { Text("Calendario") }
        )
        NavigationBarItem(
            selected = currentRoute == "profileScreen",
            onClick = { onNavigate("profileScreen") },
            icon = { Icon(Icons.Filled.Person, contentDescription = "Perfil") },
            label = { Text("Perfil") }
        )
    }
}
