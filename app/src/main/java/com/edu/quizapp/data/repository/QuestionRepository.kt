package com.edu.quizapp.data.repository

import com.edu.quizapp.data.models.Question
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class QuestionRepository {
    private val db = FirebaseFirestore.getInstance()
    private val questionCollection = db.collection("questions")

    suspend fun getQuestionById(id: String): Question? {
        val snapshot = questionCollection.document(id).get().await()
        return snapshot.toObject(Question::class.java)
    }

    suspend fun getQuestionsByIds(ids: List<String>): List<Question> {
        return ids.mapNotNull { getQuestionById(it) }
    }
}
