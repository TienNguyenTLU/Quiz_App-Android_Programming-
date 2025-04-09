package com.edu.quizapp.ui.student.result

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.edu.quizapp.R
import com.edu.quizapp.databinding.ActivityQuizResultBinding
import com.edu.quizapp.ui.student.dashboard.StudentDashboardActivity
import java.util.concurrent.TimeUnit

class QuizResultActivity : AppCompatActivity() {

    private lateinit var binding: ActivityQuizResultBinding
    private lateinit var viewModel: QuizResultViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityQuizResultBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel = ViewModelProvider(this).get(QuizResultViewModel::class.java)

        val resultId = intent.getStringExtra("RESULT_ID")
        val testId = intent.getStringExtra("TEST_ID")

        if (resultId != null) {
            viewModel.loadResult(resultId)
        }

        if (testId != null) {
            viewModel.loadTest(testId)
        }

        setupObservers()
        setupListeners()
    }

    private fun setupObservers() {
        viewModel.result.observe(this) { result ->
            binding.correctCount.text = result.correctCount.toString()
            binding.incorrectCount.text = result.incorrectCount.toString()
            binding.skippedCount.text = result.skippedCount.toString()
            binding.testScore.text = String.format("%.1f", result.score)

            // Định dạng thời gian làm bài
            val minutes = TimeUnit.MILLISECONDS.toMinutes(result.timeTaken)
            val seconds = TimeUnit.MILLISECONDS.toSeconds(result.timeTaken) % 60
            binding.testDuration.text = String.format("%02d:%02d", minutes, seconds)
        }

        viewModel.test.observe(this) { test ->
            binding.testName.text = test.testName
        }

        viewModel.isLoading.observe(this) { isLoading ->
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        }
    }

    private fun setupListeners() {
        binding.backButton.setOnClickListener {
            navigateToDashboard()
        }

        binding.bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navigation_home -> {
                    navigateToDashboard()
                    true
                }
                else -> false
            }
        }
    }

    private fun navigateToDashboard() {
        val intent = Intent(this, StudentDashboardActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(intent)
        finish()
    }

    override fun onBackPressed() {
        navigateToDashboard()
    }
}
