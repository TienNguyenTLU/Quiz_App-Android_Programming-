package com.edu.quizapp.utils

import android.util.Patterns

class ForgotPasswordValidator {

    fun validateEmail(email: String): Boolean {
        return Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }
}