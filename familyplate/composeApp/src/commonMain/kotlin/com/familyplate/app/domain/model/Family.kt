package com.familyplate.app.domain.model

data class Family(
    val id: String = "",
    val name: String = "",
    val inviteCode: String = "",
    val memberIds: List<String> = emptyList(),
    val createdBy: String = "",
    val createdAt: Long = 0
)
