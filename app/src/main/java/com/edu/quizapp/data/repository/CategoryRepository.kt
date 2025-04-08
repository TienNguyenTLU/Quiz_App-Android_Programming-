package com.edu.quizapp.data.repository

import com.edu.quizapp.data.models.Category
import com.edu.quizapp.data.models.Classes
import com.edu.quizapp.data.models.Test
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class CategoryRepository {

    private val db = FirebaseFirestore.getInstance()

    suspend fun getCategories(): List<Category> {
        return try {
            db.collection("categories").get().await().documents.map { document ->
                Category(
                    categoryId = document.id,
                    categoryName = document.getString("categoryName") ?: ""
                )
            }
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }

    suspend fun getClassesByCategory(categoryId: String): List<Classes> {
        return try {
            db.collection("classes").whereEqualTo("categoryId", categoryId).get().await().documents.map { document ->
                Classes.fromMap(document.data ?: emptyMap())
            }
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }

    suspend fun getTestsByCategory(categoryId: String): List<Test> {
        return try {
            db.collection("tests").whereEqualTo("categoryId", categoryId).get().await().documents.map { document ->
                Test(
                    testId = document.id,
                    testName = document.getString("testName") ?: "",
                    classCode = document.getString("classCode") ?: "",
                    questions = document.get("questions") as? List<String> ?: emptyList(),
                    duration = document.getLong("duration") ?: 0,
                    questionCount = document.getLong("questionCount")?.toInt() ?: 0
                )
            }
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }
}