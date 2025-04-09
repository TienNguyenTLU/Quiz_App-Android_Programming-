package com.edu.quizapp.adapter.student.result

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.edu.quizapp.databinding.ItemStudentResultBinding
import com.edu.quizapp.data.models.QuizResult

class StudentResultAdapter(
    private var results: List<QuizResult>,
    private val onItemClick: (String) -> Unit
) : RecyclerView.Adapter<StudentResultAdapter.ResultViewHolder>() {

    fun updateData(newResults: List<QuizResult>) {
        results = newResults
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ResultViewHolder {
        val binding = ItemStudentResultBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ResultViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ResultViewHolder, position: Int) {
        holder.bind(results[position])
    }

    override fun getItemCount(): Int = results.size

    inner class ResultViewHolder(
        private val binding: ItemStudentResultBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        init {
            binding.root.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    onItemClick(results[position].id)
                }
            }
        }

        fun bind(result: QuizResult) {
            binding.apply {
                testName.text = result.quizName
                testScore.text = "${result.score}%"
                testDuration.text = result.duration
                correctCount.text = result.correctAnswers.toString()
                incorrectCount.text = result.incorrectAnswers.toString()
                skippedCount.text = result.skippedQuestions.toString()
            }
        }
    }
} 