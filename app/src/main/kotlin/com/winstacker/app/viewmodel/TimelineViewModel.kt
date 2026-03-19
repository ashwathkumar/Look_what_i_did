package com.winstacker.app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.winstacker.app.data.model.Category
import com.winstacker.app.data.model.CategoryCount
import com.winstacker.app.data.model.WinWithCategory
import com.winstacker.app.data.repository.CategoryRepository
import com.winstacker.app.data.repository.WinRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.temporal.TemporalAdjusters
import javax.inject.Inject

enum class Timeframe { TODAY, WEEK, MONTH, YEAR, ALL_TIME }

/** null = show all; negative = "Other" bucket */
sealed class CategoryFilter {
    object All : CategoryFilter()
    data class ByCategory(val categoryId: Long) : CategoryFilter()
    object OtherCategories : CategoryFilter()
}

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class TimelineViewModel @Inject constructor(
    private val winRepository: WinRepository,
    private val categoryRepository: CategoryRepository
) : ViewModel() {

    private val _timeframe = MutableStateFlow(Timeframe.WEEK)
    val timeframe: StateFlow<Timeframe> = _timeframe.asStateFlow()

    private val _categoryFilter = MutableStateFlow<CategoryFilter>(CategoryFilter.All)
    val categoryFilter: StateFlow<CategoryFilter> = _categoryFilter.asStateFlow()

    val allCategories: StateFlow<List<Category>> = categoryRepository
        .getAllCategories()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    private val dateRange: StateFlow<Pair<Long, Long>> = _timeframe.map { tf ->
        computeDateRange(tf)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), computeDateRange(Timeframe.WEEK))

    val categoryCounts: StateFlow<List<CategoryCount>> = dateRange
        .flatMapLatest { (start, end) ->
            winRepository.getCategoryCountsInRange(start, end)
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    val wins: StateFlow<List<WinWithCategory>> = combine(dateRange, _categoryFilter) { range, filter ->
        Pair(range, filter)
    }.flatMapLatest { (range, filter) ->
        val (start, end) = range
        when (filter) {
            is CategoryFilter.All -> winRepository.getWinsInRange(start, end)
            is CategoryFilter.ByCategory -> winRepository.getWinsInRangeByCategory(start, end, filter.categoryId)
            is CategoryFilter.OtherCategories -> winRepository.getWinsInRangeForOther(start, end)
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    fun setTimeframe(tf: Timeframe) {
        _timeframe.value = tf
        _categoryFilter.value = CategoryFilter.All
    }

    fun setCategoryFilter(filter: CategoryFilter) {
        _categoryFilter.value = filter
    }

    private fun computeDateRange(tf: Timeframe): Pair<Long, Long> {
        val today = LocalDate.now()
        return when (tf) {
            Timeframe.TODAY -> {
                val d = today.toEpochDay()
                d to d
            }
            Timeframe.WEEK -> {
                val mon = today.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))
                val sun = today.with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY))
                mon.toEpochDay() to sun.toEpochDay()
            }
            Timeframe.MONTH -> {
                val start = today.withDayOfMonth(1)
                val end = today.with(TemporalAdjusters.lastDayOfMonth())
                start.toEpochDay() to end.toEpochDay()
            }
            Timeframe.YEAR -> {
                val start = today.withDayOfYear(1)
                val end = today.with(TemporalAdjusters.lastDayOfYear())
                start.toEpochDay() to end.toEpochDay()
            }
            Timeframe.ALL_TIME -> {
                0L to LocalDate.of(9999, 12, 31).toEpochDay()
            }
        }
    }

    fun deleteWin(winId: Long) {
        viewModelScope.launch {
            winRepository.deleteWinById(winId)
        }
    }
}
