package com.edu.quizapp.ui.student.result

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.edu.quizapp.data.models.Result
import com.edu.quizapp.data.repository.ResultRepository
import com.edu.quizapp.data.repository.TestRepository
import com.edu.quizapp.data.models.QuizResult
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class StudentResultViewModel : ViewModel() {

    private val resultRepository = ResultRepository()
    private val testRepository = TestRepository()
    private val firestore = FirebaseFirestore.getInstance()

    private val _results = MutableLiveData<List<QuizResult>>()
    val results: LiveData<List<QuizResult>> = _results

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    // Class để kết hợp kết quả và thông tin bài kiểm tra
    data class ResultWithTestInfo(
        val result: Result,
        val testName: String,
        val testDuration: Long
    )

    fun fetchResultsForStudent(studentId: String) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                val snapshot = firestore.collection("quiz_results")
                    .whereEqualTo("studentId", studentId)
                    .orderBy("timestamp", com.google.firebase.firestore.Query.Direction.DESCENDING)
                    .get()
                    .await()

                val resultsList = snapshot.documents.mapNotNull { doc ->
                    doc.toObject(QuizResult::class.java)?.copy(id = doc.id)
                }
                _results.value = resultsList
            } catch (e: Exception) {
                Log.e("StudentResultViewModel", "Error fetching results: ${e.message}")
                _results.value = emptyList()
            } finally {
                _isLoading.value = false
            }
        }
    }
}
