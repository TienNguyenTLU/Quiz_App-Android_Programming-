package com.edu.quizapp.data.repository

import android.util.Log
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

    suspend fun createClass(classes: Classes): Boolean {
        return try {
            classCollection.document(classes.classId).set(classes).await()
            true
        } catch (e: Exception) {
            false
        }
    }

    suspend fun addStudentToClass(classId: String, studentId: String): Boolean {
        Log.d("ClassRepository", "Adding studentId: $studentId to classId: $classId")
        return try {
            classCollection.document(classId).update("students", FieldValue.arrayUnion(studentId)).await()
            Log.d("ClassRepository", "addStudentToClass Success")
            true
        } catch (e: Exception) {
            Log.e("ClassRepository", "Error adding student to class: ${e.message}")
            false
        }
    }

    suspend fun deleteClass(classId: String): Boolean {
        return try {
            classCollection.document(classId).delete().await()
            Log.d("ClassRepository", "Class deleted successfully: $classId")
            true
        } catch (e: Exception) {
            Log.e("ClassRepository", "Error deleting class: ${e.message}")
            false
        }
    }
}