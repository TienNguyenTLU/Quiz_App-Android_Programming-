package com.edu.quizapp.data.repository

import android.util.Log
import com.edu.quizapp.data.models.Test
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class TestRepository {
    private val db = FirebaseFirestore.getInstance()
    private val testCollection = db.collection("tests")
    private val classCollection = db.collection("classes")

    suspend fun getTestById(id: String): Test? {
        val snapshot = testCollection.document(id).get().await()
        return snapshot.toObject(Test::class.java)
    }

    suspend fun getTestsByClassId(classId: String): List<Test> {
        return testCollection.whereEqualTo("classId", classId).get().await().toObjects(Test::class.java)
    }

    suspend fun getTestsByClassCode(classCode: String): List<Test> {
        return try {
            testCollection.whereEqualTo("classCode", classCode).get().await().toObjects(Test::class.java)
        } catch (e: Exception) {
            Log.e("TestRepository", "Error getting tests by classCode: ${e.message}")
            emptyList()
        }
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

    suspend fun updateQuestionCount(testId: String, questionCount: Int): Boolean {
        return try {
            testCollection.document(testId).update("questionCount", questionCount).await()
            true
        } catch (e: Exception) {
            Log.e("TestRepository", "Error updating questionCount: ${e.message}")
            false
        }
    }

    private suspend fun getClassName(classCode: String): String {
        return classCollection.document(classCode).get().await().getString("className") ?: ""
    }

}