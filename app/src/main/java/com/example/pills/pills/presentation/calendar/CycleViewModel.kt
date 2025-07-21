package com.example.pills.pills.presentation.cycle

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pills.pills.domain.repository.CycleRepository
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.auth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.time.LocalDate

data class CalendarEvent(
    val date: LocalDate,
    val pillTaken: Boolean = false,
    val isMenstruation: Boolean = false,
    val isOvulation: Boolean = false,
    val isFertileWindow: Boolean = false,
    val other: Boolean = false
)

@Serializable
data class Cycle(
    val id: String,
    val user_id: String,
    val start_date: String,
    val pill_count: Int,
    val end_date: String,
    val current_day: Int,
    val is_deleted: Boolean
)

class CycleViewModel(
private val cycleRepository: CycleRepository,
private val supabaseClient: SupabaseClient
) : ViewModel() {

    private val _calendarEvents = MutableStateFlow<Map<LocalDate, CalendarEvent>>(emptyMap())
    val calendarEvents: StateFlow<Map<LocalDate, CalendarEvent>> = _calendarEvents

    private val _cycleState = MutableStateFlow<Result<Cycle>?>(null)
    val cycleState: StateFlow<Result<Cycle>?> = _cycleState

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    private val userId = supabaseClient.auth.currentUserOrNull()?.id.toString()

    fun fetchActiveCycle() {
        viewModelScope.launch {
            val result = cycleRepository.getActiveCycle(userId)

            result.onSuccess {
                cycle -> _cycleState.value = Result.success(cycle)

                val startDate = LocalDate.parse(cycle.start_date)

                Log.d("CycleViewModel", "Ciclo activo encontrado: ${Json.encodeToString(cycle)}")

                val pillCount = cycle.pill_count
                generateEvents(startDate, pillCount)
            }.onFailure {
                _cycleState.value = Result.failure(it)
                _error.value = "Error cargando ciclo: ${it.message}"
            }
        }
    }

    fun startNewCycle(startDate: String, pillCount: Int = 21) {
        viewModelScope.launch {
            cycleRepository.createCycle(userId, startDate, pillCount)
            fetchActiveCycle()
        }
    }

    private fun generateEvents(startDate: LocalDate, pillCount: Int) {
        val events = mutableMapOf<LocalDate, CalendarEvent>()

        for (i in 0 until 28) {
            val date = startDate.plusDays(i.toLong())

            val isTakingPills = i in 0..20
            val isPlaceboDays = i in 21..28
            val isWithdrawalBleeding = i in 22..26

            events[date] = CalendarEvent(
                date = date,
                isMenstruation = isWithdrawalBleeding,
                isOvulation = isPlaceboDays,
                other = isTakingPills && !isWithdrawalBleeding
            )
        }


        _calendarEvents.value = events
    }
}
