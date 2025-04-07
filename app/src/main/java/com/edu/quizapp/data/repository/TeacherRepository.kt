package com.edu.quizapp.data.repository

import android.net.Uri
import android.util.Log
import com.edu.quizapp.data.models.Teacher
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.UUID

class TeacherRepository {
    private val db = FirebaseFirestore.getInstance()
    private val teacherCollection = db.collection("teachers")
    private val storage = FirebaseStorage.getInstance()

    suspend fun getTeacherById(uid: String): Teacher? = withContext(Dispatchers.IO) {
        val snapshot = teacherCollection.document(uid).get().await()
        return@withContext snapshot.toObject(Teacher::class.java)
    }

    suspend fun createTeacher(teacher: Teacher): Boolean = withContext(Dispatchers.IO) {
        return@withContext try {
            teacherCollection.document(teacher.uid).set(teacher).await()
            true
        } catch (e: Exception) {
            Log.e("TeacherRepository", "Error creating teacher: ${e.message}")
            false
        }
    }

    suspend fun updateTeacher(uid: String, teacher: Teacher, imageUri: Uri?): Boolean = withContext(Dispatchers.IO) {
        return@withContext try {
            val updatedTeacher = if (imageUri != null) {
                val imageUrl = uploadImage(uid, imageUri)
                teacher.copy(profileImageUrl = imageUrl)
            } else {
                teacher
            }
            teacherCollection.document(uid).set(updatedTeacher).await()
            true
        } catch (e: Exception) {
            Log.e("TeacherRepository", "Error updating teacher: ${e.message}")
            false
        }
    }

    private suspend fun uploadImage(uid: String, imageUri: Uri): String = withContext(Dispatchers.IO) {
        val ref = storage.reference.child("teachers/$uid/${UUID.randomUUID()}")
        ref.putFile(imageUri).await()
        ref.downloadUrl.await().toString()
    }
}