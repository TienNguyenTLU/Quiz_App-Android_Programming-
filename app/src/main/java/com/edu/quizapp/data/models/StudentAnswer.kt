package com.edu.quizapp.data.models

import com.google.firebase.firestore.PropertyName

data class StudentAnswer(
    val questionId: String = "",
    val selectedAnswer: String = "",
    @PropertyName("correct")
    val isCorrect: Boolean = false
)
