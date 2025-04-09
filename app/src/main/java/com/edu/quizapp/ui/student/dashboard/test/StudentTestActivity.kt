package com.edu.quizapp.ui.student.dashboard.test

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.edu.quizapp.R
import com.edu.quizapp.databinding.ActivityTestStudentBinding // Import binding class
import com.edu.quizapp.ui.student.profile.StudentProfileActivity
import com.google.android.material.bottomnavigation.BottomNavigationView

class StudentTestActivity : AppCompatActivity() {

    private lateinit var binding: ActivityTestStudentBinding // Declare binding
    private lateinit var viewModel: StudentTestViewModel
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: StudentTestAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTestStudentBinding.inflate(layoutInflater) // Initialize binding
        setContentView(binding.root) // Set content view using binding.root

        viewModel = ViewModelProvider(this).get(StudentTestViewModel::class.java)

        recyclerView = binding.testRecyclerView // Use binding to access views
        recyclerView.layoutManager = LinearLayoutManager(this)
        adapter = StudentTestAdapter(emptyList())
        recyclerView.adapter = adapter
        viewModel.tests.observe(this) { tests ->
            adapter.updateData(tests)
        }

        viewModel.fetchTestsForStudent()

        setupBottomNavigation(binding.bottomNavigation) // Pass binding.bottomNavigation
    }

    private fun setupBottomNavigation(bottomNavigationView: BottomNavigationView) {
        bottomNavigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navigation_notifications -> {
                    true
                }
                R.id.navigation_home -> {
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
}