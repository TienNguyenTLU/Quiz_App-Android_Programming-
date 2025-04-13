package com.edu.quizapp.ui.teacher.dashboard.analystic

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.edu.quizapp.R
import com.edu.quizapp.data.repository.UserRepository
import com.edu.quizapp.databinding.ActivityAnalysticBinding
import com.edu.quizapp.ui.shared.SharedUserViewModel
import com.edu.quizapp.ui.shared.SharedUserViewModelFactory
import com.edu.quizapp.ui.teacher.dashboard.TeacherDashboardActivity
import com.edu.quizapp.ui.teacher.dashboard.analystic.byclass.AnalysticByClassActivity
import com.edu.quizapp.ui.teacher.dashboard.analystic.bytest.AnalysticByTestActivity
import com.edu.quizapp.ui.teacher.profile.TeacherProfileActivity
import com.google.firebase.auth.FirebaseAuth

class AnalysticActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAnalysticBinding
    private lateinit var sharedUserViewModel: SharedUserViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAnalysticBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupClickListeners()

        setupBottomNavigationTeacher()
        binding.backButton.setOnClickListener {
            finish()
        }

        val userRepository = UserRepository()
        val factory = SharedUserViewModelFactory(userRepository)
        sharedUserViewModel = ViewModelProvider(this, factory)[SharedUserViewModel::class.java]
        setupObservers()
    }

    private fun setupObservers() {
        sharedUserViewModel.userData.observe(this) { user ->
            if (user != null) {
                binding.topBarInclude.userName.text = user.name
                binding.topBarInclude.welcomeMessage.text = "Chào mừng đến với 3T"
            } else {
                Toast.makeText(this, "Không thể tải thông tin người dùng.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun setupClickListeners() {
        binding.statsByClassCard.setOnClickListener {
            Log.d("AnalysticActivity", "statsByClassCard clicked")

            // Lấy teacherId từ FirebaseAuth
            val teacherId = FirebaseAuth.getInstance().currentUser?.uid ?: ""

            if (teacherId.isNotEmpty()) {
                val intent = Intent(this, AnalysticByClassActivity::class.java)
                intent.putExtra("teacherId", teacherId)
                startActivity(intent)
            } else {
                // Xử lý trường hợp không có teacherId (ví dụ: hiển thị thông báo lỗi)
                Log.e("AnalysticActivity", "Teacher ID is missing.")
            }
        }

        binding.statsByTestCard.setOnClickListener {
            startActivity(Intent(this, AnalysticByTestActivity::class.java))
        }
    }



    private fun setupBottomNavigationTeacher() {
        binding.bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {

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
}