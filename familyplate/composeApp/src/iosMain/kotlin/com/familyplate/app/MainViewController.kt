package com.familyplate.app

import androidx.compose.ui.window.ComposeUIViewController
import com.familyplate.app.di.appModule
import org.koin.core.context.startKoin

fun MainViewController() = ComposeUIViewController {
    App()
}

fun initKoin() {
    startKoin {
        modules(appModule)
    }
}
