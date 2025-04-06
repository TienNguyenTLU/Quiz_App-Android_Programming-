package com.edu.quizapp.data.repository

import com.edu.quizapp.data.models.Result
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class ResultRepository {
    private val db = FirebaseFirestore.getInstance()
    private val resultCollection = db.collection("results")

    suspend fun getResultsByStudentId(studentId: String): List<Result> {
        val snapshot = resultCollection.whereEqualTo("studentId", studentId).get().await()
        return snapshot.toObjects(Result::class.java)
    }

    suspend fun saveResult(result: Result) {
        resultCollection.document(result.resultId).set(result).await()
    }
}
