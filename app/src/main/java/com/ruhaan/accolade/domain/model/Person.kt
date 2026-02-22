package com.ruhaan.accolade.domain.model

data class Person(
    val id: Int,
    val name: String,
    val biography: String?,
    val profilePath: String?,
    val birthday: String?,
    val placeOfBirth: String?,
)
