package com.example.pills.pills.infrastructure.ViewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pills.pills.domain.entities.Notification
import com.example.pills.pills.domain.repository.NotificationRepository
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.auth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class NotificationViewModel(
    private val repository: NotificationRepository,
    private val supabaseClient: SupabaseClient
) : ViewModel() {

    private val userId = supabaseClient.auth.currentUserOrNull()?.id.toString()
    private val _notifications = MutableStateFlow<List<Notification>>(emptyList())
    val notifications: StateFlow<List<Notification>> = _notifications

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    fun loadNotifications() {
        viewModelScope.launch {
            repository.getAndDeleteNotificationsForUser(userId).onSuccess { list ->
                _notifications.value = list
            }.onFailure { throwable ->
                _error.value = throwable.message
            }
        }
    }
}
