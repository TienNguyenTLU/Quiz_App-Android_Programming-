package com.edu.quizapp.ui.teacher.dashboard.analystic.byclass

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.edu.quizapp.R
import com.edu.quizapp.adapter.teacher.analystic.AnalysticByClassAdapter
import com.edu.quizapp.data.models.Classes
import com.edu.quizapp.data.repository.UserRepository
import com.edu.quizapp.databinding.ActivityAnalysticByClassBinding
import com.edu.quizapp.ui.shared.SharedUserViewModel
import com.edu.quizapp.ui.shared.SharedUserViewModelFactory

class AnalysticByClassActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAnalysticByClassBinding
    private lateinit var viewModel: AnalysticByClassViewModel
    private lateinit var adapter: AnalysticByClassAdapter
    private lateinit var sharedUserViewModel: SharedUserViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAnalysticByClassBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val teacherId = intent.getStringExtra("teacherId") ?: ""

        viewModel = ViewModelProvider(this)[AnalysticByClassViewModel::class.java]

        // Khởi tạo SharedUserViewModel
        val userRepository = UserRepository()
        val factory = SharedUserViewModelFactory(userRepository)
        sharedUserViewModel = ViewModelProvider(this, factory)[SharedUserViewModel::class.java]

        setupRecyclerView()
        observeViewModel()
        observeSharedUserViewModel() // Thêm observe cho SharedUserViewModel
        setupListeners()

        viewModel.setTeacherId(teacherId)
        viewModel.fetchClasses()
    }

    private fun setupRecyclerView() {
        adapter = AnalysticByClassAdapter(emptyList()) { classes ->
            val intent = Intent(this, StatisticalClassActivity::class.java)
            intent.putExtra("classId", classes.classId)
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

    private fun observeSharedUserViewModel() {
        sharedUserViewModel.userData.observe(this) { user ->
            if (user != null) {
                binding.topBarInclude.userName.text = user.name
                binding.topBarInclude.welcomeMessage.text = "Chào mừng đến với 3T"
            } else {
                Toast.makeText(this, "Không thể tải thông tin người dùng.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun setupListeners() {
        binding.backButton.setOnClickListener {
            finish()
        }
    }
}