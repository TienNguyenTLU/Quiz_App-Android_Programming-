package com.edu.quizapp.data.models

data class Notification(
    val notificationId: String = "",
    val senderId: String = "",
    val message: String = "",
    val timestamp: Long = 0
) {
    companion object {
        fun fromMap(map: Map<String, Any>): Notification {
            return Notification(
                notificationId = map["notificationId"] as? String ?: "",
                senderId = map["senderId"] as? String ?: "",
                message = map["message"] as? String ?: "",
                timestamp = map["timestamp"] as? Long ?: 0
            )
        }
    }

    fun toMap(): Map<String, Any> {
        return mapOf(
            "notificationId" to notificationId,
            "senderId" to senderId,
            "message" to message,
            "timestamp" to timestamp
        )
    }
}