package com.edu.quizapp.data.models

data class Result(
    val resultId: String = "",
    val studentId: String = "",
    val testId: String = "",
    val correctCount: Long = 0,
    val incorrectCount: Long = 0,
    val skippedCount: Long = 0,
    val score: Double = 0.0,
    val timeTaken: Long = 0
)