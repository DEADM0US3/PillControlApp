package com.example.pills.pills.presentation.friends

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.pills.pills.domain.repository.FriendWithCycleInfo
import com.example.pills.pills.presentation.homePage.CircleAvatar
import com.example.pills.ui.theme.Black
import com.example.pills.ui.theme.Pink
import org.koin.androidx.compose.koinViewModel
import com.example.pills.pills.infrastructure.ViewModel.FriendsViewModel
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import androidx.compose.ui.text.style.TextAlign
import com.example.pills.pills.navigation.Screen
import com.example.pills.pills.presentation.loading.LoadingScreen
import kotlinx.coroutines.delay

@Composable
fun FriendScreen(
    onBackPressed: () -> Unit,
    viewModel: FriendsViewModel = koinViewModel()
) {
    var showDialog by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        viewModel.loadFriends()
    }

    val loading = viewModel.loading

    if (loading)
        return LoadingScreen()


    val friends = viewModel.friends // Ahora List<User>

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(Color(0xFFF48FB1), Color(0xFFFCE4EC))
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            FriendsScreenHeader(onBackPressed)
            FriendsListSection(
                friends = friends,
                onAddClick = { showDialog = true },
                onRemindClick = { friend -> viewModel.sendReminder(friend.friend_id) }
            )
            Spacer(Modifier.height(80.dp))
        }

        if (showDialog) {
            AddFriendDialog(
                onDismiss = { showDialog = false },
                onAddFriend = { friendId ->
                    viewModel.addFriend(friendId) {
                        showDialog = false
                    }
                }
            )
        }
    }
}

@Composable
fun FriendsScreenHeader(onBackPressed: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .statusBarsPadding(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White.copy(alpha = 0.15f)
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = onBackPressed,
                modifier = Modifier
                    .size(48.dp)
                    .background(Color.White.copy(alpha = 0.2f), CircleShape)
            ) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Regresar",
                    tint = Color.White
                )
            }
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                text = "Amigas",
                color = Color.White,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun FriendsListSection(
    friends: List<FriendWithCycleInfo>,
    onAddClick: () -> Unit,
    onRemindClick: (FriendWithCycleInfo) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Amigas", fontWeight = FontWeight.Bold, color = Black)
                IconButton(onClick = onAddClick) {
                    Icon(Icons.Default.Add, contentDescription = "Agregar", tint = Black)
                }
            }

            Spacer(Modifier.height(8.dp))

            friends.forEach { user ->
                FriendItem(user = user, onRemind = { onRemindClick(user) })
            }
        }
    }
}

@Composable
fun FriendItem(user: FriendWithCycleInfo, onRemind: () -> Unit) {
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
fun AddFriendDialog(
    onDismiss: () -> Unit,
    onAddFriend: (String) -> Unit
) {
    var friendName by remember { mutableStateOf("") }

    val Pink = Color(0xFFEA5A8C)
    val PinkLight = Color(0xFFFFF0F6)
    val GrayText = Color(0xFFBDBDBD)
    val White = Color(0xFFFFFFFF)

    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            Button(
                onClick = {
                    if (friendName.isNotBlank()) {
                        onAddFriend(friendName.trim())
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = Pink),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.padding(8.dp)
            ) {
                Text(
                    text = "Agregar",
                    color = White,
                    fontWeight = FontWeight.Bold
                )
            }
        },
        dismissButton = {
            Button(
                onClick = onDismiss,
                colors = ButtonDefaults.buttonColors(containerColor = White),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.padding(8.dp)
            ) {
                Text(
                    text = "Cancelar",
                    color = Pink,
                    fontWeight = FontWeight.Bold
                )
            }
        },
        title = {
            Text(
                text = "Agregar nueva amiga 💕",
                fontWeight = FontWeight.Bold,
                color = Pink,
                fontSize = 20.sp,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center
            )
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                OutlinedTextField(
                    value = friendName,
                    onValueChange = { friendName = it },
                    label = { Text("ID o correo de la amiga") },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Conéctate con tus personas favoritas y cuídense juntas 💖",
                    fontSize = 13.sp,
                    color = GrayText,
                    textAlign = TextAlign.Center
                )
            }
        },
        containerColor = White,
        shape = RoundedCornerShape(20.dp),
        tonalElevation = 6.dp
    )
}
