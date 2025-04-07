package com.edu.quizapp.ui.student.profile

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.edu.quizapp.databinding.ActivityChangePasswordStudentBinding
import kotlinx.coroutines.launch

class ChangePasswordStudentActivity : AppCompatActivity() {

    private lateinit var binding: ActivityChangePasswordStudentBinding
    private lateinit var viewModel: ProfileSettingsStudentViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChangePasswordStudentBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel = ViewModelProvider(this)[ProfileSettingsStudentViewModel::class.java]

        binding.changePasswordButton.setOnClickListener {
            val oldPassword = binding.oldPassword.text.toString()
            val newPassword = binding.newPassword.text.toString()
            val confirmPassword = binding.confirmPassword.text.toString()

            if (newPassword == confirmPassword) {
                lifecycleScope.launch {
                    viewModel.changePassword(oldPassword, newPassword).observe(this@ChangePasswordStudentActivity) { isSuccess ->
                        if (isSuccess) {
                            Toast.makeText(this@ChangePasswordStudentActivity, "Đổi mật khẩu thành công", Toast.LENGTH_SHORT).show()
                            finish()
                        } else {
                            Toast.makeText(this@ChangePasswordStudentActivity, "Đổi mật khẩu thất bại", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            } else {
                Toast.makeText(this@ChangePasswordStudentActivity, "Mật khẩu mới không khớp", Toast.LENGTH_SHORT).show()
            }
        }

        binding.forgotPasswordLink.setOnClickListener {
            lifecycleScope.launch {
                viewModel.sendPasswordResetEmail().observe(this@ChangePasswordStudentActivity) { isSuccess ->
                    if (isSuccess) {
                        Toast.makeText(this@ChangePasswordStudentActivity, "Đã gửi email đặt lại mật khẩu", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(this@ChangePasswordStudentActivity, "Gửi email thất bại", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }

        binding.backButton.setOnClickListener {
            finish()
        }
    }
}