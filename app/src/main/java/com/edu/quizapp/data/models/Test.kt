package com.edu.quizapp.data.models

data class Test(
    val testId: String = "",
    val testName: String = "",
    val classCode: String = "",
    val questions: List<String> = emptyList(), // Danh sách các document ID của câu hỏi
    val duration: Long = 0,
    val questionCount: Int = 0 // Thêm tổng số câu hỏi
)