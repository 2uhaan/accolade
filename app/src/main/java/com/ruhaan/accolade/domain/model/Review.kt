package com.ruhaan.accolade.domain.model

data class Review(
    val id: String,
    val author: String,
    val content: String,
    val rating: Int?, // percentage 0–100, nullable if reviewer didn't rate
    val avatarPath: String?,
    val createdAt: String, // formatted date string
)
