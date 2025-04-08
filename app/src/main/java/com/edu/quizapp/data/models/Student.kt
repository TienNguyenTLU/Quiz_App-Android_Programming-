package com.edu.quizapp.data.models

import android.util.Log

data class Student(
    val uid: String = "",
    val studentsId: String = "",
    val name: String = "",
    val phone: String = "",
    val address: String = "",
    val profileImageUrl: String = "",
    val email: String = ""
) {
    companion object {
        fun fromMap(map: Map<String, Any>): Student {
            Log.d("Student", "Mapping from map: $map")
            return Student(
                uid = map["uid"] as? String ?: "",
                studentsId = map["studentsId"] as? String ?: "",
                name = map["name"] as? String ?: "",
                phone = map["phone"] as? String ?: "",
                address = map["address"] as? String ?: "",
                profileImageUrl = map["profileImageUrl"] as? String ?: "",
                email = map["email"] as String ?: ""
            )
        }
    }

    fun toMap(): Map<String, Any> {
        return mapOf(
            "uid" to uid,
            "studentsId" to studentsId,
            "name" to name,
            "phone" to phone,
            "address" to address,
            "profileImageUrl" to profileImageUrl,
            "email" to email
        )
    }
}