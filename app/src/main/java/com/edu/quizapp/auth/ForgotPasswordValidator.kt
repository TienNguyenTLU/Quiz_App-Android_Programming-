package com.edu.quizapp.auth

import android.util.Patterns

class ForgotPasswordValidator {

    fun validateEmail(email: String): Boolean {
        return Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }
}