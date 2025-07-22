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
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.pills.pills.infrastructure.ViewModel.PillViewModel
import com.example.pills.pills.presentation.cycle.CycleViewModel
import com.example.pills.ui.theme.Black
import com.kizitonwose.calendar.compose.rememberCalendarState
import org.koin.androidx.compose.koinViewModel
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter

private val Pink = Color(0xFFEA5A8C)
private val PinkLight = Color(0xFFFFF0F6)
private val LightGray = Color(0xFFF3F3F3)
private val GrayText = Color(0xFFBDBDBD)
private val White = Color(0xFFFFFFFF)


@Composable
fun TakePillComponent(
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
        firstDayOfWeek = DayOfWeek.SUNDAY
    )


    LaunchedEffect(visibleMonth) {
        cycleViewModel.fetchActiveCycle()
        pillViewModel.loadPillOfDay(today)
        calendarState.scrollToMonth(visibleMonth)

    }

    val cycleState by cycleViewModel.cycleState.collectAsState()
    val pillsOfMonth by pillViewModel.uiState.collectAsState()

    Log.d("CalendarScreen", "Pills of month: $pillsOfMonth")

    val formattedDate = remember {
        today.format(DateTimeFormatter.ofPattern("d 'de' MMMM"))
    }

    val isTakenToday = pillsOfMonth?.pillsOfMonth?.any {
        LocalDate.parse(it.day_taken) == today && it.status == "taken"
    } == true



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
                        onClick = {
                            if (!isTakenToday) {
                                pillViewModel.takePill(cycleState?.getOrNull()?.id ?: "", today, "taken", null)
                            }
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (isTakenToday) LightGray else Pink
                        ),
                        shape = RoundedCornerShape(16.dp),
                        contentPadding = PaddingValues(horizontal = 20.dp, vertical = 10.dp),
                        enabled = !isTakenToday
                    ) {
                        Text(
                            text = if (isTakenToday) "Ya registrada" else "Registrar toma",
                            color = if (isTakenToday) GrayText else White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp
                        )
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

}