package com.edu.quizapp.ui.teacher.profile

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.edu.quizapp.R
import com.edu.quizapp.auth.LoginActivity
import com.edu.quizapp.databinding.ActivityTeacherPofileBinding
import com.edu.quizapp.ui.student.profile.ChangePasswordTeacherActivity
import com.edu.quizapp.ui.teacher.dashboard.TeacherDashboardActivity
import com.google.firebase.auth.FirebaseAuth

class TeacherProfileActivity : AppCompatActivity() {

    private lateinit var binding: ActivityTeacherPofileBinding
    private lateinit var viewModel: TeacherProfileViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTeacherPofileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel = ViewModelProvider(this)[TeacherProfileViewModel::class.java]

        setupObservers()
        setupListeners()
        loadTeacherData()
    }

    private fun loadTeacherData() {
        val uid = FirebaseAuth.getInstance().currentUser?.uid
        if (uid != null) {
            viewModel.loadTeacherData()
        } else {
            Toast.makeText(this, "Không thể lấy thông tin người dùng.", Toast.LENGTH_SHORT).show()
            finish()
        }
    }

    private fun setupObservers() {
        viewModel.teacherData.observe(this) { teacher ->
            if (teacher != null) {
                binding.profileName.text = teacher.name
                binding.profileEmail.text = teacher.email
                loadProfileImage(teacher.profileImageUrl)
            } else {
                Toast.makeText(this, "Không thể tải thông tin giáo viên.", Toast.LENGTH_SHORT).show()
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
                    startActivity(Intent(this, TeacherDashboardActivity::class.java))
                    finish()
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
        val intent = Intent(this, EditProfileTeacherActivity::class.java)
        startActivity(intent)
    }

    private fun showChangePasswordFragment() {
        val intent = Intent(this,ChangePasswordTeacherActivity::class.java)
        startActivity(intent)
    }

    private fun showLogoutFragment() {
        val intent = Intent(this, LogoutConfirmationTeacherActivity::class.java)
        startActivity(intent)
    }
}