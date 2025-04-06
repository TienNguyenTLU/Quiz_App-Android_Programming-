package com.edu.quizapp.auth

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.edu.quizapp.utils.LoginValidator
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class LoginViewModel : ViewModel() {

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()
    private val validator = LoginValidator()

    private val _loginResult = MutableLiveData<LoginResult>()
    val loginResult: LiveData<LoginResult> = _loginResult

    fun loginUser(email: String, password: String) {
        viewModelScope.launch {
            if (!validator.validateEmail(email)) {
                _loginResult.value = LoginResult.Error("Email không hợp lệ.")
                return@launch
            }

            if (!validator.validatePassword(password)) {
                _loginResult.value = LoginResult.Error("Mật khẩu phải có ít nhất 6 ký tự.")
                return@launch
            }

            _loginResult.value = LoginResult.Loading
            try {
                auth.signInWithEmailAndPassword(email, password).await()
                val user = auth.currentUser
                user?.let {
                    redirectToMainScreen(it.uid)
                } ?: run {
                    _loginResult.value = LoginResult.Error("Người dùng không tồn tại.")
                }
            } catch (e: Exception) {
                _loginResult.value = LoginResult.Error(e.localizedMessage ?: "Đăng nhập thất bại.")
            }
        }
    }

    private suspend fun redirectToMainScreen(uid: String) {
        try {
            val document = db.collection("users").document(uid).get().await()
            if (document.exists()) {
                val role = document.getString("role")
                withContext(Dispatchers.Main) {
                    if (role == "Học sinh") {
                        _loginResult.value = LoginResult.Success("Student")
                    } else if (role == "Giáo viên") {
                        _loginResult.value = LoginResult.Success("Teacher")
                    } else {
                        _loginResult.value = LoginResult.Success("Other")
                    }
                }
            } else {
                _loginResult.value = LoginResult.Error("Email hoặc mật khẩu không hợp lệ.")
            }
        } catch (e: Exception) {
            _loginResult.value = LoginResult.Error(e.localizedMessage ?: "Lỗi truy vấn Firestore.")
        }
    }
}

sealed class LoginResult {
    object Loading : LoginResult()
    data class Success(val role: String) : LoginResult()
    data class Error(val message: String) : LoginResult()
}