package com.edu.quizapp.data.repository

import com.edu.quizapp.data.models.Teacher
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class TeacherRepository {
    private val db = FirebaseFirestore.getInstance()
    private val teacherCollection = db.collection("teachers")

    suspend fun getTeacherById(uid: String): Teacher? {
        val snapshot = teacherCollection.document(uid).get().await()
        return snapshot.toObject(Teacher::class.java)
    }
}
