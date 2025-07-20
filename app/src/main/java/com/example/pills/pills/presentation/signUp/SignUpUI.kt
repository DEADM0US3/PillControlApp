package com.example.pills.pills.presentation.signUp

import org.koin.androidx.compose.koinViewModel
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.pills.R

@Composable
fun SignUpScreen(
    navigateToLogin: () -> Unit,
    navigateToOtp: (String) -> Unit,
) {
    val viewModel: SignUpViewModel = koinViewModel()
    var passwordVisible by remember { mutableStateOf(false) }
    val state = viewModel.stateFlow.collectAsState().value
    val context = LocalContext.current

    LaunchedEffect(key1 = context) {
        viewModel.validationEvents.collect { event ->
            if (event is SignUpViewModel.ValidationEvent.Success) {
                navigateToOtp(event.email)
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFD56A83))
            .padding(20.dp)
            .verticalScroll(rememberScrollState()),
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
            text = "Crear Cuenta",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = state.name,
            onValueChange = { viewModel.onEvent(SignUpFormEvent.NameChanged(it)) },
            isError = state.nameError != null,
            shape = CircleShape,
            label = { Text("Nombre Completo") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            colors = TextFieldDefaults.colors(
                unfocusedContainerColor = Color.Transparent,
                focusedContainerColor = Color.Transparent,
                cursorColor = Color(0xFFDF7A92),
                focusedIndicatorColor = Color(0xFFDF7A92),
                unfocusedIndicatorColor = Color(0xFFDF7A92),
                focusedLabelColor = Color(0xFFDF7A92),
                unfocusedLabelColor = Color.White,
                focusedTextColor = Color.White,      // Color del texto cuando el campo está enfocado
                unfocusedTextColor = Color.White     // Color del texto cuando el campo no está enfocado
            )
        )
        if (state.nameError != null) {
            Text(
                text = state.nameError,
                color = Color.White,
                modifier = Modifier.padding(horizontal = 22.dp),
                fontSize = 13.sp
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = state.email,
            onValueChange = { viewModel.onEvent(SignUpFormEvent.EmailChanged(it)) },
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
                focusedLabelColor = Color(0xFFDF7A92),
                unfocusedLabelColor = Color.White,
                focusedTextColor = Color.White,      // Color del texto cuando el campo está enfocado
                unfocusedTextColor = Color.White     // Color del texto cuando el campo no está enfocado
            )
        )
        if (state.emailError != null) {
            Text(
                text = state.emailError,
                color = Color.White,
                modifier = Modifier.padding(horizontal = 22.dp),
                fontSize = 13.sp
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = state.password,
            onValueChange = { viewModel.onEvent(SignUpFormEvent.PasswordChanged(it)) },
            isError = state.passwordError != null,
            shape = CircleShape,
            label = { Text("Contraseña") },
            trailingIcon = {
                val image = if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff
                IconButton(onClick = { passwordVisible = !passwordVisible }) {
                    Icon(imageVector = image, contentDescription = "Toggle Password Visibility", tint = Color.White)
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
                focusedLabelColor = Color(0xFFDF7A92),
                unfocusedLabelColor = Color.White,
                focusedTextColor = Color.White,      // Color del texto cuando el campo está enfocado
                unfocusedTextColor = Color.White     // Color del texto cuando el campo no está enfocado
            )
        )
        if (state.passwordError != null) {
            Text(
                text = state.passwordError,
                color = Color.White,
                modifier = Modifier.padding(horizontal = 22.dp),
                fontSize = 13.sp
            )
        }

        Spacer(modifier = Modifier.height(5.dp))

        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Checkbox(
                checked = state.acceptedTerms,
                onCheckedChange = { viewModel.onEvent(SignUpFormEvent.AcceptTerms(it)) },
                colors = CheckboxDefaults.colors(
                    checkedColor = Color.White,
                    uncheckedColor = Color(0xFF9C9EA1)
                )
            )
            Text(
                text = "Acepto los términos y condiciones",
                color = Color.White,
                fontSize = 14.sp,
                modifier = Modifier.clickable { }
            )
        }
        if (state.termsError != null) {
            Text(
                text = state.termsError,
                color = Color.White,
                modifier = Modifier.padding(horizontal = 22.dp),
                fontSize = 13.sp
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        Button(
            onClick = { viewModel.onEvent(SignUpFormEvent.Submit) },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF36F9D))
        ) {
            if (state.isLoading) {
                CircularProgressIndicator(
                    color = Color.White,
                    strokeWidth = 2.dp,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(10.dp))
            }
            Text(text = "Registrarse", color = Color.White, fontWeight = FontWeight.Bold)
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "¿Ya tienes una cuenta? Inicia Sesión",
            color = Color.White,
            modifier = Modifier.clickable { navigateToLogin() },
            fontWeight = FontWeight.Bold
        )
    }
}