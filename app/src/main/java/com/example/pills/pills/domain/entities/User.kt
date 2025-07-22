package com.example.pills.pills.domain.entities

import kotlinx.serialization.Serializable

@Serializable
data class User(
    val id: String?,
    val full_name: String?,
    val profile_image_url: String?,
    val created_at: String?,
    val updated_at: String?
)
