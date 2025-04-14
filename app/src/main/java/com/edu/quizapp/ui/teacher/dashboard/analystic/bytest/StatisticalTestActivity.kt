package com.edu.quizapp.ui.teacher.dashboard.analystic.bytest

import android.app.AlertDialog
import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.Gravity
import android.view.View
import android.widget.EditText
import android.widget.TableLayout
import android.widget.TableRow
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.edu.quizapp.R
import com.edu.quizapp.data.models.Result
import com.edu.quizapp.databinding.ActivityStatisticalTestBinding
import com.opencsv.CSVWriter
import java.io.File
import java.io.FileWriter
import java.io.IOException

class StatisticalTestActivity : AppCompatActivity() {

    private lateinit var binding: ActivityStatisticalTestBinding
    private lateinit var viewModel: StatisticalTestViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityStatisticalTestBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel = ViewModelProvider(this)[StatisticalTestViewModel::class.java]

        val testId = intent.getStringExtra("testId") ?: run {
            Log.e("StatisticalTestActivity", "testId is null")
            finish()
            return
        }

        val testName = intent.getStringExtra("testName") ?: "Bài kiểm tra"
        binding.testTitle.text = "Thống kê điểm theo bài kiểm tra: $testName"

        viewModel.fetchResultsAndStudents(testId)

        viewModel.results.observe(this) { results ->
            if (viewModel.students.value != null) {
                displayResultsTable(results)
            } else {
                Log.w("StatisticalTestActivity", "Students data is not available yet.")
            }
        }

        viewModel.students.observe(this) { students ->
            if (viewModel.results.value != null) {
                displayResultsTable(viewModel.results.value!!)
            }
        }

        binding.buttonBack.setOnClickListener {
            finish()
        }

        binding.exportCsvButton.setOnClickListener {
            showFileNameDialog()
        }
    }

    private fun displayResultsTable(results: List<Result>) {
        val tableLayout = binding.scoresTable
        tableLayout.removeAllViews()

        val headerRow = TableRow(this)
        val headerStudentName = TextView(this).apply {
            text = "Học sinh"
            textSize = 16f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            setTextColor(Color.parseColor("#333333"))
            layoutParams = TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 1f)
        }
        val headerStudentScore = TextView(this).apply {
            text = "Điểm"
            textSize = 16f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            setTextColor(Color.parseColor("#333333"))
            gravity = Gravity.END
            minWidth = 60
        }
        headerRow.addView(headerStudentName)
        headerRow.addView(headerStudentScore)
        tableLayout.addView(headerRow)

        var totalScore = 0.0
        var passedStudents = 0

        for (result in results) {
            val row = TableRow(this)
            val studentName = TextView(this).apply {
                text = viewModel.students.value?.get(result.studentId)?.name ?: "Unknown"
                textSize = 16f
                setTextColor(Color.parseColor("#333333"))
                layoutParams = TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 1f)
            }
            val studentScore = TextView(this).apply {
                text = result.score.toString()
                textSize = 16f
                setTextColor(Color.parseColor("#333333"))
                gravity = Gravity.END
            }

            row.addView(studentName)
            row.addView(studentScore)
            tableLayout.addView(row)

            totalScore += result.score
            if (result.score >= 4) {
                passedStudents++
            }
        }

        val averageScore = if (results.isNotEmpty()) totalScore / results.size else 0.0

        binding.averageScore.text = String.format("%.1f", averageScore)
        binding.passedStudentsCount.text = "$passedStudents/${results.size}"
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

    private fun exportDataToCSV(fileName: String) {
        try {
            Log.d("ExportCSV", "Exporting data to CSV")
            val downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
            val csvFile = File(downloadsDir, "$fileName.csv")
            val writer = CSVWriter(FileWriter(csvFile))

            val tableLayout = binding.scoresTable
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