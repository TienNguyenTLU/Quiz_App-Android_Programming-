package com.edu.quizapp.data.models

data class Test(
    val testId: String = "",
    val testName: String = "",
    val classCode: String = "",
    val questions: List<String> = emptyList(), // Danh sách các document ID của câu hỏi
    val duration: Long = 0,
    val questionCount: Int = 0, // Thêm tổng số câu hỏi
    val isCompleted: Boolean = false // Thêm trạng thái hoàn thành
) {
    companion object {
        fun fromMap(map: Map<String, Any>): Test {
            return Test(
                testId = map["testId"] as? String ?: "",
                testName = map["testName"] as? String ?: "",
                classCode = map["classCode"] as? String ?: "",
                questions = map["questions"] as? List<String> ?: emptyList(),
                duration = map["duration"] as? Long ?: 0,
                questionCount = map["questionCount"] as? Int ?: 0,
                isCompleted = map["isCompleted"] as? Boolean ?: false // Thêm mapping cho isCompleted
            )
        }
    }

    fun toMap(): Map<String, Any> {
        return mapOf(
            "testId" to testId,
            "testName" to testName,
            "classCode" to classCode,
            "questions" to questions,
            "duration" to duration,
            "questionCount" to questionCount,
            "isCompleted" to isCompleted // Thêm isCompleted vào toMap
        )
    }
}