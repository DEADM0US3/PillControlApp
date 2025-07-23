package com.example.pills.pills.infrastructure.ViewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pills.pills.domain.entities.Pill
import com.example.pills.pills.domain.repository.PillRepository
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.auth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate
import kotlin.toString

data class PillUiState(
    val isLoading: Boolean = false,
    val pillsOfMonth: List<Pill> = emptyList(),
    val errorMessage: String? = null,
    val pillOfDay: Pill ? = null,
    val pillOfToday: Pill ? = null,
    val successMessage: String? = null
)

class PillViewModel(
    private val repository: PillRepository,
    private val client: SupabaseClient
) : ViewModel() {

    private val _uiState = MutableStateFlow(PillUiState())
    val uiState: StateFlow<PillUiState> = _uiState

    private val userId = client.auth.currentUserOrNull()?.id.toString();

    fun loadPillsOfMonth( year: Int, month: Int) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null, successMessage = null)
            val result = repository.getPillsInMonth(userId, year, month)
            _uiState.value = if (result.isSuccess) {
                _uiState.value.copy(
                    isLoading = false,
                    pillsOfMonth = result.getOrDefault(emptyList()),
                    errorMessage = null
                )
            } else {
                _uiState.value.copy(
                    isLoading = false,
                    errorMessage = result.exceptionOrNull()?.message ?: "Error desconocido"
                )
            }
        }
    }

    fun loadPillOfToday( date: LocalDate) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null, successMessage = null)
            val result = repository.getPillByDate(userId, date.toString())
            _uiState.value = if (result.isSuccess) {
                _uiState.value.copy(
                    isLoading = false,
                    pillOfToday = result.getOrNull(),
                    errorMessage = null
                )
            } else {
                _uiState.value.copy(
                    isLoading = false,
                    errorMessage = result.exceptionOrNull()?.message ?: "Error desconocido"
                )
            }
        }
    }
    fun loadPillOfDay( date: LocalDate) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null, successMessage = null)
            val result = repository.getPillByDate(userId, date.toString())
            _uiState.value = if (result.isSuccess) {
                _uiState.value.copy(
                    isLoading = false,
                    pillOfDay = result.getOrNull(),
                    errorMessage = null
                )
            } else {
                _uiState.value.copy(
                    isLoading = false,
                    errorMessage = result.exceptionOrNull()?.message ?: "Error desconocido"
                )
            }
        }
    }

    fun takePill(
        
        cycleId: String,
        date: LocalDate,
        status: String,
        complications: String? = null
    ) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null, successMessage = null)
            val result = repository.takePill(userId, cycleId, date.toString(), status, complications)
            _uiState.value = if (result.isSuccess) {
                _uiState.value.copy(
                    isLoading = false,
                    successMessage = "Pastilla registrada correctamente",
                    errorMessage = null
                )
            } else {
                _uiState.value.copy(
                    isLoading = false,
                    errorMessage = result.exceptionOrNull()?.message ?: "Error desconocido"
                )
            }
        }
    }

    fun editPill(
        pillId: String,
        status: String?,
        complications: String?
    ) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null, successMessage = null)
            val result = repository.editPill(pillId, status, complications)
            _uiState.value = if (result.isSuccess) {
                _uiState.value.copy(
                    isLoading = false,
                    successMessage = "Pastilla actualizada correctamente",
                    errorMessage = null
                )
            } else {
                _uiState.value.copy(
                    isLoading = false,
                    errorMessage = result.exceptionOrNull()?.message ?: "Error desconocido"
                )
            }
        }
    }

    fun clearMessages() {
        _uiState.value = _uiState.value.copy(errorMessage = null, successMessage = null)
    }
}
