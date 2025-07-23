package com.example.pills.homePage


import android.util.Log
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
import androidx.compose.material.icons.filled.Face
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
import com.example.pills.pills.domain.repository.FriendWithUser
import com.example.pills.pills.infrastructure.ViewModel.PillViewModel
import com.example.pills.pills.presentation.components.TakePillComponent
import com.example.pills.pills.presentation.cycle.CycleViewModel
import com.example.pills.pills.infrastructure.ViewModel.FriendsViewModel
import com.example.pills.ui.theme.Pink
import com.example.pills.ui.theme.Black
import com.example.pills.ui.theme.GrayText
import com.example.pills.ui.theme.LightGray
import com.kizitonwose.calendar.compose.rememberCalendarState
import org.koin.androidx.compose.koinViewModel
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.util.Locale


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
        firstDayOfWeek = java.time.DayOfWeek.SUNDAY
    )

    LaunchedEffect(visibleMonth) {
        cycleViewModel.fetchActiveCycle()
        pillViewModel.loadPillsOfMonth(visibleMonth.year, visibleMonth.monthValue)
        calendarState.scrollToMonth(visibleMonth)

    }
    LaunchedEffect(Unit) {
        friendsViewModel.loadFriends()
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
        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "ESTADO DEL CICLO",
                    color = Black,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )

                if (cycle != null) {
                    TextButton(
                        onClick = { showDeleteDialog = true },
                        contentPadding = PaddingValues(4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = null,
                            tint = Color.Red,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Eliminar",
                            color = Color.Red,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                    }
                }
            }

            if (cycle != null) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(PinkLight, RoundedCornerShape(12.dp))
                        .padding(vertical = 16.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    DateBlock(title = "Inicio", date = startDate)
                    DateBlock(title = "Fin", date = endDate)
                }
            }

            if (cycle == null) {
                var pillCountInput by remember { mutableStateOf("21") }
                var takeHourInput by remember { mutableStateOf("08:00") }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Cantidad de pastillas",
                    color = Black,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )
                OutlinedTextField(
                    value = pillCountInput,
                    onValueChange = { pillCountInput = it.filter { c -> c.isDigit() } },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 6.dp),
                    placeholder = { Text("21") }
                )

                Text(
                    text = "Hora de toma (HH:mm)",
                    color = Black,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )
                OutlinedTextField(
                    value = takeHourInput,
                    onValueChange = { takeHourInput = it },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 6.dp),
                    placeholder = { Text("08:00") }
                )

                Spacer(modifier = Modifier.height(12.dp))

                Button(
                    onClick = {
                        val pillCount = pillCountInput.toIntOrNull() ?: 21
                        val hour = takeHourInput.ifBlank { "08:00" }
                        cycleViewModel.startNewCycle(today, pillCount, hour)
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Pink),
                    shape = RoundedCornerShape(16.dp),
                    contentPadding = PaddingValues(horizontal = 20.dp, vertical = 12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "NUEVO CICLO",
                        color = White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                }
            }

            if (showDeleteDialog) {
                StyledDeleteDialog(
                    onConfirm = {
                        cycleViewModel.deleteCurrentCycle()
                        showDeleteDialog = false
                    },
                    onDismiss = {
                        showDeleteDialog = false
                    }
                )
            }

        }
    }
}

@Composable
fun StyledDeleteDialog(
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = PinkLight, // Fondo rosa claro
        shape = RoundedCornerShape(20.dp), // Esquinas redondeadas grandes
        title = {
            Text(
                text = "¿Eliminar ciclo?",
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                color = Black
            )
        },
        text = {
            Text(
                text = "¿Estás seguro de que deseas eliminar el ciclo actual? Esta acción no se puede deshacer.",
                fontSize = 14.sp,
                color = Black
            )
        },
        confirmButton = {
            Button(
                onClick = onConfirm,
                colors = ButtonDefaults.buttonColors(containerColor = Color.Red),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.padding(end = 8.dp)
            ) {
                Text(
                    text = "Eliminar",
                    color = White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )
            }
        },
        dismissButton = {
            OutlinedButton(
                onClick = onDismiss,
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.padding(start = 8.dp)
            ) {
                Text(
                    text = "Cancelar",
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = Pink
                )
            }
        }
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
    friends: List<FriendWithUser>,
    onRemindClick: (FriendWithUser) -> Unit,
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
fun FriendItemHome(
    user: FriendWithUser,
    onRemind: () -> Unit
) {
    Row(
        Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        CircleAvatar()
        Spacer(Modifier.width(8.dp))
        Text(
            text = user.name ?: "Sin nombre",
            modifier = Modifier.weight(1f),
            color = Black,
            fontSize = 16.sp
        )
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
