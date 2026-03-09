package com.familyplate.app.data.repository

import com.familyplate.app.domain.model.User
import com.familyplate.app.domain.repository.AuthRepository
import dev.gitlive.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class AuthRepositoryImpl(
    private val firebaseAuth: FirebaseAuth
) : AuthRepository {

    override val authState: Flow<User?> = firebaseAuth.authStateChanged.map { firebaseUser ->
        firebaseUser?.let { user ->
            User(
                id = user.uid,
                email = user.email ?: "",
                displayName = user.displayName ?: "",
                familyId = null,
                createdAt = 0L
            )
        }
    }

    override suspend fun signIn(email: String, password: String): Result<User> = runCatching {
        val result = firebaseAuth.signInWithEmailAndPassword(email, password)
        val fbUser = result.user ?: throw IllegalStateException("Sign in succeeded but user is null")
        User(
            id = fbUser.uid,
            email = fbUser.email ?: "",
            displayName = fbUser.displayName ?: "",
            familyId = null,
            createdAt = 0L
        )
    }

    override suspend fun signUp(email: String, password: String, displayName: String): Result<User> = runCatching {
        val result = firebaseAuth.createUserWithEmailAndPassword(email, password)
        val fbUser = result.user ?: throw IllegalStateException("Sign up succeeded but user is null")
        fbUser.updateProfile(displayName = displayName)
        User(
            id = fbUser.uid,
            email = fbUser.email ?: "",
            displayName = displayName,
            familyId = null,
            createdAt = 0L
        )
    }

    override fun signOut() {
        firebaseAuth.signOut()
    }

    override fun isSignedIn(): Boolean = firebaseAuth.currentUser != null

    override fun getCurrentUserId(): String? = firebaseAuth.currentUser?.uid

}
