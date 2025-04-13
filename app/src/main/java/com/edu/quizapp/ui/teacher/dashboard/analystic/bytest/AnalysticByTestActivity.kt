package com.edu.quizapp.ui.teacher.dashboard.analystic.bytest

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.edu.quizapp.adapter.teacher.analystic.AnalysticByTestAdapter
import com.edu.quizapp.data.models.Test
import com.edu.quizapp.data.models.Teacher
import com.edu.quizapp.data.models.Classes
import com.edu.quizapp.data.repository.UserRepository
import com.edu.quizapp.databinding.ActivityAnalysticByTestBinding
import com.edu.quizapp.ui.shared.SharedUserViewModel
import com.edu.quizapp.ui.shared.SharedUserViewModelFactory

class AnalysticByTestActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAnalysticByTestBinding
    private lateinit var viewModel: AnalysticByTestViewModel
    private lateinit var adapter: AnalysticByTestAdapter
    private lateinit var sharedUserViewModel: SharedUserViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAnalysticByTestBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel = ViewModelProvider(this)[AnalysticByTestViewModel::class.java]


        // Khởi tạo SharedUserViewModel
        val userRepository = UserRepository()
        val factory = SharedUserViewModelFactory(userRepository)
        sharedUserViewModel = ViewModelProvider(this, factory)[SharedUserViewModel::class.java]

        observeSharedUserViewModel()
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
}