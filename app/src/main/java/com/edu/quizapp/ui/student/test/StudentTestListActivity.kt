package com.edu.quizapp.ui.student.test

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.edu.quizapp.adapter.student.classes.StudentTestListAdapter
import com.edu.quizapp.databinding.ActivityStudentTestListBinding
import com.google.firebase.auth.FirebaseAuth

class StudentTestListActivity : AppCompatActivity() {

    private lateinit var binding: ActivityStudentTestListBinding
    private lateinit var viewModel: StudentTestViewModel
    private lateinit var adapter: StudentTestListAdapter
    private var classCode: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityStudentTestListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupViewModel()
        setupRecyclerView()
        setupListeners()
        loadTests()
    }

    private fun setupViewModel() {
        viewModel = ViewModelProvider(this)[StudentTestViewModel::class.java]

        viewModel.tests.observe(this) { tests ->
            adapter.updateData(tests)

            if (tests.isEmpty()) {
                binding.emptyView.visibility = View.VISIBLE
                binding.testRecyclerView.visibility = View.GONE
            } else {
                binding.emptyView.visibility = View.GONE
                binding.testRecyclerView.visibility = View.VISIBLE
            }

            binding.progressBar.visibility = View.GONE
        }

        viewModel.isLoading.observe(this) { isLoading ->
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        }
    }

    private fun setupRecyclerView() {
        adapter = StudentTestListAdapter(emptyList())
        binding.testRecyclerView.layoutManager = LinearLayoutManager(this)
        binding.testRecyclerView.adapter = adapter

        adapter.setOnItemClickListener { test ->
            // Handle test item click
            // You can start a new activity or show test details
        }
    }

    private fun setupListeners() {
        binding.backButton.setOnClickListener {
            finish()
        }

        binding.refreshLayout.setOnRefreshListener {
            loadTests()
            binding.refreshLayout.isRefreshing = false
        }
    }

    private fun loadTests() {
        binding.progressBar.visibility = View.VISIBLE
        classCode = intent.getStringExtra("CLASS_CODE") ?: ""

        if (classCode.isNotEmpty()) {
            viewModel.fetchTestsByClassCode(classCode)
        } else {
            viewModel.fetchTestsForStudent()
        }
    }
    
    override fun onResume() {
        super.onResume()
        loadTests()
    }
}
