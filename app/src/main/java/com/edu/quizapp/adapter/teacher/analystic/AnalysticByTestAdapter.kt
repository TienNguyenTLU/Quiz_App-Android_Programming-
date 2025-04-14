package com.edu.quizapp.adapter.teacher.analystic

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.edu.quizapp.R
import com.edu.quizapp.data.models.Test
import com.edu.quizapp.data.models.Teacher
import com.edu.quizapp.data.models.Classes

class AnalysticByTestAdapter(
    private var tests: List<Test>,
    private var teachers: Map<String, Teacher>,
    private var classes: Map<String, Classes>,
    private val onItemClick: (Test) -> Unit
) : RecyclerView.Adapter<AnalysticByTestAdapter.AnalysticByTestViewHolder>() {

    inner class AnalysticByTestViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val testName: TextView = itemView.findViewById(R.id.test_name)
        val durationTextView: TextView = itemView.findViewById(R.id.test_date)

        fun bind(test: Test) {
            testName.text = test.testName
            // Hiển thị thời gian làm bài trực tiếp từ Firestore
            durationTextView.text = "Thời gian: ${test.duration} phút"
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AnalysticByTestViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.analystic_test_item, parent, false)
        return AnalysticByTestViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: AnalysticByTestViewHolder, position: Int) {
        holder.bind(tests[position])
        holder.itemView.setOnClickListener {
            onItemClick(tests[position])
        }
    }

    override fun getItemCount(): Int {
        return tests.size
    }

    fun updateData(newTests: List<Test>, newTeachers: Map<String, Teacher>, newClasses: Map<String, Classes>) {
        tests = newTests
        teachers = newTeachers
        classes = newClasses
        notifyDataSetChanged()
    }
}