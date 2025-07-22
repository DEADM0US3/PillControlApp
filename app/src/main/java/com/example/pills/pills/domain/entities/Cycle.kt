package com.example.pills.pills.domain.entities

import kotlinx.serialization.Serializable

@Serializable
data class Cycle(
    val user_id: String,
    val start_date: String,
    val pill_count: Int,
    val end_date: String,
    val current_day: Int,
) : BaseEntity()

