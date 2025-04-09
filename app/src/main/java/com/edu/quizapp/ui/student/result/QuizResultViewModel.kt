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

class QuizResultViewModel : ViewModel() {

    private val resultRepository = ResultRepository()
    private val testRepository = TestRepository()

    private val _result = MutableLiveData<Result>()
    val result: LiveData<Result> = _result

    private val _test = MutableLiveData<Test>()
    val test: LiveData<Test> = _test

    private val _isLoading = MutableLiveData<Boolean>(false)
    val isLoading: LiveData<Boolean> = _isLoading

    fun loadResult(resultId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val results = resultRepository.getResultsByStudentId("") // Chỉ để lấy danh sách kết quả
                val foundResult = results.find { it.resultId == resultId }
                foundResult?.let {
                    _result.value = it
                }
            } catch (e: Exception) {
                Log.e("QuizResultViewModel", "Error loading result: ${e.message}")
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun loadTest(testId: String) {
        viewModelScope.launch {
            try {
                val testData = testRepository.getTestById(testId)
                testData?.let {
                    _test.value = it
                }
            } catch (e: Exception) {
                Log.e("QuizResultViewModel", "Error loading test: ${e.message}")
            }
        }
    }
}
