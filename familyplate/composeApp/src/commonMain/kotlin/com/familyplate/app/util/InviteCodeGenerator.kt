package com.familyplate.app.util

object InviteCodeGenerator {
    private val chars = "ABCDEFGHJKLMNPQRSTUVWXYZ23456789"

    fun generate(length: Int = 6): String {
        return (1..length)
            .map { chars.random() }
            .joinToString("")
    }
}
