package com.edu.quizapp.ui.student.profile

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.edu.quizapp.databinding.ActivityChangePasswordBinding
import kotlinx.coroutines.launch

class ChangePasswordActivity : AppCompatActivity() {

    private lateinit var binding: ActivityChangePasswordBinding
    private lateinit var viewModel: ProfileSettingsViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChangePasswordBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel = ViewModelProvider(this)[ProfileSettingsViewModel::class.java]

        binding.changePasswordButton.setOnClickListener {
            val oldPassword = binding.oldPassword.text.toString()
            val newPassword = binding.newPassword.text.toString()
            val confirmPassword = binding.confirmPassword.text.toString()

            if (newPassword == confirmPassword) {
                lifecycleScope.launch {
                    viewModel.changePassword(oldPassword, newPassword).observe(this@ChangePasswordActivity) { isSuccess ->
                        if (isSuccess) {
                            Toast.makeText(this@ChangePasswordActivity, "Đổi mật khẩu thành công", Toast.LENGTH_SHORT).show()
                            finish()
                        } else {
                            Toast.makeText(this@ChangePasswordActivity, "Đổi mật khẩu thất bại", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            } else {
                Toast.makeText(this@ChangePasswordActivity, "Mật khẩu mới không khớp", Toast.LENGTH_SHORT).show()
            }
        }

        binding.forgotPasswordLink.setOnClickListener {
            lifecycleScope.launch {
                viewModel.sendPasswordResetEmail().observe(this@ChangePasswordActivity) { isSuccess ->
                    if (isSuccess) {
                        Toast.makeText(this@ChangePasswordActivity, "Đã gửi email đặt lại mật khẩu", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(this@ChangePasswordActivity, "Gửi email thất bại", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }

        binding.backButton.setOnClickListener {
            finish()
        }
    }
}