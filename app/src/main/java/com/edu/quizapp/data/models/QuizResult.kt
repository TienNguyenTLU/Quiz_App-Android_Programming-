package com.edu.quizapp.data.models

data class QuizResult(
    val id: String = "",
    val studentId: String = "",
    val quizId: String = "",
    val quizName: String = "",
    val score: Int = 0,
    val correctAnswers: Int = 0,
    val incorrectAnswers: Int = 0,
    val skippedQuestions: Int = 0,
    val duration: String = "",
    val timestamp: Long = System.currentTimeMillis()
) 