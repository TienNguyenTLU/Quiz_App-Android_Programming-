package com.edu.quizapp.ui.student.dashboard.test

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.edu.quizapp.data.models.Student // Import Student model
import com.edu.quizapp.data.models.Test
import com.edu.quizapp.data.repository.TestRepository
import com.edu.quizapp.data.repository.StudentRepository // Import StudentRepository
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch

class StudentTestViewModel : ViewModel() {

    private val testRepository = TestRepository()
    private val studentRepository = StudentRepository() // Use StudentRepository

    private val _tests = MutableLiveData<List<Test>>()
    val tests: LiveData<List<Test>> = _tests

    fun fetchTestsForStudent() {
        viewModelScope.launch {
            try {
                val currentUserId = FirebaseAuth.getInstance().currentUser?.uid
                currentUserId?.let { userId ->
                    val student = studentRepository.getStudentById(userId) // Get Student by id
                    student?.classes?.let { classIds ->
                        val allTests = mutableListOf<Test>()
                        classIds.forEach { classId ->
                            val testsInClass = testRepository.getTestsByClassCode(classId)
                            allTests.addAll(testsInClass)
                        }
                        _tests.value = allTests
                    }
                }
            } catch (e: Exception) {
                // Xử lý lỗi, ví dụ: hiển thị thông báo lỗi
                Log.e("StudentTestViewModel", "Error fetching tests: ${e.message}")
                _tests.value = emptyList() // Hoặc giá trị mặc định khác
            }
        }
    }
}