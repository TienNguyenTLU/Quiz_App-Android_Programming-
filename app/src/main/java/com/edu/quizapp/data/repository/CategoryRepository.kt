package com.edu.quizapp.data.repository

import com.edu.quizapp.data.models.Category
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class CategoryRepository {
    private val db = FirebaseFirestore.getInstance()
    private val categoryCollection = db.collection("categories")

    suspend fun getAllCategories(): List<Category> {
        val snapshot = categoryCollection.get().await()
        return snapshot.toObjects(Category::class.java)
    }
}
