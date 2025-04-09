package com.edu.quizapp.ui.student.dashboard.test

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.edu.quizapp.R
import com.edu.quizapp.databinding.ActivityTestStudentBinding
import com.edu.quizapp.ui.student.dashboard.StudentDashboardActivity
import com.edu.quizapp.ui.student.profile.StudentProfileActivity
import com.google.android.material.bottomnavigation.BottomNavigationView

class StudentTestActivity : AppCompatActivity() {

    private lateinit var binding: ActivityTestStudentBinding
    private lateinit var viewModel: StudentTestViewModel
    private lateinit var adapter: StudentTestAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTestStudentBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupToolbar()
        setupViewModel()
        setupRecyclerView()
        setupBottomNavigation()
        
        // Load tests for the student
        loadTests()
    }

    private fun setupToolbar() {
        setSupportActionBar(binding.toolbar.toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        binding.toolbar.toolbarTitle.text = "Bài kiểm tra"
        binding.toolbar.backButton.setOnClickListener {
            onBackPressed()
        }
    }

    private fun setupViewModel() {
        viewModel = ViewModelProvider(this).get(StudentTestViewModel::class.java)
        
        viewModel.tests.observe(this) { tests ->
            adapter.updateData(tests)
            
            // Show empty view if there are no tests
            if (tests.isEmpty()) {
                binding.emptyView.visibility = View.VISIBLE
                binding.testRecyclerView.visibility = View.GONE
            } else {
                binding.emptyView.visibility = View.GONE
                binding.testRecyclerView.visibility = View.VISIBLE
            }
            
            // Hide loading indicator
            binding.progressBar.visibility = View.GONE
        }
        
        viewModel.isLoading.observe(this) { isLoading ->
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        }
    }

    private fun setupRecyclerView() {
        adapter = StudentTestAdapter(emptyList())
        binding.testRecyclerView.layoutManager = LinearLayoutManager(this)
        binding.testRecyclerView.adapter = adapter
    }

    private fun loadTests() {
        binding.progressBar.visibility = View.VISIBLE
        viewModel.fetchTestsForStudent()
    }

    private fun setupBottomNavigation() {
        // Thiết lập bottom navigation
        binding.bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navigation_home -> {
                    val intent = Intent(this, StudentDashboardActivity::class.java)
                    startActivity(intent)
                    finish()
                    true
                }
                R.id.navigation_profile -> {
                    val intent = Intent(this, StudentProfileActivity::class.java)
                    startActivity(intent)
                    finish()
                    true
                }
                else -> false
            }
        }
    }
    
    override fun onResume() {
        super.onResume()
        // Reload tests to reflect any changes when returning to this screen
        loadTests()
    }
}