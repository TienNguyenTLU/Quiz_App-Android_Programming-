package com.edu.quizapp.data.repository

import com.edu.quizapp.data.models.Classes
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class ClassRepository {
    private val db = FirebaseFirestore.getInstance()
    private val classCollection = db.collection("classes")

    suspend fun getClassById(id: String): Classes? {
        val snapshot = classCollection.document(id).get().await()
        return snapshot.toObject(Classes::class.java)
    }

    suspend fun addRequestToClass(classId: String, studentId: String): Boolean {
        return try {
            classCollection.document(classId).update("requests", FieldValue.arrayUnion(studentId)).await()
            true
        } catch (e: Exception) {
            false
        }
    }

    suspend fun getAllClasses(): List<Classes> {
        return classCollection.get().await().toObjects(Classes::class.java)
    }
}
