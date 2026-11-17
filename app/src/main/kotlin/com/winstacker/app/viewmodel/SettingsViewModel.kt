package com.winstacker.app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.winstacker.app.data.repository.AppAccent
import com.winstacker.app.data.repository.AppTheme
import com.winstacker.app.data.repository.UserPreferencesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val preferencesRepository: UserPreferencesRepository
) : ViewModel() {

    val appTheme: StateFlow<AppTheme> = preferencesRepository.appTheme
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), AppTheme.SYSTEM)

    val appAccent: StateFlow<AppAccent> = preferencesRepository.appAccent
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), AppAccent.GREEN)

    fun setTheme(theme: AppTheme) {
        viewModelScope.launch {
            preferencesRepository.setAppTheme(theme)
        }
    }

    fun setAccent(accent: AppAccent) {
        viewModelScope.launch {
            preferencesRepository.setAppAccent(accent)
        }
    }
}
