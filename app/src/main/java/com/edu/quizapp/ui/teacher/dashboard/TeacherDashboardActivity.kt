package com.edu.quizapp.ui.teacher.dashboard

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.get
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.edu.quizapp.R
import com.edu.quizapp.databinding.ActivityTeacherDashboardBinding
import com.edu.quizapp.ui.teacher.dashboard.classroom.ClassManagementActivity
import com.edu.quizapp.ui.teacher.dashboard.test.TestManagementActivity
import com.edu.quizapp.ui.teacher.profile.TeacherProfileActivity

class TeacherDashboardActivity : AppCompatActivity() {

    private lateinit var binding: ActivityTeacherDashboardBinding
    private lateinit var viewModel: TeacherDashboardViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTeacherDashboardBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel = ViewModelProvider(this)[TeacherDashboardViewModel::class.java]

        setupObservers()
        setupBottomNavigationTeacher()
        setupFeatureLayoutClickListeners()

        binding.bottomNavigation.selectedItemId = R.id.navigation_home
    }

    override fun onResume() {
        super.onResume()
        // Cập nhật giao diện khi Activity được resume
        viewModel.loadData()
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

        viewModel.teacherData.observe(this) { teacher ->
            if (teacher != null) {
                // Load ảnh đại diện từ teacher nếu có
                if (!teacher.profileImageUrl.isNullOrEmpty()) {
                    Glide.with(this)
                        .load(teacher.profileImageUrl)
                        .placeholder(R.drawable.ic_default_profile)
                        .error(R.drawable.ic_default_profile)
                        .into(binding.topBarInclude.profileImage)
                } else {
                    binding.topBarInclude.profileImage.setImageResource(R.drawable.ic_default_profile)
                }
            } else {
                viewModel.createTeacherIfNotExist()
            }
        }
    }

    private fun setupBottomNavigationTeacher() {
        binding.bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navigation_notifications -> {
                    true
                }
                R.id.navigation_home -> {
                    // Chỉ trả về true để giữ nguyên Activity hiện tại
                    true
                }
                R.id.navigation_profile -> {
                    val intent = Intent(this, TeacherProfileActivity::class.java)
                    startActivity(intent)
                    true
                }
                else -> false
            }
        }
    }

    private fun setupFeatureLayoutClickListeners() {
//        binding.featureLayout.getChildAt(0).setOnClickListener{
//            val intent = Intent(this, CategoryManagementActivity::class.java)
//            startActivity(intent)
//        }
        binding.featureLayout.getChildAt(1).setOnClickListener{
            val intent = Intent(this, TestManagementActivity::class.java)
            startActivity(intent)
        }
        binding.featureLayout.getChildAt(2).setOnClickListener {
            val intent = Intent(this, ClassManagementActivity::class.java)
            startActivity(intent)
        }
    }
}