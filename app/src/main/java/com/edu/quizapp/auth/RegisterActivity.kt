package com.edu.quizapp.auth

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.edu.quizapp.MainActivity
import com.edu.quizapp.R
import com.edu.quizapp.databinding.ActivityRegisterBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore
    private lateinit var validator: RegisterValidator

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()
        validator = RegisterValidator()

        setupRoleDropdown()
        setupListeners()
    }

    private fun setupRoleDropdown() {
        val roles = resources.getStringArray(R.array.roles_array).filter { it != "Admin" }
        val adapter = ArrayAdapter(this, R.layout.dropdown_item, roles)
        binding.actvRole.setAdapter(adapter)
    }

    private fun setupListeners() {
        binding.btnRegister.setOnClickListener {
            registerUser()
        }

        binding.tvLogin.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }
    }

    private fun registerUser() {
        val fullName = binding.etFullName.text.toString().trim()
        val role = binding.actvRole.text.toString().trim()
        val email = binding.etEmail.text.toString().trim()
        val password = binding.etPassword.text.toString().trim()
        val confirmPassword = binding.etConfirmPassword.text.toString().trim()

        var isValid = true

        if (fullName.isEmpty()) {
            binding.tilFullName.error = "Không được để trống"
            isValid = false
        } else if (!validator.validateFullName(fullName)) {
            binding.tilFullName.error = "Họ và tên không hợp lệ"
            isValid = false
        } else {
            binding.tilFullName.error = null
        }

        if (role.isEmpty()) {
            binding.tilRole.error = "Không được để trống"
            isValid = false
        } else if (!validator.validateRole(role)) {
            binding.tilRole.error = "Chọn vai trò hợp lệ"
            isValid = false
        } else {
            binding.tilRole.error = null
        }

        if (email.isEmpty()) {
            binding.tilEmail.error = "Không được để trống"
            isValid = false
        } else if (!validator.validateEmail(email)) {
            binding.tilEmail.error = "Email không hợp lệ"
            isValid = false
        } else {
            binding.tilEmail.error = null
        }

        if (password.isEmpty()) {
            binding.tilPassword.error = "Không được để trống"
            isValid = false
        } else if (!validator.validatePassword(password)) {
            binding.tilPassword.error = "Mật khẩu không hợp lệ"
            isValid = false
        } else {
            binding.tilPassword.error = null
        }

        if (confirmPassword.isEmpty()) {
            binding.tilConfirmPassword.error = "Không được để trống"
            isValid = false
        } else if (!validator.validateConfirmPassword(password, confirmPassword)) {
            binding.tilConfirmPassword.error = "Mật khẩu không khớp"
            isValid = false
        } else {
            binding.tilConfirmPassword.error = null
        }

        if (isValid) {
            binding.progressBar.visibility = View.VISIBLE
            auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val user = auth.currentUser
                        user?.sendEmailVerification()
                            ?.addOnCompleteListener { verificationTask ->
                                binding.progressBar.visibility = View.GONE
                                if (verificationTask.isSuccessful) {
                                    Toast.makeText(this, "Email xác thực đã được gửi.", Toast.LENGTH_SHORT).show()
                                    verifyEmail()
                                } else {
                                    Toast.makeText(this, "Lỗi gửi email xác thực.", Toast.LENGTH_SHORT).show()
                                }
                            }
                    } else {
                        binding.progressBar.visibility = View.GONE
                        Toast.makeText(this, "Đăng ký thất bại: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                    }
                }
        }
    }

    private fun verifyEmail() {
        val user = auth.currentUser
        user?.reload()?.addOnCompleteListener { reloadTask ->
            if (reloadTask.isSuccessful) {
                if (user.isEmailVerified) {
                    saveUserDataToFirestore(user.uid, binding.etFullName.text.toString().trim(), binding.actvRole.text.toString().trim(), binding.etEmail.text.toString().trim())
                } else {
                    Toast.makeText(this, "Vui lòng kiểm tra email của bạn để xác thực.", Toast.LENGTH_LONG).show()
                }
            } else {
                Toast.makeText(this, "Lỗi kiểm tra xác thực email.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun saveUserDataToFirestore(uid: String, fullName: String, role: String, email: String) {
        val user = hashMapOf(
            "uid" to uid,
            "name" to fullName,
            "role" to role,
            "email" to email
        )

        db.collection("users").document(uid)
            .set(user)
            .addOnSuccessListener {
                Toast.makeText(this, "Đăng ký thành công", Toast.LENGTH_SHORT).show()
                startActivity(Intent(this, MainActivity::class.java))
                finish()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Lỗi: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }
}