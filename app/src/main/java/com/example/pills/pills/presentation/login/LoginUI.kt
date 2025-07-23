package com.example.pills.pills.presentation.login

import android.widget.Toast
import androidx.activity.compose.LocalActivity
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.wear.compose.material.ScalingLazyColumn
import androidx.wear.compose.material.ScalingLazyListState
import androidx.wear.compose.material.rememberScalingLazyListState
import com.example.pills.Logger
import com.example.pills.R
import org.koin.androidx.compose.koinViewModel

@Composable
fun LoginScreenWear(
    navigateToHome: () -> Unit
) {
    var passwordVisible by remember { mutableStateOf(false) }
    val viewModel: LoginViewModel = koinViewModel()
    val state = viewModel.state
    val context = LocalContext.current
    val activity = LocalActivity.current ?: throw IllegalStateException("LoginScreen must be hosted in an Activity")

    LaunchedEffect(Unit) {
        viewModel.validationEvents.collect { event ->
            when (event) {
                is LoginViewModel.ValidationEvent.Success -> {
                    Toast.makeText(context, "Login successful", Toast.LENGTH_SHORT).show()
                    navigateToHome()
                }
                is LoginViewModel.ValidationEvent.EmailNotVerified -> {
                    Toast.makeText(context, "Email not verified. OTP sent.", Toast.LENGTH_LONG).show()
//                    navigateToOtp(event.email)
                }
                is LoginViewModel.ValidationEvent.Failure -> {
                    Logger.e("LoginUI.kt", "Error: ${event.error}")
                    Toast.makeText(context, "Error: ${event.error}", Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    val listState: ScalingLazyListState = rememberScalingLazyListState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFD56A83))
    ) {
        ScalingLazyColumn(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top,
            state = listState,
            contentPadding = PaddingValues(12.dp)
        ) {
            item {
                Text(
                    text = "Iniciar Sesión",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }

            item {
                Spacer(modifier = Modifier.height(8.dp))
            }

            item {
                OutlinedTextField(
                    value = state.email,
                    onValueChange = { viewModel.onEvent(LoginFormEvent.EmailChanged(it)) },
                    isError = state.emailError != null,
                    shape = CircleShape,
                    label = { Text("Usuario") },
                    modifier = Modifier
                        .fillMaxWidth(0.9f),
                    singleLine = true,
                    colors = TextFieldDefaults.colors(
                        unfocusedContainerColor = Color.Transparent,
                        focusedContainerColor = Color.Transparent,
                        cursorColor = Color(0xFFDF7A92),
                        focusedIndicatorColor = Color(0xFFDF7A92),
                        unfocusedIndicatorColor = Color(0xFFDF7A92),
                        focusedLabelColor = Color(0xFFF6F0F1),
                        unfocusedLabelColor = Color.White,
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White
                    )
                )
            }

            if (state.emailError != null) {
                item {
                    Text(
                        text = state.emailError,
                        color = Color.White,
                        fontSize = 12.sp,
                        modifier = Modifier.fillMaxWidth(0.9f)
                    )
                }
            }

            item {
                Spacer(modifier = Modifier.height(6.dp))
            }

            item {
                OutlinedTextField(
                    value = state.password,
                    onValueChange = { viewModel.onEvent(LoginFormEvent.PasswordChanged(it)) },
                    isError = state.passwordError != null,
                    shape = CircleShape,
                    label = { Text("Contraseña") },
                    trailingIcon = {
                        val icon = if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff
                        IconButton(onClick = { passwordVisible = !passwordVisible }) {
                            Icon(
                                imageVector = icon,
                                contentDescription = "Toggle Password Visibility",
                                tint = Color.White
                            )
                        }
                    },
                    visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    modifier = Modifier.fillMaxWidth(0.9f),
                    singleLine = true,
                    colors = TextFieldDefaults.colors(
                        unfocusedContainerColor = Color.Transparent,
                        focusedContainerColor = Color.Transparent,
                        cursorColor = Color(0xFFDF7A92),
                        focusedIndicatorColor = Color(0xFFDF7A92),
                        unfocusedIndicatorColor = Color(0xFFDF7A92),
                        focusedLabelColor = Color(0xFFF6F0F1),
                        unfocusedLabelColor = Color.White,
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White
                    )
                )
            }

            if (state.passwordError != null) {
                item {
                    Text(
                        text = state.passwordError,
                        color = Color.White,
                        fontSize = 12.sp,
                        modifier = Modifier.fillMaxWidth(0.9f)
                    )
                }
            }

            item {
                Spacer(modifier = Modifier.height(10.dp))
            }

            item {
                Button(
                    onClick = { viewModel.onEvent(LoginFormEvent.Submit) },
                    modifier = Modifier.fillMaxWidth(0.8f),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF36F9D))
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "Entrar",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                        if (state.isLoading) {
                            Spacer(modifier = Modifier.width(6.dp))
                            CircularProgressIndicator(
                                color = Color.White,
                                strokeWidth = 2.dp,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}
