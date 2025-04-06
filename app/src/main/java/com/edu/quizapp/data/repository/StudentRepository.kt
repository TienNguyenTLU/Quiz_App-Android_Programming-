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
        return try {
            val snapshot = studentsCollection.document(uid).get().await()
            if (snapshot.exists()) {
                Student.fromMap(snapshot.data!!)
            } else {
                null
            }
        } catch (e: Exception) {
            Log.e("StudentRepository", "Error getting student by ID: ${e.message}")
            null
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
            studentsCollection.document(uid).update(updatedStudent.toMap()).await()
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
}