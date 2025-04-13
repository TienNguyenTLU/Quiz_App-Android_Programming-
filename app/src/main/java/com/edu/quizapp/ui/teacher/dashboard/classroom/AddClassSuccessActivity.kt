package com.edu.quizapp.ui.teacher.dashboard.classroom

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.edu.quizapp.R
import com.edu.quizapp.data.repository.UserRepository
import com.edu.quizapp.databinding.ActivityAddClassSuccessBinding
import com.edu.quizapp.ui.shared.SharedUserViewModel
import com.edu.quizapp.ui.shared.SharedUserViewModelFactory
import com.edu.quizapp.ui.teacher.dashboard.TeacherDashboardActivity
import com.edu.quizapp.ui.teacher.profile.TeacherProfileActivity

class AddClassSuccessActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddClassSuccessBinding
    private lateinit var sharedUserViewModel: SharedUserViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddClassSuccessBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupBottomNavigationTeacher()
        binding.backButton.setOnClickListener {
            finish() // Quay lại ClassManagementActivity
        }

        // Khởi tạo SharedUserViewModel
        val userRepository = UserRepository()
        val factory = SharedUserViewModelFactory(userRepository)
        sharedUserViewModel = ViewModelProvider(this, factory)[SharedUserViewModel::class.java]
        observeSharedUserViewModel()
    }

    private fun observeSharedUserViewModel() {
        sharedUserViewModel.userData.observe(this) { user ->
            if (user != null) {
                binding.topBarInclude.userName.text = user.name
                binding.topBarInclude.welcomeMessage.text = "Chào mừng đến với 3T"
            } else {
                Toast.makeText(this, "Không thể tải thông tin người dùng.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun setupBottomNavigationTeacher() {
        binding.bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {

                R.id.navigation_home -> {
                    // Xử lý sự kiện cho navigation_home
                    val intent = Intent(this, TeacherDashboardActivity::class.java)
                    startActivity(intent)
                    true
                }
                R.id.navigation_profile -> {
                    // Xử lý sự kiện cho navigation_profile
                    val intent = Intent(this, TeacherProfileActivity::class.java)
                    startActivity(intent)
                    true
                }
                else -> false
            }
        }
    }
}