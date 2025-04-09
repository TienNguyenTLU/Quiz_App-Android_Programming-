package com.edu.quizapp.ui.student.result

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.edu.quizapp.adapter.student.result.StudentResultAdapter
import com.edu.quizapp.databinding.ActivityStudentResultListBinding
import com.google.firebase.auth.FirebaseAuth

class StudentResultListActivity : AppCompatActivity() {

    private lateinit var binding: ActivityStudentResultListBinding
    private lateinit var viewModel: StudentResultViewModel
    private lateinit var adapter: StudentResultAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityStudentResultListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupViewModel()
        setupRecyclerView()
        setupListeners()
        loadResults()
    }

    private fun setupViewModel() {
        viewModel = ViewModelProvider(this).get(StudentResultViewModel::class.java)

        viewModel.results.observe(this) { results ->
            adapter.updateData(results)

            // Hiển thị thông báo nếu không có kết quả
            if (results.isEmpty()) {
                binding.emptyView.visibility = View.VISIBLE
                binding.resultRecyclerView.visibility = View.GONE
            } else {
                binding.emptyView.visibility = View.GONE
                binding.resultRecyclerView.visibility = View.VISIBLE
            }

            // Ẩn ProgressBar khi đã tải xong
            binding.progressBar.visibility = View.GONE
        }

        viewModel.isLoading.observe(this) { isLoading ->
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        }
    }

    private fun setupRecyclerView() {
        adapter = StudentResultAdapter(emptyList()) { resultId ->
            // Xử lý khi click vào một kết quả
            val intent = Intent(this, QuizResultActivity::class.java).apply {
                putExtra("RESULT_ID", resultId)
            }
            startActivity(intent)
        }
        binding.resultRecyclerView.layoutManager = LinearLayoutManager(this)
        binding.resultRecyclerView.adapter = adapter
    }

    private fun setupListeners() {
        binding.backButton.setOnClickListener {
            finish()
        }

        binding.refreshLayout.setOnRefreshListener {
            loadResults()
            binding.refreshLayout.isRefreshing = false
        }
    }

    private fun loadResults() {
        binding.progressBar.visibility = View.VISIBLE
        val currentUserId = FirebaseAuth.getInstance().currentUser?.uid

        currentUserId?.let { userId ->
            viewModel.fetchResultsForStudent(userId)
        } ?: run {
            binding.progressBar.visibility = View.GONE
            binding.emptyView.visibility = View.VISIBLE
            binding.resultRecyclerView.visibility = View.GONE
        }
    }
}
