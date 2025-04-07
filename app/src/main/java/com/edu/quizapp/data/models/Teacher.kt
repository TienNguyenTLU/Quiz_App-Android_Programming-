package com.edu.quizapp.data.models

data class Teacher(
    val uid: String = "",
    val name: String = "",
    val phone: String = "",
    val address: String = "",
    val email: String = "",
    val profileImageUrl: String = ""
) {
    companion object {
        fun fromMap(map: Map<String, Any>): Teacher {
            return Teacher(
                uid = map["uid"] as? String ?: "",
                email = map["email"] as? String ?: "",
                phone = map["phone"] as? String ?: "",
                address = map["address"] as? String ?: "",
                name = map["name"] as? String ?: "",
                profileImageUrl = map["profileImageUrl"] as? String ?: ""
            )
        }
    }

    fun toMap(): Map<String, Any> {
        return mapOf(
            "uid" to uid,
            "email" to email,
            "phone" to phone,
            "name" to name,
            "address" to address,
            "profileImageUrl" to profileImageUrl
        )
    }
}