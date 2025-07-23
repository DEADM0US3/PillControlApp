package com.example.pills.pills.presentation.profile

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Cake
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.pills.R
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.delay

// Paleta de colores premium basada en el degradado
object ProfileColors {
    val primary = Color(0xFFF48FB1)
    val primaryLight = Color(0xFFFCE4EC)
    val primaryDark = Color(0xFFE91E63)
    val accent = Color(0xFFFF6B9D)
    val surface = Color(0xFFFFFBFE)
    val surfaceVariant = Color(0xFFF8F0F5)
    val surfaceTint = Color(0xFFFAF0F5)
    val onSurface = Color(0xFF1C1B1F)
    val onSurfaceVariant = Color(0xFF49454F)
    val outline = Color(0xFF79747E)
    val success = Color(0xFF00BCD4)
    val warning = Color(0xFFFF9800)
    val error = Color(0xFFFF5252)
    val glassMorphism = Color(0x40FFFFFF)
}

@Composable
fun EditProfileScreen(
    userName: String = "Laura Torres",
    userEmail: String = "laura@example.com",
    userPhone: String = "+52 123 456 7890",
    userAge: String = "28",
    onBackPressed: () -> Unit = {},
    onSaveProfile: (String, String, String, String) -> Unit = { _, _, _, _ -> }
) {
    val viewModel: EditProfileViewModel = viewModel()
    val state by viewModel.state.collectAsState()

    var showSaveDialog by remember { mutableStateOf(false) }
    var showSuccessAnimation by remember { mutableStateOf(false) }

    // Estados para animaciones
    var profileImageScale by remember { mutableStateOf(1f) }
    val profileImageAnimation = animateFloatAsState(
        targetValue = profileImageScale,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        )
    )

    // Cargar datos del usuario al iniciar
    LaunchedEffect(Unit) {
        viewModel.loadUserProfile(userName, userEmail, userPhone, userAge)
    }

    // Manejar éxito del guardado con animación
    LaunchedEffect(state.isSuccess) {
        if (state.isSuccess) {
            showSuccessAnimation = true
            delay(2000)
            try {
                onSaveProfile(state.name, state.email, state.phone, state.age)
            } catch (e: Exception) {
                // Manejar error de callback de forma segura
            }
        }
    }

    val context = LocalContext.current

    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        try {
            uri?.let {
                viewModel.onEvent(EditProfileEvent.UpdateProfileImage(it.toString()))
                profileImageScale = 1.1f
                // Volver a escala normal después de la animación
                profileImageScale = 1f
            }
        } catch (e: Exception) {
            viewModel.onEvent(EditProfileEvent.UpdateProfileImage(""))
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        // Fondo principal con degradado
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color(0xFFF48FB1),
                            Color(0xFFFCE4EC)
                        )
                    )
                )
        ) {
            // Header estático en la parte superior
            ModernHeader(onBackPressed = onBackPressed)

            // Contenido scrolleable debajo del header
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                contentPadding = PaddingValues(bottom = 100.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                item {
                    Column {
                        Spacer(modifier = Modifier.height(24.dp))

                        // Sección de avatar con animaciones
                        AnimatedProfileSection(
                            state = state,
                            profileImageAnimation = profileImageAnimation.value,
                            onImageClick = {
                                imagePickerLauncher.launch("image/*")
                            }
                        )

                        Spacer(modifier = Modifier.height(32.dp))
                    }
                }

                // Mostrar mensaje de error con animación
                item {
                    AnimatedVisibility(
                        visible = state.errorMessage != null,
                        enter = slideInVertically() + fadeIn(),
                        exit = slideOutVertically() + fadeOut()
                    ) {
                        ErrorCard(errorMessage = state.errorMessage ?: "")
                    }
                }

                // Formulario con campos modernos
                item {
                    Column {
                        ModernFormSection(
                            state = state,
                            viewModel = viewModel
                        )

                        Spacer(modifier = Modifier.height(32.dp))
                    }
                }

                // Botón de guardar con animaciones
                item {
                    Column {
                        ModernSaveButton(
                            isLoading = state.isLoading,
                            onSave = {
                                showSaveDialog = true
                            }
                        )

                        Spacer(modifier = Modifier.height(40.dp))
                    }
                }
            }
        }

        // Overlay de éxito con animación completa
        AnimatedVisibility(
            visible = showSuccessAnimation,
            enter = fadeIn() + scaleIn(),
            exit = fadeOut() + scaleOut(),
            modifier = Modifier.fillMaxSize()
        ) {
            SuccessOverlay(onDismiss = { showSuccessAnimation = false })
        }
    }

    // Diálogo moderno de confirmación
    if (showSaveDialog) {
        ModernConfirmationDialog(
            onConfirm = {
                viewModel.onEvent(EditProfileEvent.SaveProfile)
                showSaveDialog = false
            },
            onDismiss = { showSaveDialog = false }
        )
    }
}



@Composable
fun AnimatedProfileSection(
    state: EditProfileState,
    profileImageAnimation: Float,
    onImageClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Container del avatar con múltiples capas
        Box(
            modifier = Modifier.size(160.dp),
            contentAlignment = Alignment.Center
        ) {
            // Círculo de fondo animado
            var rotationAngle by remember { mutableStateOf(0f) }
            LaunchedEffect(Unit) {
                while (true) {
                    rotationAngle += 1f
                    delay(100)
                }
            }

            // Anillo externo giratorio
            Box(
                modifier = Modifier
                    .size(160.dp)
                    .rotate(rotationAngle)
                    .background(
                        Brush.sweepGradient(
                            colors = listOf(
                                ProfileColors.primary.copy(alpha = 0.3f),
                                ProfileColors.accent.copy(alpha = 0.6f),
                                ProfileColors.primary.copy(alpha = 0.3f)
                            )
                        ),
                        CircleShape
                    )
            )

            // Avatar principal
            Box(
                modifier = Modifier
                    .size(140.dp)
                    .scale(profileImageAnimation)
                    .clip(CircleShape)
                    .background(
                        Brush.radialGradient(
                            colors = listOf(
                                ProfileColors.primaryLight,
                                ProfileColors.surface
                            )
                        )
                    )
                    .border(4.dp, ProfileColors.surface, CircleShape)
                    .clickable { onImageClick() }
                    .shadow(
                        elevation = 8.dp,
                        shape = CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                if (!state.profileImageUri.isNullOrBlank()) {
                    AsyncImage(
                        model = ImageRequest.Builder(LocalContext.current)
                            .data(state.profileImageUri)
                            .crossfade(true)
                            .build(),
                        contentDescription = "Foto de perfil",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop,
                        error = painterResource(id = R.drawable.ic_launcher_foreground)
                    )
                } else {
                    Image(
                        painter = painterResource(id = R.drawable.ic_launcher_foreground),
                        contentDescription = "Foto de perfil",
                        modifier = Modifier.size(100.dp)
                    )
                }
            }

            // Botón de cámara flotante con animación
            var cameraScale by remember { mutableStateOf(1f) }
            val cameraAnimation by animateFloatAsState(
                targetValue = cameraScale,
                animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy)
            )

            Box(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .offset((-8).dp, (-8).dp)
                    .size(48.dp)
                    .scale(cameraAnimation)
                    .clip(CircleShape)
                    .background(
                        Brush.linearGradient(
                            colors = listOf(
                                ProfileColors.accent,
                                ProfileColors.primary
                            )
                        )
                    )
                    .border(3.dp, ProfileColors.surface, CircleShape)
                    .clickable {
                        cameraScale = 1.2f
                        cameraScale = 1f
                        onImageClick()
                    }
                    .shadow(6.dp, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.CameraAlt,
                    contentDescription = "Cambiar foto",
                    tint = Color.White,
                    modifier = Modifier.size(24.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Texto con animación de entrada
        AnimatedVisibility(
            visible = true,
            enter = fadeIn() + slideInVertically()
        ) {
            Text(
                text = "Toca para cambiar tu foto",
                color = Color.White.copy(alpha = 0.9f),
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
fun ErrorCard(errorMessage: String) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp)
            .shadow(4.dp, RoundedCornerShape(16.dp)),
        colors = CardDefaults.cardColors(
            containerColor = ProfileColors.error.copy(alpha = 0.1f)
        ),
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(
            modifier = Modifier.padding(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.ErrorOutline,
                contentDescription = null,
                tint = ProfileColors.error,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = errorMessage,
                color = ProfileColors.error,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium
            )
        }
    }
    Spacer(modifier = Modifier.height(16.dp))
}

@Composable
fun ModernFormSection(
    state: EditProfileState,
    viewModel: EditProfileViewModel
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp)
    ) {
        // Campos con animaciones de entrada
        val fields = listOf(
            FieldData("Nombre completo", state.name, Icons.Default.Person) { value: String ->
                viewModel.onEvent(EditProfileEvent.UpdateName(value))
            },
            FieldData("Correo electrónico", state.email, Icons.Default.Email) { value: String ->
                viewModel.onEvent(EditProfileEvent.UpdateEmail(value))
            },
            FieldData("Teléfono", state.phone, Icons.Default.Phone) { value: String ->
                viewModel.onEvent(EditProfileEvent.UpdatePhone(value))
            },
            FieldData("Edad", state.age, Icons.Default.Cake) { value: String ->
                viewModel.onEvent(EditProfileEvent.UpdateAge(value))
            }
        )

        fields.forEachIndexed { index, fieldData ->
            AnimatedVisibility(
                visible = true,
                enter = fadeIn(
                    animationSpec = tween(
                        durationMillis = 300,
                        delayMillis = index * 100
                    )
                ) + slideInVertically(
                    animationSpec = tween(
                        durationMillis = 300,
                        delayMillis = index * 100
                    )
                ) { it }
            ) {
                ModernTextField(
                    label = fieldData.label,
                    value = fieldData.value,
                    onValueChange = fieldData.onValueChange,
                    icon = fieldData.icon,
                    keyboardType = when (fieldData.label) {
                        "Correo electrónico" -> KeyboardType.Email
                        "Teléfono" -> KeyboardType.Phone
                        "Edad" -> KeyboardType.Number
                        else -> KeyboardType.Text
                    }
                )
            }

            if (index < fields.size - 1) {
                Spacer(modifier = Modifier.height(20.dp))
            }
        }
    }
}

@Composable
fun ModernTextField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    icon: ImageVector,
    keyboardType: KeyboardType = KeyboardType.Text
) {
    var isFocused by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(
                elevation = if (isFocused) 8.dp else 4.dp,
                shape = RoundedCornerShape(20.dp)
            ),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = ProfileColors.surface
        )
    ) {
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            label = {
                Text(
                    text = label,
                    fontWeight = FontWeight.Medium
                )
            },
            leadingIcon = {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .background(
                            if (isFocused) ProfileColors.primaryLight else ProfileColors.surfaceVariant,
                            CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = if (isFocused) ProfileColors.primary else ProfileColors.outline,
                        modifier = Modifier.size(20.dp)
                    )
                }
            },
            keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color.Transparent,
                unfocusedBorderColor = Color.Transparent,
                focusedLabelColor = ProfileColors.primary,
                unfocusedLabelColor = ProfileColors.onSurfaceVariant,
                cursorColor = ProfileColors.primary,
                focusedTextColor = ProfileColors.onSurface,
                unfocusedTextColor = ProfileColors.onSurface
            ),
            shape = RoundedCornerShape(20.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(4.dp),
            singleLine = true
        )
    }
}

@Composable
fun ModernSaveButton(
    isLoading: Boolean,
    onSave: () -> Unit
) {
    var isPressed by remember { mutableStateOf(false) }
    val buttonScale by animateFloatAsState(
        targetValue = if (isPressed) 0.95f else 1f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy)
    )

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp)
            .scale(buttonScale)
            .shadow(
                elevation = 12.dp,
                shape = RoundedCornerShape(28.dp),
                spotColor = ProfileColors.primary.copy(alpha = 0.4f)
            )
            .clickable {
                isPressed = true
                onSave()
                isPressed = false
            },
        shape = RoundedCornerShape(28.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.Transparent
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(64.dp)
                .background(
                    Brush.horizontalGradient(
                        colors = listOf(
                            ProfileColors.accent,
                            ProfileColors.primary,
                            ProfileColors.accent
                        )
                    )
                ),
            contentAlignment = Alignment.Center
        ) {
            if (isLoading) {
                Row(
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    CircularProgressIndicator(
                        color = Color.White,
                        modifier = Modifier.size(24.dp),
                        strokeWidth = 3.dp
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = "Guardando...",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                }
            } else {
                Row(
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Save,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = "Guardar Cambios",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    )
                }
            }
        }
    }
}

@Composable
fun ModernConfirmationDialog(
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = ProfileColors.surface,
        shape = RoundedCornerShape(28.dp),
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .background(
                            ProfileColors.primaryLight,
                            CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Save,
                        contentDescription = null,
                        tint = ProfileColors.primary,
                        modifier = Modifier.size(24.dp)
                    )
                }
                Spacer(modifier = Modifier.width(16.dp))
                Text(
                    text = "Guardar Cambios",
                    fontWeight = FontWeight.Bold,
                    color = ProfileColors.onSurface,
                    fontSize = 20.sp
                )
            }
        },
        text = {
            Text(
                text = "¿Estás seguro de que quieres guardar los cambios en tu perfil?",
                color = ProfileColors.onSurfaceVariant,
                fontSize = 16.sp,
                lineHeight = 24.sp
            )
        },
        confirmButton = {
            Button(
                onClick = onConfirm,
                colors = ButtonDefaults.buttonColors(
                    containerColor = ProfileColors.primary
                ),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.height(48.dp)
            ) {
                Text(
                    text = "Guardar",
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss,
                modifier = Modifier.height(48.dp)
            ) {
                Text(
                    text = "Cancelar",
                    color = ProfileColors.primary,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    )
}

@Composable
fun SuccessOverlay(onDismiss: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(ProfileColors.primary.copy(alpha = 0.95f)),
        contentAlignment = Alignment.Center
    ) {
        Card(
            shape = RoundedCornerShape(32.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color.White
            ),
            modifier = Modifier
                .size(280.dp)
                .shadow(16.dp, RoundedCornerShape(32.dp))
        ) {
            Column(
                modifier = Modifier.fillMaxSize().padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                // Ícono de éxito animado
                var iconScale by remember { mutableStateOf(0f) }
                LaunchedEffect(Unit) {
                    iconScale = 1.2f
                    delay(200)
                    iconScale = 1f
                }

                val iconAnimation by animateFloatAsState(
                    targetValue = iconScale,
                    animationSpec = spring(
                        dampingRatio = Spring.DampingRatioMediumBouncy,
                        stiffness = Spring.StiffnessLow
                    )
                )

                Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = null,
                    tint = ProfileColors.success,
                    modifier = Modifier
                        .size(64.dp)
                        .scale(iconAnimation)
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "¡Perfil Actualizado!",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = ProfileColors.onSurface,
                    textAlign = TextAlign.Center
                )

                Text(
                    text = "Tus cambios se han guardado correctamente",
                    fontSize = 14.sp,
                    color = ProfileColors.onSurfaceVariant,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )

                Spacer(modifier = Modifier.height(24.dp))

                Button(
                    onClick = { onDismiss() },
                    colors = ButtonDefaults.buttonColors(containerColor = ProfileColors.primary)
                ) {
                    Text("Cerrar", color = Color.White)
                }
            }
        }
    }
}

// Clase auxiliar para los campos del formulario
data class FieldData(
    val label: String,
    val value: String,
    val icon: ImageVector,
    val onValueChange: (String) -> Unit
)

@Composable
fun ModernHeader(onBackPressed: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp) // Padding más equilibrado
            .statusBarsPadding(), // Asegura que no se superponga con la barra de estado
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White.copy(alpha = 0.15f)
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp), // Padding interno consistente
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = onBackPressed,
                modifier = Modifier
                    .size(48.dp)
                    .background(
                        Color.White.copy(alpha = 0.2f),
                        CircleShape
                    )
            ) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Regresar",
                    tint = Color.White,
                    modifier = Modifier.size(24.dp)
                )
            }
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                text = "Editar Perfil",
                color = Color.White,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}