package com.edu.quizapp.ui.teacher.dashboard.category

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.edu.quizapp.adapter.teacher.classes.ClassesListAdapter
import com.edu.quizapp.databinding.ActivityClassesByCategoryBinding

class ClassesByCategoryActivity : AppCompatActivity() {

    private lateinit var binding: ActivityClassesByCategoryBinding
    private lateinit var viewModel: CategoryManagementViewModel
    private lateinit var adapter: ClassesListAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityClassesByCategoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel = ViewModelProvider(this)[CategoryManagementViewModel::class.java]

        val categoryId = intent.getStringExtra("categoryId") ?: ""

        setupRecyclerView()
        setupObservers()
        viewModel.loadClassesByCategory(categoryId)
    }

    private fun setupRecyclerView() {
        adapter = ClassesListAdapter(emptyList())

        binding.classesRecyclerView.apply {
            layoutManager = LinearLayoutManager(this@ClassesByCategoryActivity)
            adapter = adapter
        }
    }

    private fun setupObservers() {
        viewModel.classes.observe(this) { classes ->
            adapter.updateClasses(classes)
        }
    }
}