package com.familyplate.app.data.repository

import com.familyplate.app.domain.model.Family
import com.familyplate.app.domain.model.User
import com.familyplate.app.domain.repository.FamilyRepository
import com.familyplate.app.util.InviteCodeGenerator
import dev.gitlive.firebase.firestore.FirebaseFirestore
import kotlinx.datetime.Clock

class FamilyRepositoryImpl(
    private val firestore: FirebaseFirestore
) : FamilyRepository {

    private val familiesCollection = firestore.collection("families")
    private val usersCollection = firestore.collection("users")

    override suspend fun createFamily(name: String, userId: String): Result<Family> = runCatching {
        val inviteCode = InviteCodeGenerator.generate()
        val createdAt = Clock.System.now().toEpochMilliseconds()
        val data = mapOf(
            "name" to name,
            "inviteCode" to inviteCode,
            "memberIds" to listOf(userId),
            "createdBy" to userId,
            "createdAt" to createdAt
        )
        val docRef = familiesCollection.add(data)
        Family(
            id = docRef.id,
            name = name,
            inviteCode = inviteCode,
            memberIds = listOf(userId),
            createdBy = userId,
            createdAt = createdAt
        )
    }

    override suspend fun joinFamily(inviteCode: String, userId: String): Result<Family> = runCatching {
        val snapshot = familiesCollection
            .where { "inviteCode" equalTo inviteCode }
            .get()
        val doc = snapshot.documents.firstOrNull()
            ?: throw NoSuchElementException("No family found with invite code: $inviteCode")
        val currentMemberIds = (doc.get<List<String>>("memberIds") ?: emptyList()).toMutableList()
        if (userId !in currentMemberIds) {
            currentMemberIds.add(userId)
            doc.reference.update(mapOf("memberIds" to currentMemberIds))
        }
        Family(
            id = doc.id,
            name = doc.get<String>("name") ?: "",
            inviteCode = doc.get<String>("inviteCode") ?: "",
            memberIds = currentMemberIds,
            createdBy = doc.get<String>("createdBy") ?: "",
            createdAt = (doc.get<Number>("createdAt")?.toLong()) ?: 0L
        )
    }

    override suspend fun getFamily(familyId: String): Result<Family?> = runCatching {
        val doc = familiesCollection.document(familyId).get()
        if (!doc.exists) return@runCatching null
        Family(
            id = doc.id,
            name = doc.get<String>("name") ?: "",
            inviteCode = doc.get<String>("inviteCode") ?: "",
            memberIds = doc.get<List<String>>("memberIds") ?: emptyList(),
            createdBy = doc.get<String>("createdBy") ?: "",
            createdAt = (doc.get<Number>("createdAt")?.toLong()) ?: 0L
        )
    }

    override suspend fun updateUserFamily(userId: String, familyId: String): Result<Unit> = runCatching {
        usersCollection.document(userId).set(mapOf("familyId" to familyId), merge = true)
    }

    override suspend fun getUserProfile(userId: String): Result<User?> = runCatching {
        val doc = usersCollection.document(userId).get()
        if (!doc.exists) return@runCatching null
        User(
            id = doc.id,
            email = doc.get<String>("email") ?: "",
            displayName = doc.get<String>("displayName") ?: "",
            familyId = doc.get<String>("familyId"),
            createdAt = (doc.get<Number>("createdAt")?.toLong()) ?: 0L
        )
    }

    override suspend fun createUserProfile(user: User): Result<Unit> = runCatching {
        val data = mapOf(
            "email" to user.email,
            "displayName" to user.displayName,
            "familyId" to user.familyId,
            "createdAt" to user.createdAt
        )
        usersCollection.document(user.id).set(data)
    }

}
