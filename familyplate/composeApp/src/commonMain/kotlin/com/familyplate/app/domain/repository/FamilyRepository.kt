package com.familyplate.app.domain.repository

import com.familyplate.app.domain.model.Family
import com.familyplate.app.domain.model.User

interface FamilyRepository {
    suspend fun createFamily(name: String, userId: String): Result<Family>
    suspend fun joinFamily(inviteCode: String, userId: String): Result<Family>
    suspend fun getFamily(familyId: String): Result<Family?>
    suspend fun updateUserFamily(userId: String, familyId: String): Result<Unit>
    suspend fun getUserProfile(userId: String): Result<User?>
    suspend fun createUserProfile(user: User): Result<Unit>
}
