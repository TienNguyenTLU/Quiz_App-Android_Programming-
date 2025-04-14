package com.edu.quizapp.data.repository

import android.util.Log
import com.edu.quizapp.data.models.Classes
import com.edu.quizapp.data.models.Notification
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
            Log.d("ClassRepository", "maxStudents before save: ${classes.maxStudents}")
            classCollection.document(classes.classId).set(classes).await()
            true
        } catch (e: Exception) {
            false
        }
    }

    suspend fun classCodeExists(classCode: String): Boolean {
        return try {
            val snapshot = classCollection.whereEqualTo("classCode", classCode).get().await()
            !snapshot.isEmpty
        } catch (e: Exception) {
            Log.e("ClassRepository", "Error checking classCode existence: ${e.message}")
            false
        }
    }

    suspend fun addStudentToClass(classId: String, studentsId: String): Boolean {
        Log.d("ClassRepository", "Adding studentsId: $studentsId to classId: $classId")
        return try {
            // Add student's studentsId to class's students list
            classCollection.document(classId).update("students", FieldValue.arrayUnion(studentsId)).await()

            // Get student's uid from students collection
            val querySnapshot = db.collection("students").whereEqualTo("studentsId", studentsId).get().await()
            if (!querySnapshot.isEmpty) {
                val studentDoc = querySnapshot.documents[0]
                val studentUid = studentDoc.id

                // Add class to student's classes list
                db.collection("students").document(studentUid).update("classes", FieldValue.arrayUnion(classId)).await()

                Log.d("ClassRepository", "addStudentToClass Success with studentsId: $studentsId")
                true
            } else {
                Log.e("ClassRepository", "Student not found with studentsId: $studentsId")
                false
            }
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

    suspend fun addNotification(classId: String, notification: Notification): Boolean {
        return try {
            classCollection.document(classId).update("notifications", FieldValue.arrayUnion(notification.toMap())).await()
            true
        } catch (e: Exception) {
            Log.e("ClassRepository", "Error adding notification: ${e.message}")
            false
        }
    }

    suspend fun getJoinRequests(classId: String): List<String> {
        val snapshot = classCollection.document(classId).get().await()
        val classes = snapshot.toObject(Classes::class.java)
        return classes?.requests ?: emptyList()
    }

    suspend fun approveJoinRequest(classId: String, studentId: String): Boolean {
        return try {
            // Get student's studentsId from students collection
            val studentDoc = db.collection("students").document(studentId).get().await()
            val studentsId = studentDoc.data?.get("studentsId") as? String ?: ""

            // Add student's studentsId to class's students list and remove from requests
            classCollection.document(classId).update(
                mapOf(
                    "students" to FieldValue.arrayUnion(studentsId),
                    "requests" to FieldValue.arrayRemove(studentId)
                )
            ).await()

            // Add class to student's classes list
            db.collection("students").document(studentId).update("classes", FieldValue.arrayUnion(classId)).await()

            Log.d("ClassRepository", "approveJoinRequest Success with studentsId: $studentsId")
            true
        } catch (e: Exception) {
            Log.e("ClassRepository", "Error approving join request: ${e.message}")
            false
        }
    }

    suspend fun removeStudentFromClass(classId: String, studentId: String): Boolean {
        return try {
            // Get student's studentsId from students collection
            val studentDoc = db.collection("students").document(studentId).get().await()
            val studentsId = studentDoc.data?.get("studentsId") as? String ?: ""

            // Remove student's studentsId from class's students list
            classCollection.document(classId).update("students", FieldValue.arrayRemove(studentsId)).await()

            // Remove class from student's classes list
            db.collection("students").document(studentId).update("classes", FieldValue.arrayRemove(classId)).await()

            Log.d("ClassRepository", "removeStudentFromClass Success with studentsId: $studentsId")
            true
        } catch (e: Exception) {
            Log.e("ClassRepository", "Error removing student from class: ${e.message}")
            false
        }
    }
}