package com.winstacker.app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.winstacker.app.data.repository.WinRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.temporal.TemporalAdjusters
import javax.inject.Inject

data class StatsUiState(
    val currentStreak: Int = 0,
    val longestStreak: Int = 0,
    val winsThisWeek: Int = 0,
    val winsThisMonth: Int = 0,
    val winsThisYear: Int = 0,
    val topCategoryId: Long? = null,
    val isLoading: Boolean = true
)

@HiltViewModel
class StatsViewModel @Inject constructor(
    private val winRepository: WinRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(StatsUiState())
    val uiState: StateFlow<StatsUiState> = _uiState.asStateFlow()

    init {
        loadStats()
    }

    fun loadStats() {
        viewModelScope.launch {
            val today = LocalDate.now()
            val weekStart = today.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY)).toEpochDay()
            val weekEnd = today.with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY)).toEpochDay()
            val monthStart = today.withDayOfMonth(1).toEpochDay()
            val monthEnd = today.with(TemporalAdjusters.lastDayOfMonth()).toEpochDay()
            val yearStart = today.withDayOfYear(1).toEpochDay()
            val yearEnd = today.with(TemporalAdjusters.lastDayOfYear()).toEpochDay()

            val currentStreak = winRepository.calculateCurrentStreak()
            val longestStreak = winRepository.calculateLongestStreak()
            val winsWeek = winRepository.getTotalWinsInRange(weekStart, weekEnd)
            val winsMonth = winRepository.getTotalWinsInRange(monthStart, monthEnd)
            val winsYear = winRepository.getTotalWinsInRange(yearStart, yearEnd)
            val topCat = winRepository.getTopCategoryInRange(monthStart, monthEnd)

            _uiState.value = StatsUiState(
                currentStreak = currentStreak,
                longestStreak = longestStreak,
                winsThisWeek = winsWeek,
                winsThisMonth = winsMonth,
                winsThisYear = winsYear,
                topCategoryId = topCat?.categoryId,
                isLoading = false
            )
        }
    }
}
