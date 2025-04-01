package com.edu.quizapp.data.models

data class User(
    val uid: String = "",
    val email: String = "",
    val role: String = "",
    val name: String = ""
) {
    companion object {
        fun fromMap(map: Map<String, Any>): User {
            return User(
                uid = map["uid"] as? String ?: "",
                email = map["email"] as? String ?: "",
                role = map["role"] as? String ?: "",
                name = map["name"] as? String ?: ""
            )
        }
    }

    fun toMap(): Map<String, Any> {
        return mapOf(
            "uid" to uid,
            "email" to email,
            "role" to role,
            "name" to name
        )
    }
}