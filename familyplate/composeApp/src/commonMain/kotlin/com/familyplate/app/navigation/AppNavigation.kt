package com.familyplate.app.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import com.familyplate.app.domain.repository.AuthRepository
import com.familyplate.app.domain.repository.FamilyRepository
import com.familyplate.app.ui.auth.SignInScreen
import com.familyplate.app.ui.auth.SignUpScreen
import com.familyplate.app.ui.family.CreateFamilyScreen
import com.familyplate.app.ui.family.FamilySetupScreen
import com.familyplate.app.ui.family.JoinFamilyScreen
import com.familyplate.app.ui.home.HomeScreen
import androidx.compose.runtime.collectAsState
import kotlinx.coroutines.launch
import org.koin.compose.koinInject

sealed class AppScreen {
    data object SignIn : AppScreen()
    data object SignUp : AppScreen()
    data object FamilySetup : AppScreen()
    data object CreateFamily : AppScreen()
    data object JoinFamily : AppScreen()
    data object Home : AppScreen()
}

@Composable
fun AppNavigation() {
    val authRepository: AuthRepository = koinInject()
    val familyRepository: FamilyRepository = koinInject()
    val authUser by authRepository.authState.collectAsState(initial = null)
    val coroutineScope = rememberCoroutineScope()

    var currentScreen by remember { mutableStateOf<AppScreen>(AppScreen.SignIn) }

    LaunchedEffect(authUser) {
        if (authUser == null) {
            currentScreen = AppScreen.SignIn
        } else {
            val profile = familyRepository.getUserProfile(authUser!!.id).getOrNull()
            currentScreen = if (profile?.familyId != null) AppScreen.Home else AppScreen.FamilySetup
        }
    }

    when (currentScreen) {
        AppScreen.SignIn -> SignInScreen(
            onSignInSuccess = { /* LaunchedEffect will handle navigation when authState updates */ },
            onNavigateToSignUp = { currentScreen = AppScreen.SignUp }
        )
        AppScreen.SignUp -> SignUpScreen(
            onSignUpSuccess = { /* LaunchedEffect will handle navigation when authState updates */ },
            onNavigateToSignIn = { currentScreen = AppScreen.SignIn }
        )
        AppScreen.FamilySetup -> FamilySetupScreen(
            onCreateFamily = { currentScreen = AppScreen.CreateFamily },
            onJoinFamily = { currentScreen = AppScreen.JoinFamily }
        )
        AppScreen.CreateFamily -> CreateFamilyScreen(
            onFamilyCreated = { currentScreen = AppScreen.Home },
            onBack = { currentScreen = AppScreen.FamilySetup }
        )
        AppScreen.JoinFamily -> JoinFamilyScreen(
            onFamilyJoined = { currentScreen = AppScreen.Home },
            onBack = { currentScreen = AppScreen.FamilySetup }
        )
        AppScreen.Home -> HomeScreen(
            onSignOut = { authRepository.signOut() }
        )
    }
}
