package com.edu.quizapp.ui.teacher.dashboard.analystic.byclass

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TableLayout
import android.widget.TableRow
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.edu.quizapp.R
import com.edu.quizapp.data.models.Result
import com.edu.quizapp.databinding.ActivityStatisticalClassBinding
import kotlinx.coroutines.launch

class StatisticalClassActivity : AppCompatActivity() {

    private lateinit var binding: ActivityStatisticalClassBinding
    private lateinit var viewModel: StatisticalClassViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityStatisticalClassBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val classId = intent.getStringExtra("classId") ?: ""

        viewModel = ViewModelProvider(this)[StatisticalClassViewModel::class.java]

        setupListeners()
        observeViewModel(classId)

        viewModel.fetchResultsByClass(classId)
        viewModel.fetchTestsByClass(classId)
    }

    private fun setupListeners() {
        binding.buttonBack.setOnClickListener {
            finish()
        }
    }

    private fun observeViewModel(classId: String) {
        viewModel.results.observe(this) { results ->
            displayResults(results, classId)
        }

        viewModel.errorMessage.observe(this) { errorMessage ->
            if (!errorMessage.isNullOrEmpty()) {
                Log.e("StatisticalClassActivity", "Error: $errorMessage")
                // Hiển thị thông báo lỗi cho người dùng nếu cần thiết
            }
        }
    }

    private fun displayResults(results: List<Result>, classId: String) {
        val tableLayout = binding.tableClassListStats
        val noTestsCard = binding.noTestsCard
        // Xóa tất cả các hàng hiện có (ngoại trừ hàng tiêu đề)
        if (tableLayout.childCount > 1) {
            tableLayout.removeViews(1, tableLayout.childCount - 1)
        }
        lifecycleScope.launch {
            val tests = viewModel.repository.getTestsByClass(classId)
            if (tests.isEmpty()) {
                noTestsCard.visibility = View.VISIBLE
                tableLayout.visibility = View.GONE
                binding.emptyClassListStats.visibility = View.GONE
            } else {
                noTestsCard.visibility = View.GONE
                tableLayout.visibility = View.VISIBLE

                val hasResults = tests.any { viewModel.repository.hasResultsForTest(it.testId) }
                if (!hasResults) {
                    binding.emptyClassListStats.text = "Lớp này chưa có học sinh làm bài kiểm tra nào."
                    tableLayout.visibility = View.GONE
                    binding.emptyClassListStats.visibility = View.VISIBLE
                } else {
                    if (results.isEmpty()) {
                        binding.emptyClassListStats.text = "Không có kết quả nào để hiển thị."
                        tableLayout.visibility = View.GONE
                        binding.emptyClassListStats.visibility = View.VISIBLE
                    } else {
                        tableLayout.visibility = View.VISIBLE
                        binding.emptyClassListStats.visibility = View.GONE
                        for (result in results) {
                            val row = TableRow(this@StatisticalClassActivity)
                            row.layoutParams = TableLayout.LayoutParams(
                                TableLayout.LayoutParams.MATCH_PARENT,
                                TableLayout.LayoutParams.WRAP_CONTENT
                            )
                            row.setPadding(8, 8, 8, 8)

                            val studentName = viewModel.repository.getStudentName(result.studentId) ?: "Unknown"
                            val className = viewModel.repository.getClassName(result.studentId) ?: "Unknown"
                            val testName = viewModel.repository.getTestName(result.testId) ?: "Unknown"

                            val nameTextView = TextView(this@StatisticalClassActivity)
                            nameTextView.text = studentName
                            nameTextView.textSize = 16f
                            nameTextView.setTextColor(resources.getColor(R.color.black))
                            row.addView(nameTextView)

                            val classTextView = TextView(this@StatisticalClassActivity)
                            classTextView.text = className
                            classTextView.textSize = 16f
                            classTextView.setTextColor(resources.getColor(R.color.black))
                            classTextView.gravity = android.view.Gravity.CENTER
                            row.addView(classTextView)

                            val testTextView = TextView(this@StatisticalClassActivity)
                            testTextView.text = testName
                            testTextView.textSize = 16f
                            testTextView.setTextColor(resources.getColor(R.color.black))
                            testTextView.gravity = android.view.Gravity.CENTER
                            row.addView(testTextView)

                            val scoreTextView = TextView(this@StatisticalClassActivity)
                            scoreTextView.text = result.score.toString()
                            scoreTextView.textSize = 16f
                            scoreTextView.setTextColor(resources.getColor(R.color.black))
                            scoreTextView.gravity = android.view.Gravity.END
                            row.addView(scoreTextView)

                            tableLayout.addView(row)
                        }
                    }
                }
            }
        }
    }
}