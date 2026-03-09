package com.familyplate.app.di

import com.familyplate.app.data.repository.AuthRepositoryImpl
import com.familyplate.app.data.repository.FamilyRepositoryImpl
import com.familyplate.app.domain.repository.AuthRepository
import com.familyplate.app.domain.repository.FamilyRepository
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.auth.FirebaseAuth
import dev.gitlive.firebase.auth.auth
import dev.gitlive.firebase.firestore.FirebaseFirestore
import dev.gitlive.firebase.firestore.firestore
import org.koin.dsl.module

val appModule = module {
    single<FirebaseAuth> { Firebase.auth }
    single<FirebaseFirestore> { Firebase.firestore }
    single<AuthRepository> { AuthRepositoryImpl(get()) }
    single<FamilyRepository> { FamilyRepositoryImpl(get()) }
}
