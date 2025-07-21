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
import org.koin.androidx.compose.koinViewModel
import kotlinx.coroutines.delay
import kotlin.toString

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
fun ModernHeader(onBackPressed: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(
            onClick = onBackPressed,
            modifier = Modifier
                .size(48.dp)
                .background(
                    color = ProfileColors.glassMorphism,
                    shape = CircleShape
                )
        ) {
            Icon(
                imageVector = Icons.Default.ArrowBack,
                contentDescription = "Volver",
                tint = ProfileColors.onSurface
            )
        }
        
        Spacer(modifier = Modifier.weight(1f))
        
        Text(
            text = "Editar Perfil",
            style = MaterialTheme.typography.headlineSmall.copy(
                fontWeight = FontWeight.Bold,
                color = ProfileColors.onSurface
            )
        )
        
        Spacer(modifier = Modifier.weight(1f))
    }
}

@Composable
fun AnimatedProfileSection(
    state: EditProfileState,
    profileImageAnimation: Float,
    onImageClick: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(120.dp)
                .scale(profileImageAnimation)
                .clip(CircleShape)
                .shadow(8.dp, CircleShape)
                .clickable { onImageClick() }
                .background(ProfileColors.surfaceVariant)
        ) {
            if (state.profileImageUri?.isNotEmpty() == true) {
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(state.profileImageUri)
                        .crossfade(true)
                        .build(),
                    contentDescription = "Foto de perfil",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop,
                    onError = {
                        // Manejo de error de carga de imagen
                    }
                )
            } else {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = "Agregar foto",
                    modifier = Modifier
                        .size(48.dp)
                        .align(Alignment.Center),
                    tint = ProfileColors.onSurfaceVariant
                )
            }
            
            // Icono de cámara en la esquina
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .align(Alignment.BottomEnd)
                    .background(ProfileColors.primary, CircleShape)
            ) {
                Icon(
                    imageVector = Icons.Default.CameraAlt,
                    contentDescription = "Cambiar foto",
                    modifier = Modifier
                        .size(20.dp)
                        .align(Alignment.Center),
                    tint = Color.White
                )
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Text(
            text = "Toca para cambiar la foto",
            style = MaterialTheme.typography.bodyMedium.copy(
                color = ProfileColors.onSurfaceVariant
            )
        )
    }
}

@Composable
fun ErrorCard(errorMessage: String) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        colors = CardDefaults.cardColors(
            containerColor = ProfileColors.error.copy(alpha = 0.1f)
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.ErrorOutline,
                contentDescription = "Error",
                tint = ProfileColors.error,
                modifier = Modifier.size(24.dp)
            )
            
            Spacer(modifier = Modifier.width(12.dp))
            
            Text(
                text = errorMessage,
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = ProfileColors.error
                )
            )
        }
    }
}

@Composable
fun SuccessOverlay() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(ProfileColors.success.copy(alpha = 0.9f)),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = Icons.Default.CheckCircle,
                contentDescription = "Éxito",
                tint = Color.White,
                modifier = Modifier.size(64.dp)
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Text(
                text = "¡Perfil actualizado exitosamente!",
                style = MaterialTheme.typography.headlineSmall.copy(
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                ),
                textAlign = TextAlign.Center
            )
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
        title = {
            Text(
                text = "Confirmar cambios",
                style = MaterialTheme.typography.headlineSmall.copy(
                    fontWeight = FontWeight.Bold
                )
            )
        },
        text = {
            Text(
                text = "¿Estás seguro de que quieres guardar los cambios en tu perfil?",
                style = MaterialTheme.typography.bodyMedium
            )
        },
        confirmButton = {
            TextButton(
                onClick = onConfirm,
                colors = ButtonDefaults.textButtonColors(
                    contentColor = ProfileColors.primary
                )
            ) {
                Text("Guardar")
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss,
                colors = ButtonDefaults.textButtonColors(
                    contentColor = ProfileColors.onSurfaceVariant
                )
            ) {
                Text("Cancelar")
            }
        },
        containerColor = ProfileColors.surface,
        shape = RoundedCornerShape(16.dp)
    )
}

@Composable
fun FieldData(
    label: String,
    value: String,
    icon: ImageVector,
    onValueChange: (String) -> Unit,
    errorMessage: String? = null,
    keyboardType: KeyboardType = KeyboardType.Text
) {
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium.copy(
                color = ProfileColors.onSurfaceVariant,
                fontWeight = FontWeight.Medium
            ),
            modifier = Modifier.padding(bottom = 8.dp)
        )
        
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            leadingIcon = {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = ProfileColors.onSurfaceVariant
                )
            },
            keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = ProfileColors.primary,
                unfocusedBorderColor = ProfileColors.outline,
                focusedLabelColor = ProfileColors.primary,
                unfocusedLabelColor = ProfileColors.onSurfaceVariant
            ),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth(),
            isError = errorMessage != null
        )
        
        if (errorMessage != null) {
            Text(
                text = errorMessage,
                style = MaterialTheme.typography.bodySmall.copy(
                    color = ProfileColors.error
                ),
                modifier = Modifier.padding(top = 4.dp)
            )
        }
    }
}

@Composable
fun ModernTextField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    icon: ImageVector,
    errorMessage: String? = null,
    keyboardType: KeyboardType = KeyboardType.Text
) {
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium.copy(
                color = ProfileColors.onSurfaceVariant,
                fontWeight = FontWeight.Medium
            ),
            modifier = Modifier.padding(bottom = 8.dp)
        )
        
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            leadingIcon = {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = ProfileColors.onSurfaceVariant
                )
            },
            keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = ProfileColors.primary,
                unfocusedBorderColor = ProfileColors.outline,
                focusedLabelColor = ProfileColors.primary,
                unfocusedLabelColor = ProfileColors.onSurfaceVariant
            ),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth(),
            isError = errorMessage != null
        )
        
        if (errorMessage != null) {
            Text(
                text = errorMessage,
                style = MaterialTheme.typography.bodySmall.copy(
                    color = ProfileColors.error
                ),
                modifier = Modifier.padding(top = 4.dp)
            )
        }
    }
}

@Composable
fun ModernSaveButton(
    onClick: () -> Unit,
    isEnabled: Boolean = true,
    isLoading: Boolean = false
) {
    Button(
        onClick = onClick,
        enabled = isEnabled && !isLoading,
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = ProfileColors.primary,
            disabledContainerColor = ProfileColors.outline.copy(alpha = 0.3f)
        ),
        shape = RoundedCornerShape(16.dp)
    ) {
        if (isLoading) {
            CircularProgressIndicator(
                modifier = Modifier.size(24.dp),
                color = Color.White,
                strokeWidth = 2.dp
            )
        } else {
            Icon(
                imageVector = Icons.Default.Save,
                contentDescription = null,
                modifier = Modifier.size(20.dp)
            )
            
            Spacer(modifier = Modifier.width(8.dp))
            
            Text(
                text = "Guardar Cambios",
                style = MaterialTheme.typography.bodyLarge.copy(
                    fontWeight = FontWeight.Bold
                )
            )
        }
    }
}

@Composable
fun ModernFormSection(
    state: EditProfileState,
    viewModel: EditProfileViewModel
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
    ) {
        ModernTextField(
            label = "Nombre completo",
            value = state.name,
            onValueChange = { 
                try {
                    viewModel.onEvent(EditProfileEvent.UpdateName(it)) 
                } catch (e: Exception) {
                    // Manejo de error al actualizar nombre
                }
            },
            icon = Icons.Default.Person,
//            errorMessage = state.nameError,
            keyboardType = KeyboardType.Text
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        ModernTextField(
            label = "Correo electrónico",
            value = state.email,
            onValueChange = { 
                try {
                    viewModel.onEvent(EditProfileEvent.UpdateEmail(it)) 
                } catch (e: Exception) {
                    // Manejo de error al actualizar email
                }
            },
            icon = Icons.Default.Email,
//            errorMessage = state.emailError,
            keyboardType = KeyboardType.Email
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        ModernTextField(
            label = "Teléfono",
            value = state.phone,
            onValueChange = { 
                try {
                    viewModel.onEvent(EditProfileEvent.UpdatePhone(it)) 
                } catch (e: Exception) {
                    // Manejo de error al actualizar teléfono
                }
            },
            icon = Icons.Default.Phone,
//            errorMessage = state.phoneError,
            keyboardType = KeyboardType.Phone
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        ModernTextField(
            label = "Edad",
            value = state.age,
            onValueChange = { 
                try {
                    viewModel.onEvent(EditProfileEvent.UpdateAge(it)) 
                } catch (e: Exception) {
                    // Manejo de error al actualizar edad
                }
            },
            icon = Icons.Default.Cake,
//            errorMessage = state.ageError,
            keyboardType = KeyboardType.Number
        )
    }
}

@Composable
fun EditProfileScreen(
    onBackPressed: () -> Unit,
    viewModel: EditProfileViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsState()
    val context = LocalContext.current

    // Animación para la imagen de perfil
    val profileImageAnimation by animateFloatAsState(
        targetValue = if (!state.profileImageUri.isNullOrEmpty()) 1.05f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "profileImageAnimation"
    )

    // Estado diálogo confirmación guardar
    var showConfirmationDialog by remember { mutableStateOf(false) }

    // Selector de imagen
//    val imageLauncher = rememberLauncherForActivityResult(
//        contract = ActivityResultContracts.GetContent()
//    ) { uri ->
//        uri?.let {
//            viewModel.onEvent(EditProfileEvent.UpdateProfileImage(it.toString()))
//            // Si subes imagen a storage, lanza evento UploadImage también
//            // viewModel.onEvent(EditProfileEvent.UploadImage(it.toString()))
//        }
//    }

    // Cargar perfil al iniciar
    LaunchedEffect(Unit) {
        viewModel.onEvent(EditProfileEvent.LoadUserProfile)
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(ProfileColors.surface, ProfileColors.surfaceVariant)
                )
            )
    ) {
        if (state.isLoading) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(
                    color = ProfileColors.primary,
                    modifier = Modifier.size(48.dp)
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(bottom = 100.dp)
            ) {
                item {
                    ModernHeader(onBackPressed = onBackPressed)

                    Spacer(modifier = Modifier.height(24.dp))

//                    AnimatedProfileSection(
//                        state = state,
//                        profileImageAnimation = profileImageAnimation,
//                        onImageClick = { imageLauncher.launch("image/*") }
//                    )

                    Spacer(modifier = Modifier.height(32.dp))

                    state.errorMessage?.let { errorMessage ->
                        ErrorCard(errorMessage = errorMessage)
                        Spacer(modifier = Modifier.height(16.dp))
                    }

                    ModernFormSection(state = state, viewModel = viewModel)

                    Spacer(modifier = Modifier.height(32.dp))

                    ModernSaveButton(
                        onClick = { showConfirmationDialog = true },
                        isEnabled = state.name.isNotBlank() && state.email.isNotBlank(),
                        isLoading = state.isLoading
                    )

                    Spacer(modifier = Modifier.height(32.dp))
                }
            }

            if (state.isSuccess) {
                SuccessOverlay()
            }

            if (showConfirmationDialog) {
                ModernConfirmationDialog(
                    onConfirm = {
                        viewModel.onEvent(EditProfileEvent.SaveProfile)
                        showConfirmationDialog = false
                    },
                    onDismiss = { showConfirmationDialog = false }
                )
            }
        }
    }
}
