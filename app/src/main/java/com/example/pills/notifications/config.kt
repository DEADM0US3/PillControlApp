package  com.example.pills.notifications

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import com.example.pills.R
import com.example.pills.pills.domain.repository.CycleRepository
import com.example.pills.pills.domain.repository.NotificationRepository
import com.example.pills.pills.domain.supabase.SupabaseClientProvider
import io.github.jan.supabase.auth.auth
import kotlinx.coroutines.*
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit

class NotificationForegroundService : Service() {

    private val notificationRepository = NotificationRepository(SupabaseClientProvider.client)
    private val cycleRepository = CycleRepository(SupabaseClientProvider.client)
    private val CHANNEL_ID = "pill_reminder_channel"
    private var serviceJob = Job()
    private val scope = CoroutineScope(Dispatchers.IO + serviceJob)

    override fun onCreate() {
        super.onCreate()

        CoroutineScope(Dispatchers.Default).launch {
            SupabaseClientProvider.client.auth.loadFromStorage()
        }

        createNotificationChannel()
        startForeground(1, createNotification("Servicio activo", "Esperando recordatorios..."))
        startCheckingNotifications()
    }

    private fun startCheckingNotifications() {
        scope.launch {
            while (isActive) {
                try {

                    SupabaseClientProvider.client.auth.loadFromStorage()

                    val userId = SupabaseClientProvider.client.auth.currentUserOrNull()?.id.orEmpty()
                    if (userId.isEmpty()) {
                        Log.e("NotificationService", "Usuario no autenticado")
                        delay(60000L)
                        continue
                    }

                    // 1. Mostrar notificaciones personales
                    val personalResult = notificationRepository.getAndDeleteNotificationsForUser(userId)
                    if (personalResult.isSuccess) {
                        val notifications = personalResult.getOrNull().orEmpty()
                        notifications.forEachIndexed { index, notification ->
                            showNotification(
                                id = 1000 + index,
                                title = "Mensaje de ${notification.senderName}",
                                message = notification.message
                            )
                        }
                    } else {
                        Log.e("NotificationService", "Error al obtener notificaciones")
                    }

                    // 2. Verificar si es hora de tomar la pastilla
                    val cycleResult = cycleRepository.getActiveCycle(userId)
                    if (cycleResult.isSuccess) {
                        val cycle = cycleResult.getOrNull()
                        val takeHourStr = cycle?.take_hour

                        if (!takeHourStr.isNullOrBlank()) {
                            val takeHour = LocalTime.parse(takeHourStr, DateTimeFormatter.ofPattern("HH:mm:ss"))
                            val now = LocalTime.now()
                            val minutesDiff = ChronoUnit.MINUTES.between(takeHour, now)

                            if (kotlin.math.abs(minutesDiff) <= 30) {
                                showNotification(
                                    id = 2000,
                                    title = "Recordatorio de pastilla",
                                    message = "Es hora de tomar tu pastilla"
                                )
                            }
                        }
                    } else {
                        Log.e("NotificationService", "Error al obtener ciclo activo")
                    }
                } catch (e: Exception) {
                    Log.e("NotificationService", "Error en comprobación: ${e.message}")
                }

                delay(60000L) // Esperar 1 minuto antes de la próxima comprobación
            }
        }
    }

    private fun createNotification(title: String, message: String): Notification {
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val notificationBuilder = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle(title)
            .setSmallIcon(R.drawable.mascot_happy)
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_HIGH)

        return notificationBuilder.build()
    }

    private fun showNotification(id: Int, title: String, message: String) {
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle(title)
            .setContentText(message)
            .setSmallIcon(R.drawable.mascot_happy)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .build()

        notificationManager.notify(id, notification)
    }

    private fun createNotificationChannel() {
        val channel = NotificationChannel(
            CHANNEL_ID,
            "Recordatorios de pastillas",
            NotificationManager.IMPORTANCE_HIGH
        )
        val notificationManager = getSystemService(NotificationManager::class.java)
        notificationManager.createNotificationChannel(channel)
    }

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onDestroy() {
        super.onDestroy()
        serviceJob.cancel()
    }
}
