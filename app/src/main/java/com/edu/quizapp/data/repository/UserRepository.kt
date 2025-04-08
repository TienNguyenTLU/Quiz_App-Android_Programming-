package com.edu.quizapp.data.repository

import android.util.Log
import com.edu.quizapp.data.models.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class UserRepository {
    private val db = FirebaseFirestore.getInstance()
    private val userCollection = db.collection("users")
    private val auth = FirebaseAuth.getInstance()

    suspend fun getUserByUid(uid: String): User? {
        return try {
            val snapshot = userCollection.document(uid).get().await()
            if (snapshot.exists()) {
                User.fromMap(snapshot.data!!)
            } else {
                null
            }
        } catch (e: Exception) {
            Log.e("UserRepository", "Error getting user by UID: ${e.message}")
            null
        }
    }

    suspend fun saveUser(user: User): Boolean {
        return try {
            userCollection.document(user.uid).set(user.toMap()).await()
            Log.d("UserRepository", "User saved successfully: ${user.uid}")
            true
        } catch (e: Exception) {
            Log.e("UserRepository", "Error saving user: ${e.message}")
            false
        }
    }

    suspend fun updateUser(user: User): Boolean {
        return try {
            userCollection.document(user.uid).update(user.toMap()).await()
            Log.d("UserRepository", "User updated successfully: ${user.uid}")
            true
        } catch (e: Exception) {
            Log.e("UserRepository", "Error updating user: ${e.message}")
            false
        }
    }

    suspend fun deleteUser(uid: String): Boolean {
        return try {
            userCollection.document(uid).delete().await()
            Log.d("UserRepository", "User deleted successfully: $uid")
            true
        } catch (e: Exception) {
            Log.e("UserRepository", "Error deleting user: ${e.message}")
            false
        }
    }

    suspend fun changePassword(oldPassword: String, newPassword: String): Boolean {
        return try {
            val user = auth.currentUser
            if (user != null) {
                val credential = com.google.firebase.auth.EmailAuthProvider.getCredential(user.email!!, oldPassword)
                user.reauthenticate(credential).await()
                user.updatePassword(newPassword).await()
                Log.d("UserRepository", "Password changed successfully")
                true
            } else {
                false
            }
        } catch (e: Exception) {
            Log.e("UserRepository", "Error changing password: ${e.message}")
            false
        }
    }

    suspend fun sendPasswordResetEmail(): Boolean {
        return try {
            val user = auth.currentUser
            if (user != null) {
                auth.sendPasswordResetEmail(user.email!!).await()
                Log.d("UserRepository", "Password reset email sent")
                true
            } else {
                false
            }
        } catch (e: Exception) {
            Log.e("UserRepository", "Error sending password reset email: ${e.message}")
            false
        }
    }

    suspend fun getCurrentUserUid(): String? {
        return auth.currentUser?.uid
    }

    suspend fun getUserById(userId: String): User? {
        return try {
            val snapshot = userCollection.document(userId).get().await()
            if (snapshot.exists()) {
                User.fromMap(snapshot.data!!)
            } else {
                null
            }
        } catch (e: Exception) {
            Log.e("UserRepository", "Error getting user by ID: ${e.message}")
            null
        }
    }

    suspend fun getUsersByIds(userIds: List<String>): List<User> {
        return try {
            val snapshots = userCollection.whereIn("uid", userIds).get().await()
            snapshots.documents.mapNotNull {
                if (it.exists()) {
                    User.fromMap(it.data!!)
                } else {
                    null
                }
            }
        } catch (e: Exception) {
            Log.e("UserRepository", "Error getting users by IDs: ${e.message}")
            emptyList()
        }
    }
}