package com.edu.quizapp.ui.teacher.dashboard.analystic.byclass

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.Settings
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.TableLayout
import android.widget.TableRow
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.edu.quizapp.R
import com.edu.quizapp.data.models.Result
import com.edu.quizapp.databinding.ActivityStatisticalClassBinding
import com.opencsv.CSVWriter
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileWriter

class StatisticalClassActivity : AppCompatActivity() {

    private lateinit var binding: ActivityStatisticalClassBinding
    private lateinit var viewModel: StatisticalClassViewModel
    private var currentClassId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityStatisticalClassBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val classId = intent.getStringExtra("classId") ?: ""
        currentClassId = classId

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

        binding.exportCsvButton.setOnClickListener {
            showFileNameDialog()
        }
    }

    private fun showFileNameDialog() {
        val input = EditText(this)
        input.setText("") // Tên file mặc định

        AlertDialog.Builder(this)
            .setTitle("Nhập tên file CSV")
            .setView(input)
            .setPositiveButton("Xuất") { _, _ ->
                val fileName = input.text.toString()
                exportDataToCSV(fileName)
            }
            .setNegativeButton("Hủy", null)
            .show()
    }

    private fun observeViewModel(classId: String) {
        viewModel.results.observe(this) { results ->
            displayResults(results, classId)
        }

        viewModel.errorMessage.observe(this) { errorMessage ->
            if (!errorMessage.isNullOrEmpty()) {
                Log.e("StatisticalClassActivity", "Error: $errorMessage")
            }
        }

        viewModel.tests.observe(this) { tests ->
            handleTests(tests, classId)
        }
    }

    private fun handleTests(tests: List<com.edu.quizapp.data.models.Test>, classId: String) {
        val tableLayout = binding.tableClassListStats
        val noTestsCard = binding.noTestsCard
        val emptyClassListStats = binding.emptyClassListStats

        if (tests.isEmpty()) {
            noTestsCard.visibility = View.VISIBLE
            tableLayout.visibility = View.GONE
            emptyClassListStats.visibility = View.GONE
            return
        }

        lifecycleScope.launch {
            val hasResults = tests.any { viewModel.repository.hasResultsForTest(it.testId) }
            if (!hasResults) {
                emptyClassListStats.text = "Lớp này chưa có học sinh làm bài kiểm tra nào."
                tableLayout.visibility = View.GONE
                emptyClassListStats.visibility = View.VISIBLE
                noTestsCard.visibility = View.GONE
            } else {
                tableLayout.visibility = View.VISIBLE
                emptyClassListStats.visibility = View.GONE
                noTestsCard.visibility = View.GONE
            }
        }
    }

    private fun displayResults(results: List<Result>, classId: String) {
        val tableLayout = binding.tableClassListStats
        val emptyClassListStats = binding.emptyClassListStats

        if (results.isEmpty()) {
            emptyClassListStats.text = "Không có kết quả nào để hiển thị."
            tableLayout.visibility = View.GONE
            emptyClassListStats.visibility = View.VISIBLE
            return
        }

        if (tableLayout.childCount > 1) {
            tableLayout.removeViews(1, tableLayout.childCount - 1)
        }

        tableLayout.visibility = View.VISIBLE
        emptyClassListStats.visibility = View.GONE

        lifecycleScope.launch {
            for (result in results) {
                val row = TableRow(this@StatisticalClassActivity)
                row.layoutParams = TableLayout.LayoutParams(
                    TableLayout.LayoutParams.MATCH_PARENT,
                    TableLayout.LayoutParams.WRAP_CONTENT
                )
                row.setPadding(8, 8, 8, 8)

                val studentName = viewModel.repository.getStudentName(result.studentId) ?: "Unknown"
                val className = viewModel.repository.getClassName(classId) ?: "Unknown"
                val testName = viewModel.repository.getTestName(result.testId) ?: "Unknown"

                val nameTextView = TextView(this@StatisticalClassActivity).apply {
                    text = studentName
                    textSize = 16f
                    setTextColor(resources.getColor(R.color.black))
                }
                row.addView(nameTextView)

                val classTextView = TextView(this@StatisticalClassActivity).apply {
                    text = className
                    textSize = 16f
                    setTextColor(resources.getColor(R.color.black))
                    gravity = android.view.Gravity.CENTER
                }
                row.addView(classTextView)

                val testTextView = TextView(this@StatisticalClassActivity).apply {
                    text = testName
                    textSize = 16f
                    setTextColor(resources.getColor(R.color.black))
                    gravity = android.view.Gravity.CENTER
                }
                row.addView(testTextView)

                val scoreTextView = TextView(this@StatisticalClassActivity).apply {
                    text = result.score.toString()
                    textSize = 16f
                    setTextColor(resources.getColor(R.color.black))
                    gravity = android.view.Gravity.END
                }
                row.addView(scoreTextView)

                tableLayout.addView(row)
            }
        }
    }

    private fun exportDataToCSV(fileName: String) {
        try {
            Log.d("ExportCSV", "Exporting data to CSV")
            val downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
            val csvFile = File(downloadsDir, "$fileName.csv")
            val writer = CSVWriter(FileWriter(csvFile))

            val tableLayout = binding.tableClassListStats
            for (i in 0 until tableLayout.childCount) {
                val row = tableLayout.getChildAt(i) as TableRow
                val rowData = mutableListOf<String>()
                for (j in 0 until row.childCount) {
                    val textView = row.getChildAt(j) as TextView
                    rowData.add(textView.text.toString())
                }
                writer.writeNext(rowData.toTypedArray())
            }

            writer.close()
            Toast.makeText(this, "Xuất file CSV thành công.", Toast.LENGTH_SHORT).show()
            Log.d("ExportCSV", "CSV export successful")
        } catch (e: Exception) {
            Log.e("ExportCSV", "Error exporting CSV: ${e.message}")
            Toast.makeText(this, "Lỗi xuất file CSV: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }
}