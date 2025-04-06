package com.edu.quizapp.ui.student.profile

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.edu.quizapp.auth.LoginActivity
import com.edu.quizapp.databinding.ActivityLogoutConfirmationBinding

class LogoutConfirmationActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLogoutConfirmationBinding
    private lateinit var viewModel: ProfileSettingsViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLogoutConfirmationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel = ViewModelProvider(this)[ProfileSettingsViewModel::class.java]

        binding.confirmButton.setOnClickListener {
            viewModel.logout()
            // Chuyển đến màn hình đăng nhập
            val intent = Intent(this, LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK // Xóa back stack
            startActivity(intent)
            finish() // Đóng Activity hiện tại
        }

        binding.cancelButton.setOnClickListener {
            onBackPressed() // Quay lại Activity trước đó
        }
    }
}