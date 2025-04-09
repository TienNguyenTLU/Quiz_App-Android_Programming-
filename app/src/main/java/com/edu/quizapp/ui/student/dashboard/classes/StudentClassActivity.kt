package com.edu.quizapp.ui.student.dashboard.classes

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.edu.quizapp.R
import com.edu.quizapp.adapter.student.classes.StudentClassAdapter
import com.edu.quizapp.data.repository.TeacherRepository
import com.edu.quizapp.databinding.ActivityClassStudentBinding
import com.edu.quizapp.ui.student.dashboard.StudentDashboardActivity
import com.edu.quizapp.ui.student.profile.StudentProfileActivity
import com.google.firebase.auth.FirebaseAuth

class StudentClassActivity : AppCompatActivity() {

    private lateinit var binding: ActivityClassStudentBinding
    private lateinit var viewModel: StudentClassViewModel
    private lateinit var adapter: StudentClassAdapter
    private lateinit var teacherRepository: TeacherRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityClassStudentBinding.inflate(layoutInflater)
        setContentView(binding.root)
        Log.d("StudentClassActivity", "onCreate()")

        setupToolbar()
        setupViewModel()
        setupRecyclerView()
        setupBottomNavigation()

        val studentId = FirebaseAuth.getInstance().currentUser?.uid ?: ""
        viewModel.loadStudentClasses(studentId)
    }

    private fun setupToolbar() {
        setSupportActionBar(binding.toolbar.toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        binding.toolbar.toolbarTitle.text = "Lớp học của tôi"
        binding.toolbar.backButton.setOnClickListener {
            onBackPressed()
        }
    }

    private fun setupViewModel() {
        viewModel = ViewModelProvider(this)[StudentClassViewModel::class.java]
        teacherRepository = TeacherRepository() // Khởi tạo teacherRepository ở đây
        setupObservers()
    }

    private fun setupObservers() {
        viewModel.classes.observe(this) { classes ->
            adapter.updateClasses(classes)
        }
    }

    private fun setupRecyclerView() {
        adapter = StudentClassAdapter(emptyList(), teacherRepository) // Truyền teacherRepository vào adapter
        binding.classRecyclerView.layoutManager = LinearLayoutManager(this)
        binding.classRecyclerView.adapter = adapter
        Log.d("StudentClassActivity", "setupRecyclerView() called, adapter set: $adapter")
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

    override fun onStart() {
        super.onStart()
        Log.d("StudentClassActivity", "onStart()")
    }

    override fun onResume() {
        super.onResume()
        Log.d("StudentClassActivity", "onResume()")
        if (binding.classRecyclerView.adapter == null) {
            binding.classRecyclerView.adapter = adapter
            Log.d("StudentClassActivity", "onResume() adapter set: $adapter")
        }
    }
}