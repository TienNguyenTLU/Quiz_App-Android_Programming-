package com.edu.quizapp.ui.teacher.dashboard.analystic.byclass

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.edu.quizapp.data.models.Classes
import com.edu.quizapp.data.repository.ClassRepository
import kotlinx.coroutines.launch

class AnalysticByClassViewModel : ViewModel() {

    private val _classes = MutableLiveData<List<Classes>>()
    val classes: LiveData<List<Classes>> = _classes

    private val _errorMessage = MutableLiveData<String?>()
    val errorMessage: LiveData<String?> = _errorMessage

    private val classRepository = ClassRepository()
    private var teacherId: String? = null

    fun setTeacherId(teacherId: String) {
        this.teacherId = teacherId
    }

    fun fetchClasses() {
        viewModelScope.launch {
            try {
                if (teacherId.isNullOrEmpty()) {
                    _errorMessage.value = "Teacher ID is missing."
                    _classes.value = emptyList()
                    return@launch
                }

                val allClasses = classRepository.getAllClasses()
                val teacherClasses = allClasses.filter { it.teacherId == teacherId }
                _classes.value = teacherClasses
                _errorMessage.value = null // Clear any previous error
            } catch (e: Exception) {
                _errorMessage.value = "Failed to fetch classes: ${e.message}"
                _classes.value = emptyList()
            }
        }
    }
}