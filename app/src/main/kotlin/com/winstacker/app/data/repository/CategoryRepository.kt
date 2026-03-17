package com.winstacker.app.data.repository

import com.winstacker.app.data.db.CategoryDao
import com.winstacker.app.data.model.Category
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CategoryRepository @Inject constructor(
    private val categoryDao: CategoryDao
) {
    fun getAllCategories(): Flow<List<Category>> = categoryDao.getAllCategories()

    fun getPresetCategories(): Flow<List<Category>> = categoryDao.getPresetCategories()

    suspend fun getCategoryById(id: Long): Category? = categoryDao.getCategoryById(id)

    suspend fun addCategory(category: Category): Long = categoryDao.insertCategory(category)

    suspend fun updateCategory(category: Category) = categoryDao.updateCategory(category)

    suspend fun deleteCategory(category: Category) {
        check(!category.isPreset) { "Preset categories cannot be deleted" }
        categoryDao.deleteCategory(category)
    }

    suspend fun seedPresetsIfNeeded() {
        if (categoryDao.getCount() == 0) {
            categoryDao.insertAll(Category.PRESET_CATEGORIES)
        }
    }
}
