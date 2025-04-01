package com.edu.quizapp.auth

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.edu.quizapp.MainActivity
import com.edu.quizapp.R
import com.edu.quizapp.databinding.ActivityRegisterBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.edu.quizapp.data.models.User
import kotlinx.coroutines.*
import kotlinx.coroutines.tasks.await
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore
    private lateinit var validator: RegisterValidator
    private val coroutineScope = CoroutineScope(Dispatchers.Main)

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
                        user?.let {
                            sendEmailVerification(it.uid, fullName, role, email)
                        }
                    } else {
                        binding.progressBar.visibility = View.GONE
                        Toast.makeText(this, "Đăng ký thất bại: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                    }
                }
        }
    }

    private fun sendEmailVerification(uid: String, fullName: String, role: String, email: String) {
        auth.currentUser?.sendEmailVerification()
            ?.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    binding.progressBar.visibility = View.GONE
                    Toast.makeText(this, "Đăng ký thành công. Vui lòng kiểm tra email để xác thực.", Toast.LENGTH_LONG).show()
                    saveUserDataToFireStore(uid, fullName, role, email)
                    checkEmailVerified(uid) // Kiểm tra xác thực email
                } else {
                    binding.progressBar.visibility = View.GONE
                    Toast.makeText(this, "Gửi email xác thực thất bại: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun checkEmailVerified(uid: String) {
        coroutineScope.launch {
            try {
                val user = auth.currentUser
                user?.reload()?.await()
                if (user?.isEmailVerified == true) {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(this@RegisterActivity, "Email đã được xác thực.", Toast.LENGTH_SHORT).show()
                        startActivity(Intent(this@RegisterActivity, LoginActivity::class.java))
                        finish()
                    }
                } else {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(this@RegisterActivity, "Email chưa được xác thực. Vui lòng xác thực email để tiếp tục.", Toast.LENGTH_LONG).show()
                        binding.progressBar.visibility = View.GONE
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@RegisterActivity, "Lỗi kiểm tra xác thực email: ${e.message}", Toast.LENGTH_SHORT).show()
                    binding.progressBar.visibility = View.GONE
                }
            }
        }
    }

    private fun saveUserDataToFireStore(uid: String, fullName: String, role: String, email: String) {
        binding.progressBar.visibility = View.VISIBLE
        Log.d("RegisterActivity", "saveUserDataToFirestore: uid=$uid, fullName=$fullName, role=$role, email=$email")

        val user = User(uid, email, role, fullName)

        coroutineScope.launch {
            try {
                withContext(Dispatchers.IO) {
                    saveUserToFireStoreSuspend(uid, user)
                }
                withContext(Dispatchers.Main) {
                    binding.progressBar.visibility = View.GONE
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    binding.progressBar.visibility = View.GONE
                    Log.e("RegisterActivity", "Error saving user data: ${e.message}")
                    Toast.makeText(this@RegisterActivity, "Lỗi khi lưu dữ liệu người dùng: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
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

    override fun onDestroy() {
        super.onDestroy()
        coroutineScope.cancel()
    }
}