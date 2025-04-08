package com.edu.quizapp.ui.teacher.dashboard.test

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.edu.quizapp.data.models.Test
import com.edu.quizapp.databinding.ActivityTestDetailBinding
import com.google.firebase.firestore.FirebaseFirestore

class TestDetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityTestDetailBinding
    private val db = FirebaseFirestore.getInstance()

    companion object {
        const val REQUEST_CODE_TEST_DETAIL = 123
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTestDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val testId = intent.getStringExtra("testId")

        if (testId != null) {
            loadTestData(testId)
        } else {
            // Xử lý lỗi nếu testId là null
        }

        binding.backButton.setOnClickListener {
            finish()
        }
    }

    private fun loadTestData(testId: String) {
        db.collection("tests").document(testId)
            .get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    val test = document.toObject(Test::class.java)
                    if (test != null) {
                        displayTestData(test)
                    }
                } else {
                    // Xử lý nếu document không tồn tại
                }
            }
            .addOnFailureListener { exception ->
                // Xử lý lỗi khi lấy dữ liệu
                Toast.makeText(this, "Lỗi tải dữ liệu: ${exception.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun displayTestData(test: Test) {
        binding.testName.text = test.testName
        binding.testCode.text = "Mã bài kiểm tra: ${test.testId}"
        binding.teacherName.text = "Giáo viên: ${test.classCode}"
        binding.questionCount.text = "Số lượng câu hỏi: ${test.questionCount}"
        binding.duration.text = "Thời gian làm bài: ${test.duration} phút"
        // Thêm các trường khác nếu cần

        binding.deleteButton.setOnClickListener {
            AlertDialog.Builder(this)
                .setTitle("Xác nhận xóa")
                .setMessage("Bạn có chắc chắn muốn xóa bài kiểm tra này?")
                .setPositiveButton("Xóa") { _, _ ->
                    deleteTestAndQuestions(test.testId, test.questions)
                }
                .setNegativeButton("Hủy", null)
                .show()
        }
    }

    private fun deleteTestAndQuestions(testId: String, questionIds: List<String>) {
        val batch = db.batch()

        // Xóa bài kiểm tra
        val testRef = db.collection("tests").document(testId)
        batch.delete(testRef)

        // Xóa các câu hỏi liên quan
        questionIds.forEach { questionId ->
            val questionRef = db.collection("questions").document(questionId)
            batch.delete(questionRef)
        }

        batch.commit()
            .addOnSuccessListener {
                Toast.makeText(this, "Bài kiểm tra đã được xóa.", Toast.LENGTH_SHORT).show()
                setResult(RESULT_OK) // Gửi kết quả về
                finish()
            }
            .addOnFailureListener { exception ->
                Toast.makeText(this, "Lỗi xóa: ${exception.message}", Toast.LENGTH_SHORT).show()
            }
    }
}