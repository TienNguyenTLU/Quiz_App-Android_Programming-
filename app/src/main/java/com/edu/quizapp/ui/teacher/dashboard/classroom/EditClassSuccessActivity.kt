package com.edu.quizapp.ui.teacher.dashboard.classroom

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.edu.quizapp.R
import com.edu.quizapp.databinding.ActivityEditClassSuccessBinding
import com.edu.quizapp.ui.teacher.dashboard.TeacherDashboardActivity
import com.edu.quizapp.ui.teacher.profile.TeacherProfileActivity

class EditClassSuccessActivity : AppCompatActivity() {

    private lateinit var binding: ActivityEditClassSuccessBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditClassSuccessBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupBottomNavigationTeacher()

        binding.backButton.setOnClickListener {
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