package com.example.pills.pills.infrastructure.ViewModel

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pills.pills.domain.repository.FriendRepository
import com.example.pills.pills.domain.repository.FriendWithUser
import com.example.pills.pills.domain.repository.NotificationRepository
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.auth
import kotlinx.coroutines.launch

class FriendsViewModel(
    private val friendRepository: FriendRepository,
    private val notificationRepository: NotificationRepository,
    private val supabaseClient: SupabaseClient
) : ViewModel() {

    private val userId = supabaseClient.auth.currentUserOrNull()?.id.orEmpty()

    var friends by mutableStateOf<List<FriendWithUser>>(emptyList())

    var loading by mutableStateOf(false)
        private set

    fun loadFriends() {
        viewModelScope.launch {
            loading = true
            val result = friendRepository.getFriends(userId)
            loading = false

            result.onSuccess { list ->
                friends = list
                Log.d("FriendsViewModel", "Loaded friends: $friends")
            }.onFailure { error ->

                Log.e("FriendsViewModel", "Error loading friends", error)
                friends = emptyList()

            }
        }
    }

    fun addFriend(friendEmail: String, onSuccess: () -> Unit) {
        viewModelScope.launch {
            friendRepository.getUserIdByEmail(friendEmail)
                .onSuccess { friendId ->
                    val result = friendRepository.addFriend(userId, friendId)
                    if (result.isSuccess) {
                        loadFriends()
                        onSuccess()
                    } else {
                        Log.e("FriendsViewModel", "Error adding friend: ${result.exceptionOrNull()}")
                    }
                }
                .onFailure {
                    Log.e("FriendsViewModel", "Error finding user by email: ${it.message}")
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