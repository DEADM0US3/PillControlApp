package com.example.pills.pills.presentation.calendar

import android.util.Log
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
import com.example.pills.pills.presentation.components.TakePillComponent
import com.example.pills.pills.presentation.cycle.CycleViewModel
import com.kizitonwose.calendar.compose.VerticalCalendar
import com.kizitonwose.calendar.compose.rememberCalendarState
import org.koin.androidx.compose.koinViewModel
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

val colorMenstruation = Color(0xFFFFC1C1)  // Rosa claro
val colorFertile = Color(0xFFC1FFC1)       // Verde claro
val colorOvulation = Color(0xFFFFFFC1)     // Amarillo claro
val colorPillTaken = Color(0xFFA6FFC9)     // Verde fuerte

@Composable
fun CalendarScreen(
    onDayClick: (LocalDate) -> Unit = {},
    cycleViewModel: CycleViewModel = koinViewModel(),
    pillViewModel: PillViewModel = koinViewModel()
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


    LaunchedEffect(visibleMonth) {
        cycleViewModel.fetchActiveCycle()
        pillViewModel.loadPillsOfMonth(visibleMonth.year, visibleMonth.monthValue)
        calendarState.scrollToMonth(visibleMonth)

    }

    val cycleState by cycleViewModel.cycleState.collectAsState()
    val calendarEvents by cycleViewModel.calendarEvents.collectAsState()
    val pillsOfMonth by pillViewModel.uiState.collectAsState()

    Log.d("CalendarScreen", "Pills of month: $pillsOfMonth")

    fun openDialog(date: LocalDate) {
        dialogDate = date
        showDialog = true
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
        TakePillComponent()

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
                        val dayEvent = calendarEvents[day.date]
                        
                        
                        val cellColor = when {
                            pillsOfMonth.pillsOfMonth.any {
                                LocalDate.parse(it.day_taken) == day.date &&
                                        it.status == "taken"
                            } -> colorPillTaken
                            isToday -> White
                            isSelected -> Pink
                            dayEvent?.isMenstruation == true -> colorMenstruation
                            dayEvent?.isOvulation == true -> GrayLine
                            dayEvent?.other == true -> colorOvulation
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
                                        if (
                                            pillsOfMonth.pillsOfMonth.any {
                                                LocalDate.parse(it.day_taken) == day.date &&
                                                        it.status == "taken"
                                            }
                                        ) {
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
                }
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
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