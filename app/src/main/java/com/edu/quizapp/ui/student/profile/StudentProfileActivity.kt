package com.edu.quizapp.ui.student.profile

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.edu.quizapp.R
import com.edu.quizapp.auth.LoginActivity
import com.edu.quizapp.databinding.ActivityStudentProfileBinding
import com.edu.quizapp.ui.student.dashboard.StudentDashboardActivity
import com.google.firebase.auth.FirebaseAuth

class StudentProfileActivity : AppCompatActivity() {

    private lateinit var binding: ActivityStudentProfileBinding
    private lateinit var viewModel: StudentProfileViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityStudentProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel = ViewModelProvider(this)[StudentProfileViewModel::class.java]

        setupObservers()
        setupListeners()
        loadStudentData()

        // Hiển thị Fragment mặc định (nếu bạn muốn hiển thị một Fragment cho thông tin hồ sơ)
        // showProfileInfo() // Nếu bạn có một fragment thông tin hồ sơ riêng
    }

    private fun loadStudentData() {
        val uid = FirebaseAuth.getInstance().currentUser?.uid
        if (uid != null) {
            viewModel.loadStudentData(uid)
        } else {
            Toast.makeText(this, "Không thể lấy thông tin người dùng.", Toast.LENGTH_SHORT).show()
            finish()
        }
    }

    private fun setupObservers() {
        viewModel.studentData.observe(this) { student ->
            if (student != null) {
                binding.profileName.text = student.name
                binding.profileEmail.text = student.email
                loadProfileImage(student.profileImageUrl)
            } else {
                Toast.makeText(this, "Không thể tải thông tin hồ sơ.", Toast.LENGTH_SHORT).show()
            }
        }

        viewModel.logoutEvent.observe(this) { shouldLogout ->
            if (shouldLogout) {
                startActivity(Intent(this, LoginActivity::class.java))
                finish()
            }
        }
    }

    private fun loadProfileImage(imageUrl: String?) {
        if (!imageUrl.isNullOrEmpty()) {
            Glide.with(this)
                .load(imageUrl)
                .placeholder(R.drawable.ic_profile_placeholder)
                .error(R.drawable.ic_profile_placeholder)
                .into(binding.profileImage)
        } else {
            binding.profileImage.setImageResource(R.drawable.ic_profile_placeholder)
        }
    }

    private fun setupListeners() {
        binding.editProfileButtonLayout.setOnClickListener {
            showEditProfileFragment()
        }

        binding.changePasswordButtonLayout.setOnClickListener {
            showChangePasswordFragment()
        }

        binding.logoutButtonLayout.setOnClickListener {
            showLogoutFragment()
        }

        binding.bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navigation_home -> {
                    startActivity(Intent(this, StudentDashboardActivity::class.java))
                    finish()
                    true
                }
                R.id.navigation_notifications -> {
                    // Xử lý sự kiện notifications (nếu cần)
                    true
                }
                R.id.navigation_profile -> {
                    // Xử lý sự kiện profile (nếu cần)
                    true
                }
                else -> false
            }
        }
    }

    private fun showEditProfileFragment() {
        val intent = Intent(this, EditProfileActivity::class.java)
        startActivity(intent)
    }

    private fun showChangePasswordFragment() {
        val intent = Intent(this, ChangePasswordActivity::class.java)
        startActivity(intent)
    }

    private fun showLogoutFragment() {
        val intent = Intent(this, LogoutConfirmationActivity::class.java)
        startActivity(intent)
    }

    private fun showProfileInfo() {
        // Hiển thị thông tin hồ sơ (nếu bạn muốn hiển thị một Fragment cho thông tin hồ sơ)
    }
}