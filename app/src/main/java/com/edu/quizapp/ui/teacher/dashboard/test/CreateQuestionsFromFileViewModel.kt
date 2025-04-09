package com.edu.quizapp.ui.teacher.dashboard.test

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.edu.quizapp.data.models.Question
import com.edu.quizapp.data.repository.QuestionRepository
import com.edu.quizapp.data.repository.TestRepository
import kotlinx.coroutines.launch

class CreateQuestionsFromFileViewModel : ViewModel() {

    private val questionRepository = QuestionRepository()
    private val testRepository = TestRepository()

    fun saveQuestions(questions: List<Question>, testId: String, callback: (Boolean, String) -> Unit) {
        viewModelScope.launch {
            try {
                var allQuestionsSaved = true
                var errorMessage = ""
                questions.forEach { question ->
                    val success = questionRepository.createQuestion(question)
                    if (!success) {
                        allQuestionsSaved = false
                        errorMessage = "Lỗi lưu câu hỏi: ${question.questionId}"
                        android.util.Log.e("CreateQuestionsVM", "Lỗi lưu câu hỏi: ${question.questionId}")
                        return@forEach
                    }
                }
                if (allQuestionsSaved) {
                    // Cập nhật questionCount trong collection "tests"
                    val updateSuccess = testRepository.updateQuestionCount(testId, questions.size)
                    if (updateSuccess) {
                        callback(true, "Lưu câu hỏi thành công.")
                    } else {
                        callback(false, "Lưu câu hỏi thành công, nhưng không cập nhật được số lượng câu hỏi.")
                    }
                } else {
                    callback(false, errorMessage)
                }
            } catch (e: Exception) {
                android.util.Log.e("CreateQuestionsVM", "Lỗi tổng thể: ${e.message}")
                callback(false, "Lỗi lưu câu hỏi: ${e.message}")
            }
        }
    }
}