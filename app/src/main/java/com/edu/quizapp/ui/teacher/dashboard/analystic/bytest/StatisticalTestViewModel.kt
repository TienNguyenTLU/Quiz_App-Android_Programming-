package com.edu.quizapp.ui.teacher.dashboard.analystic.bytest

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.edu.quizapp.data.models.Result
import com.edu.quizapp.data.models.Student
import com.edu.quizapp.data.repository.StatisticalTestRepository
import kotlinx.coroutines.launch

class StatisticalTestViewModel(private val repository: StatisticalTestRepository = StatisticalTestRepository()) : ViewModel() {

    private val _results = MutableLiveData<List<Result>>()
    val results: LiveData<List<Result>> = _results

    private val _students = MutableLiveData<Map<String, Student>>()
    val students: LiveData<Map<String, Student>> = _students

    fun fetchResultsAndStudents(testId: String) {
        viewModelScope.launch {
            try {
                val results = repository.getResultsByTestId(testId)
                _results.value = results
                Log.d("StatisticalTestViewModel", "Results fetched: ${results.size}")

                val students = repository.getStudents()
                _students.value = students
                Log.d("StatisticalTestViewModel", "Students fetched: ${students.size}")
            } catch (e: Exception) {
                Log.e("StatisticalTestViewModel", "Error fetching data: ${e.message}")
                e.printStackTrace()
                _results.value = emptyList()
                _students.value = emptyMap()
            }
        }
    }
}