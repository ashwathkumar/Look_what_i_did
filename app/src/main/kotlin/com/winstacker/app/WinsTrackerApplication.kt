package com.winstacker.app

import android.app.Application
import androidx.glance.appwidget.GlanceAppWidgetManager
import com.winstacker.app.data.repository.CategoryRepository
import com.winstacker.app.widget.WinsWidget
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltAndroidApp
class WinsTrackerApplication : Application() {

    @Inject
    lateinit var categoryRepository: CategoryRepository

    @Inject
    lateinit var winRepository: com.winstacker.app.data.repository.WinRepository

    private val applicationScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    override fun onCreate() {
        super.onCreate()
        applicationScope.launch {
            categoryRepository.seedPresetsIfNeeded()
            winRepository.pruneOldEntries()
            // Refresh widget on startup so it shows current data after reinstall/restart.
            runCatching {
                GlanceAppWidgetManager(this@WinsTrackerApplication)
                    .getGlanceIds(WinsWidget::class.java)
                    .forEach { id -> WinsWidget().update(this@WinsTrackerApplication, id) }
            }
        }
    }
}
