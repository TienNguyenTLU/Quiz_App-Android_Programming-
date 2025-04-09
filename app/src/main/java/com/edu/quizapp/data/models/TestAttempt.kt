package com.edu.quizapp.data.models

import com.google.firebase.firestore.PropertyName

data class TestAttempt(
    val attemptId: String = "",
    val testId: String = "",
    val studentId: String = "",
    val startTime: Long = 0,
    val endTime: Long = 0,
    val answers: MutableList<StudentAnswer> = mutableListOf(),
    @PropertyName("completed")
    val isCompleted: Boolean = false
)