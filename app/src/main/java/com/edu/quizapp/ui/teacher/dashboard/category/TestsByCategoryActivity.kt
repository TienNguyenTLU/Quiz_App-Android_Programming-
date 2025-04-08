package com.edu.quizapp.ui.teacher.dashboard.category

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.edu.quizapp.adapter.teacher.category.TestsListAdapter
import com.edu.quizapp.databinding.ActivityTestsByCategoryBinding

class TestsByCategoryActivity : AppCompatActivity() {

    private lateinit var binding: ActivityTestsByCategoryBinding
    private lateinit var viewModel: CategoryManagementViewModel
    private lateinit var adapter: TestsListAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTestsByCategoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel = ViewModelProvider(this)[CategoryManagementViewModel::class.java]

        val categoryId = intent.getStringExtra("categoryId") ?: ""

        setupRecyclerView()
        setupObservers()
        viewModel.loadTestsByCategory(categoryId)
    }

    private fun setupRecyclerView() {
        adapter = TestsListAdapter(emptyList())

        binding.testsRecyclerView.apply {
            layoutManager = LinearLayoutManager(this@TestsByCategoryActivity)
            adapter = adapter
        }
    }

    private fun setupObservers() {
        viewModel.tests.observe(this) { tests ->
            adapter.updateTests(tests)
        }
    }
}