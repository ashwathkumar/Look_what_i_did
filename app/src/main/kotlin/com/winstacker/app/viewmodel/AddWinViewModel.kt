package com.winstacker.app.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.glance.appwidget.GlanceAppWidgetManager
import com.winstacker.app.data.model.Category
import com.winstacker.app.data.model.Win
import com.winstacker.app.data.model.WinSource
import com.winstacker.app.data.repository.CategoryRepository
import com.winstacker.app.data.repository.WinRepository
import com.winstacker.app.widget.WinsWidget
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class AddWinViewModel @Inject constructor(
    private val winRepository: WinRepository,
    private val categoryRepository: CategoryRepository,
    @ApplicationContext private val context: Context
) : ViewModel() {

    val categories: StateFlow<List<Category>> = categoryRepository
        .getAllCategories()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    private val _loadedWin = MutableStateFlow<Win?>(null)
    val loadedWin: StateFlow<Win?> = _loadedWin.asStateFlow()

    private val _saveSuccess = MutableSharedFlow<Unit>()
    val saveSuccess: SharedFlow<Unit> = _saveSuccess.asSharedFlow()

    private var widgetUpdateJob: Job? = null

    fun loadWin(id: Long) {
        viewModelScope.launch {
            _loadedWin.value = winRepository.getWinById(id)
        }
    }

    fun saveWin(
        existingWin: Win?,
        title: String,
        notes: String,
        categoryId: Long?,
        date: LocalDate
    ) {
        if (title.isBlank()) return
        viewModelScope.launch {
            val now = System.currentTimeMillis()
            if (existingWin == null) {
                winRepository.addWin(
                    Win(
                        title = title.trim(),
                        notes = notes.trim(),
                        categoryId = categoryId,
                        dateEpochDay = date.toEpochDay(),
                        createdAtMs = now,
                        updatedAtMs = now,
                        source = WinSource.MANUAL
                    )
                )
            } else {
                winRepository.updateWin(
                    old = existingWin,
                    new = existingWin.copy(
                        title = title.trim(),
                        notes = notes.trim(),
                        categoryId = categoryId,
                        dateEpochDay = date.toEpochDay(),
                        updatedAtMs = now
                    )
                )
            }
            _saveSuccess.emit(Unit)
            scheduleWidgetUpdate()
        }
    }

    fun deleteWin(win: Win, onComplete: () -> Unit) {
        viewModelScope.launch {
            winRepository.deleteWin(win)
            // Update widget before navigating — onComplete() clears viewModelScope,
            // so we can't defer this with a delay the way saves do.
            runCatching {
                GlanceAppWidgetManager(context).getGlanceIds(WinsWidget::class.java)
                    .forEach { WinsWidget().update(context, it) }
            }
            onComplete()
        }
    }

    private fun scheduleWidgetUpdate() {
        widgetUpdateJob?.cancel()
        widgetUpdateJob = viewModelScope.launch {
            delay(300)
            runCatching {
                GlanceAppWidgetManager(context).getGlanceIds(WinsWidget::class.java)
                    .forEach { WinsWidget().update(context, it) }
            }
        }
    }
}
