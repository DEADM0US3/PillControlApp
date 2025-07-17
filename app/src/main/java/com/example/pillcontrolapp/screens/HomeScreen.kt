package com.example.pillcontrolapp.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController


@Composable
fun HomeScreen(navController: NavHostController) {
    Column(
        modifier = Modifier
            .padding(30.dp, 50.dp)
    ) {
        Box(
            modifier = Modifier
                .background(Color(0xFFF36F9D), shape = RoundedCornerShape(12.dp))
                .padding(40.dp, 4.dp)
        ) {
            Text(
                "Hola, Laura",
                fontSize = 20.sp,
                color = Color.White
            )
        }

        Spacer(modifier = Modifier.height(6.dp))

        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(12.dp))
                .fillMaxWidth()
                .border(2.dp, Color(0xFFDF7A92), shape = RoundedCornerShape(12.dp))
                .padding(16.dp)

        ) {

            Row ()
            {
                Text(
                    "Hola, Laura",
                    fontSize = 20.sp,
                    color = Color.Black
                )
            }

        }

    }
}

