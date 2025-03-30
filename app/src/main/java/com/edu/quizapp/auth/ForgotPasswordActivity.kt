package com.edu.quizapp.auth

import android.app.AlertDialog
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.edu.quizapp.databinding.ActivityForgotPasswordBinding
import com.google.firebase.auth.FirebaseAuth

class ForgotPasswordActivity : AppCompatActivity() {

    private lateinit var binding: ActivityForgotPasswordBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var validator: ForgotPasswordValidator

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityForgotPasswordBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()
        validator = ForgotPasswordValidator()

        setupListeners()
    }

    private fun setupListeners() {
        binding.btnSendEmail.setOnClickListener { sendPasswordResetEmail() }
        binding.btnCancel.setOnClickListener { finish() }
    }

    private fun sendPasswordResetEmail() {
        val email = binding.etEmail.text.toString().trim()

        if (!validator.validateEmail(email)) {
            binding.tilEmail.error = "Email không hợp lệ"
            return
        } else {
            binding.tilEmail.error = null
        }

        AlertDialog.Builder(this)
            .setTitle("Xác nhận")
            .setMessage("Bạn có muốn gửi email đặt lại mật khẩu đến $email không?")
            .setPositiveButton("Có") { _, _ ->
                binding.progressBar.visibility = View.VISIBLE
                auth.sendPasswordResetEmail(email)
                    .addOnCompleteListener { task ->
                        binding.progressBar.visibility = View.GONE
                        if (task.isSuccessful) {
                            Toast.makeText(this, "Email đặt lại mật khẩu đã được gửi.", Toast.LENGTH_LONG).show()
                            finish()
                        } else {
                            Toast.makeText(this, "Lỗi: ${task.exception?.message}", Toast.LENGTH_LONG).show()
                        }
                    }
            }
            .setNegativeButton("Không", null)
            .show()
    }
}