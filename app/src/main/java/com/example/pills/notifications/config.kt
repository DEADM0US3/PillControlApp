package com.example.pills.notifications

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.work.CoroutineWorker
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkerParameters
import java.util.concurrent.TimeUnit
import com.example.pills.R
import com.example.pills.pills.domain.repository.NotificationRepository
import com.example.pills.pills.domain.supabase.SupabaseClientProvider

class MyPeriodicWorker(
    context: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(context, workerParams) {

    private val notificationRepository: NotificationRepository = NotificationRepository(supabaseClient = SupabaseClientProvider.client)

    @SuppressLint("RestrictedApi")
    override suspend fun doWork(): Result {
        val userId = inputData.getString("userId") ?: return Result.failure()

        return when (val result = notificationRepository.getAndDeleteNotificationsForUser(userId)) {
            is Result.Success -> {
                result.getOrNull()?.forEach { notification ->
                    showNotification("Mensaje de ${notification.sender_id}", notification.message)
                }
                Result.success()
            }
            else -> Result.failure()
        }
    }


    private fun showNotification(title: String, message: String) {
        val channelId = "pill_reminder_channel"
        val notificationManager =
            applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // Crear canal para Android 8.0+
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
            .setSmallIcon(android.R.drawable.ic_delete) // Cambia por tu icono
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .build()

        notificationManager.notify(1001, notification)
    }
}
