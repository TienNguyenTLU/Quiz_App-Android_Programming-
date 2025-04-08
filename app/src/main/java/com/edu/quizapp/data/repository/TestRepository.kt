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

    suspend fun getTestsByClassId(classId: String): List<Test> {
        return testCollection.whereEqualTo("classId", classId).get().await().toObjects(Test::class.java)
    }

    suspend fun getAllTests(): List<Test> {
        return testCollection.get().await().toObjects(Test::class.java)
    }

    suspend fun createTest(test: Test): Boolean {
        return try {
            testCollection.document(test.testId).set(test).await()
            true
        } catch (e: Exception) {
            false
        }
    }

    suspend fun updateTest(test: Test): Boolean {
        return try {
            testCollection.document(test.testId).set(test).await()
            true
        } catch (e: Exception) {
            false
        }
    }

    suspend fun deleteTest(testId: String): Boolean {
        return try {
            testCollection.document(testId).delete().await()
            true
        } catch (e: Exception) {
            false
        }
    }
}