package com.ruhaan.accolade.domain.model

data class PaginatedResult<T>(
    val items: List<T>,
    val currentPage: Int,
    val totalPages: Int,
)
