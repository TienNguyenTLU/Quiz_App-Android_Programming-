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
            val questions = mutableListOf<Question>()
            for (id in questionIds) {
                val snapshot = questionsCollection.document(id).get().await()
                snapshot.toObject(Question::class.java)?.let {
                    questions.add(it)
                }
            }
            questions
        } catch (e: Exception) {
            Log.e("QuizRepository", "Error getting questions: ${e.message}")
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

        attemptRef.update(
            mapOf(
                "endTime" to currentTime,
                "isCompleted" to true
            )
        ).await()

        return attemptRef.get().await().toObject(TestAttempt::class.java)
            ?: throw Exception("Không tìm thấy bài làm")
    }

    suspend fun getTestAttempt(attemptId: String): TestAttempt? {
        return attemptsCollection.document(attemptId).get().await().toObject(TestAttempt::class.java)
    }

    suspend fun getTestAttemptsByStudentAndTest(studentId: String, testId: String): List<TestAttempt> {
        return attemptsCollection
            .whereEqualTo("studentId", studentId)
            .whereEqualTo("testId", testId)
            .get()
            .await()
            .toObjects(TestAttempt::class.java)
    }
}
