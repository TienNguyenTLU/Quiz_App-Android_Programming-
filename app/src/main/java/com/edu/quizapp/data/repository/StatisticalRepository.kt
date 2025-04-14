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
        val results = resultsCollection.get().await().toObjects(Result::class.java)
        return results.filter { result ->
            val test = testsCollection.document(result.testId).get().await().toObject(Test::class.java)
            test?.classId == classId
        }
    }

    suspend fun getStudentName(studentId: String): String? {
        return studentsCollection.document(studentId).get().await().toObject(Student::class.java)?.name
    }

    suspend fun getClassName(classId: String): String? { // Sửa đổi hàm getClassName()
        return classesCollection.document(classId).get().await().toObject(Classes::class.java)?.className
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