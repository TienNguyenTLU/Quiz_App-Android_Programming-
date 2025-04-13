package com.edu.quizapp.data.repository

import com.edu.quizapp.data.models.Result
import com.edu.quizapp.data.models.Student
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class StatisticalTestRepository {

    private val db = FirebaseFirestore.getInstance()
    private val resultsCollection = db.collection("results")
    private val studentsCollection = db.collection("students")

    suspend fun getResultsByTestId(testId: String): List<Result> {
        return resultsCollection.whereEqualTo("testId", testId).get().await().toObjects(Result::class.java)
    }

    suspend fun getStudents(): Map<String, Student> {
        val students = studentsCollection.get().await().toObjects(Student::class.java)
        return students.associateBy { it.uid }
    }
}