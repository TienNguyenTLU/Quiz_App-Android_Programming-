package com.edu.quizapp.ui.teacher.dashboard.analystic.byclass

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.edu.quizapp.R
import com.edu.quizapp.adapter.teacher.analystic.AnalysticByClassAdapter
import com.edu.quizapp.data.models.Classes
import com.edu.quizapp.databinding.ActivityAnalysticByClassBinding

class AnalysticByClassActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAnalysticByClassBinding
    private lateinit var viewModel: AnalysticByClassViewModel
    private lateinit var adapter: AnalysticByClassAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAnalysticByClassBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val teacherId = intent.getStringExtra("teacherId") ?: ""

        viewModel = ViewModelProvider(this)[AnalysticByClassViewModel::class.java]

        setupRecyclerView()
        observeViewModel()
        setupListeners()

        viewModel.setTeacherId(teacherId)
        viewModel.fetchClasses()
    }

    private fun setupRecyclerView() {
        adapter = AnalysticByClassAdapter(emptyList()) { classes -> // Thêm listener
            // Xử lý sự kiện click ở đây
            val intent = Intent(this, StatisticalClassActivity::class.java)
            intent.putExtra("classId", classes.classId) // Truyền classId vào intent
            startActivity(intent)
        }
        binding.recyclerViewClassListStats.layoutManager = LinearLayoutManager(this)
        binding.recyclerViewClassListStats.adapter = adapter
    }

    private fun observeViewModel() {
        viewModel.classes.observe(this) { classes ->
            if (classes.isNullOrEmpty()) {
                binding.recyclerViewClassListStats.visibility = View.GONE
                binding.emptyClassListStats.visibility = View.VISIBLE
            } else {
                binding.recyclerViewClassListStats.visibility = View.VISIBLE
                binding.emptyClassListStats.visibility = View.GONE
                adapter.updateData(classes)
            }
        }

        viewModel.errorMessage.observe(this) { errorMessage ->
            if (!errorMessage.isNullOrEmpty()) {
                Log.e("AnalysticByClassActivity", "Error: $errorMessage")
                // Hiển thị thông báo lỗi cho người dùng nếu cần thiết
            }
        }
    }

    private fun setupListeners() {
        binding.backButton.setOnClickListener {
            finish()
        }
    }
}