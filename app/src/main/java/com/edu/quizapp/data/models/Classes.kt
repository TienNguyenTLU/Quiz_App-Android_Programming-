package com.edu.quizapp.data.models

data class Classes(
    val classId: String = "",
    val className: String = "",
    val teacherId: String = "",
    val students: List<String> = emptyList(), // Danh sách studentsId
    val requests: List<String> = emptyList(),
    val classImageUrl: String = "",
    val classCode: String = ""
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
                classCode = map["classCode"] as? String ?: ""
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
            "classCode" to classCode
        )
    }
}