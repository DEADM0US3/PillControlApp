package com.example.pills.pills.presentation.calendar

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.text.font.FontFamily

import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.sp
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.res.painterResource

private val Pink = Color(0xFFEA5A8C)
private val PinkLight = Color(0xFFFFF0F6)
private val PinkDark = Color(0xFFD81B60)
private val PinkShadow = Color(0x33EA5A8C)
private val CardShadow = Color(0x22000000)
private val Black = Color(0xFF222222)
private val GrayLine = Color(0xFFBDBDBD)
private val LightGray = Color(0xFFF3F3F3)
private val GrayText = Color(0xFFBDBDBD)

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
            .background(Color.White)
            .padding(horizontal = 12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(18.dp))
        // Título
        Text(
            text = "Calendario",
            color = Pink,
            fontSize = 34.sp,
            fontWeight = FontWeight.ExtraBold,
            letterSpacing = 1.sp,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp, bottom = 8.dp),
            textAlign = TextAlign.Center
        )
        // Tarjeta de toma
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .shadow(2.dp, RoundedCornerShape(16.dp))
                .clip(RoundedCornerShape(16.dp))
                .background(Color.White)
                .border(2.dp, Pink, RoundedCornerShape(16.dp))
                .padding(10.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                // Icono de reloj (reemplaza por tu SVG)
                Box(
                    modifier = Modifier
                        .size(54.dp)
                        .clip(CircleShape)
                        .background(LightGray),
                    contentAlignment = Alignment.Center
                ) {
                    // TODO: Reemplazar por tu SVG de reloj en caso que quieren que sea identico con el diseño jeje
                    // Icon(painterResource(id = R.drawable.ic_clock), contentDescription = "Reloj")
                    Text("🕒", fontSize = 32.sp)
                }
                Spacer(modifier = Modifier.width(10.dp))
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
                    Spacer(modifier = Modifier.height(2.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        // Hora con fondo rosa claro y dígitos grandes
                        Box(
                            modifier = Modifier
                                .background(PinkLight, RoundedCornerShape(6.dp))
                                .padding(horizontal = 4.dp, vertical = 2.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text("08", fontWeight = FontWeight.Bold, fontSize = 20.sp, color = Black)
                                Text(":", fontWeight = FontWeight.Bold, fontSize = 20.sp, color = Pink)
                                Text("30", fontWeight = FontWeight.Bold, fontSize = 20.sp, color = Black)
                            }
                        }
                        Text("Pm", color = GrayText, fontSize = 14.sp, modifier = Modifier.padding(start = 4.dp))
                        Spacer(modifier = Modifier.width(10.dp))
                        Button(
                            onClick = { /* TODO: Registrar toma */ },
                            colors = ButtonDefaults.buttonColors(containerColor = Pink),
                            shape = RoundedCornerShape(12.dp),
                            elevation = ButtonDefaults.buttonElevation(6.dp),
                            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 2.dp)
                        ) {
                            Text("Registrar toma", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        }
                    }
                    Spacer(modifier = Modifier.height(2.dp))
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
        Spacer(modifier = Modifier.height(10.dp))
        // Navegación de mes
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            // Flecha izquierda (reemplaza por tu SVG)
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .clickable { visibleMonth = visibleMonth.minusMonths(1) },
                contentAlignment = Alignment.Center
            ) {
                // TODO: Reemplaza por tu SVG de flecha izquierda
                // Icon(painterResource(id = R.drawable.ic_arrow_left), contentDescription = "Anterior")
                Text("<<", color = Black, fontSize = 24.sp)
            }
            Spacer(modifier = Modifier.width(8.dp))
            // Nombre del mes y año
            Box(
                modifier = Modifier
                    .background(Pink, shape = RoundedCornerShape(16.dp))
                    .padding(horizontal = 32.dp, vertical = 4.dp)
            ) {
                Text(
                    text = visibleMonth.month.getDisplayName(TextStyle.FULL, Locale.getDefault()).replaceFirstChar { it.uppercase() } + " ${visibleMonth.year}",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
            // Flecha derecha (reemplaza por tu SVG)
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .clickable { visibleMonth = visibleMonth.plusMonths(1) },
                contentAlignment = Alignment.Center
            ) {
                // TODO: Reemplaza por tu SVG de flecha derecha
                // Icon(painterResource(id = R.drawable.ic_arrow_right), contentDescription = "Siguiente")
                Text(">>", color = Black, fontSize = 24.sp)
            }
        }
        // Línea negra
        Spacer(modifier = Modifier.height(8.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(3.dp)
                .background(Black)
        )
        Spacer(modifier = Modifier.height(4.dp))
        // Encabezado de días de la semana
        Row(Modifier.fillMaxWidth()) {
            listOf("Domingo", "Lunes", "Martes", "Miércoles", "Jueves", "Viernes", "Sábado").forEach { dayName ->
                Text(
                    text = dayName.take(3),
                    modifier = Modifier.weight(1f),
                    style = MaterialTheme.typography.labelMedium,
                    color = Black,
                    textAlign = TextAlign.Center
                )
            }
        }
        Spacer(modifier = Modifier.height(4.dp))
        // Calendario estático (sin scroll vertical)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(44.dp * 6) // 6 filas de 44dp
        ) {
            VerticalCalendar(
                state = calendarState,
                userScrollEnabled = false,
                dayContent = { day ->
                    val isSelected = day.date == selectedDate
                    val isToday = day.date == today
                    val isInMonth = day.position == com.kizitonwose.calendar.core.DayPosition.MonthDate
                    val cellColor = when {
                        isSelected -> PinkDark
                        isInMonth -> Pink
                        else -> LightGray.copy(alpha = 0.5f)
                    }
                    val shadow = if (isInMonth) 4.dp else 0.dp
                    Box(
                        modifier = Modifier
                            .aspectRatio(1f)
                            .padding(4.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(cellColor)
                            .then(if (isInMonth) Modifier.shadow(shadow, RoundedCornerShape(12.dp)) else Modifier)
                            .border(
                                width = if (isToday) 2.dp else 1.dp,
                                color = if (isToday) PinkDark else GrayLine,
                                shape = RoundedCornerShape(12.dp)
                            )
                            .clickable(enabled = isInMonth) {
                                selectedDate = day.date
                                openDialog(day.date)
                            },
                        contentAlignment = Alignment.TopCenter
                    ) {
                        Column(
                            modifier = Modifier.fillMaxSize(),
                            verticalArrangement = Arrangement.SpaceBetween,
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            // Número del día
                            Text(
                                text = day.date.dayOfMonth.toString(),
                                color = if (isInMonth) Black else Color.LightGray,
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp,
                                modifier = Modifier.padding(top = 6.dp)
                            )
                            // Placeholder para íconos de eventos (esquina inferior)
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(bottom = 4.dp, start = 4.dp, end = 4.dp),
                                horizontalArrangement = Arrangement.Start,
                                verticalAlignment = Alignment.Bottom
                            ) {
                                // TODO: Reemplaza estos Box por tus SVGs según el evento de cada día
                                // Ejemplo:
                                // if (hayPastillaTomada) Icon(painterResource(id = R.drawable.ic_pastilla), ...)
                                // if (hayPeriodo) Icon(painterResource(id = R.drawable.ic_periodo), ...)
                                // if (hayObservacion) Icon(painterResource(id = R.drawable.ic_observacion), ...)
                                if (isInMonth && day.date.dayOfMonth % 7 == 0) {
                                    Box(
                                        modifier = Modifier
                                            .size(14.dp)
                                            .clip(CircleShape)
                                            .background(PinkDark)
                                    ) {}
                                }
                                if (isInMonth && day.date.dayOfMonth % 5 == 0) {
                                    Box(
                                        modifier = Modifier
                                            .size(14.dp)
                                            .clip(CircleShape)
                                            .background(Color.Red)
                                            .padding(start = 2.dp)
                                    ) {}
                                }
                                if (isInMonth && day.date.dayOfMonth % 3 == 0) {
                                    Box(
                                        modifier = Modifier
                                            .size(14.dp)
                                            .clip(RoundedCornerShape(3.dp))
                                            .background(Color(0xFFFFF59D))
                                            .padding(start = 2.dp)
                                    ) {}
                                }
                            }
                        }
                    }
                },
                monthHeader = {}
            )
        }
        Spacer(modifier = Modifier.height(35.dp))
        // Leyenda de simbología
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    // TODO: Reemplaza por tu SVG de pastilla tomada
                    // Icon(painterResource(id = R.drawable.ic_pastilla), ...)
                    Box(
                        modifier = Modifier
                            .size(20.dp)
                            .clip(CircleShape)
                            .background(PinkLight)
                    ) {}
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Pastilla tomada", fontSize = 14.sp, color = Black)
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    // TODO: Reemplaza por tu SVG de día marcado
                    // Icon(painterResource(id = R.drawable.ic_marcado), ...)
                    Box(
                        modifier = Modifier
                            .height(5.dp)
                            .width(28.dp)
                            .background(Pink, RoundedCornerShape(2.dp))
                    ) {}
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Día marcado", fontSize = 14.sp, color = Black)
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    // TODO: Reemplaza por tu SVG de día de descanso
                    // Icon(painterResource(id = R.drawable.ic_descanso), ...)
                    Box(
                        modifier = Modifier
                            .size(20.dp)
                            .clip(CircleShape)
                            .background(Color.Black.copy(alpha = 0.15f))
                    ) {}
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Día de descanso", fontSize = 14.sp, color = Black)
                }
            }
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    // TODO: Reemplaza por tu SVG de periodo
                    // Icon(painterResource(id = R.drawable.ic_periodo), ...)
                    Box(
                        modifier = Modifier
                            .size(20.dp)
                            .clip(CircleShape)
                            .background(Color.Red)
                    ) {}
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Presencia del periodo", fontSize = 14.sp, color = Black)
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    // TODO: Reemplaza por tu SVG de observaciones
                    // Icon(painterResource(id = R.drawable.ic_observacion), ...)
                    Box(
                        modifier = Modifier
                            .size(20.dp)
                            .clip(RoundedCornerShape(4.dp))
                            .background(Color(0xFFFFF59D))
                    ) {}
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Observaciones", fontSize = 14.sp, color = Black)
                }
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
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