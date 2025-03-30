package com.edu.quizapp.data.models

data class Classes(
    val classId: String = "",
    val className: String = "",
    val teacherId: String = "",
    val students: List<String> = emptyList(),
    val requests: List<String> = emptyList()
)