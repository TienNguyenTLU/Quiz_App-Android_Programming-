package com.edu.quizapp.data.repository

import android.util.Log
import com.edu.quizapp.data.models.Test
import com.edu.quizapp.data.models.Teacher
import com.edu.quizapp.data.models.Classes
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class AnalysticByTestRepository {

    private val db = FirebaseFirestore.getInstance()
    private val testsCollection = db.collection("tests")
    private val teachersCollection = db.collection("teachers")
    private val classesCollection = db.collection("classes")

    suspend fun getTests(): List<Test> {
        return testsCollection.get().await().toObjects(Test::class.java)
    }

    suspend fun getTeachers(): Map<String, Teacher> {
        val teachers = teachersCollection.get().await().toObjects(Teacher::class.java)
        return teachers.associateBy { it.uid }
    }

    suspend fun getClasses(): Map<String, Classes> {
        return try {
            val classes = classesCollection.get().await().toObjects(Classes::class.java)
            classes.associateBy { it.classId }
        } catch (e: Exception) {
            Log.e("AnalysticByTestRepository", "Error getting classes: ${e.message}")
            emptyMap()
        }
    }
}