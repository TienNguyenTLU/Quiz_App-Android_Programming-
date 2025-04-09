package com.edu.quizapp.data.models

data class TestAttempt(
    val attemptId: String = "",
    val testId: String = "",
    val studentId: String = "",
    val startTime: Long = 0,
    val endTime: Long = 0,
    val answers: MutableList<StudentAnswer> = mutableListOf(),
    val isCompleted: Boolean = false
)