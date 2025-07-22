package com.example.pills.homePage


import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
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
import com.example.pills.pills.presentation.calendar.PillViewModel
import com.example.pills.pills.presentation.components.TakePillComponent
import com.example.pills.pills.presentation.cycle.CycleViewModel
import com.example.pills.pills.presentation.friends.FriendsViewModel
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
            TakePillComponent()
            CycleStatusSection()
            FriendsListSectionHome(
                friends = friends,
                onRemindClick = { friend -> friendsViewModel.sendReminder(friend.friend_id) },
                navigateToFriends = navigateToFriends
            )

            Spacer(Modifier.height(80.dp)) // Para no tapar el bottom nav
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
fun ProtectionStatusSection() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(2.dp, RoundedCornerShape(16.dp))
            .clip(RoundedCornerShape(16.dp))
            .background(Color.White)
            .padding(12.dp)
    ) {
        Column {
            Text("Estado de protección: Caso protegido", color = GrayText, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(8.dp))
            LinearProgressIndicator(
                progress = 0.8f,
                color = Color(0xFF4CAF50),
                trackColor = LightGray,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(RoundedCornerShape(4.dp))
            )
            Spacer(Modifier.height(8.dp))
            Text("Píldora actual: 20 / 28", color = Black)
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

    if (cycle != null) {
        val formatter = DateTimeFormatter.ofPattern("d 'de' MMMM", Locale("es", "MX"))
        val startDate = cycle.start_date.format(formatter)
        val endDate = cycle.end_date.format(formatter)

        StatusCard(
            title = "ESTADO DEL CICLO",
            content = null // ya no se usa contenido en texto plano
        ) {
            // Contenido personalizado dentro del StatusCard
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                DateBlock(title = "Inicio", date = startDate)
                DateBlock(title = "Fin", date = endDate)
            }
        }
    } else {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            CircularProgressIndicator(
                color = Color(0xFFFF4081),
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Cargando ciclo...",
                color = Color(0xFF555555),
                style = MaterialTheme.typography.bodyMedium
            )
        }

    }
}

@Composable
fun StatusCard(
    title: String,
    content: String? = null,
    contentBlock: @Composable (() -> Unit)? = null
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .shadow(2.dp, RoundedCornerShape(16.dp))
            .clip(RoundedCornerShape(16.dp))
            .background(Color.White)
            .padding(16.dp)
    ) {
        Column {
            Box(modifier = Modifier.fillMaxWidth().height(40.dp)) {
                Text(
                    title,
                    color = Color.Black,
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.align(Alignment.Center),

                )
            }
            Spacer(Modifier.height(8.dp))
            if (content != null) {
                Text(content, color = Color.Gray)
            }
            if (contentBlock != null) {
                contentBlock()
            }
        }
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
