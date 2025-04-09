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
        return try {
            Log.d("TestRepository", "Fetching tests for class ID: $classId")
            val tests = testCollection.whereEqualTo("classId", classId).get().await().toObjects(Test::class.java)
            Log.d("TestRepository", "Found ${tests.size} tests for class ID $classId")
            tests.forEach { test ->
                Log.d("TestRepository", "Test: id=${test.testId}, name=${test.testName}, classId=${test.classId}")
            }
            tests
        } catch (e: Exception) {
            Log.e("TestRepository", "Error getting tests by classId: ${e.message}")
            Log.e("TestRepository", "Stack trace: ${e.stackTraceToString()}")
            emptyList()
        }
    }

    suspend fun getTestsByClassCode(classCode: String): List<Test> {
        return try {
            Log.d("TestRepository", "Fetching tests for class code: $classCode")
            val tests = testCollection.whereEqualTo("classCode", classCode).get().await().toObjects(Test::class.java)
            Log.d("TestRepository", "Found ${tests.size} tests for class code $classCode")
            tests.forEach { test ->
                Log.d("TestRepository", "Test: id=${test.testId}, name=${test.testName}, classCode=${test.classCode}")
            }
            tests
        } catch (e: Exception) {
            Log.e("TestRepository", "Error getting tests by classCode: ${e.message}")
            Log.e("TestRepository", "Stack trace: ${e.stackTraceToString()}")
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

    suspend fun updateTestsWithClassId() {
        try {
            val tests = getAllTests()
            for (test in tests) {
                if (test.classId.isEmpty()) {
                    // Tìm classId từ classCode
                    val classDoc = classCollection.whereEqualTo("classCode", test.classCode).get().await()
                    if (!classDoc.isEmpty) {
                        val classId = classDoc.documents[0].id
                        val updatedTest = test.copy(classId = classId)
                        updateTest(updatedTest)
                        Log.d("TestRepository", "Updated test ${test.testId} with classId: $classId")
                    }
                }
            }
        } catch (e: Exception) {
            Log.e("TestRepository", "Error updating tests with classId: ${e.message}")
        }
    }

}