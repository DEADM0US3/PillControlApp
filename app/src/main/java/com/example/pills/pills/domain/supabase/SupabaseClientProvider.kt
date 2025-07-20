package com.example.pills.pills.domain.supabase

import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.auth.Auth
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.storage.Storage
import io.github.jan.supabase.storage.storage

object SupabaseClientProvider {
    //Villafaña 
    //private const val SUPABASE_URL = "https://hkzkyzeibwvecbqbuxbr.supabase.co"
    //private const val SUPABASE_ANON_KEY = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6Imhremt5emVpYnd2ZWNicWJ1eGJyIiwicm9sZSI6ImFub24iLCJpYXQiOjE3NTI3NjQ4NzYsImV4cCI6MjA2ODM0MDg3Nn0.FouzAqQahrNk83AVWq1Ts-rU-s9NlZbX4imU5k0N-MQ"

    //canchesky
    private const val SUPABASE_URL = "https://yhbmhoqvqajgqsowihag.supabase.co"
    private const val SUPABASE_ANON_KEY = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6InloYm1ob3F2cWFqZ3Fzb3dpaGFnIiwicm9sZSI6ImFub24iLCJpYXQiOjE3NTI5ODEwMDgsImV4cCI6MjA2ODU1NzAwOH0.r2eDAzsAFZx8EiLhG6biOrqZ9lSkovo5bpkIvMpuLus"

    val client: SupabaseClient by lazy {
        createSupabaseClient(
            supabaseUrl = SUPABASE_URL,
            supabaseKey = SUPABASE_ANON_KEY
        ) {
            install(Auth) {
                alwaysAutoRefresh = true
                autoLoadFromStorage = true
            }
            install(Postgrest)
            install(Storage)
        }
    }
}
