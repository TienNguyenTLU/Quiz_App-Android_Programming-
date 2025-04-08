package com.edu.quizapp.ui.teacher.dashboard.test

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.edu.quizapp.R
import com.edu.quizapp.data.models.Question
import com.edu.quizapp.databinding.ActivityCreateQuestionsFromFileBinding
import com.edu.quizapp.ui.teacher.dashboard.TeacherDashboardActivity
import com.edu.quizapp.ui.teacher.profile.TeacherProfileActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.BufferedReader
import java.io.InputStreamReader
import java.util.UUID

class CreateQuestionsFromFileActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCreateQuestionsFromFileBinding
    private lateinit var viewModel: CreateQuestionsFromFileViewModel
    private var selectedFileUri: Uri? = null
    private var testId: String? = null

    companion object {
        private const val FILE_PICKER_REQUEST_CODE = 100
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCreateQuestionsFromFileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel = ViewModelProvider(this)[CreateQuestionsFromFileViewModel::class.java]
        testId = intent.getStringExtra("testId")

        setupListeners()
        setupBottomNavigationTeacher()
    }

    private fun setupListeners() {
        binding.selectFileButton.setOnClickListener {
            val intent = Intent(Intent.ACTION_GET_CONTENT).apply {
                type = "*/*"
            }
            startActivityForResult(intent, FILE_PICKER_REQUEST_CODE)
        }

        binding.backToAddTestButton.setOnClickListener {
            finish()
        }

        binding.createQuestionsButton.setOnClickListener {
            val questionCountStr = binding.questionCountEditText.text.toString().trim()
            if (questionCountStr.isEmpty()) {
                Toast.makeText(this, "Vui lòng nhập số lượng câu hỏi.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val questionCount = questionCountStr.toIntOrNull()
            if (questionCount == null || questionCount <= 0) {
                Toast.makeText(this, "Số lượng câu hỏi không hợp lệ.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (selectedFileUri == null) {
                Toast.makeText(this, "Vui lòng chọn file đề thi.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            testId?.let { testId ->
                GlobalScope.launch(Dispatchers.IO) {
                    readQuestionsFromFile(selectedFileUri!!, questionCount) { questions ->
                        GlobalScope.launch(Dispatchers.Main) {
                            viewModel.saveQuestions(questions) { success, message ->
                                if (success) {
                                    // Chuyển sang AddTestSuccessActivity khi thành công
                                    val intent = Intent(this@CreateQuestionsFromFileActivity, AddTestSuccessActivity::class.java)
                                    startActivity(intent)
                                    finish() // Kết thúc CreateQuestionsFromFileActivity
                                } else {
                                    Toast.makeText(this@CreateQuestionsFromFileActivity, message, Toast.LENGTH_SHORT).show()
                                }
                            }
                        }
                    }
                }
            } ?: run {
                Toast.makeText(this, "Không tìm thấy thông tin bài kiểm tra.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == FILE_PICKER_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            selectedFileUri = data?.data
            binding.filePathTextView.text = selectedFileUri?.path ?: "File đã chọn"
        }
    }

    private suspend fun readQuestionsFromFile(fileUri: Uri, questionCount: Int, callback: (List<Question>) -> Unit) {
        withContext(Dispatchers.IO) {
            try {
                contentResolver.openInputStream(fileUri)?.use { inputStream ->
                    val reader = BufferedReader(InputStreamReader(inputStream))
                    val lines = reader.readLines()
                    val questions = parseQuestionsFromLines(lines, questionCount)
                    withContext(Dispatchers.Main) {
                        callback(questions)
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@CreateQuestionsFromFileActivity, "Lỗi đọc file: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun parseQuestionsFromLines(lines: List<String>, questionCount: Int): List<Question> {
        val questions = mutableListOf<Question>()
        val randomLines = lines.shuffled().take(questionCount)

        randomLines.forEach { line ->
            val parts = line.split(",")
            if (parts.size >= 3) {
                val questionText = parts[0].trim()
                val answers = parts.subList(1, parts.size - 1).map { it.trim() }
                val correctAnswer = parts.last().trim().toLongOrNull() ?: 0

                val question = Question(
                    questionId = UUID.randomUUID().toString(),
                    questionText = questionText,
                    answers = answers,
                    correctAnswer = correctAnswer
                )
                questions.add(question)
            }
        }
        return questions
    }

    private fun setupBottomNavigationTeacher() {
        binding.bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navigation_notifications -> {
                    true
                }
                R.id.navigation_home -> {
                    val intent = Intent(this, TeacherDashboardActivity::class.java)
                    startActivity(intent)
                    true
                }
                R.id.navigation_profile -> {
                    val intent = Intent(this, TeacherProfileActivity::class.java)
                    startActivity(intent)
                    true
                }
                else -> false
            }
        }
    }
}