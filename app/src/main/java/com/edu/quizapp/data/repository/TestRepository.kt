package com.edu.quizapp.data.repository

import com.edu.quizapp.data.models.Test
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class TestRepository {
    private val db = FirebaseFirestore.getInstance()
    private val testCollection = db.collection("tests")

    suspend fun getTestById(id: String): Test? {
        val snapshot = testCollection.document(id).get().await()
        return snapshot.toObject(Test::class.java)
    }
}
