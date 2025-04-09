package com.edu.quizapp.ui.teacher.dashboard.test

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.edu.quizapp.data.models.Question
import com.edu.quizapp.data.models.Test
import com.edu.quizapp.data.repository.QuestionRepository
import com.edu.quizapp.data.repository.TestRepository
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class CreateQuestionsFromFileViewModel : ViewModel() {

    private val questionRepository = QuestionRepository()
    private val testRepository = TestRepository()
    
    private val _saveState = MutableStateFlow<SaveState>(SaveState.Idle)
    val saveState: StateFlow<SaveState> = _saveState

    sealed class SaveState {
        object Idle : SaveState()
        object Saving : SaveState()
        data class Success(val message: String) : SaveState()
        data class Error(val message: String) : SaveState()
    }

    fun saveQuestions(questions: List<Question>, testId: String) {
        if (_saveState.value is SaveState.Saving) return

        viewModelScope.launch {
            try {
                _saveState.value = SaveState.Saving
                var allQuestionsSaved = true
                var errorMessage = ""
                val savedQuestionIds = mutableListOf<String>()
                
                // Create a supervisor job to handle individual question saves
                val supervisorJob = SupervisorJob()
                val scope = CoroutineScope(Dispatchers.IO + supervisorJob)
                
                // Save questions sequentially to avoid overwhelming Firestore
                for (question in questions) {
                    try {
                        if (!isActive) {
                            _saveState.value = SaveState.Error("Operation cancelled")
                            return@launch
                        }

                        val success = scope.async {
                            questionRepository.createQuestion(question)
                        }.await()

                        if (!success) {
                            allQuestionsSaved = false
                            errorMessage = "Lỗi lưu câu hỏi: ${question.questionId}"
                            android.util.Log.e("CreateQuestionsVM", "Lỗi lưu câu hỏi: ${question.questionId}")
                            break
                        } else {
                            // Add the question ID to the list of saved question IDs
                            savedQuestionIds.add(question.questionId)
                        }
                    } catch (e: CancellationException) {
                        android.util.Log.d("CreateQuestionsVM", "Question save cancelled")
                        throw e
                    } catch (e: Exception) {
                        allQuestionsSaved = false
                        errorMessage = "Lỗi lưu câu hỏi: ${question.questionId} - ${e.message}"
                        android.util.Log.e("CreateQuestionsVM", "Lỗi lưu câu hỏi: ${question.questionId}", e)
                        break
                    }
                }

                if (allQuestionsSaved) {
                    // Update the test with the new question IDs
                    val test = testRepository.getTestById(testId)
                    test?.let { currentTest ->
                        val updatedTest = currentTest.copy(
                            questions = savedQuestionIds,
                            questionCount = savedQuestionIds.size
                        )
                        val updateSuccess = testRepository.updateTest(updatedTest)
                        if (updateSuccess) {
                            _saveState.value = SaveState.Success("Lưu câu hỏi thành công")
                        } else {
                            _saveState.value = SaveState.Error("Lỗi cập nhật bài kiểm tra")
                        }
                    } ?: run {
                        _saveState.value = SaveState.Error("Không tìm thấy bài kiểm tra")
                    }
                } else {
                    _saveState.value = SaveState.Error(errorMessage)
                }
            } catch (e: Exception) {
                _saveState.value = SaveState.Error("Lỗi: ${e.message}")
            }
        }
    }
}