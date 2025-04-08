package com.edu.quizapp.adapter.teacher.test

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.edu.quizapp.data.models.Test
import com.edu.quizapp.databinding.TestListItemBinding
import com.edu.quizapp.ui.teacher.dashboard.test.TestDetailActivity

class TestListAdapter(
    private var tests: List<Test>,
    private val context: Context,
    private val onDetailsClicked: (Test) -> Unit
) : RecyclerView.Adapter<TestListAdapter.TestViewHolder>() {

    inner class TestViewHolder(val binding: TestListItemBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TestViewHolder {
        val binding = TestListItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return TestViewHolder(binding)
    }

    override fun onBindViewHolder(holder: TestViewHolder, position: Int) {
        val test = tests[position]
        holder.binding.testName.text = test.testName
        holder.binding.detailsButton.setOnClickListener {
            val intent = Intent(context, TestDetailActivity::class.java)
            intent.putExtra("testId", test.testId) // Truyền testId
            context.startActivity(intent)
        }
        // Thêm các trường khác nếu cần
    }

    override fun getItemCount(): Int {
        return tests.size
    }

    fun updateTests(newTests: List<Test>) {
        tests = newTests
        notifyDataSetChanged()
    }
}