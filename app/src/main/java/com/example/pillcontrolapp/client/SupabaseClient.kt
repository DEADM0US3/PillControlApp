package com.example.pillcontrolapp.client

import io.github.jan.supabase.auth.Auth
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.postgrest.Postgrest


object SupabaseClient {
    val client = createSupabaseClient(
    supabaseUrl = "https://hkzkyzeibwvecbqbuxbr.supabase.co",
    supabaseKey = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6Imhremt5emVpYnd2ZWNicWJ1eGJyIiwicm9sZSI6ImFub24iLCJpYXQiOjE3NTI3NjQ4NzYsImV4cCI6MjA2ODM0MDg3Nn0.FouzAqQahrNk83AVWq1Ts-rU-s9NlZbX4imU5k0N-MQ"
    ) {
        install(Auth)
        install(Postgrest)
    }
}