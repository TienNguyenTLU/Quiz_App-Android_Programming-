package com.edu.quizapp.ui.teacher.dashboard.test

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.edu.quizapp.data.models.Test
import com.edu.quizapp.data.repository.TestRepository
import kotlinx.coroutines.launch

class TestManagementViewModel : ViewModel() {

    private val testRepository = TestRepository()

    private val _tests = MutableLiveData<List<Test>>()
    val tests: LiveData<List<Test>> = _tests

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _errorMessage = MutableLiveData<String?>()
    val errorMessage: LiveData<String?> = _errorMessage

    private var allTests: List<Test> = emptyList() // Lưu trữ danh sách bài kiểm tra gốc

    init {
        loadTests()
    }

    fun loadTests() {
        _isLoading.value = true
        viewModelScope.launch {
            try {
                allTests = testRepository.getAllTests() // Lưu trữ danh sách gốc
                _tests.value = allTests
                _isLoading.value = false
                _errorMessage.value = null
            } catch (e: Exception) {
                _errorMessage.value = e.message
                _isLoading.value = false
            }
        }
    }

    fun searchTests(query: String) {
        val filteredTests = allTests.filter { test ->
            test.testName.contains(query, ignoreCase = true)
        }
        _tests.value = filteredTests
    }

    fun deleteTest(testId: String) {
        viewModelScope.launch {
            try {
                val success = testRepository.deleteTest(testId)
                if (success) {
                    loadTests() // Tải lại danh sách sau khi xóa
                } else {
                    _errorMessage.value = "Không thể xóa bài kiểm tra."
                }
            } catch (e: Exception) {
                _errorMessage.value = e.message
            }
        }
    }
}