package com.example.pills.pills.presentation.calendar

import android.util.Log
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.material3.TextFieldDefaults
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
import com.example.pills.pills.domain.entities.Pill
import com.example.pills.pills.infrastructure.ViewModel.PillViewModel
import com.example.pills.pills.presentation.components.TakePillComponent
import com.example.pills.pills.presentation.cycle.CycleViewModel
import com.example.pills.pills.presentation.loading.LoadingScreen
import com.kizitonwose.calendar.compose.VerticalCalendar
import com.kizitonwose.calendar.compose.rememberCalendarState
import kotlinx.coroutines.delay
import org.koin.androidx.compose.koinViewModel
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.TextStyle
import java.util.*


// Paleta de colores simplificada y estable
private val Pink = Color(0xFFEA5A8C)
private val PinkLight = Color(0xFFFFF0F6)
private val PinkLigth2 = Color(0xFFFF98CD)
private val PinkDark = Color(0xFFD81B60)
private val PinkShadow = Color(0x33EA5A8C)
private val CardShadow = Color(0x22000000)
private val Black = Color(0xFF222222)
private val GrayLine = Color(0xFFBDBDBD)
private val LightGray = Color(0xFFF3F3F3)
private val GrayText = Color(0xFFBDBDBD)
private val White = Color(0xFFFFFFFF)
private val RedPeriod = Color(0xFFE53935)
private val YellowObs = Color(0xFF7D00B9)

val colorMenstruation = Color(0xFFFFC1C1)  // Rosa claro
val colorFertile = Color(0xFFC1FFC1)       // Verde claro
val colorOvulation = Color(0xFFEDF5FD)     // Amarillo claro
val colorPillTaken = Color(0xFFA6FFC9)     // Verde fuerte
val Today = Color(0xFFA6E3FF)     // Verde fuerte




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
    var infoDialogMessage by remember { mutableStateOf<Pair<String, String>?>(null) }

    fun showInfoDialog(title: String, message: String) {
        infoDialogMessage = title to message
    }

    val scrollState = rememberScrollState()
    var isLoading by remember { mutableStateOf(true) }

    LaunchedEffect(visibleMonth) {
        cycleViewModel.fetchActiveCycle()
        pillViewModel.loadPillsOfMonth(visibleMonth.year, visibleMonth.monthValue)
        calendarState.scrollToMonth(visibleMonth)
    }

    val cycleState by cycleViewModel.cycleState.collectAsState()
    val calendarEvents by cycleViewModel.calendarEvents.collectAsState()
    val pillsOfMonth by pillViewModel.uiState.collectAsState()

    LaunchedEffect(Unit) {
        delay(400)
        isLoading = false
    }

    if (isLoading) {
        return LoadingScreen()
    }

    fun openDialog(date: LocalDate) {
        val pillTakenToday = pillsOfMonth.pillsOfMonth.any {
            LocalDate.parse(it.day_taken) == LocalDate.now() && it.status == "taken"
        }
        val pillTakenOnDate = pillsOfMonth.pillsOfMonth.any {
            LocalDate.parse(it.day_taken) == date && it.status == "taken"
        }
        when {
            date.isAfter(LocalDate.now()) -> {
                showInfoDialog(
                    "No puedes registrar una toma en el futuro.",
                    "Selecciona una fecha válida."
                )
            }
            date == LocalDate.now() && pillTakenToday -> {
                showInfoDialog(
                    "Ya tomaste la pastilla hoy.",
                    "No es posible editar este día."
                )
            }
            pillTakenOnDate -> {
                showInfoDialog(
                    "Ya tomaste la pastilla este día.",
                    "No puedes editar un registro ya confirmado."
                )
            }
            else -> {
                dialogDate = date
                showDialog = true
            }
        }
    }

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

        TakePillComponent()

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
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

            Box(
                modifier = Modifier
                    .background(Pink, shape = RoundedCornerShape(25.dp))
                    .padding(horizontal = 28.dp, vertical = 12.dp)
            ) {
                Text(
                    text = visibleMonth.month.getDisplayName(TextStyle.FULL, Locale.getDefault())
                        .replaceFirstChar { it.uppercase() }.uppercase(),
                    color = White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    letterSpacing = 0.5.sp
                )
            }
            Spacer(modifier = Modifier.width(16.dp))

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

                        val pillTakenToday = pillsOfMonth.pillsOfMonth.any {
                            LocalDate.parse(it.day_taken) == day.date && it.status == "taken"
                        }

                        val cellColor = when {
                            day.date.isAfter(today) -> LightGray.copy(alpha = 0.4f) // gris para futuros
                            pillTakenToday -> PinkLight // color para días con pastilla tomada
                            isToday -> Today
                            isSelected -> PinkLight
                            dayEvent?.isMenstruation == true -> colorMenstruation
                            dayEvent?.isOvulation == true -> LightGray.copy(alpha = 2.5f)
                            dayEvent?.other == true -> colorOvulation
                            isInMonth -> White
                            else -> LightGray.copy(alpha = 0.5f)
                        }

                        val textColor = when {
                            day.date.isAfter(today) -> GrayText.copy(alpha = 0.6f)
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
                                    width = 1.dp,
                                    color = GrayLine,
                                    shape = RoundedCornerShape(12.dp)
                                )
                                .clickable(enabled = isInMonth && !day.date.isAfter(today)) {
                                    selectedDate = day.date
                                    openDialog(day.date)
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            Column(
                                modifier = Modifier.fillMaxSize(),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
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

                                if (isInMonth) {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(bottom = 3.dp),
                                        horizontalArrangement = Arrangement.Center,
                                        verticalAlignment = Alignment.Bottom
                                    ) {
                                        val dayOfMonth = day.date.dayOfMonth

                                        if (pillsOfMonth.pillsOfMonth.any {
                                                LocalDate.parse(it.day_taken) == day.date &&
                                                        it.status == "taken"
                                            }) {
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

                                        if (pillsOfMonth.pillsOfMonth.any {
                                                LocalDate.parse(it.day_taken) == day.date &&
                                                        it.complications != null
                                            }) {
                                            Box(
                                                modifier = Modifier
                                                    .size(8.dp)
                                                    .clip(RoundedCornerShape(2.dp))
                                                    .background(YellowObs)
                                            )
                                        }
                                    }
                                } else {
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
                        Text(
                            "Pastilla tomada",
                            fontSize = 12.sp,
                            color = Black,
                            fontWeight = FontWeight.Medium
                        )
                    }
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .height(4.dp)
                                .width(26.dp)
                                .background(Pink, RoundedCornerShape(2.dp))
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            "Día marcado",
                            fontSize = 12.sp,
                            color = Black,
                            fontWeight = FontWeight.Medium
                        )
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
                        Text(
                            "Día de descanso",
                            fontSize = 12.sp,
                            color = Black,
                            fontWeight = FontWeight.Medium
                        )
                    }
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(18.dp)
                                .clip(RoundedCornerShape(4.dp))
                                .background(YellowObs)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            "Observaciones",
                            fontSize = 12.sp,
                            color = Black,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
    }

    if (showDialog && dialogDate != null) {
        PillEditDialog(
            date = dialogDate!!,
            onDismiss = { showDialog = false },
            pillViewModel = pillViewModel,
            cycleViewModel = cycleViewModel
        )
    }

    infoDialogMessage?.let { (title, message) ->
        AlertDialog(
            onDismissRequest = { infoDialogMessage = null },
            confirmButton = {
                Button(
                    onClick = { infoDialogMessage = null },
                    colors = ButtonDefaults.buttonColors(containerColor = Pink),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text("Entendido", color = White)
                }
            },
            title = {
                Text(text = title, fontWeight = FontWeight.Bold, color = Pink)
            },
            text = {
                Text(text = message, color = Black)
            },
            containerColor = White,
            shape = RoundedCornerShape(16.dp)
        )
    }
}



@Composable
fun PillEditDialog(
    date: LocalDate,
    pillViewModel: PillViewModel,
    cycleViewModel: CycleViewModel,
    onDismiss: () -> Unit,
) {

    LaunchedEffect(date) {
        pillViewModel.loadPillOfDay(date)
    }

    val uiState by pillViewModel.uiState.collectAsState()
    val activeCycleResult by cycleViewModel.cycleState.collectAsState()

    var hour by remember { mutableStateOf("") }
    var observationText by remember { mutableStateOf("") }
    var isHourValid by remember { mutableStateOf(true) }

    val pill = uiState.pillOfDay
    val cycle = activeCycleResult?.getOrNull()

    // Validación de hora
    fun isValidHourFormat(input: String): Boolean {
        return Regex("^([01]?[0-9]|2[0-3]):[0-5][0-9]").matches(input)
    }

    // Carga valores si ya existe la pastilla
    LaunchedEffect(pill) {
        pill?.let {
            hour = it.hour_taken ?: ""
            observationText = it.complications ?: ""
        }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        shape = RoundedCornerShape(16.dp),
        containerColor = White,
        title = {
            Text(
                text = "Editar toma del día ${date.dayOfMonth} de ${date.month.getDisplayName(TextStyle.FULL, Locale.getDefault()).replaceFirstChar { it.uppercase() }}",
                color = Pink,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp
            )
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("Hora (HH:mm)", color = Pink, fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                OutlinedTextField(
                    value = hour,
                    label = { Text("Hora de toma", color = Black.copy(alpha = 0.4f)) },
                    onValueChange = {
                        hour = it
                        isHourValid = it.isBlank() || isValidHourFormat(it)
                    },
                    isError = !isHourValid,
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 6.dp),
                    colors = TextFieldDefaults.colors(
                        focusedIndicatorColor = Pink,
                        focusedLabelColor = Pink,
                        errorIndicatorColor = Color.Red,
                        unfocusedContainerColor = Color.Transparent,
                        focusedContainerColor = Color.Transparent
                    )
                )

                if (!isHourValid) {
                    Text(
                        "Formato inválido. Usa HH:mm",
                        color = Color.Red,
                        fontSize = 12.sp,
                        modifier = Modifier.align(Alignment.Start)
                    )
                }

                Spacer(Modifier.height(12.dp))

                Text("Observaciones", color = Pink, fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                OutlinedTextField(
                    value = observationText,
                    onValueChange = { observationText = it },
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(100.dp)
                        .padding(vertical = 6.dp),
                    placeholder = {
                        Text("Escribe aquí tus observaciones...", color = Black.copy(alpha = 0.4f))
                    },
                    colors = TextFieldDefaults.colors(
                        focusedIndicatorColor = Pink,
                        focusedLabelColor = Pink,
                        unfocusedContainerColor = Color.Transparent,
                        focusedContainerColor = Color.Transparent
                    ),
                    maxLines = 4,
                    singleLine = false
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (!isHourValid) return@Button

                    pill?.let {
                        pillViewModel.editPill(
                            it.id.toString(),
                            hour.ifBlank { null },
                            "taken",
                            observationText.ifBlank { null }
                        )
                        pillViewModel.loadPillsOfMonth(date.year, date.monthValue)
                    } ?: run {
                        cycle?.let {
                            pillViewModel.takePill(
                                cycleId = it.id.toString(),
                                date = date,
                                hour_taken = hour.ifBlank { null },
                                status = "taken",
                                complications = observationText.ifBlank { null }
                            )
                        }
                        pillViewModel.loadPillsOfMonth(date.year, date.monthValue)
                    }
                    onDismiss()
                },
                colors = ButtonDefaults.buttonColors(containerColor = Pink),
                shape = RoundedCornerShape(12.dp),
                contentPadding = PaddingValues(horizontal = 20.dp, vertical = 10.dp)
            ) {
                Text("Guardar", color = White, fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            Button(
                onClick = onDismiss,
                colors = ButtonDefaults.outlinedButtonColors(contentColor = Pink),
                shape = RoundedCornerShape(12.dp),
                border = BorderStroke(1.dp, Pink),
                contentPadding = PaddingValues(horizontal = 20.dp, vertical = 10.dp)
            ) {
                Text("Cancelar", fontWeight = FontWeight.Bold)
            }
        }
    )
}
