package com.edu.quizapp.ui.teacher.dashboard.analystic.byclass

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.edu.quizapp.data.models.Result
import com.edu.quizapp.data.models.Test
import com.edu.quizapp.data.repository.StatisticalRepository
import kotlinx.coroutines.launch

class StatisticalClassViewModel(val repository: StatisticalRepository = StatisticalRepository()) : ViewModel() {

    private val _results = MutableLiveData<List<Result>>()
    val results: LiveData<List<Result>> = _results

    private val _errorMessage = MutableLiveData<String?>()
    val errorMessage: LiveData<String?> = _errorMessage

    private val _tests = MutableLiveData<List<Test>>()
    val tests: LiveData<List<Test>> = _tests

    fun fetchResultsByClass(classId: String) {
        viewModelScope.launch {
            try {
                val results = repository.getResultsByClass(classId)
                _results.value = results
                _errorMessage.value = null
            } catch (e: Exception) {
                _errorMessage.value = "Failed to fetch results: ${e.message}"
                _results.value = emptyList()
            }
        }
    }

    fun fetchTestsByClass(classId: String) {
        viewModelScope.launch {
            try {
                val tests = repository.getTestsByClass(classId)
                _tests.value = tests
                _errorMessage.value = null
            } catch (e: Exception) {
                _errorMessage.value = "Failed to fetch tests: ${e.message}"
                _tests.value = emptyList()
            }
        }
    }
}