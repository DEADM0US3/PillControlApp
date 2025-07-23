package com.example.pills.pills.presentation.components

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.Black
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.pills.pills.infrastructure.ViewModel.PillViewModel
import com.example.pills.pills.presentation.cycle.CycleViewModel
import com.kizitonwose.calendar.compose.rememberCalendarState
import org.koin.androidx.compose.koinViewModel
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalTime
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit

private val Pink = Color(0xFFEA5A8C)
private val PinkLight = Color(0xFFFFF0F6)
private val LightGray = Color(0xFFF3F3F3)
private val GrayText = Color(0xFFBDBDBD)
private val White = Color(0xFFFFFFFF)
// private val Black = Color(0xFF222222)

@Composable
fun TakePillComponent(
    cycleViewModel: CycleViewModel = koinViewModel(),
    pillViewModel: PillViewModel = koinViewModel()
) {

    val today = remember { LocalDate.now() }
    var visibleMonth by remember { mutableStateOf(YearMonth.now()) }


    LaunchedEffect(visibleMonth) {
        cycleViewModel.fetchActiveCycle()
        pillViewModel.loadPillOfToday(today)
    }

    val cycleState by cycleViewModel.cycleState.collectAsState()
    val pillsOfMonth by pillViewModel.uiState.collectAsState()

    Log.d("CalendarScreen", "Pills of month: $pillsOfMonth")

    val formattedDate = remember {
        today.format(DateTimeFormatter.ofPattern("d 'de' MMMM"))
    }

    val isTakenToday = pillsOfMonth?.pillOfToday?.let {
        LocalDate.parse(it.day_taken) == today && it.status == "taken"
    } == true

    val now = LocalTime.now()

    val isTimeToTake = cycleState?.getOrNull()?.take_hour?.let { hourStr ->
        try {
            val takeTime = LocalTime.parse(hourStr, DateTimeFormatter.ofPattern("HH:mm:ss"))
            val now = LocalTime.now()
            val minutesDiff = ChronoUnit.MINUTES.between(now, takeTime)

            minutesDiff <= 30
        } catch (e: Exception) {
            false
        }
    } ?: false

//    val isTimeToTake = cycleState?.getOrNull()?.take_hour?.let { hourStr ->
//        try {
//            val takeTime = LocalTime.parse(hourStr, DateTimeFormatter.ofPattern("HH:mm:ss"))
//            val now = LocalTime.now()
//
//            !now.isBefore(takeTime)
//
//
//        } catch (e: Exception) {
//            false
//        }
//    } ?: false

    val takeHourFormatted = cycleState?.getOrNull()?.take_hour?.let { hourStr ->
        try {
            val time = LocalTime.parse(hourStr, DateTimeFormatter.ofPattern("HH:mm:ss"))
            val hour12 = if (time.hour % 12 == 0) 12 else time.hour % 12
            val amPm = if (time.hour < 12) "Am" else "Pm"
            Triple(hour12.toString(), time.minute.toString().padStart(2, '0'), amPm)
        } catch (e: Exception) {
            Triple("--", "--", "")
        }
    } ?: Triple("--", "--", "")

    Log.d("TakePillComponent", "Take hour formatted: ${cycleState?.getOrNull()?.take_hour}")

    val nextDoseText = cycleState?.getOrNull()?.take_hour?.let { hourStr ->
        try {
            val takeTime = LocalTime.parse(hourStr, DateTimeFormatter.ofPattern("HH:mm:ss"))
            val now = LocalTime.now()

            val minutesDiff = ChronoUnit.MINUTES.between(now, takeTime)

            when {
                minutesDiff > 0 -> {
                    val hours = minutesDiff / 60
                    val minutes = minutesDiff % 60
                    "Próxima toma en %02d:%02d hrs".format(hours, minutes)
                }
                minutesDiff >= -30 -> { // Dentro de ventana de 30 min antes o después
                    "Puedes tomarla ahora"
                }
                else -> {
                    "Toma atrasada"
                }
            }
        } catch (e: Exception) {
            "Hora de toma no disponible"
        }
    } ?: "Hora de toma no disponible"

    var AlertDialogTake by remember { mutableStateOf(false) }
    val Pink = Color(0xFFE91E63) // O tu tono exacto


    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(White)
            .padding(20.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
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
                    color = com.example.pills.ui.theme.Black,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center
                )
                Text(
                    text = formattedDate,
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
                    Box(
                        modifier = Modifier
                            .background(PinkLight, RoundedCornerShape(12.dp))
                            .padding(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(takeHourFormatted.first, fontWeight = FontWeight.Bold, fontSize = 20.sp, color = com.example.pills.ui.theme.Black)
                            Text(":", fontWeight = FontWeight.Bold, fontSize = 20.sp, color = Pink)
                            Text(takeHourFormatted.second, fontWeight = FontWeight.Bold, fontSize = 20.sp, color = com.example.pills.ui.theme.Black)
                        }
                    }
                    Text(
                        takeHourFormatted.third,
                        color = GrayText,
                        fontSize = 14.sp,
                        modifier = Modifier.padding(start = 4.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Button(
                        onClick = {
                            if (!isTakenToday && isTimeToTake) {
                                pillViewModel.takePill(cycleState?.getOrNull()?.id ?: "", today, "taken", null)
                                AlertDialogTake = true
                            }
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = when {
                                isTakenToday -> LightGray
                                !isTimeToTake -> LightGray
                                else -> Pink
                            }
                        ),
                        shape = RoundedCornerShape(16.dp),
                        contentPadding = PaddingValues(horizontal = 20.dp, vertical = 10.dp),
                        enabled = !isTakenToday
                    ) {
                        Text(
                            text = when {
                                isTakenToday -> "Ya registrada"
                                !isTimeToTake -> "Espera la hora"
                                else -> "Registrar toma"
                            },
                            color = when {
                                isTakenToday || !isTimeToTake -> GrayText
                                else -> White
                            },
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp
                        )
                    }
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = nextDoseText,
                    color = GrayText,
                    fontSize = 12.sp,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center
                )

            }
        }
    }

    if (AlertDialogTake) {
        AlertDialog(
            onDismissRequest = { AlertDialogTake = false },
            confirmButton = {
                Button(
                    onClick = { AlertDialogTake = false },
                    colors = ButtonDefaults.buttonColors(containerColor = Pink),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.padding(8.dp)
                ) {
                    Text(
                        text = "Entendido",
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                }
            },
            title = {
                Text(
                    text = "¡Toma Registrada! 💊",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF333333)
                    )
                )
            },
            text = {
                Text(
                    text = "La toma de la pastilla se ha guardado exitosamente. ¡Sigue así! 💖",
                    style = MaterialTheme.typography.bodyMedium.copy(color = Color(0xFF666666))
                )
            },
            containerColor = Color.White,
            shape = RoundedCornerShape(20.dp),
            tonalElevation = 6.dp
        )
    }
}


