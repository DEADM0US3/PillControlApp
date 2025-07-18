package com.example.pills.pills.presentation.otpVerification

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.pills.R
import org.koin.androidx.compose.koinViewModel
import kotlin.collections.none

@Composable
fun OtpVerificationScreen(
    email: String,
    flow: String,
    navigateAfterOtp: () -> Unit,
    onBackPressed: () -> Unit
) {
    val viewModel: OtpViewModel = koinViewModel()
    val state by viewModel.state.collectAsStateWithLifecycle()
    val focusRequesters = remember { List(6) { FocusRequester() } }
    val focusManager = LocalFocusManager.current
    val keyboardManager = LocalSoftwareKeyboardController.current
    val context = LocalContext.current

    LaunchedEffect(state.focusedIndex) {
        state.focusedIndex?.let { index ->
            focusRequesters.getOrNull(index)?.requestFocus()
        }
    }

    LaunchedEffect(state.code, keyboardManager) {
        val allNumbersEntered = state.code.none { it == null }
        if (allNumbersEntered) {
            focusRequesters.forEach { it.freeFocus() }
            focusManager.clearFocus()
            keyboardManager?.hide()
        }
    }

    LaunchedEffect(key1 = flow) {
        if (flow == "reset") {
            val prefs = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
            prefs.edit().putBoolean("pending_reset_password", true).apply()
        }
    }

    Scaffold(
        topBar = {
            Column {
                Spacer(modifier = Modifier.height(12.dp))
                IconButton(onClick = { onBackPressed() }) {
                    Icon(
                        imageVector = Icons.Default.ArrowBackIosNew,
                        contentDescription = "Back Arrow",
                        tint = Color.White
                    )
                }
            }
        },
        containerColor = Color(0xFFD56A83)
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp, vertical = innerPadding.calculateTopPadding()),
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
                text = "Código de verificación",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Revisa tu correo $email para ver el código de verificación.",
                fontSize = 14.sp,
                color = Color.White
            )
            Spacer(modifier = Modifier.height(48.dp))
            OtpScreen(
                state = state,
                focusRequesters = focusRequesters,
                onAction = { action ->
                    if (action is OtpAction.OnEnterNumber && action.number != null) {
                        focusRequesters[action.index].freeFocus()
                    }
                    viewModel.onAction(action)
                }
            )
            Spacer(modifier = Modifier.height(48.dp))
            Button(
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF36F9D)),
                onClick = { viewModel.verifyOtp(email) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                shape = CircleShape
            ) {
                Text(text = "Verificar", fontSize = 16.sp, color = Color.White, fontWeight = FontWeight.Bold)
            }
            when (state.isValid) {
                true -> LaunchedEffect(Unit) { navigateAfterOtp() }
                false -> Text("Código inválido. Intenta de nuevo.", color = Color.White)
                null -> Unit
            }
        }
    }
}
