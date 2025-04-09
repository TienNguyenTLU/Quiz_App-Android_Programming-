package com.edu.quizapp.data.models

data class StudentAnswer(
    val questionId: String = "",
    val selectedAnswer: String = "",
    val isCorrect: Boolean = false
)
