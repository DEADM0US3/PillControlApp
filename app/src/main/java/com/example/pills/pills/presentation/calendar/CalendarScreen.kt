package com.example.pills.pills.presentation.calendar

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
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
import androidx.compose.foundation.border
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults

private val Pink = Color(0xFFF48FB1)
private val PinkLight = Color(0xFFFFF0F6)
private val PinkDark = Color(0xFFD81B60)

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

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(horizontal = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(24.dp))
        Text(
            text = "Calendario",
            color = Pink,
            fontSize = 32.sp,
            style = MaterialTheme.typography.headlineLarge,
        )
        Spacer(modifier = Modifier.height(16.dp))
        // Sección superior de registrar toma
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(Color.White)
                .border(1.dp, Pink, RoundedCornerShape(16.dp))
                .padding(12.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                // Icono de reloj (placeholder)
                Box(
                    modifier = Modifier
                        .size(56.dp)
                        .clip(CircleShape)
                        .background(PinkLight),
                    contentAlignment = Alignment.Center
                ) {
                    Text("🕒", fontSize = 28.sp)
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text("TOMA DE HOY", color = Pink, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    Text("21 de mayo", color = Color.Gray, fontSize = 14.sp)
                    Spacer(modifier = Modifier.height(2.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("08:30 pm", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = PinkDark)
                        Spacer(modifier = Modifier.width(8.dp))
                        Button(
                            onClick = { /* TODO: Registrar toma */ },
                            colors = ButtonDefaults.buttonColors(containerColor = Pink),
                            shape = RoundedCornerShape(8.dp),
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 2.dp)
                        ) {
                            Text("Registrar toma", color = Color.White, fontWeight = FontWeight.Bold)
                        }
                    }
                    Spacer(modifier = Modifier.height(2.dp))
                    Text("Próxima toma en 02:00 hrs", color = Color.Gray, fontSize = 12.sp)
                }
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
        // Navegación de mes
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            // Flecha izquierda
            Text(
                text = "<",
                fontSize = 28.sp,
                color = Pink,
                modifier = Modifier
                    .clickable { visibleMonth = visibleMonth.minusMonths(1) }
                    .padding(8.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            // Nombre del mes y año
            Box(
                modifier = Modifier
                    .background(Pink, shape = RoundedCornerShape(16.dp))
                    .padding(horizontal = 24.dp, vertical = 4.dp)
            ) {
                Text(
                    text = visibleMonth.month.getDisplayName(TextStyle.FULL, Locale.getDefault()).replaceFirstChar { it.uppercase() } + " ${visibleMonth.year}",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
            // Flecha derecha
            Text(
                text = ">",
                fontSize = 28.sp,
                color = Pink,
                modifier = Modifier
                    .clickable { visibleMonth = visibleMonth.plusMonths(1) }
                    .padding(8.dp)
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
        // Encabezado de días de la semana
        Row(Modifier.fillMaxWidth()) {
            listOf("Domingo", "Lunes", "Martes", "Miércoles", "Jueves", "Viernes", "Sábado").forEach { dayName ->
                Text(
                    text = dayName.take(3),
                    modifier = Modifier.weight(1f),
                    style = MaterialTheme.typography.labelMedium,
                    color = Pink,
                    textAlign = TextAlign.Center
                )
            }
        }
        Spacer(modifier = Modifier.height(4.dp))
        VerticalCalendar(
            state = calendarState,
            dayContent = { day ->
                val isSelected = day.date == selectedDate
                val isToday = day.date == today
                val isInMonth = day.position == com.kizitonwose.calendar.core.DayPosition.MonthDate
                Box(
                    modifier = Modifier
                        .aspectRatio(1f)
                        .padding(2.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(
                            when {
                                isSelected -> Pink
                                isInMonth -> PinkLight
                                else -> Color(0xFFF3F3F3)
                            }
                        )
                        .border(
                            width = if (isToday) 2.dp else 1.dp,
                            color = if (isToday) PinkDark else Color.LightGray,
                            shape = RoundedCornerShape(8.dp)
                        )
                        .clickable(enabled = isInMonth) {
                            selectedDate = day.date
                            openDialog(day.date)
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = day.date.dayOfMonth.toString(),
                        color = when {
                            isSelected -> Color.White
                            isInMonth -> PinkDark
                            else -> Color.LightGray
                        }
                    )
                }
            }
        )
        Spacer(modifier = Modifier.height(16.dp))
        // Leyenda de simbología
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(16.dp)
                        .clip(CircleShape)
                        .background(PinkDark)
                ) {}
                Spacer(modifier = Modifier.width(8.dp))
                Text("Pastilla tomada", fontSize = 14.sp)
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(16.dp)
                        .clip(CircleShape)
                        .background(Color.Red)
                ) {}
                Spacer(modifier = Modifier.width(8.dp))
                Text("Presencia del periodo", fontSize = 14.sp)
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(16.dp)
                        .clip(CircleShape)
                        .background(Pink)
                ) {}
                Spacer(modifier = Modifier.width(8.dp))
                Text("Día marcado", fontSize = 14.sp)
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(16.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .background(Color(0xFFFFF59D))
                ) {}
                Spacer(modifier = Modifier.width(8.dp))
                Text("Observaciones", fontSize = 14.sp)
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(16.dp)
                        .clip(CircleShape)
                        .background(Color.Gray)
                ) {}
                Spacer(modifier = Modifier.width(8.dp))
                Text("Día de descanso", fontSize = 14.sp)
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
    }

    // Modal personalizado para anotaciones del día
    if (showDialog && dialogDate != null) {
        var horaToma by remember { mutableStateOf("") }
        var cantidadPastillas by remember { mutableStateOf("") }
        var menstruacion by remember { mutableStateOf(false) }
        var observacion by remember { mutableStateOf(0) } // índice del emoji seleccionado
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
                    Text("Agregar", color = Color.White, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                Button(
                    onClick = { showDialog = false },
                    colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.padding(horizontal = 8.dp)
                ) {
                    Text("Cerrar", color = Pink, fontWeight = FontWeight.Bold)
                }
            },
            title = {
                Text(
                    text = "Anotaciones del día:  ${dialogDate?.dayOfMonth} de ${dialogDate?.month?.getDisplayName(java.time.format.TextStyle.FULL, Locale.getDefault())?.replaceFirstChar { it.uppercase() }}",
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
                        Text(if (menstruacion) "SÍ" else "NO", color = if (menstruacion) PinkDark else Color.Gray, fontWeight = FontWeight.Bold)
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
            containerColor = Color.White
        )
    }
} 