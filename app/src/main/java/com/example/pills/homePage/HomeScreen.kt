package com.example.pills.homePage

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun HomeScreen(
    navigateToLogin: () -> Unit
) {
    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        HomeScreenUI()
    }
} 