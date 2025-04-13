package com.edu.quizapp.data.repository

import com.edu.quizapp.data.models.Classes
import com.edu.quizapp.data.models.Result
import com.edu.quizapp.data.models.Student
import com.edu.quizapp.data.models.Test
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class StatisticalRepository {

    private val db = FirebaseFirestore.getInstance()
    private val resultsCollection = db.collection("results")
    private val studentsCollection = db.collection("students")
    private val testsCollection = db.collection("tests")
    private val classesCollection = db.collection("classes")

    suspend fun getResultsByClass(classId: String): List<Result> {
        return resultsCollection.get().await().toObjects(Result::class.java).filter { result ->
            val student = studentsCollection.document(result.studentId).get().await().toObject(Student::class.java)
            student?.classes?.contains(classId) == true
        }
    }

    suspend fun getStudentName(studentId: String): String? {
        return studentsCollection.document(studentId).get().await().toObject(Student::class.java)?.name
    }

    suspend fun getClassName(studentId: String): String? {
        val student = studentsCollection.document(studentId).get().await().toObject(Student::class.java)
        return student?.classes?.mapNotNull { classId ->
            classesCollection.document(classId).get().await().toObject(Classes::class.java)?.className
        }?.firstOrNull()
    }

    suspend fun getTestName(testId: String): String? {
        return testsCollection.document(testId).get().await().toObject(Test::class.java)?.testName
    }

    suspend fun getTestsByClass(classId: String): List<Test> {
        return testsCollection.whereEqualTo("classId", classId).get().await().toObjects(Test::class.java)
    }

    suspend fun hasResultsForTest(testId: String): Boolean {
        val results = resultsCollection.whereEqualTo("testId", testId).get().await().toObjects(Result::class.java)
        return results.isNotEmpty()
    }
}