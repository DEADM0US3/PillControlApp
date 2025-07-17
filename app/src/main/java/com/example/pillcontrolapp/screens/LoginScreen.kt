package com.example.pillcontrolapp.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.pillcontrolapp.R
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.pillcontrolapp.viewModels.SupabaseAuthViewModel

@Composable
fun LoginScreen(navController: NavController,
                onLoginSuccess: () -> Unit = {},
                viewModel: SupabaseAuthViewModel = viewModel()
) {
    val context = LocalContext.current
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFD56A83))
            .padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top,
    ) {
        Icon(
            painter = painterResource(id = R.drawable.pillcontrollogo),
            contentDescription = null,
            tint = Color.White,
            modifier = Modifier.size(260.dp)
        )

        Spacer(modifier = Modifier.height(0.dp))

        Text("Iniciar Sesión",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White,

            )

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                painter = painterResource(id = R.drawable.user_logo), // Tu ícono de la izquierda
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(26.dp) // Ajusta según lo necesites
            )

            Spacer(modifier = Modifier.width(16.dp))

            TextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Usuario") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,

                )
        }

        Spacer(modifier = Modifier.height(8.dp))

        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                painter = painterResource(id = R.drawable.password_logo), // Tu ícono de la izquierda
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(26.dp) // Ajusta según lo necesites
            )

            Spacer(modifier = Modifier.width(16.dp))

            TextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Contraseña") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                visualTransformation = PasswordVisualTransformation(),
                colors = TextFieldDefaults.colors(
                    cursorColor = Color(0xFFDF7A92),
                    focusedIndicatorColor = Color(0xFFDF7A92),
                    unfocusedIndicatorColor = Color(0xFFDF7A92),
                    focusedLabelColor = Color(0xFFDF7A92))
            )

        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                if (email.isNotBlank() && password.isNotBlank()) {
                    viewModel.login(
                        context,
                        userEmail =  email,
                        userPassword = password,
                    )

                    errorMessage = null
                    onLoginSuccess()
                } else {
                    errorMessage = "Completa ambos campos."
                }
            },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF36F9D))
        ) {
            Text("Entrar")
        }

        errorMessage?.let {
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = it, color = Color.Red)
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = { navController.navigate("register") }) {
            Text("¿No tienes cuenta Registrate Aquí?")
        }
    }
}