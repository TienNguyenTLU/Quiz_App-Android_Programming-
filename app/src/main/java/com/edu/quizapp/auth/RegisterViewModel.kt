package com.edu.quizapp.auth

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.edu.quizapp.R
import com.edu.quizapp.data.models.User
import com.edu.quizapp.utils.RegisterValidator // Đã cập nhật đường dẫn gói
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

class RegisterViewModel : ViewModel() {

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()
    private val validator = RegisterValidator()

    private val _registerResult = MutableLiveData<RegisterResult>()
    val registerResult: LiveData<RegisterResult> = _registerResult

    fun registerUser(fullName: String, role: String, email: String, password: String, confirmPassword: String) {
        viewModelScope.launch {
            if (!validator.validateFullName(fullName)) {
                _registerResult.value = RegisterResult.Error(R.string.invalid_full_name)
                return@launch
            }

            if (!validator.validateRole(role)) {
                _registerResult.value = RegisterResult.Error(R.string.invalid_role)
                return@launch
            }

            if (!validator.validateEmail(email)) {
                _registerResult.value = RegisterResult.Error(R.string.invalid_email)
                return@launch
            }

            if (!validator.validatePassword(password)) {
                _registerResult.value = RegisterResult.Error(R.string.invalid_password)
                return@launch
            }

            if (!validator.validateConfirmPassword(password, confirmPassword)) {
                _registerResult.value = RegisterResult.Error(R.string.password_mismatch)
                return@launch
            }

            _registerResult.value = RegisterResult.Loading
            try {
                auth.createUserWithEmailAndPassword(email, password).await()
                val user = auth.currentUser
                user?.let {
                    sendEmailVerification(it.uid, fullName, role, email)
                } ?: run {
                    _registerResult.value = RegisterResult.Error(R.string.registration_failed)
                }
            } catch (e: Exception) {
                _registerResult.value = RegisterResult.Error(R.string.registration_failed)
            }
        }
    }

    private fun sendEmailVerification(uid: String, fullName: String, role: String, email: String) {
        viewModelScope.launch {
            try {
                auth.currentUser?.sendEmailVerification()?.await()
                saveUserDataToFireStore(uid, fullName, role, email)
                checkEmailVerified(uid)
            } catch (e: Exception) {
                _registerResult.value = RegisterResult.Error(R.string.verification_email_failed)
            }
        }
    }

    private suspend fun checkEmailVerified(uid: String) {
        try {
            val user = auth.currentUser
            user?.reload()?.await()
            if (user?.isEmailVerified == true) {
                _registerResult.value = RegisterResult.EmailVerified
            } else {
                _registerResult.value = RegisterResult.EmailNotVerified
            }
        } catch (e: Exception) {
            _registerResult.value = RegisterResult.Error(R.string.verification_check_failed)
        }
    }

    private suspend fun saveUserDataToFireStore(uid: String, fullName: String, role: String, email: String) {
        val user = User(uid, email, role, fullName)
        try {
            withContext(Dispatchers.IO) {
                saveUserToFireStoreSuspend(uid, user)
            }
            _registerResult.value = RegisterResult.Success
        } catch (e: Exception) {
            _registerResult.value = RegisterResult.Error(R.string.save_user_data_failed)
        }
    }

    private suspend fun saveUserToFireStoreSuspend(uid: String, user: User) = suspendCoroutine<Unit> { continuation ->
        db.collection("users").document(uid)
            .set(user.toMap())
            .addOnSuccessListener {
                continuation.resume(Unit)
            }
            .addOnFailureListener { e ->
                continuation.resumeWithException(e)
            }
    }
}

sealed class RegisterResult {
    object Loading : RegisterResult()
    object Success : RegisterResult()
    object EmailVerified : RegisterResult()
    object EmailNotVerified : RegisterResult()
    data class Error(val messageResId: Int) : RegisterResult()
}