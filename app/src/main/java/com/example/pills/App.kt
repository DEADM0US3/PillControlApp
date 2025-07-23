package com.example.pills

import android.app.Application
import android.content.Intent
import android.os.Build
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.example.pills.di.viewModelModule
import com.example.pills.di.appModule
import com.example.pills.notifications.NotificationForegroundService
import com.example.pills.pills.domain.supabase.SupabaseClientProvider
import io.github.jan.supabase.auth.auth
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin
import org.koin.android.ext.koin.androidLogger
import java.util.concurrent.TimeUnit

class App : Application() {
    override fun onCreate() {
        super.onCreate()

        CoroutineScope(Dispatchers.Default).launch {
            SupabaseClientProvider.client.auth.loadFromStorage()
        }

        val intent = Intent(this, NotificationForegroundService::class.java)

        startForegroundService(intent) // API 26+


        startKoin {
            androidLogger()  // Logs Koin initialization
            androidContext(this@App)
            modules(appModule, viewModelModule) // Add your modules here created in KoinModule.kt
        }
    }

}





