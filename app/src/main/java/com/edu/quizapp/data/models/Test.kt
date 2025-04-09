package com.edu.quizapp.data.models

import com.google.firebase.firestore.PropertyName

data class Test(
    val testId: String = "",
    val testName: String = "",
    val classCode: String = "",
    val classId: String = "",
    val questions: List<String> = emptyList(),
    val duration: Long = 0,
    val questionCount: Int = 0 // Thêm tổng số câu hỏi
) {
    companion object {
        fun fromMap(map: Map<String, Any>): Test {
            return Test(
                testId = map["testId"] as? String ?: "",
                testName = map["testName"] as? String ?: "",
                classCode = map["classCode"] as? String ?: "",
                classId = map["classId"] as? String ?: "",
                questions = map["questions"] as? List<String> ?: emptyList(),
                duration = map["duration"] as? Long ?: 0,
                questionCount = map["questionCount"] as? Int ?: 0
            )
        }
    }

    fun toMap(): Map<String, Any> {
        return mapOf(
            "testId" to testId,
            "testName" to testName,
            "classCode" to classCode,
            "classId" to classId,
            "questions" to questions,
            "duration" to duration,
            "questionCount" to questionCount
        )
    }
}