package com.edu.quizapp.utils

import android.util.Patterns

class RegisterValidator {

    fun validateFullName(fullName: String): Boolean {
        return fullName.isNotBlank() && fullName.length >= 2 && fullName.matches("^[\\p{L}\\s]+$".toRegex())
    }

    fun validateRole(role: String): Boolean {
        return role == "Giáo viên" || role == "Học sinh"
    }

    fun validateEmail(email: String): Boolean {
        return Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    fun validatePassword(password: String): Boolean {
        return password.length >= 8 &&
                password.matches(".*[A-Z].*".toRegex()) &&
                password.matches(".*[a-z].*".toRegex()) &&
                password.matches(".*[0-9].*".toRegex()) &&
                password.matches(".*[!@#\$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>\\/?].*".toRegex())
    }

    fun validateConfirmPassword(password: String, confirmPassword: String): Boolean {
        return password == confirmPassword
    }
}