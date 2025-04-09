package com.edu.quizapp.adapter.student.classes

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.edu.quizapp.R
import com.edu.quizapp.data.models.Test

class StudentTestListAdapter(private var tests: List<Test>) : RecyclerView.Adapter<StudentTestListAdapter.TestViewHolder>() {

    class TestViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val testName: TextView = itemView.findViewById(R.id.testName)
        val testClass: TextView = itemView.findViewById(R.id.testClass)
        val questionCount: TextView = itemView.findViewById(R.id.questionCount)
        val testDuration: TextView = itemView.findViewById(R.id.testDuration)
        val testStatus: TextView = itemView.findViewById(R.id.testStatus) // Thêm TextView trạng thái
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

        // Hiển thị trạng thái hoàn thành
        if (test.isCompleted) {
            holder.testStatus.text = "Trạng thái: Đã hoàn thành"
            holder.testStatus.setTextColor(android.graphics.Color.GREEN) // Màu xanh lá cây cho đã hoàn thành
        } else {
            holder.testStatus.text = "Trạng thái: Chưa hoàn thành"
            holder.testStatus.setTextColor(android.graphics.Color.RED) // Màu đỏ cho chưa hoàn thành
        }

        Log.d("AdapterBind", "Bind at position $position: ${test.testName}")
    }

    override fun getItemCount() = tests.size

    fun updateData(newTests: List<Test>) {
        tests = newTests
        notifyDataSetChanged()
    }
}