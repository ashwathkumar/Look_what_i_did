package com.winstacker.app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.winstacker.app.data.model.Category
import com.winstacker.app.data.model.CategoryCount
import com.winstacker.app.data.model.WinWithCategory
import com.winstacker.app.data.repository.CategoryRepository
import com.winstacker.app.data.repository.WinRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val winRepository: WinRepository,
    private val categoryRepository: CategoryRepository
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

    val allCategories: StateFlow<List<Category>> = categoryRepository
        .getAllCategories()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    /** Triggers the celebration when the user just logged their 3rd win today */
    private val _showCelebration = MutableStateFlow(false)
    val showCelebration: StateFlow<Boolean> = _showCelebration.asStateFlow()

    private var celebrationShownToday = false
    // -1 means "not yet observed"; used to detect upward transitions to 3
    private var previousWinCount = -1

    init {
        viewModelScope.launch {
            todayWinCount.collect { count ->
                if (previousWinCount == -1) {
                    // First emission — record the baseline without triggering celebration.
                    // This prevents firing when the count is already ≥3 on launch and
                    // the user deletes a win back down to exactly 3.
                    previousWinCount = count
                } else {
                    if (count == 3 && previousWinCount < 3 && !celebrationShownToday) {
                        celebrationShownToday = true
                        _showCelebration.value = true
                    }
                    previousWinCount = count
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
