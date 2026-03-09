package com.familyplate.app

import androidx.compose.runtime.Composable
import com.familyplate.app.navigation.AppNavigation
import com.familyplate.app.ui.theme.FamilyPlateTheme

@Composable
fun App() {
    FamilyPlateTheme {
        AppNavigation()
    }
}
