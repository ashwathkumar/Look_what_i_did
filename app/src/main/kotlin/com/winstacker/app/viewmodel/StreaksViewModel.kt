package com.winstacker.app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.winstacker.app.data.repository.WinRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.temporal.TemporalAdjusters
import javax.inject.Inject

data class StreaksUiState(
    val currentStreak: Int = 0,
    val longestStreak: Int = 0,
    val winsToday: Int = 0,
    val winsThisWeek: Int = 0,
    val winsThisMonth: Int = 0,
    val winsThisYear: Int = 0,
    val topCategoryId: Long? = null,
    val isLoading: Boolean = true
)

@HiltViewModel
class StreaksViewModel @Inject constructor(
    private val winRepository: WinRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(StreaksUiState())
    val uiState: StateFlow<StreaksUiState> = _uiState.asStateFlow()

    /**
     * Streaks and period totals come from suspending queries rather than Flows,
     * so the screen re-reads them every time it is shown. That keeps the counts
     * honest after a win is logged, deleted or edited elsewhere in the app.
     */
    fun refresh() {
        viewModelScope.launch {
            val today = LocalDate.now()
            val todayDay = today.toEpochDay()
            val weekStart = today.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY)).toEpochDay()
            val weekEnd = today.with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY)).toEpochDay()
            val monthStart = today.withDayOfMonth(1).toEpochDay()
            val monthEnd = today.with(TemporalAdjusters.lastDayOfMonth()).toEpochDay()
            val yearStart = today.withDayOfYear(1).toEpochDay()
            val yearEnd = today.with(TemporalAdjusters.lastDayOfYear()).toEpochDay()

            _uiState.value = StreaksUiState(
                currentStreak = winRepository.calculateCurrentStreak(),
                longestStreak = winRepository.calculateLongestStreak(),
                winsToday = winRepository.getWinCountForDaySuspend(todayDay),
                winsThisWeek = winRepository.getTotalWinsInRange(weekStart, weekEnd),
                winsThisMonth = winRepository.getTotalWinsInRange(monthStart, monthEnd),
                winsThisYear = winRepository.getTotalWinsInRange(yearStart, yearEnd),
                topCategoryId = winRepository.getTopCategoryInRange(monthStart, monthEnd)?.categoryId,
                isLoading = false
            )
        }
    }
}
