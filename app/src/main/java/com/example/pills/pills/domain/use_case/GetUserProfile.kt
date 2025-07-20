package com.example.pills.pills.domain.use_case

import com.example.pills.pills.domain.repository.ProfileRepository

/**
 * Use case for getting the current user's profile
 */
class GetUserProfile(
    private val repository: ProfileRepository
) {
    suspend operator fun invoke(): Result<ProfileRepository.UserProfile> {
        return repository.getUserProfile()
    }
} 