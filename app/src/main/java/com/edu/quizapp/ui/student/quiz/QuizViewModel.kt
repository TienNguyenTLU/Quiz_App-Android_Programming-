package com.edu.quizapp.ui.student.quiz

import android.os.CountDownTimer
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.edu.quizapp.data.models.Question
import com.edu.quizapp.data.models.Result
import com.edu.quizapp.data.models.StudentAnswer
import com.edu.quizapp.data.models.Test
import com.edu.quizapp.data.models.TestAttempt
import com.edu.quizapp.data.repository.QuizRepository
import com.edu.quizapp.data.repository.ResultRepository
import com.edu.quizapp.data.repository.TestRepository
import kotlinx.coroutines.launch
import java.util.UUID

class QuizViewModel : ViewModel() {
    private val quizRepository = QuizRepository()
    private val testRepository = TestRepository()
    private val resultRepository = ResultRepository()

    private val _test = MutableLiveData<Test>()
    val test: LiveData<Test> = _test

    private val _questions = MutableLiveData<List<Question>>()
    val questions: LiveData<List<Question>> = _questions

    private val _currentQuestionIndex = MutableLiveData<Int>(0)
    val currentQuestionIndex: LiveData<Int> = _currentQuestionIndex

    private val _currentQuestion = MutableLiveData<Question>()
    val currentQuestion: LiveData<Question> = _currentQuestion

    private val _timeRemaining = MutableLiveData<Long>()
    val timeRemaining: LiveData<Long> = _timeRemaining

    private val _isLoading = MutableLiveData<Boolean>(false)
    val isLoading: LiveData<Boolean> = _isLoading

    private val _navigateToResult = MutableLiveData<Boolean>(false)
    val navigateToResult: LiveData<Boolean> = _navigateToResult

    private val _resultId = MutableLiveData<String>()
    val resultId: LiveData<String> = _resultId

    private var currentAttempt: TestAttempt? = null
    private var timer: CountDownTimer? = null
    private var startTime: Long = 0

    fun loadTest(testId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val testData = testRepository.getTestById(testId)
                testData?.let {
                    _test.value = it
                    loadQuestions(it.questions)
                    startTimer(it.duration * 60 * 1000) // Chuyển phút thành mili giây
                }
            } catch (e: Exception) {
                Log.e("QuizViewModel", "Error loading test: ${e.message}")
            } finally {
                _isLoading.value = false
            }
        }
    }

    private fun loadQuestions(questionIds: List<String>) {
        viewModelScope.launch {
            try {
                val questionsList = quizRepository.getQuestionsForTest(questionIds)
                _questions.value = questionsList
                if (questionsList.isNotEmpty()) {
                    _currentQuestion.value = questionsList[0]
                }
            } catch (e: Exception) {
                Log.e("QuizViewModel", "Error loading questions: ${e.message}")
            }
        }
    }

    fun startQuiz(testId: String, studentId: String) {
        viewModelScope.launch {
            try {
                startTime = System.currentTimeMillis()
                currentAttempt = quizRepository.startTestAttempt(testId, studentId)
            } catch (e: Exception) {
                Log.e("QuizViewModel", "Error starting quiz: ${e.message}")
            }
        }
    }

    fun submitAnswer(selectedAnswer: String) {
        viewModelScope.launch {
            try {
                val currentQ = _currentQuestion.value ?: return@launch
                val isCorrect = selectedAnswer == currentQ.correctAnswer

                val answer = StudentAnswer(
                    questionId = currentQ.questionId,
                    selectedAnswer = selectedAnswer,
                    isCorrect = isCorrect
                )

                currentAttempt?.let {
                    quizRepository.submitAnswer(it.attemptId, answer)
                }

                // Chuyển đến câu hỏi tiếp theo
                moveToNextQuestion()
            } catch (e: Exception) {
                Log.e("QuizViewModel", "Error submitting answer: ${e.message}")
            }
        }
    }

    fun skipQuestion() {
        moveToNextQuestion()
    }

    private fun moveToNextQuestion() {
        val questions = _questions.value ?: return
        val currentIndex = _currentQuestionIndex.value ?: 0

        if (currentIndex < questions.size - 1) {
            _currentQuestionIndex.value = currentIndex + 1
            _currentQuestion.value = questions[currentIndex + 1]
        } else {
            // Đã trả lời hết câu hỏi, kết thúc bài làm
            finishQuiz()
        }
    }

    fun finishQuiz() {
        viewModelScope.launch {
            try {
                timer?.cancel()

                currentAttempt?.let {
                    val finishedAttempt = quizRepository.finishTestAttempt(it.attemptId)

                    // Tính toán kết quả
                    val correctCount = finishedAttempt.answers.count { it.isCorrect }
                    val incorrectCount = finishedAttempt.answers.count { !it.isCorrect }
                    val skippedCount = (_questions.value?.size ?: 0) - finishedAttempt.answers.size

                    val totalQuestions = _questions.value?.size ?: 1
                    val score = (correctCount.toDouble() / totalQuestions) * 10 // Tính điểm trên thang 10

                    val timeTaken = if (finishedAttempt.endTime > 0) {
                        finishedAttempt.endTime - finishedAttempt.startTime
                    } else {
                        System.currentTimeMillis() - startTime
                    }

                    // Lưu kết quả
                    val resultId = UUID.randomUUID().toString()
                    val result = Result(
                        resultId = resultId,
                        studentId = finishedAttempt.studentId,
                        testId = finishedAttempt.testId,
                        correctCount = correctCount.toLong(),
                        incorrectCount = incorrectCount.toLong(),
                        skippedCount = skippedCount.toLong(),
                        score = score,
                        timeTaken = timeTaken
                    )

                    resultRepository.saveResult(result)

                    // Cập nhật trạng thái hoàn thành bài kiểm tra
                    val test = _test.value
                    test?.let { testData ->
                        val updatedTest = testData.copy(isCompleted = true)
                        testRepository.updateTest(updatedTest)
                    }

                    _resultId.value = resultId
                    _navigateToResult.value = true
                }
            } catch (e: Exception) {
                Log.e("QuizViewModel", "Error finishing quiz: ${e.message}")
            }
        }
    }

    private fun startTimer(durationMillis: Long) {
        timer?.cancel()

        timer = object : CountDownTimer(durationMillis, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                _timeRemaining.value = millisUntilFinished / 1000
            }

            override fun onFinish() {
                finishQuiz()
            }
        }.start()
    }

    override fun onCleared() {
        super.onCleared()
        timer?.cancel()
    }
}
