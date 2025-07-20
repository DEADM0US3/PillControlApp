package com.example.pills.pills.domain.use_case

import com.example.pills.pills.domain.repository.ProfileRepository

/**
 * Use case for updating user profile information
 */
class UpdateUserProfile(
    private val repository: ProfileRepository
) {
    suspend operator fun invoke(
        fullName: String,
        email: String,
        phone: String?,
        age: String?
    ): Result<Unit> {
        return repository.updateUserProfile(
            fullName = fullName,
            email = email,
            phone = phone,
            age = age
        )
    }
} 