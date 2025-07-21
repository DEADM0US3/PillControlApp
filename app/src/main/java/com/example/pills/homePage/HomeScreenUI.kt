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
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.pills.R
import com.example.pills.pills.presentation.calendar.PillViewModel
import com.example.pills.pills.presentation.components.TakePillComponent
import com.example.pills.pills.presentation.cycle.CycleViewModel
import com.example.pills.ui.theme.Pink
import com.example.pills.ui.theme.Black
import com.example.pills.ui.theme.GrayText
import com.example.pills.ui.theme.LightGray
import com.patrykandpatrick.vico.compose.chart.Chart
import com.patrykandpatrick.vico.compose.chart.line.lineChart
import com.patrykandpatrick.vico.core.chart.Chart
import com.patrykandpatrick.vico.core.entry.ChartEntryModelProducer
import com.patrykandpatrick.vico.core.entry.FloatEntry
import org.koin.androidx.compose.koinViewModel
import java.time.format.DateTimeFormatter
import java.util.Locale

@Composable
fun HomeScreenUI(
    navigateToFriends : () -> Unit
) {
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
            FriendsListSection(
                navigateToFriends
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
fun MascotReminderSection() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text("No olvides estar tomando agua", color = Pink, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(12.dp))
            Box(
                modifier = Modifier
                    .size(190.dp)
                    .clip(CircleShape)
                    .background(LightGray),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.Face, contentDescription = null, modifier = Modifier.size(100.dp))
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


        Column {
            StatusCard(
                title = "Estado del ciclo: En toma",
                content = "Inicio: $startDate\nFin: $endDate"
            )
        }
    } else {
        Text("Cargando ciclo...", modifier = Modifier.padding(16.dp))
    }
}

@Composable
fun StatusCard(title: String, content: String) {
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
            Text(title, color = Black, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(4.dp))
            Text(content, color = GrayText)
        }
    }
}

@Composable
fun FriendsListSection(
    navigateToFriends : () -> Unit
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
            FriendItem("Valeria García")
            FriendItem("Yesenia Torres")
            FriendItem("Luna Aguilar")
        }
    }
}

@Composable
fun FriendItem(name: String) {
    Row(
        Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        CircleAvatar()
        Spacer(Modifier.width(8.dp))
        Text(name, modifier = Modifier.weight(1f), color = Black)
        Button(
            onClick = { /* Recordar */ },
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
