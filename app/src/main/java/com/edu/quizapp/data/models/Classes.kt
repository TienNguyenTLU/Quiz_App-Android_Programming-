package com.edu.quizapp.data.models

data class Classes(
    val classId: String = "",
    val className: String = "",
    val teacherId: String = "",
    val students: List<String> = emptyList(),
    val requests: List<String> = emptyList(), // userId -> status
    val classImageUrl: String = "",
    val classCode: String = "",
    val notifications: List<Notification> = emptyList() // Thêm notifications
) {
    companion object {
        fun fromMap(map: Map<String, Any>): Classes {
            return Classes(
                classId = map["classId"] as? String ?: "",
                className = map["className"] as? String ?: "",
                teacherId = map["teacherId"] as? String ?: "",
                students = map["students"] as? List<String> ?: emptyList(),
                requests = map["requests"] as? List<String> ?: emptyList(),
                classImageUrl = map["classImageUrl"] as? String ?: "",
                classCode = map["classCode"] as? String ?: "",
                notifications = (map["notifications"] as? List<Map<String, Any>> ?: emptyList()).map { Notification.fromMap(it) }
            )
        }
    }

    fun toMap(): Map<String, Any> {
        return mapOf(
            "classId" to classId,
            "className" to className,
            "teacherId" to teacherId,
            "students" to students,
            "requests" to requests,
            "classImageUrl" to classImageUrl,
            "classCode" to classCode,
            "notifications" to notifications.map { it.toMap() }
        )
    }
}