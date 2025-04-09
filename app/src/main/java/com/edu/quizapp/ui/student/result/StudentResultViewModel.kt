package com.edu.quizapp.ui.student.result

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.edu.quizapp.data.models.Result
import com.edu.quizapp.data.models.Test
import com.edu.quizapp.data.repository.ResultRepository
import com.edu.quizapp.data.repository.TestRepository
import kotlinx.coroutines.launch

class StudentResultViewModel : ViewModel() {

    private val resultRepository = ResultRepository()
    private val testRepository = TestRepository()

    private val _results = MutableLiveData<List<ResultWithTestInfo>>()
    val results: LiveData<List<ResultWithTestInfo>> = _results

    private val _isLoading = MutableLiveData<Boolean>(false)
    val isLoading: LiveData<Boolean> = _isLoading

    // Class để kết hợp kết quả và thông tin bài kiểm tra
    data class ResultWithTestInfo(
        val result: Result,
        val testName: String,
        val testDuration: Long
    )

    fun fetchResultsForStudent(studentId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val resultsList = resultRepository.getResultsByStudentId(studentId)

                // Lấy thông tin bài kiểm tra cho mỗi kết quả
                val resultsWithTests = mutableListOf<ResultWithTestInfo>()

                for (result in resultsList) {
                    val test = testRepository.getTestById(result.testId)
                    test?.let {
                        resultsWithTests.add(
                            ResultWithTestInfo(
                                result = result,
                                testName = it.testName,
                                testDuration = it.duration
                            )
                        )
                    }
                }

                _results.value = resultsWithTests
            } catch (e: Exception) {
                Log.e("StudentResultViewModel", "Error fetching results: ${e.message}")
                _results.value = emptyList()
            } finally {
                _isLoading.value = false
            }
        }
    }
}
