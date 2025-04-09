package com.edu.quizapp.ui.student.test

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.edu.quizapp.data.models.Student
import com.edu.quizapp.data.models.Test
import com.edu.quizapp.data.repository.StudentRepository
import com.edu.quizapp.data.repository.TestRepository
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch

class StudentTestViewModel : ViewModel() {

    private val testRepository = TestRepository()
    private val studentRepository = StudentRepository()

    private val _tests = MutableLiveData<List<Test>>()
    val tests: LiveData<List<Test>> = _tests

    private val _isLoading = MutableLiveData<Boolean>(false)
    val isLoading: LiveData<Boolean> = _isLoading

    fun fetchTestsForStudent() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val currentUserId = FirebaseAuth.getInstance().currentUser?.uid
                currentUserId?.let { userId ->
                    val student = studentRepository.getStudentById(userId)
                    student?.let { s ->
                        val classes = s.classes ?: emptyList()
                        val allTests = mutableListOf<Test>()

                        for (classCode in classes) {
                            val testsInClass = testRepository.getTestsByClassCode(classCode)
                            allTests.addAll(testsInClass)
                        }

                        _tests.value = allTests
                    } ?: run {
                        _tests.value = emptyList()
                    }
                } ?: run {
                    _tests.value = emptyList()
                }
            } catch (e: Exception) {
                Log.e("StudentTestViewModel", "Error fetching tests: ${e.message}")
                _tests.value = emptyList()
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun fetchTestsByClassCode(classCode: String) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val testsInClass = testRepository.getTestsByClassCode(classCode)
                _tests.value = testsInClass
            } catch (e: Exception) {
                Log.e("StudentTestViewModel", "Error fetching tests by class: ${e.message}")
                _tests.value = emptyList()
            } finally {
                _isLoading.value = false
            }
        }
    }
}
