package com.edu.quizapp.auth

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.edu.quizapp.MainActivity
import com.edu.quizapp.R
import com.edu.quizapp.databinding.ActivityRegisterBinding

class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding
    private lateinit var viewModel: RegisterViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel = ViewModelProvider(this)[RegisterViewModel::class.java]

        setupRoleDropdown()
        setupListeners()
        observeRegisterResult()
    }

    private fun setupRoleDropdown() {
        val roles = resources.getStringArray(R.array.roles_array).filter { it != "Admin" }
        val adapter = ArrayAdapter(this, R.layout.dropdown_item, roles)
        binding.actvRole.setAdapter(adapter)
    }

    private fun setupListeners() {
        binding.btnRegister.setOnClickListener {
            val fullName = binding.etFullName.text.toString().trim()
            val role = binding.actvRole.text.toString().trim()
            val email = binding.etEmail.text.toString().trim()
            val password = binding.etPassword.text.toString().trim()
            val confirmPassword = binding.etConfirmPassword.text.toString().trim()
            viewModel.registerUser(fullName, role, email, password, confirmPassword)
        }

        binding.tvLogin.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }
    }

    private fun observeRegisterResult() {
        viewModel.registerResult.observe(this) { result ->
            binding.progressBar.visibility = View.GONE
            when (result) {
                is RegisterResult.Loading -> {
                    binding.progressBar.visibility = View.VISIBLE
                }
                is RegisterResult.Success -> {
                    Toast.makeText(this, R.string.registration_success, Toast.LENGTH_LONG).show()
                }
                is RegisterResult.EmailVerified -> {
                    Toast.makeText(this, R.string.email_verified, Toast.LENGTH_SHORT).show()
                    startActivity(Intent(this, LoginActivity::class.java))
                    finish()
                }
                is RegisterResult.EmailNotVerified -> {
                    Toast.makeText(this, R.string.email_not_verified, Toast.LENGTH_LONG).show()
                    binding.progressBar.visibility = View.GONE
                }
                is RegisterResult.Error -> {
                    Toast.makeText(this, result.messageResId, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}