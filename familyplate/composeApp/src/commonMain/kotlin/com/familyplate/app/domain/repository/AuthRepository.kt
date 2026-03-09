package com.familyplate.app.domain.repository

import com.familyplate.app.domain.model.User
import kotlinx.coroutines.flow.Flow

interface AuthRepository {
    val authState: Flow<User?>
    suspend fun signIn(email: String, password: String): Result<User>
    suspend fun signUp(email: String, password: String, displayName: String): Result<User>
    fun signOut()
    fun isSignedIn(): Boolean
    fun getCurrentUserId(): String?
}
