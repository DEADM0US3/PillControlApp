package com.example.pills.pills.domain.repository

import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.from
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class FriendRepository(private val supabaseClient: SupabaseClient) {

    suspend fun addFriend(userId: String?, friendId: String): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            supabaseClient.from("friends").insert(
                mapOf("user_id" to userId, "friend_id" to friendId)
            )
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getFriends(userId: String): Result<List<Map<String, Any>>> = withContext(Dispatchers.IO) {
        try {
            val response = supabaseClient.from("friends").select {
                filter { eq("user_id", userId) }
            }.decodeList<Map<String, Any>>()

            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
