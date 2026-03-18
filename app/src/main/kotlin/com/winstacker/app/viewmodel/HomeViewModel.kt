package com.winstacker.app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.winstacker.app.data.model.CategoryCount
import com.winstacker.app.data.model.WinWithCategory
import com.winstacker.app.data.repository.WinRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val winRepository: WinRepository
) : ViewModel() {

    private val today = LocalDate.now().toEpochDay()

    val todayWins: StateFlow<List<WinWithCategory>> = winRepository
        .getWinsInRange(today, today)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    val todayCategoryCounts: StateFlow<List<CategoryCount>> = winRepository
        .getCategoryCountsInRange(today, today)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    val todayWinCount: StateFlow<Int> = winRepository
        .getWinCountForDay(today)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), 0)

    /** Triggers the celebration when the user just logged their 3rd win today */
    private val _showCelebration = MutableStateFlow(false)
    val showCelebration: StateFlow<Boolean> = _showCelebration.asStateFlow()

    private var celebrationShownToday = false

    init {
        viewModelScope.launch {
            todayWinCount.collect { count ->
                if (count == 3 && !celebrationShownToday) {
                    celebrationShownToday = true
                    _showCelebration.value = true
                }
            }
        }
    }

    fun dismissCelebration() {
        _showCelebration.value = false
    }

    fun deleteWin(winId: Long) {
        viewModelScope.launch {
            winRepository.deleteWinById(winId)
        }
    }
}
