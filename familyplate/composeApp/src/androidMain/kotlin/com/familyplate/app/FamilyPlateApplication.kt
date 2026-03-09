package com.familyplate.app

import android.app.Application
import com.familyplate.app.di.appModule
import org.koin.core.context.startKoin

class FamilyPlateApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            modules(appModule)
        }
    }
}
