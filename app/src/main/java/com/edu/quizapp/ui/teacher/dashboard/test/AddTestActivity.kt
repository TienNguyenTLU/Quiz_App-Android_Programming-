// AddTestActivity.kt
package com.edu.quizapp.ui.teacher.dashboard.test

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.edu.quizapp.R
import com.edu.quizapp.data.models.Test
import com.edu.quizapp.databinding.ActivityAddTestBinding
import com.edu.quizapp.ui.teacher.dashboard.TeacherDashboardActivity
import com.edu.quizapp.ui.teacher.profile.TeacherProfileActivity
import java.util.UUID

class AddTestActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddTestBinding
    private lateinit var viewModel: AddTestViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddTestBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel = ViewModelProvider(this)[AddTestViewModel::class.java]

        setupListeners()
        setupBottomNavigationTeacher()
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

    private fun setupListeners() {
        binding.backButton.setOnClickListener {
            finish()
        }

        binding.nextButton.setOnClickListener {
            val testName = binding.testNameEditText.text.toString().trim()
            val testCode = binding.testCodeEditText.text.toString().trim()
            val subject = binding.subjectEditText.text.toString().trim()
            val classCode = binding.classCodeEditText.text.toString().trim()
            val durationStr = binding.durationEditText.text.toString().trim()

            if (testName.isEmpty() || testCode.isEmpty() || subject.isEmpty() || classCode.isEmpty() || durationStr.isEmpty()) {
                Toast.makeText(this, "Vui lòng nhập đầy đủ thông tin.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val duration = durationStr.toLongOrNull() ?: 0

            val testId = UUID.randomUUID().toString()

            val test = Test(
                testId = testId,
                testName = testName,
                classCode = classCode,
                questions = emptyList(),
                duration = duration
            )

            viewModel.addTest(test) { success, message ->
                if (success) {
                    val intent = Intent(this@AddTestActivity, CreateQuestionsFromFileActivity::class.java)
                    intent.putExtra("testId", testId)
                    startActivityForResult(intent, 123)
                } else {
                    Toast.makeText(this@AddTestActivity, message, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 123) {
            if (resultCode == Activity.RESULT_OK) {
                val allQuestionsSaved = data?.getBooleanExtra("allQuestionsSaved", false) ?: false
                if (allQuestionsSaved) {
                    val intent = Intent(this, AddTestSuccessActivity::class.java)
                    startActivity(intent)
                } else {
                    Toast.makeText(this, "Lỗi lưu câu hỏi. Vui lòng kiểm tra lại.", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, "Hủy bỏ tạo câu hỏi.", Toast.LENGTH_SHORT).show()
            }
        }
    }
}