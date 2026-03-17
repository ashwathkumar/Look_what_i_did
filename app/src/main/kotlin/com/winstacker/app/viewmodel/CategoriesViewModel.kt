package com.winstacker.app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.winstacker.app.data.model.Category
import com.winstacker.app.data.repository.CategoryRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CategoriesViewModel @Inject constructor(
    private val categoryRepository: CategoryRepository
) : ViewModel() {

    val categories: StateFlow<List<Category>> = categoryRepository
        .getAllCategories()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    fun addCategory(name: String, emoji: String, colorHex: String) {
        if (name.isBlank()) return
        viewModelScope.launch {
            categoryRepository.addCategory(
                Category(name = name.trim(), emoji = emoji, colorHex = colorHex)
            )
        }
    }

    fun updateCategory(category: Category, name: String, emoji: String, colorHex: String) {
        viewModelScope.launch {
            categoryRepository.updateCategory(
                category.copy(name = name.trim(), emoji = emoji, colorHex = colorHex)
            )
        }
    }

    fun deleteCategory(category: Category) {
        if (category.isPreset) return
        viewModelScope.launch {
            categoryRepository.deleteCategory(category)
        }
    }
}
