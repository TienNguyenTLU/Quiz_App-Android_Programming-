package com.edu.quizapp.auth

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.edu.quizapp.MainActivity
import com.edu.quizapp.R
import com.edu.quizapp.ui.student.dashboard.StudentDashboardActivity
import com.edu.quizapp.databinding.ActivityLoginBinding
import com.edu.quizapp.ui.teacher.dashboard.TeacherDashboardActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class LoginActivity : AppCompatActivity() {

    private var _binding: ActivityLoginBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: LoginViewModel
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel = ViewModelProvider(this)[LoginViewModel::class.java]
        setupUI()
    }

    private fun setupUI() {
        setupListeners()
        observeLoginResult()
        checkLoginState()
    }

    private fun checkLoginState() {
        val currentUser = auth.currentUser
        if (currentUser != null) {
            db.collection("users").document(currentUser.uid).get()
                .addOnSuccessListener { document ->
                    if (document.exists()) {
                        navigateBasedOnRole(document.getString("role"))
                    }
                }
        }
    }

    private fun navigateBasedOnRole(role: String?) {
        val intent = when (role?.lowercase()) {
            "học sinh" -> Intent(this, StudentDashboardActivity::class.java)
            "giáo viên" -> Intent(this, TeacherDashboardActivity::class.java)
            else -> Intent(this, MainActivity::class.java)
        }
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }

    private fun setupListeners() {
        with(binding) {
            btnLogin.setOnClickListener {
                val email = etEmail.text.toString().trim()
                val password = etPassword.text.toString().trim()
                viewModel.loginUser(email, password)
            }

            tvRegister.setOnClickListener {
                startActivity(Intent(this@LoginActivity, RegisterActivity::class.java))
            }

            tvForgotPassword.setOnClickListener {
                startActivity(Intent(this@LoginActivity, ForgotPasswordActivity::class.java))
            }
        }
    }

    private fun observeLoginResult() {
        viewModel.loginResult.observe(this) { result ->
            binding.progressBar.visibility = View.GONE
            when (result) {
                is LoginResult.Loading -> {
                    binding.progressBar.visibility = View.VISIBLE
                }
                is LoginResult.Success -> {
                    // Get the user's role from Firestore
                    val currentUser = auth.currentUser
                    if (currentUser != null) {
                        db.collection("users").document(currentUser.uid).get()
                            .addOnSuccessListener { document ->
                                if (document.exists()) {
                                    navigateBasedOnRole(document.getString("role"))
                                } else {
                                    Toast.makeText(this, "Không tìm thấy thông tin người dùng", Toast.LENGTH_SHORT).show()
                                }
                            }
                            .addOnFailureListener { e ->
                                Toast.makeText(this, "Lỗi: ${e.message}", Toast.LENGTH_SHORT).show()
                            }
                    }
                }
                is LoginResult.Error -> {
                    Toast.makeText(this, result.message, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}