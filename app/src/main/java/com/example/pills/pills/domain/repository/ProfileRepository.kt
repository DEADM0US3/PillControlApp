package com.example.pills.pills.domain.repository

import android.content.Context
import android.net.Uri
import android.util.Log
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
import java.io.File
import java.io.FileOutputStream
import java.util.UUID

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
        val profileImageUrl: String?,
        val createdAt: String?,
        val updatedAt: String?
    )

    suspend fun getUserProfile(): Result<UserProfile> {
        return withContext(Dispatchers.IO) {
            try {
                val currentUser = supabaseClient.auth.currentUserOrNull()
                    ?: return@withContext Result.failure(Exception("No authenticated user found"))

                val profileData = try {
                    supabaseClient
                        .from("users")
                        .select(columns = Columns.list("id", "email", "full_name", "phone", "age", "profile_image_url", "created_at", "updated_at")) {
                            filter {
                                eq("id", currentUser.id)
                            }
                        }
                        .decodeList<Map<String, Any>>()
                        .firstOrNull()
                } catch (e: Exception) {
                    Logger.d("ProfileRepository", "Could not fetch from users table: ${e.message}")
                    null
                }

                if (profileData == null) {
                    return@withContext Result.failure(Exception("User profile not found in database"))
                }

                val userProfile = UserProfile(
                    id = profileData["id"]?.toString() ?: currentUser.id,
                    email = profileData["email"]?.toString() ?: currentUser.email ?: "",
                    fullName = profileData["full_name"]?.toString(),
                    phone = profileData["phone"]?.toString(),
                    age = profileData["age"]?.toString(),
                    profileImageUrl = profileData["profile_image_url"]?.toString(),
                    createdAt = profileData["created_at"]?.toString(),
                    updatedAt = profileData["updated_at"]?.toString()
                )

                Logger.d("ProfileRepository", "User profile retrieved successfully: ${userProfile.id}")
                Result.success(userProfile)
            } catch (e: Exception) {
                Logger.e("ProfileRepository", "Error getting user profile: ${e.message}")
                Result.failure(e)
            }
        }
    }

    suspend fun updateUserProfile(
        fullName: String,
        email: String,
        phone: String?,
        age: String?
    ): Result<Unit> {
        return withContext(Dispatchers.IO) {
            try {
                if (fullName.isBlank()) return@withContext Result.failure(Exception("Full name cannot be empty"))
                if (email.isBlank()) return@withContext Result.failure(Exception("Email cannot be empty"))

                val currentUser = supabaseClient.auth.currentUserOrNull()
                    ?: return@withContext Result.failure(Exception("No authenticated user found"))

                supabaseClient.auth.updateUser {
                    data = buildJsonObject {
                        put("full_name", fullName)
                    }
                }

                try {
                    supabaseClient
                        .from("users")
                        .update(
                            mapOf(
                                "full_name" to fullName,
                                "email" to email,
                                "phone" to phone,
                                "age" to age
                            )
                        ) {
                            filter {
                                eq("id", currentUser.id)
                            }
                        }

                    Logger.d("ProfileRepository", "User profile updated successfully: ${currentUser.id}")
                } catch (e: Exception) {
                    Logger.d("ProfileRepository", "Could not update users table: ${e.message}")
                }

                Result.success(Unit)
            } catch (e: Exception) {
                Logger.e("ProfileRepository", "Error updating user profile: ${e.message}")
                Result.failure(e)
            }
        }
    }

    suspend fun updateProfileImage(imageUrl: String): Result<Unit> {
        return withContext(Dispatchers.IO) {
            try {
                if (imageUrl.isBlank()) return@withContext Result.failure(Exception("Image URL cannot be empty"))

                val currentUser = supabaseClient.auth.currentUserOrNull()
                    ?: return@withContext Result.failure(Exception("No authenticated user found"))

                supabaseClient
                    .from("users")
                    .update(
                        mapOf(
                            "profile_image_url" to imageUrl
                        )
                    ) {
                        filter {
                            eq("id", currentUser.id)
                        }
                    }

                Logger.d("ProfileRepository", "Profile image updated successfully: ${currentUser.id}")
                Result.success(Unit)
            } catch (e: Exception) {
                Logger.e("ProfileRepository", "Error updating profile image: ${e.message}")
                Result.failure(e)
            }
        }
    }

    suspend fun uploadProfileImage(imageUri: String): Result<String> {
        return withContext(Dispatchers.IO) {
            var tempFile: File? = null
            try {
                if (imageUri.isBlank()) return@withContext Result.failure(Exception("Image URI cannot be empty"))

                val currentUser = supabaseClient.auth.currentUserOrNull()
                    ?: return@withContext Result.failure(Exception("No authenticated user found"))

                val uri = Uri.parse(imageUri)
                val inputStream = context.contentResolver.openInputStream(uri)
                    ?: return@withContext Result.failure(Exception("Could not open image file"))

                tempFile = File.createTempFile("profile_image_", ".jpg", context.cacheDir)
                val outputStream = FileOutputStream(tempFile)

                inputStream.use { input ->
                    outputStream.use { output ->
                        input.copyTo(output)
                    }
                }

                if (tempFile.length() > 5 * 1024 * 1024) {
                    return@withContext Result.failure(Exception("Image file too large. Maximum size is 5MB"))
                }

                val fileName = "${currentUser.id}/profile_${UUID.randomUUID()}.jpg"

                supabaseClient.storage
                    .from("profile-images")
                    .upload(fileName, tempFile.readBytes())

                val publicUrl = supabaseClient.storage
                    .from("profile-images")
                    .publicUrl(fileName)

                Logger.d("ProfileRepository", "Image uploaded successfully: $publicUrl")
                Result.success(publicUrl)
            } catch (e: Exception) {
                Logger.e("ProfileRepository", "Error uploading profile image: ${e.message}")
                Result.failure(e)
            } finally {
                tempFile?.let { if (it.exists()) it.delete() }
            }
        }
    }

    suspend fun deleteProfileImage(imageUrl: String): Result<Unit> {
        return withContext(Dispatchers.IO) {
            try {
                if (imageUrl.isBlank()) return@withContext Result.failure(Exception("Image URL cannot be empty"))

                val currentUser = supabaseClient.auth.currentUserOrNull()
                    ?: return@withContext Result.failure(Exception("No authenticated user found"))

                val fileName = imageUrl.substringAfterLast("/")
                if (fileName.isBlank()) return@withContext Result.failure(Exception("Invalid image URL format"))

                supabaseClient.storage
                    .from("profile-images")
                    .delete(fileName)

                Logger.d("ProfileRepository", "Image deleted successfully: $fileName")
                Result.success(Unit)
            } catch (e: Exception) {
                Logger.e("ProfileRepository", "Error deleting profile image: ${e.message}")
                Result.failure(e)
            }
        }
    }

    suspend fun doesUserProfileExist(userId: String): Boolean {
        return withContext(Dispatchers.IO) {
            try {
                if (userId.isBlank()) return@withContext false

                val result = supabaseClient
                    .from("users")
                    .select(columns = Columns.list("id")) {
                        filter {
                            eq("id", userId)
                        }
                    }
                    .decodeList<Map<String, String>>()

                result.isNotEmpty()
            } catch (e: Exception) {
                Logger.e("ProfileRepository", "Error checking if user profile exists: ${e.message}")
                false
            }
        }
    }

    suspend fun createUserProfile(
        userId: String,
        email: String,
        fullName: String
    ): Result<Unit> {
        return withContext(Dispatchers.IO) {
            try {
                if (userId.isBlank()) return@withContext Result.failure(Exception("User ID cannot be empty"))
                if (email.isBlank()) return@withContext Result.failure(Exception("Email cannot be empty"))
                if (fullName.isBlank()) return@withContext Result.failure(Exception("Full name cannot be empty"))

                supabaseClient
                    .from("users")
                    .insert(
                        mapOf(
                            "id" to userId,
                            "email" to email,
                            "full_name" to fullName
                        )
                    )

                Logger.d("ProfileRepository", "User profile created successfully: $userId")
                Result.success(Unit)
            } catch (e: Exception) {
                Logger.e("ProfileRepository", "Error creating user profile: ${e.message}")
                Result.failure(e)
            }
        }
    }
}
