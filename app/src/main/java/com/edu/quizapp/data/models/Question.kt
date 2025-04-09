package com.edu.quizapp.data.models

data class Question(
    val questionId: String = "",
    val testId: String = "",
    val questionText: String = "",
    val answers: List<String> = emptyList(),
    val correctAnswer: Any = ""
) {
    companion object {
        fun fromMap(map: Map<String, Any>): Question {
            return Question(
                questionId = map["questionId"] as? String ?: "",
                testId = map["testId"] as? String ?: "",
                questionText = map["questionText"] as? String ?: "",
                answers = (map["answers"] as? List<*>)?.map { it.toString() } ?: emptyList(),
                correctAnswer = map["correctAnswer"] ?: ""
            )
        }
    }

    fun toMap(): Map<String, Any> {
        return mapOf(
            "questionId" to questionId,
            "testId" to testId,
            "questionText" to questionText,
            "answers" to answers,
            "correctAnswer" to correctAnswer
        )
    }
}