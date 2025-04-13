package com.edu.quizapp.ui.teacher.dashboard.analystic.bytest

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.edu.quizapp.adapter.teacher.analystic.AnalysticByTestAdapter
import com.edu.quizapp.data.models.Test
import com.edu.quizapp.data.models.Teacher
import com.edu.quizapp.data.models.Classes
import com.edu.quizapp.databinding.ActivityAnalysticByTestBinding

class AnalysticByTestActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAnalysticByTestBinding
    private lateinit var viewModel: AnalysticByTestViewModel
    private lateinit var adapter: AnalysticByTestAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAnalysticByTestBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel = ViewModelProvider(this)[AnalysticByTestViewModel::class.java]

        setupRecyclerView()
        observeViewModel()

        viewModel.fetchTestsAndTeachersAndClasses()

        binding.backButton.setOnClickListener{
            finish()
        }
    }

    private fun setupRecyclerView() {
        adapter = AnalysticByTestAdapter(emptyList(), emptyMap(), emptyMap()) { test ->
            // Xử lý sự kiện click vào item
            val intent = Intent(this, StatisticalTestActivity::class.java)
            intent.putExtra("testId", test.testId)
            intent.putExtra("testName", test.testName) // Truyền testName
            startActivity(intent)
        }
        binding.recyclerViewClassListStats.adapter = adapter
        binding.recyclerViewClassListStats.layoutManager = LinearLayoutManager(this)
    }

    private fun observeViewModel() {
        viewModel.testsAndTeachersAndClasses.observe(this) { (tests, teachers, classes) ->
            if (tests.isNullOrEmpty()) {
                binding.recyclerViewClassListStats.visibility = View.GONE
                binding.emptyClassListStats.visibility = View.VISIBLE
            } else {
                binding.recyclerViewClassListStats.visibility = View.VISIBLE
                binding.emptyClassListStats.visibility = View.GONE
                adapter.updateData(tests, teachers, classes)
            }
        }
    }
}