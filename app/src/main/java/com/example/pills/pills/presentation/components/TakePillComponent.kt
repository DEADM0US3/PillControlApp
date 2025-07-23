package com.example.pills.pills.presentation.components

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.wear.compose.material.ScalingLazyColumn
import com.example.pills.pills.infrastructure.ViewModel.PillViewModel
import com.example.pills.pills.presentation.cycle.CycleViewModel
import com.kizitonwose.calendar.compose.rememberCalendarState
import org.koin.androidx.compose.koinViewModel
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalDateTime
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

    val context = LocalContext.current
    val now = LocalDateTime.now()
    val hourTake = now.format(DateTimeFormatter.ofPattern("HH:mm"))

    val formattedDate = today.format(DateTimeFormatter.ofPattern("d MMM"))

    val isTakenToday = pillsOfMonth?.pillOfToday?.let {
        LocalDate.parse(it.day_taken) == today && it.status == "taken"
    } == true

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

    val takeHourFormatted = cycleState?.getOrNull()?.take_hour?.let { hourStr ->
        try {
            val time = LocalTime.parse(hourStr, DateTimeFormatter.ofPattern("HH:mm:ss"))
            val hour12 = if (time.hour % 12 == 0) 12 else time.hour % 12
            val amPm = if (time.hour < 12) "AM" else "PM"
            Triple(hour12.toString(), time.minute.toString().padStart(2, '0'), amPm)
        } catch (e: Exception) {
            Triple("--", "--", "")
        }
    } ?: Triple("--", "--", "")

    val nextDoseText = cycleState?.getOrNull()?.take_hour?.let { hourStr ->
        try {
            val takeTime = LocalTime.parse(hourStr, DateTimeFormatter.ofPattern("HH:mm:ss"))
            val now = LocalTime.now()
            val minutesDiff = ChronoUnit.MINUTES.between(now, takeTime)

            when {
                minutesDiff > 0 -> {
                    val hours = minutesDiff / 60
                    val minutes = minutesDiff % 60
                    "En %02d:%02d hrs".format(hours, minutes)
                }
                minutesDiff >= -30 -> "Tómala ahora"
                else -> "Toma atrasada"
            }
        } catch (e: Exception) {
            "Hora no disponible"
        }
    } ?: "Hora no disponible"

    val Pink = Color(0xFFE91E63)
    val PinkLight = Color(0xFFFFCDD2)
    val GrayText = Color(0xFF888888)
    val White = Color.White
    val LightGray = Color(0xFFE0E0E0)

    ScalingLazyColumn(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        contentPadding = PaddingValues(10.dp)
    ) {
        item {
            Text(
                text = "Toma de hoy",
                fontSize = 14.sp,
                color = Pink,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )
            Spacer(Modifier.height(4.dp))
            Text(
                text = formattedDate,
                fontSize = 12.sp,
                color = GrayText,
                textAlign = TextAlign.Center
            )
        }

        item {
            Spacer(Modifier.height(8.dp))
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "${takeHourFormatted.first}:${takeHourFormatted.second} ${takeHourFormatted.third}",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Pink
                )
            }
        }

        item {
            Spacer(Modifier.height(6.dp))
            Button(
                onClick = {
                    if (!isTakenToday && isTimeToTake) {
                        pillViewModel.takePill(
                            cycleState?.getOrNull()?.id ?: "",
                            today,
                            hourTake.toString(),
                            "taken",
                            null
                        )
                        Toast.makeText(context, "¡Toma registrada! 💊", Toast.LENGTH_SHORT).show()
                    }
                },
                enabled = !isTakenToday,
                colors = ButtonDefaults.buttonColors(
                    containerColor = when {
                        isTakenToday -> LightGray
                        !isTimeToTake -> LightGray
                        else -> Pink
                    }
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp)
            ) {
                Text(
                    text = when {
                        isTakenToday -> "Ya tomada"
                        !isTimeToTake -> "Espera"
                        else -> "Tomar"
                    },
                    color = if (isTakenToday || !isTimeToTake) GrayText else White,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        item {
            Spacer(Modifier.height(4.dp))
            Text(
                text = nextDoseText,
                fontSize = 11.sp,
                color = GrayText,
                textAlign = TextAlign.Center
            )
        }
    }
}