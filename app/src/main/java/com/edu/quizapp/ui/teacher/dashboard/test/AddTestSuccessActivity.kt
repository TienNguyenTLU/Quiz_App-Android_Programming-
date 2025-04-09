package com.edu.quizapp.ui.teacher.dashboard.test

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.edu.quizapp.R
import com.edu.quizapp.databinding.ActivityAddTestSuccessBinding
import com.edu.quizapp.ui.teacher.dashboard.TeacherDashboardActivity
import com.edu.quizapp.ui.teacher.profile.TeacherProfileActivity

class AddTestSuccessActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddTestSuccessBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddTestSuccessBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupListeners()
        setupBottomNavigationTeacher()
    }

    private fun setupListeners() {
        binding.backButton.setOnClickListener {
            // Quay lại TestManagementActivity
            val intent = Intent(this, TestManagementActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
            startActivity(intent)
            finish()
        }

        binding.backButtonHeader.setOnClickListener {
            // Quay lại TestManagementActivity
            val intent = Intent(this, TestManagementActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
            startActivity(intent)
            finish()
        }
    }

    private fun setupBottomNavigationTeacher() {
        binding.bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {

                R.id.navigation_home -> {
                    val intent = Intent(this, TeacherDashboardActivity::class.java)
                    startActivity(intent)
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