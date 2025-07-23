package com.example.pills.notifications

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.pills.R
import com.example.pills.pills.domain.repository.CycleRepository
import com.example.pills.pills.domain.repository.NotificationRepository
import com.example.pills.pills.domain.supabase.SupabaseClientProvider
import io.github.jan.supabase.auth.auth
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit

class NotificationWorker(
    context: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(context, workerParams) {

    private val notificationRepository = NotificationRepository(SupabaseClientProvider.client)
    private val cycleRepository = CycleRepository(SupabaseClientProvider.client)

    override suspend fun doWork(): Result {
        val userId = SupabaseClientProvider.client.auth.currentUserOrNull()?.id.orEmpty()
        if (userId.isEmpty()) return Result.failure()

        var success = true

        try {
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
                success = false
                Log.e("NotificationWorker", "Error al obtener notificaciones")
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
                            id = 2000, // id diferente al de notificaciones personales
                            title = "Recordatorio de pastilla",
                            message = "Es hora de tomar tu pastilla"
                        )
                    }
                }
            } else {
                success = false
                Log.e("NotificationWorker", "Error al obtener ciclo activo")
            }

        } catch (e: Exception) {
            Log.e("NotificationWorker", "Error en doWork: ${e.message}")
            success = false
        }

        return if (success) Result.success() else Result.failure()
    }

    private fun showNotification(id: Int, title: String, message: String) {
        val channelId = "pill_reminder_channel"
        val notificationManager =
            applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Recordatorios de pastillas",
                NotificationManager.IMPORTANCE_HIGH
            )
            notificationManager.createNotificationChannel(channel)
        }

        val notification = NotificationCompat.Builder(applicationContext, channelId)
            .setContentTitle(title)
            .setContentText(message)
            .setSmallIcon(R.drawable.google_icon)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .build()

        notificationManager.notify(id, notification)
    }
}
