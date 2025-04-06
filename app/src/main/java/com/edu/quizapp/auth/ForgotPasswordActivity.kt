package com.edu.quizapp.auth

import android.app.AlertDialog
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.edu.quizapp.databinding.ActivityForgotPasswordBinding

class ForgotPasswordActivity : AppCompatActivity() {

    private lateinit var binding: ActivityForgotPasswordBinding
    private lateinit var viewModel: ForgotPasswordViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityForgotPasswordBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel = ViewModelProvider(this)[ForgotPasswordViewModel::class.java]

        setupListeners()
        observeResetPasswordResult()
    }

    private fun setupListeners() {
        binding.btnSendEmail.setOnClickListener {
            val email = binding.etEmail.text.toString().trim()
            showConfirmationDialog(email)
        }
        binding.btnCancel.setOnClickListener { finish() }
    }

    private fun showConfirmationDialog(email: String) {
        AlertDialog.Builder(this)
            .setTitle("Xác nhận")
            .setMessage("Bạn có muốn gửi email đặt lại mật khẩu đến $email không?")
            .setPositiveButton("Có") { _, _ ->
                viewModel.sendPasswordResetEmail(email)
            }
            .setNegativeButton("Không", null)
            .show()
    }

    private fun observeResetPasswordResult() {
        viewModel.resetPasswordResult.observe(this) { result ->
            binding.progressBar.visibility = View.GONE
            when (result) {
                is ResetPasswordResult.Loading -> {
                    binding.progressBar.visibility = View.VISIBLE
                }
                is ResetPasswordResult.Success -> {
                    Toast.makeText(this, "Email đặt lại mật khẩu đã được gửi.", Toast.LENGTH_LONG).show()
                    finish()
                }
                is ResetPasswordResult.Error -> {
                    Toast.makeText(this, result.message, Toast.LENGTH_LONG).show()
                }
            }
        }
    }
}