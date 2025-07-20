package com.example.pills.pills.presentation.calendar

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kizitonwose.calendar.compose.VerticalCalendar
import com.kizitonwose.calendar.compose.rememberCalendarState
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.TextStyle
import java.util.*

// Paleta de colores simplificada y estable
private val Pink = Color(0xFFEA5A8C)
private val PinkLight = Color(0xFFFFF0F6)
private val PinkDark = Color(0xFFD81B60)
private val PinkShadow = Color(0x33EA5A8C)
private val CardShadow = Color(0x22000000)
private val Black = Color(0xFF222222)
private val GrayLine = Color(0xFFBDBDBD)
private val LightGray = Color(0xFFF3F3F3)
private val GrayText = Color(0xFFBDBDBD)
private val White = Color(0xFFFFFFFF)
private val RedPeriod = Color(0xFFE53935)
private val YellowObs = Color(0xFFFFF176)

@Composable
fun CalendarScreen(
    onDayClick: (LocalDate) -> Unit = {}
) {
    val today = remember { LocalDate.now() }
    val startMonth = remember { YearMonth.now().minusMonths(12) }
    val endMonth = remember { YearMonth.now().plusMonths(12) }
    var visibleMonth by remember { mutableStateOf(YearMonth.now()) }
    val calendarState = rememberCalendarState(
        startMonth = startMonth,
        endMonth = endMonth,
        firstVisibleMonth = visibleMonth,
        firstDayOfWeek = java.time.DayOfWeek.SUNDAY
    )
    var selectedDate by remember { mutableStateOf<LocalDate?>(null) }
    var showDialog by remember { mutableStateOf(false) }
    var dialogDate by remember { mutableStateOf<LocalDate?>(null) }

    fun openDialog(date: LocalDate) {
        dialogDate = date
        showDialog = true
    }

    // Sincronizar el mes visible con el calendario
    LaunchedEffect(visibleMonth) {
        calendarState.scrollToMonth(visibleMonth)
    }

    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFFF48FB1),
                        Color(0xFFFCE4EC)
                    )
                )
            )
            .padding(horizontal = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(24.dp))

        // Título
        Text(
            text = "Calendario",
            color = White,
            fontSize = 32.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 0.5.sp,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Tarjeta de toma simplificada
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(20.dp))
                .background(White)
                .padding(20.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                // Icono de reloj
                Box(
                    modifier = Modifier
                        .size(60.dp)
                        .clip(CircleShape)
                        .background(PinkLight),
                    contentAlignment = Alignment.Center
                ) {
                    Text("🕒", fontSize = 36.sp)
                }
                Spacer(modifier = Modifier.width(16.dp))
                Column(
                    modifier = Modifier.weight(1f),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "TOMA DE HOY",
                        color = Black,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center
                    )
                    Text(
                        text = "21 de mayo",
                        color = GrayText,
                        fontSize = 13.sp,
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        // Hora con fondo rosa claro
                        Box(
                            modifier = Modifier
                                .background(PinkLight, RoundedCornerShape(12.dp))
                                .padding(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text("8", fontWeight = FontWeight.Bold, fontSize = 20.sp, color = Black)
                                Text(":", fontWeight = FontWeight.Bold, fontSize = 20.sp, color = Pink)
                                Text("30", fontWeight = FontWeight.Bold, fontSize = 20.sp, color = Black)
                            }
                        }
                        Text("Pm", color = GrayText, fontSize = 14.sp, modifier = Modifier.padding(start = 4.dp))
                        Spacer(modifier = Modifier.width(12.dp))
                        Button(
                            onClick = { /* TODO: Registrar toma */ },
                            colors = ButtonDefaults.buttonColors(containerColor = Pink),
                            shape = RoundedCornerShape(16.dp),
                            contentPadding = PaddingValues(horizontal = 20.dp, vertical = 10.dp)
                        ) {
                            Text("Registrar toma", color = White, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        }
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Próxima toma en 02:00 hrs",
                        color = GrayText,
                        fontSize = 12.sp,
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Navegación de mes simplificada
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            // Flecha izquierda
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(White)
                    .clickable { visibleMonth = visibleMonth.minusMonths(1) },
                contentAlignment = Alignment.Center
            ) {
                Text("‹", color = PinkDark, fontSize = 24.sp, fontWeight = FontWeight.Bold)
            }
            Spacer(modifier = Modifier.width(16.dp))

            // Nombre del mes y año
            Box(
                modifier = Modifier
                    .background(Pink, shape = RoundedCornerShape(25.dp))
                    .padding(horizontal = 28.dp, vertical = 12.dp)
            ) {
                Text(
                    text = visibleMonth.month.getDisplayName(TextStyle.FULL, Locale.getDefault()).replaceFirstChar { it.uppercase() }.uppercase(),
                    color = White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    letterSpacing = 0.5.sp
                )
            }
            Spacer(modifier = Modifier.width(16.dp))

            // Flecha derecha
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(White)
                    .clickable { visibleMonth = visibleMonth.plusMonths(1) },
                contentAlignment = Alignment.Center
            ) {
                Text("›", color = PinkDark, fontSize = 24.sp, fontWeight = FontWeight.Bold)
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Encabezado de días de la semana
        Row(Modifier.fillMaxWidth()) {
            listOf("Domingo", "Lunes", "Martes", "Miércoles", "Jueves", "Viernes", "Sábado").forEach { dayName ->
                Text(
                    text = dayName.take(3),
                    modifier = Modifier.weight(1f),
                    style = MaterialTheme.typography.labelMedium,
                    color = White,
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.Medium
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Calendario estático simplificado
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(20.dp))
                .background(White)
                .padding(16.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(44.dp * 6)
            ) {
                VerticalCalendar(
                    state = calendarState,
                    userScrollEnabled = false,
                    dayContent = { day ->
                        val isSelected = day.date == selectedDate
                        val isToday = day.date == today
                        val isInMonth = day.position == com.kizitonwose.calendar.core.DayPosition.MonthDate

                        // Determinar el color de fondo de la celda
                        val cellColor = when {
                            isToday -> PinkDark
                            isSelected -> Pink
                            isInMonth -> PinkLight
                            else -> LightGray.copy(alpha = 0.5f)
                        }

                        // Determinar el color del texto
                        val textColor = when {
                            isToday -> White
                            isInMonth -> Black
                            else -> GrayText
                        }

                        Box(
                            modifier = Modifier
                                .aspectRatio(1f)
                                .padding(1.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(cellColor)
                                .border(
                                    width = if (isToday) 2.dp else 1.dp,
                                    color = if (isToday) PinkDark else GrayLine,
                                    shape = RoundedCornerShape(12.dp)
                                )
                                .clickable(enabled = isInMonth) {
                                    selectedDate = day.date
                                    openDialog(day.date)
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            Column(
                                modifier = Modifier.fillMaxSize(),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                // Número del día centrado
                                Box(
                                    modifier = Modifier.weight(1f),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = day.date.dayOfMonth.toString(),
                                        color = textColor,
                                        fontWeight = if (isToday || isSelected) FontWeight.Bold else FontWeight.Normal,
                                        fontSize = if (isToday) 16.sp else 14.sp,
                                        textAlign = TextAlign.Center
                                    )
                                }

                                // Área de indicadores en la parte inferior
                                if (isInMonth) {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(bottom = 3.dp),
                                        horizontalArrangement = Arrangement.Center,
                                        verticalAlignment = Alignment.Bottom
                                    ) {
                                        // Simulación de eventos basados en el día
                                        val dayOfMonth = day.date.dayOfMonth

                                        // Pastilla tomada (círculo pequeño rosa)
                                        if (dayOfMonth % 7 == 1) {
                                            Box(
                                                modifier = Modifier
                                                    .size(8.dp)
                                                    .clip(CircleShape)
                                                    .background(PinkDark)
                                            )
                                            if (dayOfMonth % 5 == 0 || dayOfMonth % 3 == 0) {
                                                Spacer(modifier = Modifier.width(2.dp))
                                            }
                                        }

                                        // Presencia del periodo (círculo rojo)
                                        if (dayOfMonth % 5 == 0) {
                                            Box(
                                                modifier = Modifier
                                                    .size(8.dp)
                                                    .clip(CircleShape)
                                                    .background(RedPeriod)
                                            )
                                            if (dayOfMonth % 3 == 0) {
                                                Spacer(modifier = Modifier.width(2.dp))
                                            }
                                        }

                                        // Observaciones (cuadrado amarillo)
                                        if (dayOfMonth % 3 == 0) {
                                            Box(
                                                modifier = Modifier
                                                    .size(8.dp)
                                                    .clip(RoundedCornerShape(2.dp))
                                                    .background(YellowObs)
                                            )
                                        }
                                    }
                                } else {
                                    // Espaciador para días fuera del mes
                                    Spacer(modifier = Modifier.height(12.dp))
                                }
                            }
                        }
                    },
                    monthHeader = {}
                )
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Leyenda de simbología simplificada
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(White.copy(alpha = 0.95f))
                .padding(20.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(18.dp)
                                .clip(CircleShape)
                                .background(PinkDark)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text("Pastilla tomada", fontSize = 12.sp, color = Black, fontWeight = FontWeight.Medium)
                    }
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .height(4.dp)
                                .width(26.dp)
                                .background(Pink, RoundedCornerShape(2.dp))
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text("Día marcado", fontSize = 12.sp, color = Black, fontWeight = FontWeight.Medium)
                    }
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(18.dp)
                                .clip(CircleShape)
                                .background(GrayText)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text("Día de descanso", fontSize = 12.sp, color = Black, fontWeight = FontWeight.Medium)
                    }
                }
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(18.dp)
                                .clip(CircleShape)
                                .background(RedPeriod)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text("Presencia del periodo", fontSize = 12.sp, color = Black, fontWeight = FontWeight.Medium)
                    }
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(18.dp)
                                .clip(RoundedCornerShape(4.dp))
                                .background(YellowObs)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text("Observaciones", fontSize = 12.sp, color = Black, fontWeight = FontWeight.Medium)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
    }

    // Modal personalizado para anotaciones del día
    if (showDialog && dialogDate != null) {
        var horaToma by remember { mutableStateOf("") }
        var cantidadPastillas by remember { mutableStateOf("") }
        var menstruacion by remember { mutableStateOf(false) }
        var observacion by remember { mutableStateOf(0) }
        val emojis = listOf("😊", "😐", "😴", "😢", "😠", "🤒", "😍", "🤧")

        AlertDialog(
            onDismissRequest = { showDialog = false },
            confirmButton = {
                Button(
                    onClick = {
                        // TODO: Guardar anotación
                        showDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Pink),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.padding(horizontal = 8.dp)
                ) {
                    Text("Agregar", color = White, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                Button(
                    onClick = { showDialog = false },
                    colors = ButtonDefaults.buttonColors(containerColor = White),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.padding(horizontal = 8.dp)
                ) {
                    Text("Cerrar", color = Pink, fontWeight = FontWeight.Bold)
                }
            },
            title = {
                Text(
                    text = "Anotaciones del día: ${dialogDate?.dayOfMonth} de ${dialogDate?.month?.getDisplayName(java.time.format.TextStyle.FULL, Locale.getDefault())?.replaceFirstChar { it.uppercase() }}",
                    color = Pink,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )
            },
            text = {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = horaToma,
                            onValueChange = { horaToma = it },
                            label = { Text("Hora de toma") },
                            modifier = Modifier.weight(1f)
                        )
                        OutlinedTextField(
                            value = cantidadPastillas,
                            onValueChange = { cantidadPastillas = it },
                            label = { Text("Cantidad de pastillas") },
                            modifier = Modifier.weight(1f)
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("Menstruación", fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.width(8.dp))
                        Switch(
                            checked = menstruacion,
                            onCheckedChange = { menstruacion = it },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = Pink,
                                checkedTrackColor = PinkLight
                            )
                        )
                        Text(if (menstruacion) "SÍ" else "NO", color = if (menstruacion) PinkDark else GrayText, fontWeight = FontWeight.Bold)
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Observaciones", fontWeight = FontWeight.Bold, color = Pink)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        emojis.forEachIndexed { idx, emoji ->
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(if (observacion == idx) PinkLight else Color.Transparent)
                                    .clickable { observacion = idx },
                                contentAlignment = Alignment.Center
                            ) {
                                Text(emoji, fontSize = 24.sp)
                            }
                        }
                    }
                }
            },
            shape = RoundedCornerShape(16.dp),
            containerColor = White
        )
    }
}