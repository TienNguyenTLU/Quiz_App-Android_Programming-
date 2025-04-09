package com.edu.quizapp.data.repository

import android.util.Log
import com.edu.quizapp.data.models.Question
import com.edu.quizapp.data.models.StudentAnswer
import com.edu.quizapp.data.models.TestAttempt
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import java.util.UUID

class QuizRepository {
    private val db = FirebaseFirestore.getInstance()
    private val questionsCollection = db.collection("questions")
    private val attemptsCollection = db.collection("testAttempts")

    suspend fun getQuestionsForTest(questionIds: List<String>): List<Question> {
        return try {
            Log.d("QuizRepository", "Getting questions for test with IDs: $questionIds")
            val questions = mutableListOf<Question>()
            
            // If questionIds is empty, return empty list
            if (questionIds.isEmpty()) {
                Log.e("QuizRepository", "No question IDs provided")
                return emptyList()
            }
            
            // Fetch questions by their IDs
            for (id in questionIds) {
                Log.d("QuizRepository", "Fetching question with ID: $id")
                val snapshot = questionsCollection.document(id).get().await()
                val data = snapshot.data
                if (data != null) {
                    val question = Question.fromMap(data)
                    Log.d("QuizRepository", "Successfully loaded question: ${question.questionText}")
                    Log.d("QuizRepository", "Answers: ${question.answers}")
                    Log.d("QuizRepository", "Correct answer: ${question.correctAnswer}")
                    questions.add(question)
                    Log.d("QuizRepository", "Added question to list. Current size: ${questions.size}")
                } else {
                    Log.e("QuizRepository", "Failed to load question with ID: $id - No data found")
                }
            }
            
            Log.d("QuizRepository", "Total questions loaded: ${questions.size}")
            Log.d("QuizRepository", "Returning questions list with size: ${questions.size}")
            questions
        } catch (e: Exception) {
            Log.e("QuizRepository", "Error getting questions: ${e.message}")
            Log.e("QuizRepository", "Stack trace: ${e.stackTraceToString()}")
            emptyList()
        }
    }

    suspend fun startTestAttempt(testId: String, studentId: String): TestAttempt {
        val attemptId = UUID.randomUUID().toString()
        val currentTime = System.currentTimeMillis()

        val attempt = TestAttempt(
            attemptId = attemptId,
            testId = testId,
            studentId = studentId,
            startTime = currentTime
        )

        attemptsCollection.document(attemptId).set(attempt).await()
        return attempt
    }

    suspend fun submitAnswer(attemptId: String, studentAnswer: StudentAnswer) {
        val attemptRef = attemptsCollection.document(attemptId)

        // Lấy attempt hiện tại
        val attempt = attemptRef.get().await().toObject(TestAttempt::class.java)
            ?: throw Exception("Không tìm thấy bài làm")

        // Kiểm tra xem câu hỏi đã được trả lời chưa
        val existingAnswerIndex = attempt.answers.indexOfFirst { it.questionId == studentAnswer.questionId }

        if (existingAnswerIndex != -1) {
            // Cập nhật câu trả lời hiện có
            attempt.answers[existingAnswerIndex] = studentAnswer
        } else {
            // Thêm câu trả lời mới
            attempt.answers.add(studentAnswer)
        }

        // Cập nhật lại attempt
        attemptRef.update("answers", attempt.answers).await()
    }

    suspend fun finishTestAttempt(attemptId: String): TestAttempt {
        val attemptRef = attemptsCollection.document(attemptId)
        val currentTime = System.currentTimeMillis()

        Log.d("QuizRepository", "Finishing test attempt: $attemptId")
        Log.d("QuizRepository", "Setting completed to true")

        attemptRef.update(
            mapOf(
                "endTime" to currentTime,
                "completed" to true
            )
        ).await()

        val finishedAttempt = attemptRef.get().await().toObject(TestAttempt::class.java)
            ?: throw Exception("Không tìm thấy bài làm")
        
        Log.d("QuizRepository", "Test attempt finished. isCompleted: ${finishedAttempt.isCompleted}")
        return finishedAttempt
    }

    suspend fun getTestAttempt(attemptId: String): TestAttempt? {
        return attemptsCollection.document(attemptId).get().await().toObject(TestAttempt::class.java)
    }

    suspend fun getTestAttemptsByStudentAndTest(studentId: String, testId: String): List<TestAttempt> {
        Log.d("QuizRepository", "Getting attempts for student: $studentId, test: $testId")
        val attempts = attemptsCollection
            .whereEqualTo("studentId", studentId)
            .whereEqualTo("testId", testId)
            .get()
            .await()
            .toObjects(TestAttempt::class.java)
        return attempts
    }
}
