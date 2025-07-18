package com.example.pills

import android.app.Application
import com.example.pills.di.viewModelModule
import com.example.pills.di.appModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin
import org.koin.android.ext.koin.androidLogger

class App : Application() {
    override fun onCreate() {
        super.onCreate()

        // Start Koin
        startKoin {
            androidLogger()  // Logs Koin initialization
            androidContext(this@App)
            modules(appModule, viewModelModule) // Add your modules here created in KoinModule.kt
        }
    }
}





