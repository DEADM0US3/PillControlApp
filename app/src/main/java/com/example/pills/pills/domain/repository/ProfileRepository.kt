package com.example.pills.pills.domain.repository

import android.content.Context
import com.example.pills.Logger
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.query.Columns
import io.github.jan.supabase.storage.storage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put

class ProfileRepository(
    private val supabaseClient: SupabaseClient,
    private val context: Context
) {

    data class UserProfile(
        val id: String,
        val email: String,
        val fullName: String?,
        val phone: String?,
        val age: String?,
        val profileImageUrl: String?
    )

    suspend fun getUserProfile(): Result<UserProfile> = withContext(Dispatchers.IO) {
        try {
            val currentUser = supabaseClient.auth.currentUserOrNull()
                ?: return@withContext Result.failure(Exception("No authenticated user found"))

            val profileData = supabaseClient
                .from("users")
                .select(
                    columns = Columns.list(
                        "id", "full_name", "phone", "age", "profile_image_url"
                    )
                ) {
                    filter {
                        eq("id", currentUser.id)
                    }
                }
                .decodeList<Map<String, Any>>()
                .firstOrNull()

            if (profileData == null) {
                return@withContext Result.failure(Exception("User profile not found"))
            }

            val profile = UserProfile(
                id = profileData["id"]?.toString() ?: currentUser.id,
                email = currentUser.email ?: "",
                fullName = profileData["full_name"]?.toString(),
                phone = profileData["phone"]?.toString(),
                age = profileData["age"]?.toString(),
                profileImageUrl = profileData["profile_image_url"]?.toString()
            )

            Result.success(profile)
        } catch (e: Exception) {
            Logger.e("ProfileRepository", "Error getting user profile: ${e.message}")
            Result.failure(e)
        }
    }

    suspend fun updateUserProfile(
        fullName: String,
        email: String,
        phone: String?,
        age: String?
    ): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val currentUser = supabaseClient.auth.currentUserOrNull()
                ?: return@withContext Result.failure(Exception("No authenticated user found"))

            // Actualiza email y full_name en auth.users (email en auth, full_name en metadata)
            val result = supabaseClient.auth.updateUser {
                this.email = email
                data = buildJsonObject {
                    put("full_name", fullName)
                }
            }


            if (result == null) {
                // Si 'user' no existe, puede que el resultado sea diferente.
                // Revisa la estructura de 'result'
                return@withContext Result.failure(Exception("Failed to update auth user info"))
            }

            // Actualiza el resto de datos en la tabla users
            supabaseClient
                .from("users")
                .update(
                    mapOf(
                        "full_name" to fullName,
                        "phone" to phone,
                        "age" to age
                    )
                ) {
                    filter {
                        eq("id", currentUser.id)
                    }
                }

            Result.success(Unit)
        } catch (e: Exception) {
            Logger.e("ProfileRepository", "Error updating user profile: ${e.message}")
            Result.failure(e)
        }
    }
}
