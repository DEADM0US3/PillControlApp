package com.example.pills.pills.presentation.friends

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pills.pills.domain.repository.FriendRepository
import com.example.pills.pills.domain.repository.NotificationRepository
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.auth
import kotlinx.coroutines.launch


class FriendsViewModel (
    private val friendRepository: FriendRepository,
    private val notificationRepository: NotificationRepository,
    private val supabaseClient: SupabaseClient
) : ViewModel() {


    val user = supabaseClient.auth.currentUserOrNull()
    val userId = user?.id.toString()
    
    var friends by mutableStateOf<List<String>>(emptyList())
        private set

    var loading by mutableStateOf(false)

    fun loadFriends() {
        viewModelScope.launch {
            loading = true
            val result = friendRepository.getFriends(userId)
            loading = false
            friends = result.getOrElse { emptyList() }.mapNotNull {
                it["friend_id"]?.toString() // Puedes hacer una consulta más completa si quieres el nombre
            }
        }
    }

    fun addFriend(friendId: String, onSuccess: () -> Unit) {
        viewModelScope.launch {
            val result = friendRepository.addFriend(userId, friendId)
            if (result.isSuccess) {
                loadFriends()
                onSuccess()
            }
        }
    }

    fun sendReminder(receiverId: String) {
        viewModelScope.launch {
            notificationRepository.sendNotification(
                senderId = userId,
                receiverId = receiverId,
                message = "¡Recuerda tomar tu pastilla hoy!"
            )
        }
    }
}
