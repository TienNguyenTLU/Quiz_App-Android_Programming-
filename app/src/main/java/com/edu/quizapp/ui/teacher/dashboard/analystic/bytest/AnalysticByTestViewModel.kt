package com.edu.quizapp.ui.teacher.dashboard.analystic.bytest

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.edu.quizapp.data.models.Test
import com.edu.quizapp.data.models.Teacher
import com.edu.quizapp.data.models.Classes
import com.edu.quizapp.data.repository.AnalysticByTestRepository
import kotlinx.coroutines.launch

class AnalysticByTestViewModel(private val repository: AnalysticByTestRepository = AnalysticByTestRepository()) : ViewModel() {

    private val _testsAndTeachersAndClasses = MutableLiveData<Triple<List<Test>, Map<String, Teacher>, Map<String, Classes>>>()
    val testsAndTeachersAndClasses: LiveData<Triple<List<Test>, Map<String, Teacher>, Map<String, Classes>>> = _testsAndTeachersAndClasses

    fun fetchTestsAndTeachersAndClasses() {
        viewModelScope.launch {
            val tests = repository.getTests()
            val teachers = repository.getTeachers()
            val classes = repository.getClasses() // Lấy classes từ Repository
            _testsAndTeachersAndClasses.value = Triple(tests, teachers, classes)
        }
    }
}