package com.edu.quizapp.data.repository

import android.net.Uri
import android.util.Log
import com.edu.quizapp.data.models.Student
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.tasks.await

class StudentRepository {

    private val db = FirebaseFirestore.getInstance()
    private val studentsCollection = db.collection("students")
    private val storage = FirebaseStorage.getInstance()
    private val storageRef = storage.reference

    suspend fun getStudentById(uid: String): Student? {
        Log.d("StudentRepository", "Fetching student with uid: $uid")
        return try {
            val snapshot = studentsCollection.document(uid).get().await()
            Log.d("StudentRepository", "Firestore snapshot: $snapshot")
            if (snapshot.exists()) {
                val student = Student.fromMap(snapshot.data!!)
                Log.d("StudentRepository", "Student found: $student")
                return student
            } else {
                Log.d("StudentRepository", "Student not found with uid: $uid")
                return null
            }
        } catch (e: Exception) {
            Log.e("StudentRepository", "Error getting student by uid: ${e.message}")
            return null
        }
    }

    suspend fun updateStudent(uid: String, student: Student, imageUri: Uri? = null): Boolean {
        return try {
            val imageUrl = if (imageUri != null) {
                uploadImage(uid, imageUri)
            } else {
                student.profileImageUrl
            }

            val updatedStudent = student.copy(profileImageUrl = imageUrl ?: "")
            studentsCollection.document(uid).set(updatedStudent.toMap()).await()
            Log.d("StudentRepository", "Student updated successfully: $uid")
            true
        } catch (e: Exception) {
            Log.e("StudentRepository", "Error updating student: ${e.message}")
            false
        }
    }

    suspend fun createStudent(student: Student, imageUri: Uri? = null): Boolean {
        return try {
            val imageUrl = if (imageUri != null) {
                uploadImage(student.uid, imageUri)
            } else {
                student.profileImageUrl
            }

            val newStudent = student.copy(profileImageUrl = imageUrl ?: "")
            studentsCollection.document(student.uid).set(newStudent.toMap()).await()
            Log.d("StudentRepository", "Student created successfully: ${student.uid}")
            true
        } catch (e: Exception) {
            Log.e("StudentRepository", "Error creating student: ${e.message}")
            false
        }
    }

    private suspend fun uploadImage(uid: String, imageUri: Uri): String? {
        return try {
            val imageRef = storageRef.child("images/$uid/${imageUri.lastPathSegment}")
            val uploadTask = imageRef.putFile(imageUri).await()
            uploadTask.metadata?.reference?.downloadUrl?.await().toString()
        } catch (e: Exception) {
            Log.e("StudentRepository", "Error uploading image: ${e.message}")
            null
        }
    }

    suspend fun getStudentByStudentsId(studentsId: String): Student? {
        Log.d("StudentRepository", "Fetching student with studentsId: $studentsId")
        return try {
            val snapshot = studentsCollection.whereEqualTo("studentsId", studentsId).get().await()
            Log.d("StudentRepository", "Firestore snapshot: $snapshot")
            if (!snapshot.isEmpty) {
                val student = Student.fromMap(snapshot.documents[0].data!!)
                Log.d("StudentRepository", "Student found: $student")
                return student
            } else {
                Log.d("StudentRepository", "Student not found with studentsId: $studentsId")
                return null
            }
        } catch (e: Exception) {
            Log.e("StudentRepository", "Error getting student by studentsId: ${e.message}")
            return null
        }
    }
}