// AddTestViewModel.kt
package com.edu.quizapp.ui.teacher.dashboard.test

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.edu.quizapp.data.models.Test
import com.edu.quizapp.data.repository.TestRepository
import kotlinx.coroutines.launch

class AddTestViewModel : ViewModel() {

    private val testRepository = TestRepository()

    fun addTest(test: Test, callback: (Boolean, String) -> Unit) {
        viewModelScope.launch {
            try {
                val success = testRepository.createTest(test)
                if (success) {
                    callback(true, "Thêm bài kiểm tra thành công.")
                } else {
                    callback(false, "Không thể thêm bài kiểm tra.")
                }
            } catch (e: Exception) {
                callback(false, e.message ?: "Lỗi không xác định.")
            }
        }
    }
}