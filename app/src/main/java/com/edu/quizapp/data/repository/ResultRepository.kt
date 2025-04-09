package com.edu.quizapp.data.repository

import android.util.Log
import com.edu.quizapp.data.models.Result
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class ResultRepository {
    private val db = FirebaseFirestore.getInstance()
    private val resultCollection = db.collection("results")

    suspend fun getResultsByStudentId(studentId: String): List<Result> {
        return try {
            val snapshot = resultCollection.whereEqualTo("studentId", studentId).get().await()
            snapshot.toObjects(Result::class.java)
        } catch (e: Exception) {
            Log.e("ResultRepository", "Error getting results by student ID: ${e.message}")
            emptyList()
        }
    }

    suspend fun getResultById(resultId: String): Result? {
        return try {
            val snapshot = resultCollection.document(resultId).get().await()
            snapshot.toObject(Result::class.java)
        } catch (e: Exception) {
            Log.e("ResultRepository", "Error getting result by ID: ${e.message}")
            null
        }
    }

    suspend fun getResultsByStudentAndTest(studentId: String, testId: String): List<Result> {
        return try {
            val snapshot = resultCollection
                .whereEqualTo("studentId", studentId)
                .whereEqualTo("testId", testId)
                .get()
                .await()
            snapshot.toObjects(Result::class.java)
        } catch (e: Exception) {
            Log.e("ResultRepository", "Error getting results by student and test: ${e.message}")
            emptyList()
        }
    }

    suspend fun saveResult(result: Result) {
        try {
            resultCollection.document(result.resultId).set(result).await()
        } catch (e: Exception) {
            Log.e("ResultRepository", "Error saving result: ${e.message}")
        }
    }
}
