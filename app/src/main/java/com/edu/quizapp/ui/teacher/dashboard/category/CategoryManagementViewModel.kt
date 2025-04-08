package com.edu.quizapp.ui.teacher.dashboard.category

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.edu.quizapp.data.models.Category
import com.edu.quizapp.data.models.Classes
import com.edu.quizapp.data.models.Test
import com.edu.quizapp.data.repository.CategoryRepository
import kotlinx.coroutines.launch

class CategoryManagementViewModel : ViewModel() {

    private val repository = CategoryRepository()

    private val _categories = MutableLiveData<List<Category>>()
    val categories: LiveData<List<Category>> = _categories

    private val _classes = MutableLiveData<List<Classes>>()
    val classes: LiveData<List<Classes>> = _classes

    private val _tests = MutableLiveData<List<Test>>()
    val tests: LiveData<List<Test>> = _tests

    fun loadCategories() {
        viewModelScope.launch {
            _categories.value = repository.getCategories()
        }
    }

    fun loadClassesByCategory(categoryId: String) {
        viewModelScope.launch {
            _classes.value = repository.getClassesByCategory(categoryId)
        }
    }

    fun loadTestsByCategory(categoryId: String) {
        viewModelScope.launch {
            _tests.value = repository.getTestsByCategory(categoryId)
        }
    }
}