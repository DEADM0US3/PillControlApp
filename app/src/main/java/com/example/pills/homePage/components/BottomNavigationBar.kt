package com.example.pillcontrolapp.components

import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState

@Composable
fun BottomNavigationBar(navController: NavHostController) {
    val items = listOf(
        NavigationItem("home", Icons.Default.Home, "Inicio"),
        NavigationItem("search", Icons.Default.DateRange, "Calendario"),
        NavigationItem("profile", Icons.Default.Person, "Perfil")
    )

    BottomNavigation(
        backgroundColor = Color.White // Color de fondo opcional
    ) {
        val currentDestination = navController.currentBackStackEntryAsState().value?.destination?.route
        items.forEach { item ->
            val isSelected = currentDestination == item.route

            BottomNavigationItem(
                icon = { Icon(item.icon, contentDescription = item.label, tint = if (item.route == "clock") Color.White else if (isSelected) Color(0xFFF36F9D) else Color.Gray) },
                label = { Text(item.label, color = if (item.route == "clock") Color.White else if (isSelected) Color(0xFFF36F9D) else Color.Gray) },
                selected = isSelected,
                onClick = {
                    navController.navigate(item.route) {
                        popUpTo(navController.graph.startDestinationId) { saveState = true }
                        launchSingleTop = true
                        restoreState = true
                    }
                },
                alwaysShowLabel = true,
                modifier = Modifier
                    .padding(10.dp)
            )
        }
    }
}

data class NavigationItem(val route: String, val icon: ImageVector, val label: String)
