package com.edu.quizapp.ui.teacher.dashboard.analystic.bytest

import android.graphics.Color
import android.graphics.Typeface
import android.view.Gravity
import android.os.Bundle
import android.util.Log
import android.widget.TableLayout
import android.widget.TableRow
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.edu.quizapp.data.models.Result
import com.edu.quizapp.databinding.ActivityStatisticalTestBinding

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

        // Lấy tên môn học từ intent
        val testName = intent.getStringExtra("testName") ?: "Bài kiểm tra"

        // Cập nhật tiêu đề
        binding.testTitle.text = "Thống kê điểm theo bài kiểm tra: $testName"

        // Gọi fetchResultsAndStudents một lần duy nhất
        viewModel.fetchResultsAndStudents(testId)

        viewModel.results.observe(this) { results ->
            Log.d("StatisticalTestActivity", "Results received: ${results.size}")
            // Gọi displayResultsTable chỉ khi viewModel.students.value không null
            if (viewModel.students.value != null) {
                displayResultsTable(results)
            } else {
                Log.w("StatisticalTestActivity", "Students data is not available yet.")
                // Có thể hiển thị thông báo "Đang tải dữ liệu..." hoặc xử lý khác
            }
        }

        viewModel.students.observe(this) { students ->
            Log.d("StatisticalTestActivity", "Students received: ${students.size}")
            students.forEach { (uid, student) ->
                Log.d("StatisticalTestActivity", "Student UID: $uid, Name: ${student.name}")
            }

            // In ra tất cả key và value trong viewModel.students.value
            students.forEach { (uid, student) ->
                Log.d("StatisticalTestActivity", "Student Map: Key: $uid, Value: $student")
            }

            // Gọi displayResultsTable sau khi students được load
            if (viewModel.results.value != null) {
                displayResultsTable(viewModel.results.value!!)
            }
        }

        binding.buttonBack.setOnClickListener {
            finish()
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
}