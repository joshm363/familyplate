package com.familyplate.app.domain.model

data class User(
    val id: String = "",
    val email: String = "",
    val displayName: String = "",
    val familyId: String? = null,
    val createdAt: Long = 0
)
