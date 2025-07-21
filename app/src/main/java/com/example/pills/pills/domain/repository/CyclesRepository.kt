package com.example.pills.pills.domain.repository

import android.util.Log
import com.example.pills.pills.presentation.cycle.Cycle
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.from
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.time.LocalDate
import kotlin.getOrThrow


class CycleRepository(private val supabaseClient: SupabaseClient) {

    suspend fun createCycle(
        userId: String,
        startDate: String,
        pillCount: Int = 21
    ): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            supabaseClient.from("cycles").insert(
                mapOf(
                    "user_id" to userId,
                    "start_date" to startDate,
                    "pill_count" to pillCount
                )
            )
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getActiveCycle(userId: String): Result<Cycle> = withContext(Dispatchers.IO) {
        try {
            val result = supabaseClient.from("cycles").select {
                filter {
                    eq("user_id", userId)
                    eq("is_deleted", false)
                }
                limit(1)
            }.decodeSingle<Cycle>()

            Result.success(result)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun deleteCycle(cycleId: String): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            supabaseClient.from("cycles").update(
                mapOf("is_deleted" to true)
            ) {
                filter { eq("id", cycleId) }
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getAllCycles(userId: String): Result<List<Cycle>> = withContext(Dispatchers.IO) {
        try {
            val result = supabaseClient.from("cycles").select {
                filter { eq("user_id", userId) }
            }.decodeList<Cycle>()
            Result.success(result)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}

