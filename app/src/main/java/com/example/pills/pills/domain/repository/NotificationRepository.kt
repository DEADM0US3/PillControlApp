package com.example.pills.pills.domain.repository

import com.example.pills.pills.domain.entities.Notification
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.from
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class NotificationRepository(private val supabaseClient: SupabaseClient) {

    suspend fun sendNotification(senderId: String, receiverId: String, message: String): Result<Unit> =
        withContext(Dispatchers.IO) {
            try {
                if (message.isBlank()) return@withContext Result.failure(Exception("Mensaje vacío"))

                supabaseClient.from("notifications").insert(
                    mapOf(
                        "sender_id" to senderId,
                        "receiver_id" to receiverId,
                        "message" to message
                    )
                )

                Result.success(Unit)
            } catch (e: Exception) {
                Result.failure(e)
            }
        }


    suspend fun getNotifications(receiverId: String): Result<List<Notification>> = withContext(Dispatchers.IO) {
        try {
            val response = supabaseClient.from("notifications").select {
                filter { eq("receiver_id", receiverId) }
            }.decodeList<Notification>()

            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getAndDeleteNotificationsForUser(userId: String): Result<List<Notification>> {
        return runCatching {
            val notifications = supabaseClient.from("notifications")
                .select {
                    filter {
                        eq("receiver_id", userId)
                    }
                }
                .decodeList<Notification>()

            if (notifications.isNotEmpty()) {
                supabaseClient.from("notifications")
                    .delete {
                        filter {
                            eq("receiver_id", userId)

                        }
                    }
            }

            notifications
        }
    }

}
