package com.edu.quizapp.ui.student.dashboard.classes

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.edu.quizapp.data.models.Classes // Thêm dòng import này
import com.edu.quizapp.data.repository.ClassRepository
import com.edu.quizapp.data.repository.StudentRepository
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.launch

class StudentClassViewModel : ViewModel() {

    private val classRepository = ClassRepository()
    private val studentRepository = StudentRepository()

    private val _classes = MutableLiveData<List<Classes>>()
    val classes: LiveData<List<Classes>> = _classes

    fun loadStudentClasses(studentId: String) {
        viewModelScope.launch {
            val student = studentRepository.getStudentById(studentId)
            if (student != null) {
                Log.d("StudentClassViewModel", "Student found: $student")
                Log.d("StudentClassViewModel", "Student classes: ${student.classes}")

                val deferredClasses = student.classes.map { classId ->
                    async {
                        try {
                            val classData = classRepository.getClassById(classId)
                            Log.d("StudentClassViewModel", "Class data for $classId: $classData")
                            classData
                        } catch (e: Exception) {
                            Log.e("StudentClassViewModel", "Error fetching class with id $classId: ${e.message}")
                            null
                        }
                    }
                }
                val studentClasses = deferredClasses.awaitAll().filterNotNull()
                _classes.value = studentClasses
            } else {
                Log.d("StudentClassViewModel", "Student not found for id: $studentId")
                _classes.value = emptyList()
            }
        }
    }
}