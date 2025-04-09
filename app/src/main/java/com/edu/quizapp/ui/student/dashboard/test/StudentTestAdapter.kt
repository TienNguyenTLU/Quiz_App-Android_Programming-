package com.edu.quizapp.ui.student.dashboard.test

import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.edu.quizapp.R
import com.edu.quizapp.data.models.Test
import com.edu.quizapp.data.repository.ResultRepository
import com.edu.quizapp.data.repository.QuizRepository
import com.edu.quizapp.ui.student.quiz.QuizActivity
import com.edu.quizapp.ui.student.result.QuizResultActivity
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class StudentTestAdapter(private var tests: List<Test>) : RecyclerView.Adapter<StudentTestAdapter.TestViewHolder>() {

    private val resultRepository = ResultRepository()
    private val quizRepository = QuizRepository()
    private val currentUserId = FirebaseAuth.getInstance().currentUser?.uid

    class TestViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val testName: TextView = itemView.findViewById(R.id.testName)
        val testClass: TextView = itemView.findViewById(R.id.testClass)
        val questionCount: TextView = itemView.findViewById(R.id.questionCount)
        val testDuration: TextView = itemView.findViewById(R.id.testDuration)
        val testStatus: TextView = itemView.findViewById(R.id.testStatus)
        val startTestButton: Button = itemView.findViewById(R.id.startTestButton)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TestViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.test_list_item_student, parent, false)
        return TestViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: TestViewHolder, position: Int) {
        val test = tests[position]
        holder.testName.text = "Tên bài kiểm tra: ${test.testName}"
        holder.testClass.text = "Mã lớp: ${test.classCode}"
        holder.questionCount.text = "Số câu: ${test.questionCount}"
        holder.testDuration.text = "Thời lượng: ${test.duration}p"

        // Kiểm tra trạng thái hoàn thành dựa trên TestAttempt
        CoroutineScope(Dispatchers.Main).launch {
            try {
                Log.d("StudentTestAdapter", "Checking completion status for test: ${test.testId}")
                val attempts = currentUserId?.let { userId ->
                    Log.d("StudentTestAdapter", "Getting attempts for user: $userId")
                    quizRepository.getTestAttemptsByStudentAndTest(userId, test.testId)
                } ?: run {
                    Log.e("StudentTestAdapter", "No current user ID found")
                    emptyList()
                }

                val isCompleted = attempts.any { it.isCompleted }
                Log.d("StudentTestAdapter", "Test ${test.testId} completion status: $isCompleted")
                
                if (isCompleted) {
                    holder.testStatus.text = "Trạng thái: Đã hoàn thành"
                    holder.testStatus.setTextColor(android.graphics.Color.GREEN)
                    holder.startTestButton.text = "Xem kết quả"
                } else {
                    holder.testStatus.text = "Trạng thái: Chưa hoàn thành"
                    holder.testStatus.setTextColor(android.graphics.Color.RED)
                    holder.startTestButton.text = "Bắt đầu làm bài"
                }

                // Xử lý sự kiện nhấp vào nút
                holder.startTestButton.setOnClickListener {
                    val context = holder.itemView.context
                    if (isCompleted) {
                        // Tìm kết quả của học sinh cho bài kiểm tra này
                        CoroutineScope(Dispatchers.Main).launch {
                            try {
                                val results = resultRepository.getResultsByStudentAndTest(currentUserId!!, test.testId)
                                if (results.isNotEmpty()) {
                                    val result = results.first()
                                    // Mở màn hình xem kết quả
                                    val intent = Intent(context, QuizResultActivity::class.java).apply {
                                        putExtra("RESULT_ID", result.resultId)
                                        putExtra("TEST_ID", test.testId)
                                    }
                                    context.startActivity(intent)
                                }
                            } catch (e: Exception) {
                                Log.e("TestAdapter", "Error fetching results: ${e.message}")
                            }
                        }
                    } else {
                        // Bắt đầu bài kiểm tra
                        val intent = Intent(context, QuizActivity::class.java).apply {
                            putExtra("TEST_ID", test.testId)
                        }
                        context.startActivity(intent)
                    }
                }
            } catch (e: Exception) {
                Log.e("StudentTestAdapter", "Error checking test status: ${e.message}")
                Log.e("StudentTestAdapter", "Stack trace: ${e.stackTraceToString()}")
                holder.testStatus.text = "Trạng thái: Lỗi"
                holder.testStatus.setTextColor(android.graphics.Color.RED)
            }
        }

        Log.d("AdapterBind", "Bind at position $position: ${test.testName}")
    }

    override fun getItemCount() = tests.size

    fun updateData(newTests: List<Test>) {
        tests = newTests
        notifyDataSetChanged()
    }
} 