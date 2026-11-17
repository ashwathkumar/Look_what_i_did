package com.winstacker.app.data.repository

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

enum class AppTheme { SYSTEM, LIGHT, DARK }

/**
 * Which colour palette the app paints itself with. DYNAMIC defers to the
 * wallpaper-derived Material You palette and is only offered on Android 12+.
 */
enum class AppAccent { GREEN, BLUE, PURPLE, ORANGE, ROSE, DYNAMIC }

@Singleton
class UserPreferencesRepository @Inject constructor(
    private val dataStore: DataStore<Preferences>
) {
    companion object {
        private val KEY_THEME = stringPreferencesKey("theme")
        private val KEY_ACCENT = stringPreferencesKey("accent")
    }

    val appTheme: Flow<AppTheme> = dataStore.data.map { prefs ->
        when (prefs[KEY_THEME]) {
            AppTheme.LIGHT.name -> AppTheme.LIGHT
            AppTheme.DARK.name -> AppTheme.DARK
            else -> AppTheme.SYSTEM
        }
    }

    val appAccent: Flow<AppAccent> = dataStore.data.map { prefs ->
        val stored = prefs[KEY_ACCENT]
        AppAccent.entries.firstOrNull { it.name == stored } ?: AppAccent.GREEN
    }

    suspend fun setAppTheme(theme: AppTheme) {
        dataStore.edit { prefs ->
            prefs[KEY_THEME] = theme.name
        }
    }

    suspend fun setAppAccent(accent: AppAccent) {
        dataStore.edit { prefs ->
            prefs[KEY_ACCENT] = accent.name
        }
    }
}
