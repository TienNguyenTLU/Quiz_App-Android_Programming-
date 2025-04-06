package com.edu.quizapp.auth

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.edu.quizapp.utils.ForgotPasswordValidator
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class ForgotPasswordViewModel : ViewModel() {

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val validator = ForgotPasswordValidator()

    private val _resetPasswordResult = MutableLiveData<ResetPasswordResult>()
    val resetPasswordResult: LiveData<ResetPasswordResult> = _resetPasswordResult

    fun sendPasswordResetEmail(email: String) {
        viewModelScope.launch {
            if (!validator.validateEmail(email)) {
                _resetPasswordResult.value = ResetPasswordResult.Error("Email không hợp lệ.")
                return@launch
            }

            _resetPasswordResult.value = ResetPasswordResult.Loading
            try {
                auth.sendPasswordResetEmail(email).await()
                _resetPasswordResult.value = ResetPasswordResult.Success
            } catch (e: Exception) {
                _resetPasswordResult.value = ResetPasswordResult.Error(e.localizedMessage ?: "Lỗi gửi email đặt lại mật khẩu.")
            }
        }
    }
}

sealed class ResetPasswordResult {
    object Loading : ResetPasswordResult()
    object Success : ResetPasswordResult()
    data class Error(val message: String) : ResetPasswordResult()
}