package com.edu.quizapp.auth

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.edu.quizapp.MainActivity
import com.edu.quizapp.databinding.ActivityLoginBinding
import com.edu.quizapp.ui.student.StudentActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import kotlinx.coroutines.*
import kotlinx.coroutines.tasks.await

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore
    private val coroutineScope = CoroutineScope(Dispatchers.Main)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        setupListeners()
    }

    private fun setupListeners() {
        binding.btnLogin.setOnClickListener { loginUser() }
        binding.tvRegister.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }
        binding.tvForgotPassword.setOnClickListener {
            startActivity(Intent(this, ForgotPasswordActivity::class.java))
        }
    }

    private fun loginUser() {
        val email = binding.etEmail.text.toString().trim()
        val password = binding.etPassword.text.toString().trim()

        var isValid = true

        if (email.isEmpty()) {
            binding.tilEmail.error = "Email không được để trống"
            isValid = false
        } else {
            binding.tilEmail.error = null
        }

        if (password.isEmpty()) {
            binding.tilPassword.error = "Mật khẩu không được để trống"
            isValid = false
        } else {
            binding.tilPassword.error = null
        }

        if (isValid) {
            binding.progressBar.visibility = View.VISIBLE
            auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        val user = auth.currentUser
                        user?.let {
                            redirectToMainScreen(it.uid)
                        }
                    } else {
                        binding.progressBar.visibility = View.GONE
                        Log.e("LoginActivity", "Đăng nhập thất bại: ${task.exception?.message}")
                        Toast.makeText(this, "Email hoặc mật khẩu không hợp lệ", Toast.LENGTH_SHORT).show()
                    }
                }
        }
    }

    private fun redirectToMainScreen(uid: String) {
        coroutineScope.launch(Dispatchers.IO) {
            try {
                val document = db.collection("users").document(uid).get().await()
                withContext(Dispatchers.Main) {
                    if (document.exists()) {
                        val role = document.getString("role")
                        if (role == "Học sinh") {
                            startActivity(Intent(this@LoginActivity, StudentActivity::class.java))
                        } else if (role == "Giáo viên") {
                            startActivity(Intent(this@LoginActivity, MainActivity::class.java))
                        } else {
                            startActivity(Intent(this@LoginActivity, MainActivity::class.java))
                        }
                        finish()
                    } else {
                        Toast.makeText(this@LoginActivity, "Email hoặc mật khẩu không hợp lệ", Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Log.e("LoginActivity", "Lỗi truy vấn Firestore: ${e.message}")
                    val errorMessage = when (e) {
                        is FirebaseFirestoreException -> {
                            when (e.code) {
                                FirebaseFirestoreException.Code.PERMISSION_DENIED -> "Không có quyền truy cập dữ liệu."
                                FirebaseFirestoreException.Code.UNAVAILABLE -> "Lỗi kết nối mạng."
                                else -> "Lỗi không xác định."
                            }
                        }
                        else -> "Lỗi không xác định."
                    }
                    Toast.makeText(this@LoginActivity, errorMessage, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        coroutineScope.cancel()
    }
}