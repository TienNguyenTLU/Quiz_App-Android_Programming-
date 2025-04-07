package com.edu.quizapp.ui.teacher.dashboard.classroom

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.edu.quizapp.data.models.Classes
import com.edu.quizapp.data.repository.ClassRepository
import kotlinx.coroutines.launch

class ClassManagementViewModel : ViewModel() {

    private val classRepository = ClassRepository()
    private val _classList = MutableLiveData<List<Classes>>()
    val classList: LiveData<List<Classes>> = _classList

    private val _navigateToClassDetails = MutableLiveData<Classes?>()
    val navigateToClassDetails: LiveData<Classes?> = _navigateToClassDetails

    init {
        loadClasses()
    }

    private fun loadClasses() {
        viewModelScope.launch {
            val classes = classRepository.getAllClasses()
            _classList.value = classes
        }
    }

    fun onClassClicked(classes: Classes) {
        _navigateToClassDetails.value = classes
    }

    fun onNavigationToClassDetailsComplete() {
        _navigateToClassDetails.value = null
    }

    // ... các hàm khác để thêm, sửa, xóa lớp học
}