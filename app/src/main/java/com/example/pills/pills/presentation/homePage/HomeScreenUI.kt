package com.example.pills.pills.presentation.homePage


import android.app.TimePickerDialog
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.pills.R
import com.example.pills.pills.infrastructure.ViewModel.PillViewModel
import com.example.pills.pills.presentation.components.TakePillComponent
import com.example.pills.pills.presentation.cycle.CycleViewModel
import com.example.pills.pills.infrastructure.ViewModel.FriendsViewModel
import com.example.pills.ui.theme.Pink
import com.example.pills.ui.theme.Black
import com.example.pills.ui.theme.LightGray
import com.kizitonwose.calendar.compose.rememberCalendarState
import org.koin.androidx.compose.koinViewModel
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.util.Locale
import androidx.compose.foundation.BorderStroke
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import java.time.DayOfWeek
import androidx.compose.material3.TextFieldDefaults
import com.example.pills.pills.domain.repository.FriendWithCycleInfo
import com.example.pills.pills.presentation.loading.LoadingScreen
import kotlinx.coroutines.delay
import java.time.LocalTime
import java.time.temporal.ChronoUnit
import kotlin.ranges.contains


private val Pink = Color(0xFFEA5A8C)
private val PinkLight = Color(0xFFFFF0F6)
private val LightGray = Color(0xFFF3F3F3)
private val GrayText = Color(0xFFBDBDBD)
private val White = Color(0xFFFFFFFF)
private val Black = Color(0xFF000000) // para que sea explícito


@Composable
fun HomeScreenUI(
    navigateToFriends : () -> Unit,
    cycleViewModel: CycleViewModel = koinViewModel(),
    pillViewModel: PillViewModel = koinViewModel(),
    friendsViewModel: FriendsViewModel = koinViewModel() // <-- ViewModel con lógica de amigas
) {

    val friends = friendsViewModel.friends

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
        pillViewModel.loadPillsOfMonth(visibleMonth.year, visibleMonth.monthValue)
        calendarState.scrollToMonth(visibleMonth)

    }
    LaunchedEffect(Unit) {
        friendsViewModel.loadFriends()
    }


    var isLoading by remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        delay(400)
        isLoading = false
    }
    if (isLoading){
        return LoadingScreen()

    }


    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFFF48FB1), // Mismo color que ProfileScreen y CalendarScreen
                        Color(0xFFFCE4EC)  // Mismo color que ProfileScreen y CalendarScreen
                    )
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            HeaderSection()
            ProtectionStatusSection()
            MascotReminderSection()
            CycleStatusSection()
            Spacer(Modifier.height(8.dp))
            TakePillComponent()
            FriendsListSectionHome(
                friends = friends,
                onRemindClick = { friend -> friendsViewModel.sendReminder(friend.friend_id) },
                navigateToFriends = navigateToFriends
            )
        }
    }
}

@Composable
fun HeaderSection(
    homeViewModel: HomeViewModel = koinViewModel()
) {

    val uiState by homeViewModel.uiState.collectAsState()
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current
    val cleanUserName = uiState.userName.replace("\"", "")

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(0.dp, 16.dp) ,          // para que el Row ocupe todo el ancho
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Start  // el contenido al inicio (izquierda)
    ) {
        CircleAvatar()
        Spacer(Modifier.width(8.dp))
        Button(
            onClick = {},
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF36F9D))
        ) {
            Text(text = "Hola, $cleanUserName", color = Color.White)
        }
    }
}

@Composable
fun ProtectionStatusSection(
    cycleViewModel: CycleViewModel = koinViewModel(),
    pillViewModel: PillViewModel = koinViewModel()
) {
    val cycleState by cycleViewModel.cycleState.collectAsState()
    val pillsState by pillViewModel.uiState.collectAsState()

    val cycle = cycleState?.getOrNull()
    val pillsOfMonth = pillsState?.pillsOfCycle ?: emptyList()

    // Total pastillas del ciclo o 0 si no hay
    val totalPills = cycle?.pill_count ?: 0

    // Contar cuántas pastillas se han tomado (status == "taken")
    val takenPillsCount = pillsOfMonth.count { it.status == "taken" }

    // Calcular porcentaje protección (evitar división por cero)
    val protectionProgress = if (totalPills > 0) {
        takenPillsCount.toFloat() / totalPills.toFloat()
    } else 0f

    LaunchedEffect(Unit) {
        cycleViewModel.fetchActiveCycle()
    }

    LaunchedEffect(cycle?.id) {
        pillViewModel.loadPillsOfCycle(
            cycle?.id.toString()
        )
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(4.dp, RoundedCornerShape(20.dp))
            .clip(RoundedCornerShape(20.dp))
            .background(White)
            .padding(20.dp)
    ) {
        Column {
            Spacer(Modifier.height(12.dp))
            LinearProgressIndicator(
                progress = protectionProgress.coerceIn(0f, 1f),
                color = Pink,
                trackColor = LightGray,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(10.dp)
                    .clip(RoundedCornerShape(6.dp))
            )
            Spacer(Modifier.height(12.dp))
            Text(
                text = if (cycle != null)
                    "Píldora actual: $takenPillsCount / $totalPills"
                else
                    "Sin información de ciclo",
                color = Black,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp
            )
        }
    }
}


@Composable
fun MascotReminderSection(
    homeViewModel: HomeViewModel = koinViewModel()
) {
    val uiState by homeViewModel.uiState.collectAsState()

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(10.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = uiState.mascotMessage,
                color = Pink,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                fontSize = 16.sp
            )
            Spacer(Modifier.height(5.dp))
            Box(
                /*modifier = Modifier
                    .size(190.dp)
                    .clip(CircleShape)
                    .background(LightGray.copy(alpha = 0.3f))
                    .padding(8.dp), */
                modifier = Modifier.offset(y = (-40).dp), // Sube el icono 10dp
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(id = uiState.mascotImageRes),
                    contentDescription = "Mascota",
                    modifier = Modifier.size(250.dp),
                    tint = Color.Unspecified
                )
            }
        }
    }
}

@Composable
fun CycleStatusSection(
    cycleViewModel: CycleViewModel = koinViewModel()
) {
    val cycleState by cycleViewModel.cycleState.collectAsState()
    val cycle = cycleState?.getOrNull()

    val formatter = DateTimeFormatter.ofPattern("d 'de' MMMM", Locale("es", "MX"))

    var startDate = "--/--/----"
    var endDate = "--/--/----"

    if (cycle != null) {
        startDate = cycle.start_date.format(formatter)
        val endDate2 = cycle.end_date?.format(formatter)
        if (endDate2 != null) {
            endDate = endDate2
        }
    }

    val today = LocalDate.now().toString()

    var showDeleteDialog by remember { mutableStateOf(false) }

    StatusCard {
        Column(modifier = Modifier.fillMaxWidth()) {
            // Encabezado
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "ESTADO DEL CICLO",
                    color = Pink,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )

                if (cycle != null) {
                    TextButton(
                        onClick = { showDeleteDialog = true },
                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                        colors = ButtonDefaults.textButtonColors(contentColor = Color.Red)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Eliminar",
                            tint = Color.Red,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Eliminar",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }

            // Ciclo actual (si existe)
            if (cycle != null) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(PinkLight, RoundedCornerShape(16.dp))
                        .padding(vertical = 20.dp, horizontal = 16.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    DateBlock(title = "Inicio", date = startDate)
                    DateBlock(title = "Fin", date = endDate)
                }
            }else {
                var showDialog by remember { mutableStateOf(false) }
                var pillCountInput by remember { mutableStateOf("21") }
                var takeHourInput by remember { mutableStateOf("08:00") }

                Spacer(modifier = Modifier.height(20.dp))

                Button(
                    onClick = { showDialog = true },
                    colors = ButtonDefaults.buttonColors(containerColor = Pink),
                    shape = RoundedCornerShape(16.dp),
                    contentPadding = PaddingValues(vertical = 14.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "NUEVO CICLO",
                        color = White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                }

                if (showDialog) {
                    CreateCycleDialog(
                        pillCountInput = pillCountInput,
                        takeHourInput = takeHourInput,
                        onPillCountChange = { pillCountInput = it },
                        onTakeHourChange = { takeHourInput = it },
                        onDismiss = { showDialog = false },
                        onConfirm = {
                            val pillCount = pillCountInput.toIntOrNull() ?: 21
                            val hour = takeHourInput.ifBlank { "08:00" }
                            cycleViewModel.startNewCycle(today, pillCount, hour)
                            showDialog = false
                        }
                    )
                }
            }


            // Diálogo de confirmación para eliminar
            if (showDeleteDialog) {
                StyledDeleteDialog(
                    onConfirm = {
                        cycleViewModel.deleteCurrentCycle()
                        showDeleteDialog = false
                    },
                    onDismiss = { showDeleteDialog = false }
                )
            }
        }
    }

}

@Composable
fun CreateCycleDialog(
    pillCountInput: String,
    takeHourInput: String,
    onPillCountChange: (String) -> Unit,
    onTakeHourChange: (String) -> Unit,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = Color.White,
        shape = RoundedCornerShape(16.dp),
        title = {
            Text(
                text = "Nuevo Ciclo",
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp,
                color = Pink
            )
        },
        text = {
            Column(modifier = Modifier.padding(top = 4.dp)) {
                Text(
                    text = "Cantidad de pastillas",
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 14.sp,
                    color = Pink
                )
                Spacer(modifier = Modifier.height(4.dp))
                PillCountDropdown(
                    selectedCount = pillCountInput,
                    onSelectedChange = onPillCountChange,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "Hora de toma (HH:mm)",
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 14.sp,
                    color = Pink
                )
                Spacer(modifier = Modifier.height(4.dp))
                TimePickerField(
                    time = takeHourInput,
                    onTimeChange = onTakeHourChange,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = onConfirm,
                colors = ButtonDefaults.buttonColors(containerColor = Pink),
                shape = RoundedCornerShape(12.dp),
                contentPadding = PaddingValues(horizontal = 24.dp, vertical = 10.dp)
            ) {
                Text("Iniciar", color = White, fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            OutlinedButton(
                onClick = onDismiss,
                colors = ButtonDefaults.outlinedButtonColors(contentColor = Pink),
                border = BorderStroke(1.dp, Pink),
                shape = RoundedCornerShape(12.dp),
                contentPadding = PaddingValues(horizontal = 24.dp, vertical = 10.dp)
            ) {
                Text("Cancelar", fontWeight = FontWeight.Bold)
            }
        }
    )
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PillCountDropdown(
    selectedCount: String,
    onSelectedChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }
    val options = listOf("21", "27")

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded },
        modifier = modifier
    ) {
        OutlinedTextField(
            value = selectedCount,
            onValueChange = {},
            readOnly = true,
            placeholder = { Text("Selecciona", color = Black.copy(alpha = 0.4f)) },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            modifier = Modifier
                .menuAnchor()
                .fillMaxWidth(),
            colors = TextFieldDefaults.colors(
                focusedTextColor = Pink
            ),
            shape = RoundedCornerShape(12.dp)
        )
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            options.forEach { selectionOption ->
                DropdownMenuItem(
                    text = { Text(selectionOption) },
                    onClick = {
                        onSelectedChange(selectionOption)
                        expanded = false
                    }
                )
            }
        }
    }
}

@Composable
fun TimePickerField(
    time: String,
    onTimeChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    // Parse time para abrir diálogo con hora y minuto actuales
    var hour: Int
    var minute: Int
    try {
        val parts = time.split(":")
        hour = parts[0].toInt()
        minute = parts[1].toInt()
    } catch (e: Exception) {
        hour = 8
        minute = 0
    }

    val timePickerDialog = remember {
        TimePickerDialog(
            context,
            { _, selectedHour, selectedMinute ->
                val formatted = String.format("%02d:%02d", selectedHour, selectedMinute)
                onTimeChange(formatted)
            },
            hour,
            minute,
            true // formato 24h, false para AM/PM
        )
    }

    OutlinedTextField(
        value = time,
        onValueChange = { onTimeChange(it) }, // también deja editar manualmente si quieres
        singleLine = true,
        readOnly = true, // evita que el teclado salga
        modifier = modifier
            .clickable { timePickerDialog.show() },
        placeholder = { Text("Ej: 08:00", color = Color.Black.copy(alpha = 0.4f)) },
        shape = RoundedCornerShape(12.dp),
        colors = TextFieldDefaults.colors(
            focusedContainerColor = Color.Transparent,
            unfocusedContainerColor = Color.Transparent,
            disabledContainerColor = Color.Transparent,
            focusedIndicatorColor = Pink,
            unfocusedIndicatorColor = Pink.copy(alpha = 0.4f)
        )
    )
}

@Composable
fun StyledDeleteDialog(
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            Button(
                onClick = onConfirm,
                colors = ButtonDefaults.buttonColors(containerColor = Pink),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.padding(8.dp)
            ) {
                Text(
                    text = "Eliminar",
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
            }
        },
        dismissButton = {
            OutlinedButton(
                onClick = onDismiss,
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.padding(8.dp),
                border = BorderStroke(1.dp, Pink)
            ) {
                Text(
                    text = "Cancelar",
                    fontWeight = FontWeight.Bold,
                    color = Pink
                )
            }
        },
        title = {
            Text(
                text = "¿Eliminar ciclo?",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF333333)
                )
            )
        },
        text = {
            Text(
                text = "¿Estás seguro de que deseas eliminar el ciclo actual? Esta acción no se puede deshacer.",
                style = MaterialTheme.typography.bodyMedium.copy(color = Color(0xFF666666))
            )
        },
        containerColor = Color.White,
        shape = RoundedCornerShape(20.dp),
        tonalElevation = 6.dp
    )
}


@Composable
fun StatusCard(
    contentBlock: @Composable (() -> Unit)
) {
    Box(
        modifier = Modifier

            .fillMaxWidth()
            .shadow(2.dp, RoundedCornerShape(16.dp))
            .clip(RoundedCornerShape(16.dp))
            .background(Color.White)
            .padding(12.dp)
            .background(White)
    ) {
        contentBlock()
    }
}

@Composable
fun DateBlock(title: String, date: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.padding(bottom = 4.dp)
        )
        Box(
            modifier = Modifier
                .background(color = Color(0xFFFFE0EB), shape = RoundedCornerShape(8.dp))
                .padding(horizontal = 20.dp, vertical = 10.dp)
        ) {
            Text(
                text = date,
                color = Color(0xFFE91E63),
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}


@Composable
fun FriendsListSectionHome(
    friends: List<FriendWithCycleInfo>,
    onRemindClick: (FriendWithCycleInfo) -> Unit,
    navigateToFriends: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .shadow(2.dp, RoundedCornerShape(16.dp))
            .clip(RoundedCornerShape(16.dp))
            .background(Color.White)
            .padding(12.dp)
    ) {
        Column {
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Amigas", fontWeight = FontWeight.Bold, color = Black)
                Text(
                    text = "Ver más...",
                    color = Pink,
                    modifier = Modifier.clickable {
                        navigateToFriends()
                    }
                )
            }
            Spacer(Modifier.height(8.dp))

            // Mostrar máximo 3 amigas como resumen
            friends.take(3).forEach { friend ->
                FriendItemHome(user = friend, onRemind = { onRemindClick(friend) })
            }
        }
    }
}

@Composable
fun FriendItemHome(user: FriendWithCycleInfo, onRemind: () -> Unit) {
    val hasCycle = user.recent_cycle_id != null
    val hasTakenPill = user.pill_status_today == "taken"
    val takeHour = user.take_hour // <- asegúrate de incluir `takeHour` en la clase si aún no está
    val now = remember { LocalTime.now() }

    // Calcular si está dentro de la ventana de 30 minutos
    val isTimeToRemind = takeHour?.let { hourStr ->
        try {
            val takeTime = LocalTime.parse(hourStr, DateTimeFormatter.ofPattern("HH:mm:ss"))
            val now = LocalTime.now()
            val minutesDiff = ChronoUnit.MINUTES.between(now, takeTime)

            minutesDiff <= 30
        } catch (e: Exception) {
            false
        }
    } ?: false

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFFDFDFD)),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            CircleAvatar()

            Spacer(Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = user.name ?: "Guest",
                    fontWeight = FontWeight.Bold,
                    color = Black,
                    fontSize = 16.sp
                )

                if (!hasCycle) {
                    Text("Sin ciclo activo", color = Color.Gray, fontSize = 12.sp)
                } else if (hasTakenPill) {
                    Text("Ya tomó su pastilla hoy 💊", color = Color(0xFF4CAF50), fontSize = 12.sp)
                } else if(hasCycle && !isTimeToRemind)
                {
                    Text("Le puedes recordar a las ${user.take_hour}", color = Pink, fontSize = 12.sp)
                }
                else {
                    Text("Recuerdale!!!!!", color = Color(0xFFE91E63), fontSize = 12.sp)
                }
            }

            if (hasCycle && isTimeToRemind) {
                Button(
                    onClick = onRemind,
                    colors = ButtonDefaults.buttonColors(containerColor = Pink),
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp)
                ) {
                    Text("Recordar", color = Color.White, fontSize = 12.sp)
                }
            }
        }
    }
}


@Composable
fun CircleAvatar() {
    Box(
        modifier = Modifier
            .size(40.dp)
            .clip(CircleShape)
            .background(Color(0xFFB3E5FC)),
        contentAlignment = Alignment.Center
    ) {
        // Puedes reemplazar painterResource(R.drawable.ic_profile) por tu imagen
        Image(
            painter = painterResource(id = R.drawable.ic_launcher_foreground),
            contentDescription = "Foto de perfil",
            modifier = Modifier.size(100.dp)
        )
    }
}
