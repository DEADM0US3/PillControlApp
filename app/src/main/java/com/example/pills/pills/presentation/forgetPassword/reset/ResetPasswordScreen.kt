package com.example.pills.pills.presentation.forgetPassword.reset

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.pills.R
import org.koin.androidx.compose.koinViewModel

@Composable
fun ForgetPasswordScreen(
    navigateToLogin: () -> Unit,
    navigateToOtp: (String) -> Unit
) {
    val viewModel: ResetPasswordViewModel = koinViewModel()
    val state = viewModel.stateFlow.collectAsState().value
    val context = LocalContext.current

    LaunchedEffect(key1 = context) {
        viewModel.validationEvents.collect { event ->
            if (event is ResetPasswordViewModel.ValidationEvent.Success) {
                Toast.makeText(context, "OTP enviado correctamente", Toast.LENGTH_SHORT).show()
                navigateToOtp(event.email)
            }
        }
    }

    Scaffold(
        topBar = {
            Column {
                Spacer(modifier = Modifier.height(12.dp))
                IconButton(
                    onClick = { navigateToLogin() }
                ) {
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
                text = "Restablecer contraseña",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
            Spacer(modifier = Modifier.height(16.dp))
            OutlinedTextField(
                value = state.email,
                onValueChange = { viewModel.onEvent(ForgetPasswordFormEvent.EmailChanged(it)) },
                isError = state.emailError != null,
                shape = CircleShape,
                label = { Text("Correo Electrónico") },
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
            if (state.emailError != null) {
                Text(
                    text = state.emailError,
                    color = Color.White,
                    modifier = Modifier.padding(horizontal = 22.dp),
                    fontSize = 14.sp
                )
            }
            Spacer(modifier = Modifier.height(24.dp))
            Button(
                onClick = { viewModel.onEvent(ForgetPasswordFormEvent.Submit) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF36F9D))
            ) {
                if (state.isLoading == true) {
                    CircularProgressIndicator(
                        strokeWidth = 2.dp,
                        color = Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                }
                Text(text = "Enviar OTP", color = Color.White, fontWeight = FontWeight.Bold)
            }
        }
    }
}
