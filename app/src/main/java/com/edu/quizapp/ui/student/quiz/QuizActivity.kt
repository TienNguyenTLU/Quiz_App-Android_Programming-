package com.edu.quizapp.ui.student.quiz

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.edu.quizapp.R
import com.edu.quizapp.databinding.ActivityQuizBinding
import com.edu.quizapp.ui.student.result.QuizResultActivity
import com.google.firebase.auth.FirebaseAuth

class QuizActivity : AppCompatActivity() {

    private lateinit var binding: ActivityQuizBinding
    private lateinit var viewModel: QuizViewModel

    private lateinit var questionTextView: TextView
    private lateinit var questionNumberTextView: TextView
    private lateinit var timerTextView: TextView
    private lateinit var optionsRadioGroup: RadioGroup
    private lateinit var nextButton: Button
    private lateinit var submitButton: Button

    private var testId: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityQuizBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel = ViewModelProvider(this).get(QuizViewModel::class.java)

        testId = intent.getStringExtra("TEST_ID") ?: ""
        if (testId.isEmpty()) {
            finish()
            return
        }

        initViews()
        setupObservers()

        // Bắt đầu làm bài
        val currentUser = FirebaseAuth.getInstance().currentUser
        currentUser?.uid?.let { uid ->
            viewModel.startQuiz(testId, uid)
            viewModel.loadTest(testId)
        }
    }

    private fun initViews() {
        questionTextView = binding.questionText
        questionNumberTextView = binding.questionNumber
        timerTextView = binding.timerText
        optionsRadioGroup = binding.optionsRadioGroup
        nextButton = binding.nextButton
        submitButton = binding.submitButton

        binding.backButton.setOnClickListener {
            showExitConfirmationDialog()
        }

        nextButton.setOnClickListener {
            val selectedRadioButtonId = optionsRadioGroup.checkedRadioButtonId
            if (selectedRadioButtonId != -1) {
                val radioButton = findViewById<RadioButton>(selectedRadioButtonId)
                val answer = radioButton.text.toString()
                viewModel.submitAnswer(answer)
            } else {
                viewModel.skipQuestion()
            }
            optionsRadioGroup.clearCheck()
        }

        submitButton.setOnClickListener {
            showSubmitConfirmationDialog()
        }
    }

    private fun setupObservers() {
        viewModel.currentQuestion.observe(this) { question ->
            questionTextView.text = question.questionText

            // Xóa tất cả các RadioButton hiện tại
            optionsRadioGroup.removeAllViews()

            // Thêm các tùy chọn mới
            question.answers.forEachIndexed { index, answerText ->
                val radioButton = RadioButton(this)
                radioButton.id = View.generateViewId()
                radioButton.text = answerText
                optionsRadioGroup.addView(radioButton)
            }
        }

        viewModel.currentQuestionIndex.observe(this) { index ->
            val totalQuestions = viewModel.questions.value?.size ?: 0
            questionNumberTextView.text = "Câu hỏi ${index + 1}/$totalQuestions"

            // Hiển thị nút Submit nếu đây là câu hỏi cuối cùng
            if (index == totalQuestions - 1) {
                nextButton.visibility = View.GONE
                submitButton.visibility = View.VISIBLE
            } else {
                nextButton.visibility = View.VISIBLE
                submitButton.visibility = View.GONE
            }
        }

        viewModel.timeRemaining.observe(this) { timeInSeconds ->
            val minutes = timeInSeconds / 60
            val seconds = timeInSeconds % 60
            timerTextView.text = String.format("%02d:%02d", minutes, seconds)
        }

        viewModel.navigateToResult.observe(this) { shouldNavigate ->
            if (shouldNavigate) {
                val resultId = viewModel.resultId.value
                navigateToResult(resultId)
            }
        }

        viewModel.isLoading.observe(this) { isLoading ->
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
            binding.quizContent.visibility = if (isLoading) View.GONE else View.VISIBLE
        }
    }

    private fun navigateToResult(resultId: String?) {
        if (resultId != null) {
            val intent = Intent(this, QuizResultActivity::class.java).apply {
                putExtra("RESULT_ID", resultId)
                putExtra("TEST_ID", testId)
            }
            startActivity(intent)
            finish()
        }
    }

    private fun showExitConfirmationDialog() {
        AlertDialog.Builder(this)
            .setTitle("Thoát bài kiểm tra")
            .setMessage("Bạn có chắc chắn muốn thoát? Dữ liệu bài làm có thể bị mất.")
            .setPositiveButton("Thoát") { _, _ ->
                finish()
            }
            .setNegativeButton("Hủy", null)
            .show()
    }

    private fun showSubmitConfirmationDialog() {
        AlertDialog.Builder(this)
            .setTitle("Nộp bài")
            .setMessage("Bạn có chắc chắn muốn nộp bài?")
            .setPositiveButton("Nộp bài") { _, _ ->
                viewModel.finishQuiz()
            }
            .setNegativeButton("Hủy", null)
            .show()
    }

    override fun onBackPressed() {
        showExitConfirmationDialog()
    }
}
