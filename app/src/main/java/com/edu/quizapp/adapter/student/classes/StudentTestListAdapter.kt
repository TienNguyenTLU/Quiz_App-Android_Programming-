package com.edu.quizapp.adapter.student.classes

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
import com.edu.quizapp.databinding.ItemStudentTestBinding
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class StudentTestListAdapter(private var tests: List<Test>) :
    RecyclerView.Adapter<StudentTestListAdapter.TestViewHolder>() {

    private val resultRepository = ResultRepository()
    private val quizRepository = QuizRepository()
    private val currentUserId = FirebaseAuth.getInstance().currentUser?.uid
    private var onItemClick: ((Test) -> Unit)? = null

    fun setOnItemClickListener(listener: (Test) -> Unit) {
        onItemClick = listener
    }

    fun updateData(newTests: List<Test>) {
        tests = newTests
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TestViewHolder {
        val binding = ItemStudentTestBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return TestViewHolder(binding)
    }

    override fun onBindViewHolder(holder: TestViewHolder, position: Int) {
        holder.bind(tests[position])
    }

    override fun getItemCount(): Int = tests.size

    inner class TestViewHolder(private val binding: ItemStudentTestBinding) :
        RecyclerView.ViewHolder(binding.root) {

        init {
            itemView.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    onItemClick?.invoke(tests[position])
                }
            }
        }

        fun bind(test: Test) {
            binding.apply {
                testTitle.text = test.testName
                testDescription.text = "Class Code: ${test.classCode}"
                testDuration.text = "${test.duration} minutes"
                testTotalQuestions.text = "${test.questionCount} questions"
                
                // Kiểm tra trạng thái hoàn thành dựa trên TestAttempt
                CoroutineScope(Dispatchers.Main).launch {
                    try {
                        val attempts = currentUserId?.let { userId ->
                            quizRepository.getTestAttemptsByStudentAndTest(userId, test.testId)
                        } ?: emptyList()

                        val isCompleted = attempts.any { it.isCompleted }
                        testStartTime.text = "Status: ${if (isCompleted) "Completed" else "Not Completed"}"
                        testEndTime.visibility = View.GONE // Hide the end time since we don't have it
                    } catch (e: Exception) {
                        Log.e("TestListAdapter", "Error checking test status: ${e.message}")
                        testStartTime.text = "Status: Unknown"
                    }
                }
            }
        }
    }
}