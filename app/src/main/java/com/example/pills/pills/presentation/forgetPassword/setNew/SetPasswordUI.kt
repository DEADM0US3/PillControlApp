package com.example.pills.pills.presentation.forgetPassword.setNew

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.pills.R
import org.koin.androidx.compose.koinViewModel

@Composable
fun SetPasswordScreen(
    navigateToLogin: () -> Unit,
    navigateToReset: () -> Unit
) {
    var passwordVisible by remember { mutableStateOf(false) }
    var confirmPasswordVisible by remember { mutableStateOf(false) }

    val viewModel: SetPasswordViewModel = koinViewModel()
    val state = viewModel.state.collectAsState().value
    val context = LocalContext.current

    LaunchedEffect(key1 = context) {
        viewModel.validationEvents.collect { event ->
            if (event is SetPasswordViewModel.ValidationEvent.Success) {
                val prefs = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
                prefs.edit().remove("pending_reset_password").apply()
                Toast.makeText(context, "Contraseña restablecida con éxito", Toast.LENGTH_SHORT).show()
                navigateToLogin()
            }
        }
    }

    Scaffold(
        topBar = {
            Column {
                Spacer(modifier = Modifier.height(12.dp))
                IconButton(onClick = { navigateToReset() }) {
                    Icon(
                        imageVector = Icons.Default.ArrowBackIosNew,
                        contentDescription = "Back Arrow",
                        tint = Color.White
                    )
                }
            }
        },
        containerColor = Color(0xFFD56A83)
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                painter = painterResource(id = R.drawable.pillcontrollogo),
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(200.dp)
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Nueva contraseña",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
            Spacer(modifier = Modifier.height(16.dp))
            OutlinedTextField(
                value = state.password,
                onValueChange = { viewModel.onEvent(SetPasswordFormEvent.PasswordChanged(it)) },
                isError = state.passwordError != null,
                shape = CircleShape,
                label = { Text("Contraseña") },
                trailingIcon = {
                    val image = if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff
                    IconButton(onClick = { passwordVisible = !passwordVisible }) {
                        Icon(imageVector = image, contentDescription = "Toggle Password Visibility")
                    }
                },
                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                colors = TextFieldDefaults.colors(
                    unfocusedContainerColor = Color.Transparent,
                    focusedContainerColor = Color.Transparent,
                    cursorColor = Color(0xFFDF7A92),
                    focusedIndicatorColor = Color(0xFFDF7A92),
                    unfocusedIndicatorColor = Color(0xFFDF7A92),
                    focusedLabelColor = Color(0xFFDF7A92)
                )
            )
            if (state.passwordError != null) {
                Text(
                    text = state.passwordError,
                    color = Color.White,
                    fontSize = 13.sp,
                    modifier = Modifier.padding(start = 16.dp)
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
            OutlinedTextField(
                value = state.confirmPassword,
                onValueChange = { viewModel.onEvent(SetPasswordFormEvent.ConfirmPasswordChanged(it)) },
                isError = state.confirmPasswordError != null,
                shape = CircleShape,
                label = { Text("Confirmar Contraseña") },
                trailingIcon = {
                    val image = if (confirmPasswordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff
                    IconButton(onClick = { confirmPasswordVisible = !confirmPasswordVisible }) {
                        Icon(imageVector = image, contentDescription = "Toggle Password Visibility")
                    }
                },
                visualTransformation = if (confirmPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                colors = TextFieldDefaults.colors(
                    unfocusedContainerColor = Color.Transparent,
                    focusedContainerColor = Color.Transparent,
                    cursorColor = Color(0xFFDF7A92),
                    focusedIndicatorColor = Color(0xFFDF7A92),
                    unfocusedIndicatorColor = Color(0xFFDF7A92),
                    focusedLabelColor = Color(0xFFDF7A92)
                )
            )
            if (state.confirmPasswordError != null) {
                Text(
                    text = state.confirmPasswordError,
                    color = Color.White,
                    fontSize = 13.sp,
                    modifier = Modifier.padding(start = 16.dp)
                )
            }
            Spacer(modifier = Modifier.height(27.dp))
            Button(
                onClick = { viewModel.onEvent(SetPasswordFormEvent.Submit) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF36F9D))
            ) {
                Text("Restablecer contraseña", color = Color.White, fontWeight = FontWeight.Bold)
            }
        }
    }
}