package com.edu.quizapp.ui.student.profile

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.edu.quizapp.auth.LoginActivity
import com.edu.quizapp.databinding.ActivityLogoutConfirmationStudentBinding

class LogoutConfirmationStudentActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLogoutConfirmationStudentBinding
    private lateinit var viewModel: ProfileSettingsStudentViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLogoutConfirmationStudentBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel = ViewModelProvider(this)[ProfileSettingsStudentViewModel::class.java]

        setupListeners()
        observeLogoutState()
    }

    private fun setupListeners() {
        binding.confirmButton.setOnClickListener {
            binding.confirmButton.isEnabled = false
            binding.cancelButton.isEnabled = false
            binding.progressBar.visibility = View.VISIBLE
            viewModel.logout()
        }

        binding.cancelButton.setOnClickListener {
            onBackPressed()
        }
    }

    private fun observeLogoutState() {
        viewModel.logoutEvent.observe(this) { shouldLogout ->
            if (shouldLogout) {
                Toast.makeText(this, "Đăng xuất thành công", Toast.LENGTH_SHORT).show()
                val intent = Intent(this, LoginActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
                finish()
            }
        }
    }
}