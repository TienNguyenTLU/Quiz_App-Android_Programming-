package com.edu.quizapp.data.repository

import android.net.Uri
import android.util.Log
import com.edu.quizapp.data.models.Teacher
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.tasks.await

class TeacherRepository {
    private val db = FirebaseFirestore.getInstance()
    private val teacherCollection = db.collection("teachers")
    private val storage = FirebaseStorage.getInstance()
    private val storageRef = storage.reference

    suspend fun getTeacherById(uid: String): Teacher? {
        val snapshot = teacherCollection.document(uid).get().await()
        return snapshot.toObject(Teacher::class.java)
    }

    suspend fun createTeacher(teacher: Teacher, imageUri: Uri? = null): Boolean {
        return try {
            val imageUrl = if (imageUri != null) {
                uploadImage(teacher.uid, imageUri)
            } else {
                ""
            }

            val newTeacher = teacher.copy(profileImageUrl = imageUrl)
            teacherCollection.document(teacher.uid).set(newTeacher.toMap()).await()
            Log.d("TeacherRepository", "Teacher created successfully: ${teacher.uid}")
            true
        } catch (e: Exception) {
            Log.e("TeacherRepository", "Error creating teacher: ${e.message}")
            false
        }
    }

    suspend fun updateTeacher(uid: String, teacher: Teacher, imageUri: Uri? = null): Boolean {
        return try {
            val imageUrl = if (imageUri != null) {
                uploadImage(uid, imageUri)
            } else {
                teacher.profileImageUrl
            }

            val updatedTeacher = teacher.copy(profileImageUrl = imageUrl)
            teacherCollection.document(uid).update(updatedTeacher.toMap()).await()
            Log.d("TeacherRepository", "Teacher updated successfully: $uid")
            true
        } catch (e: Exception) {
            Log.e("TeacherRepository", "Error updating teacher: ${e.message}")
            false
        }
    }

    private suspend fun uploadImage(uid: String, imageUri: Uri): String? {
        return try {
            val imageRef = storageRef.child("images/$uid/${imageUri.lastPathSegment}")
            val uploadTask = imageRef.putFile(imageUri).await()
            uploadTask.metadata?.reference?.downloadUrl?.await().toString()
        } catch (e: Exception) {
            Log.e("TeacherRepository", "Error uploading image: ${e.message}")
            null
        }
    }
}