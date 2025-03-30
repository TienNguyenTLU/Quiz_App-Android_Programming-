package com.edu.quizapp.data.models

data class Question(
    val questionId: String = "",
    val questionText: String = "",
    val answers: List<String> = emptyList(),
    val correctAnswer: Long = 0
)