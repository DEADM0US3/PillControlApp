package com.example.pills.pills.presentation.profile

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.QuestionMark
import androidx.compose.material.icons.filled.Support
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.CloudOff

import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// Paleta de colores mejorada basada en el degradado original
object HelpColors {
    val primary = Color(0xFFF48FB1)
    val primaryLight = Color(0xFFFCE4EC)
    val secondary = Color(0xFFE91E63)
    val accent = Color(0xFFFF6B9D)
    val surface = Color(0xFFFFFBFE)
    val surfaceVariant = Color(0xFFF8F0F5)
    val onSurface = Color(0xFF1C1B1F)
    val onSurfaceVariant = Color(0xFF49454F)
    val outline = Color(0xFF79747E)
    val success = Color(0xFF00BCD4)
    val warning = Color(0xFFFF9800)
    val info = Color(0xFF2196F3)
}

@Composable
fun HelpScreen(
    onBackPressed: () -> Unit = {}
) {
    Box(
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
        HelpScreenHeader(onBackPressed = onBackPressed)
        
        // Contenido scrolleable debajo del header
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 110.dp) // Espacio ajustado para el header estático
                .verticalScroll(rememberScrollState())
        ) {
            // Contenido principal con mejor espaciado
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp) // Mismo padding que el header
            ) {
            // Hero Card con animación
            HeroWelcomeCard()

            Spacer(modifier = Modifier.height(20.dp))

            // Sección FAQ con diseño mejorado
            ModernSectionHeader(
                title = "Preguntas Frecuentes",
                subtitle = "Todo lo que necesitas saber"
            )

            Spacer(modifier = Modifier.height(16.dp))

            // FAQ Items con iconos personalizados
            val faqItems = listOf(
                FAQData(
                    question = "¿Cómo registro un nuevo medicamento?",
                    answer = "Ve a la pantalla de Calendario y toca el botón '+' para agregar un nuevo medicamento. Completa toda la información requerida como nombre, dosis y horarios, después guarda los cambios.",
                    icon = Icons.Default.Star
                ),
                FAQData(
                    question = "¿Cómo configuro recordatorios?",
                    answer = "Al crear un medicamento, puedes establecer múltiples horarios específicos. La aplicación te enviará notificaciones push en esos momentos para que nunca olvides tu medicación.",
                    icon = Icons.Default.Schedule
                ),
                FAQData(
                    question = "¿Puedo cambiar mi foto de perfil?",
                    answer = "Sí, ve a tu perfil y selecciona 'Editar perfil'. Toca en tu foto actual para cambiarla desde tu galería o tomar una nueva foto con la cámara.",
                    icon = Icons.Default.AccountCircle
                ),
                FAQData(
                    question = "¿Cómo recupero mi contraseña?",
                    answer = "En la pantalla de login, toca '¿Olvidaste tu contraseña?' e ingresa tu email. Recibirás un enlace para crear una nueva contraseña de forma segura.",
                    icon = Icons.Default.Lock
                ),
                FAQData(
                    question = "¿La aplicación funciona sin internet?",
                    answer = "Puedes ver toda tu información guardada sin conexión, pero necesitas internet para sincronizar cambios, recibir actualizaciones y hacer respaldos en la nube.",
                    icon = Icons.Default.CloudOff
                )
            )

            faqItems.forEach { faqData ->
                ModernFAQItem(
                    question = faqData.question,
                    answer = faqData.answer,
                    icon = faqData.icon
                )
                Spacer(modifier = Modifier.height(12.dp))
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Sección de contacto rediseñada
            ModernSectionHeader(
                title = "Contacto y Soporte",
                subtitle = "Estamos aquí para ayudarte"
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Cards de contacto con mejor diseño
            ModernContactSection()

            Spacer(modifier = Modifier.height(32.dp))

            // Footer con información de la app
            AppInfoFooter()

            Spacer(modifier = Modifier.height(40.dp))
            }
        }
    }
}

@Composable
fun HelpScreenHeader(onBackPressed: () -> Unit) {
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
                text = "Centro de Ayuda",
                color = Color.White,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun HeroWelcomeCard() {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(
                elevation = 12.dp,
                shape = RoundedCornerShape(24.dp)
            ),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = HelpColors.surface
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.horizontalGradient(
                        colors = listOf(
                            HelpColors.primaryLight,
                            HelpColors.surface
                        )
                    )
                )
        ) {
            Column(
                modifier = Modifier.padding(28.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Icono principal con fondo circular
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .background(
                            HelpColors.primary.copy(alpha = 0.1f),
                            CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = null,
                        tint = HelpColors.primary,
                        modifier = Modifier.size(40.dp)
                    )
                }
                
                Spacer(modifier = Modifier.height(20.dp))
                
                Text(
                    text = "¡Bienvenido a PillControlBand!",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = HelpColors.onSurface,
                    textAlign = TextAlign.Center
                )
                
                Spacer(modifier = Modifier.height(12.dp))
                
                Text(
                    text = "Tu asistente personal para el control inteligente de medicamentos. Descubre todas las funcionalidades que te ayudarán a mantener tu salud al día.",
                    fontSize = 16.sp,
                    color = HelpColors.onSurfaceVariant,
                    textAlign = TextAlign.Center,
                    lineHeight = 24.sp
                )
            }
        }
    }
}

@Composable
fun ModernSectionHeader(
    title: String,
    subtitle: String
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White.copy(alpha = 0.15f)
        )
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            Text(
                text = title,
                color = Color(0xFF8B1A3A), // Rosa oscuro para mejor contraste
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = subtitle,
                color = Color(0xFFB71C1C), // Rojo oscuro para subtítulos
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

data class FAQData(
    val question: String,
    val answer: String,
    val icon: ImageVector
)

@Composable
fun ModernFAQItem(
    question: String,
    answer: String,
    icon: ImageVector
) {
    var expanded by remember { mutableStateOf(false) }
    val rotation by animateFloatAsState(
        targetValue = if (expanded) 180f else 0f,
        animationSpec = tween(300)
    )
    
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { expanded = !expanded }
            .shadow(
                elevation = if (expanded) 8.dp else 4.dp,
                shape = RoundedCornerShape(20.dp)
            ),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = HelpColors.surface
        )
    ) {
        Column(
            modifier = Modifier
                .animateContentSize(
                    animationSpec = tween(300)
                )
                .padding(20.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Icono personalizado
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .background(
                            HelpColors.primaryLight,
                            CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = HelpColors.primary,
                        modifier = Modifier.size(24.dp)
                    )
                }
                
                Spacer(modifier = Modifier.width(16.dp))
                
                Text(
                    text = question,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 16.sp,
                    color = HelpColors.onSurface,
                    modifier = Modifier.weight(1f)
                )
                
                Icon(
                    imageVector = Icons.Default.KeyboardArrowDown,
                    contentDescription = if (expanded) "Contraer" else "Expandir",
                    tint = HelpColors.primary,
                    modifier = Modifier
                        .size(24.dp)
                        .rotate(rotation)
                )
            }
            
            if (expanded) {
                Spacer(modifier = Modifier.height(16.dp))
                
                Divider(
                    color = HelpColors.primaryLight,
                    thickness = 1.dp,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                Text(
                    text = answer,
                    fontSize = 14.sp,
                    color = HelpColors.onSurfaceVariant,
                    lineHeight = 20.sp,
                    modifier = Modifier.padding(start = 64.dp)
                )
            }
        }
    }
}

@Composable
fun ModernContactSection() {
    // Card de soporte técnico
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(
                elevation = 6.dp,
                shape = RoundedCornerShape(20.dp)
            ),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = HelpColors.surface
        )
    ) {
        Column(
            modifier = Modifier.padding(24.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(56.dp)
                        .background(
                            HelpColors.success.copy(alpha = 0.1f),
                            CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Support,
                        contentDescription = null,
                        tint = HelpColors.success,
                        modifier = Modifier.size(28.dp)
                    )
                }
                
                Spacer(modifier = Modifier.width(16.dp))
                
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Soporte Técnico",
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        color = HelpColors.onSurface
                    )
                    Text(
                        text = "Disponible 24/7 para resolver tus dudas",
                        fontSize = 14.sp,
                        color = HelpColors.onSurfaceVariant
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(20.dp))
            
            // Información de contacto
            ContactInfoRow(
                icon = Icons.Default.Email,
                label = "Email de Soporte",
                value = "soporte@pillcontrolband.com",
                backgroundColor = HelpColors.info.copy(alpha = 0.1f),
                iconTint = HelpColors.info
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            ContactInfoRow(
                icon = Icons.Default.Phone,
                label = "Teléfono de Ayuda",
                value = "+52 55 1234 5678",
                backgroundColor = HelpColors.warning.copy(alpha = 0.1f),
                iconTint = HelpColors.warning
            )
        }
    }
}

@Composable
fun ContactInfoRow(
    icon: ImageVector,
    label: String,
    value: String,
    backgroundColor: Color,
    iconTint: Color
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .background(
                HelpColors.surfaceVariant,
                RoundedCornerShape(12.dp)
            )
            .padding(16.dp)
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .background(backgroundColor, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = iconTint,
                modifier = Modifier.size(20.dp)
            )
        }
        
        Spacer(modifier = Modifier.width(12.dp))
        
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = label,
                fontSize = 12.sp,
                color = HelpColors.outline,
                fontWeight = FontWeight.Medium
            )
            Text(
                text = value,
                fontWeight = FontWeight.SemiBold,
                fontSize = 16.sp,
                color = HelpColors.onSurface
            )
        }
    }
}

@Composable
fun AppInfoFooter() {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(
                elevation = 4.dp,
                shape = RoundedCornerShape(20.dp)
            ),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = HelpColors.surface
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.horizontalGradient(
                        colors = listOf(
                            HelpColors.primaryLight,
                            HelpColors.surface
                        )
                    )
                )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .size(60.dp)
                        .background(
                            HelpColors.primary.copy(alpha = 0.1f),
                            CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.QuestionMark,
                        contentDescription = null,
                        tint = HelpColors.primary,
                        modifier = Modifier.size(30.dp)
                    )
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                Text(
                    text = "PillControlBand",
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp,
                    color = HelpColors.onSurface
                )
                
                Text(
                    text = "Versión 1.0",
                    fontSize = 14.sp,
                    color = HelpColors.onSurfaceVariant
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                Text(
                    text = "Desarrollado con ❤️ para cuidar tu salud",
                    fontSize = 14.sp,
                    color = HelpColors.onSurfaceVariant,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}