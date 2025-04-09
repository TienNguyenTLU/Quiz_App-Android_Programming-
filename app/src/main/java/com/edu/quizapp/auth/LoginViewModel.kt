package com.edu.quizapp.auth

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class LoginViewModel : ViewModel() {

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()
    private val _loginResult = MutableLiveData<LoginResult>()
    val loginResult: LiveData<LoginResult> = _loginResult

    fun loginUser(email: String, password: String) {
        if (email.isEmpty() || password.isEmpty()) {
            _loginResult.value = LoginResult.Error("Vui lòng nhập email và mật khẩu")
            return
        }

        _loginResult.value = LoginResult.Loading

        viewModelScope.launch {
            try {
                val result = withContext(Dispatchers.IO) {
                    auth.signInWithEmailAndPassword(email, password).await()
                }
                
                val user = result.user
                if (user != null) {
                    // Check if email is verified
                    if (!user.isEmailVerified) {
                        _loginResult.value = LoginResult.Error("Vui lòng xác thực email trước khi đăng nhập")
                        return@launch
                    }
                    
                    // Get user role from Firestore
                    val document = db.collection("users").document(user.uid).get().await()
                    if (document.exists()) {
                        val role = document.getString("role")
                        if (role != null) {
                            _loginResult.value = LoginResult.Success(role)
                        } else {
                            _loginResult.value = LoginResult.Error("Không tìm thấy vai trò người dùng")
                        }
                    } else {
                        _loginResult.value = LoginResult.Error("Không tìm thấy thông tin người dùng")
                    }
                } else {
                    _loginResult.value = LoginResult.Error("Đăng nhập thất bại")
                }
            } catch (e: Exception) {
                _loginResult.value = LoginResult.Error(e.localizedMessage ?: "Lỗi đăng nhập không xác định")
            }
        }
    }
}

sealed class LoginResult {
    object Loading : LoginResult()
    data class Success(val role: String) : LoginResult()
    data class Error(val message: String) : LoginResult()
}