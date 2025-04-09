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
import com.google.firebase.auth.FirebaseAuth

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
    private var attemptId: String? = null

    fun loadTest(testId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                Log.d("QuizViewModel", "Loading test with ID: $testId")
                val testData = testRepository.getTestById(testId)
                testData?.let {
                    Log.d("QuizViewModel", "Test loaded successfully: ${it.testName}")
                    Log.d("QuizViewModel", "Question IDs: ${it.questions}")
                    _test.value = it
                    loadQuestions(it.questions)
                    startTimer(it.duration * 60 * 1000) // Chuyển phút thành mili giây
                } ?: run {
                    Log.e("QuizViewModel", "Test not found with ID: $testId")
                }
            } catch (e: Exception) {
                Log.e("QuizViewModel", "Error loading test: ${e.message}")
                Log.e("QuizViewModel", "Stack trace: ${e.stackTraceToString()}")
            } finally {
                _isLoading.value = false
            }
        }
    }

    private fun loadQuestions(questionIds: List<String>) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                Log.d("QuizViewModel", "Loading questions for test")
                val testId = _test.value?.testId ?: return@launch
                Log.d("QuizViewModel", "Test ID: $testId")
                Log.d("QuizViewModel", "Question IDs: $questionIds")
                
                // Use the actual question IDs from the Test object
                val questionsList = quizRepository.getQuestionsForTest(questionIds)
                
                Log.d("QuizViewModel", "Loaded ${questionsList.size} questions")
                _questions.value = questionsList
                if (questionsList.isNotEmpty()) {
                    Log.d("QuizViewModel", "Setting current question: ${questionsList[0].questionText}")
                    _currentQuestion.value = questionsList[0]
                } else {
                    Log.e("QuizViewModel", "No questions loaded for test")
                }
            } catch (e: Exception) {
                Log.e("QuizViewModel", "Error loading questions: ${e.message}")
                Log.e("QuizViewModel", "Stack trace: ${e.stackTraceToString()}")
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun startQuiz(testId: String, studentId: String) {
        viewModelScope.launch {
            try {
                Log.d("QuizViewModel", "Starting quiz for test: $testId, student: $studentId")
                startTime = System.currentTimeMillis()
                
                // Kiểm tra xem đã có attempt chưa
                if (currentAttempt == null) {
                    currentAttempt = quizRepository.startTestAttempt(testId, studentId)
                    attemptId = currentAttempt?.attemptId // Lưu attemptId
                    Log.d("QuizViewModel", "Quiz started with new attempt ID: ${currentAttempt?.attemptId}")
                } else {
                    Log.d("QuizViewModel", "Using existing attempt ID: ${currentAttempt?.attemptId}")
                }
            } catch (e: Exception) {
                Log.e("QuizViewModel", "Error starting quiz: ${e.message}")
                Log.e("QuizViewModel", "Stack trace: ${e.stackTraceToString()}")
            }
        }
    }

    fun submitAnswer(selectedAnswer: String) {
        viewModelScope.launch {
            try {
                val currentQ = _currentQuestion.value ?: return@launch
                val isCorrect = when (currentQ.correctAnswer) {
                    is Number -> selectedAnswer == currentQ.correctAnswer.toString()
                    else -> selectedAnswer == currentQ.correctAnswer.toString()
                }

                val answer = StudentAnswer(
                    questionId = currentQ.questionId,
                    selectedAnswer = selectedAnswer,
                    isCorrect = isCorrect
                )

                val attemptIdToUse = currentAttempt?.attemptId ?: attemptId
                if (attemptIdToUse != null) {
                    Log.d("QuizViewModel", "Submitting answer for attempt: $attemptIdToUse")
                    quizRepository.submitAnswer(attemptIdToUse, answer)
                } else {
                    Log.e("QuizViewModel", "Cannot submit answer: No attempt ID available")
                }

                moveToNextQuestion()
            } catch (e: Exception) {
                Log.e("QuizViewModel", "Error submitting answer: ${e.message}")
                Log.e("QuizViewModel", "Stack trace: ${e.stackTraceToString()}")
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
                Log.d("QuizViewModel", "Starting to finish quiz")
                timer?.cancel()

                // Sử dụng attemptId nếu currentAttempt là null
                val attemptIdToUse = currentAttempt?.attemptId ?: attemptId
                if (attemptIdToUse != null) {
                    Log.d("QuizViewModel", "Finishing quiz with attempt ID: $attemptIdToUse")
                    val finishedAttempt = quizRepository.finishTestAttempt(attemptIdToUse)
                    Log.d("QuizViewModel", "Quiz finished. Attempt completed: ${finishedAttempt.isCompleted}")

                    // Cập nhật currentAttempt với dữ liệu mới
                    currentAttempt = finishedAttempt

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

                    Log.d("QuizViewModel", "Saving result with ID: $resultId")
                    resultRepository.saveResult(result)

                    _resultId.value = resultId
                    _navigateToResult.value = true
                } else {
                    Log.e("QuizViewModel", "Cannot finish quiz: No attempt ID available")
                    // Thử tạo một attempt mới nếu không có
                    val currentUser = FirebaseAuth.getInstance().currentUser
                    val testId = _test.value?.testId
                    if (currentUser != null && testId != null) {
                        Log.d("QuizViewModel", "Creating new attempt for test: $testId")
                        val newAttempt = quizRepository.startTestAttempt(testId, currentUser.uid)
                        currentAttempt = newAttempt
                        attemptId = newAttempt.attemptId
                        
                        // Gọi lại finishQuiz với attempt mới
                        finishQuiz()
                    } else {
                        Log.e("QuizViewModel", "Cannot create new attempt: Missing user or test ID")
                    }
                }
            } catch (e: Exception) {
                Log.e("QuizViewModel", "Error finishing quiz: ${e.message}")
                Log.e("QuizViewModel", "Stack trace: ${e.stackTraceToString()}")
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
