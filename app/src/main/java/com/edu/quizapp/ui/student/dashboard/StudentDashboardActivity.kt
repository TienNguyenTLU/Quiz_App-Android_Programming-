package com.edu.quizapp.ui.student.dashboard

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.edu.quizapp.R
import com.edu.quizapp.databinding.ActivityStudentDashboardBinding
import com.edu.quizapp.ui.student.profile.StudentProfileActivity
import com.bumptech.glide.Glide
import com.edu.quizapp.ui.student.dashboard.classes.StudentClassActivity
import com.edu.quizapp.ui.student.dashboard.test.StudentTestActivity

class StudentDashboardActivity : AppCompatActivity() {

    private lateinit var binding: ActivityStudentDashboardBinding
    private lateinit var viewModel: StudentDashboardViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityStudentDashboardBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel = ViewModelProvider(this)[StudentDashboardViewModel::class.java]

        setupObservers()
        setupBottomNavigation()
        setupFeatureLayoutClickListeners()
        setupSearchButton()

        // Set selected item for bottom navigation
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

    private fun setupBottomNavigation() {
        // Thiết lập bottom navigation
        binding.bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navigation_home -> {
                    // Chỉ trả về true để giữ nguyên Activity hiện tại
                    true
                }
                R.id.navigation_profile -> {
                    val intent = Intent(this, StudentProfileActivity::class.java)
                    startActivity(intent)
                    true
                }
                else -> false
            }
        }
    }

    private fun setupSearchButton() {
        binding.searchButton.setOnClickListener {
            val classCode = binding.searchBar.text.toString().trim()
            if (classCode.isNotEmpty()) {
                viewModel.findClassByCode(classCode)
            } else {
                Toast.makeText(this, "Vui lòng nhập mã lớp.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun setupFeatureLayoutClickListeners() {
//        binding.featureLayout.getChildAt(0).setOnClickListener{
//            val intent = Intent(this, CategoryManagementActivity::class.java)
//            startActivity(intent)
//        }
        binding.featureLayout.getChildAt(0).setOnClickListener{
            val intent = Intent(this, StudentTestActivity::class.java)
            startActivity(intent)
        }
        binding.featureLayout.getChildAt(1).setOnClickListener {
            val intent = Intent(this, StudentClassActivity::class.java)
            startActivity(intent)
        }
    }
}