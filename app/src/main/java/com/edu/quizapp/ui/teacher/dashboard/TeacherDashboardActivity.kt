package com.edu.quizapp.ui.teacher.dashboard

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.edu.quizapp.R
import com.edu.quizapp.databinding.ActivityTeacherDashboardBinding

class TeacherDashboardActivity : AppCompatActivity() {
    private lateinit var binding: ActivityTeacherDashboardBinding
    private lateinit var viewModel: TeacherDashboardViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTeacherDashboardBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel = TeacherDashboardViewModel()
        setupObservers()
        setupBottomNavigation()

        binding.bottomNavigation.selectedItemId = R.id.navigation_home
        // Set up the UI components and listeners here
    }
    private fun setupObservers() {
        viewModel.userData.observe(this) { user ->
            if (user != null) {
                binding.topBarInclude.userName.text = user.name
                binding.topBarInclude.welcomeMessage.text = "Chào mừng đến với 3T"
            } else {
                Toast.makeText(this, "Không thể tải thông tin người dùng.", Toast.LENGTH_SHORT).show()
            }
        }

        viewModel.studentData.observe(this) { student ->
            if (student != null) {
                // Load ảnh đại diện từ student nếu có
                if (!student.profileImageUrl.isNullOrEmpty()) {
                    Glide.with(this)
                        .load(student.profileImageUrl)
                        .placeholder(R.drawable.ic_default_profile)
                        .error(R.drawable.ic_default_profile)
                        .into(binding.topBarInclude.profileImage)
                } else {
                    binding.topBarInclude.profileImage.setImageResource(R.drawable.ic_default_profile)
                }
            } else {
                viewModel.createStudentIfNotExist()
            }
        }
    }
}