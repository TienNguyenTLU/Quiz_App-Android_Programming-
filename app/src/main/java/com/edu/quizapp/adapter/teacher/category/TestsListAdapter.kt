package com.edu.quizapp.adapter.teacher.category

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.edu.quizapp.R
import com.edu.quizapp.data.models.Test

class TestsListAdapter(private var tests: List<Test>) :
    RecyclerView.Adapter<TestsListAdapter.TestsViewHolder>() {

    class TestsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val testNameTextView: TextView = itemView.findViewById(R.id.test_name_text_view)

        fun bind(test: Test) {
            testNameTextView.text = test.testName
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TestsViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_tests, parent, false)
        return TestsViewHolder(view)
    }

    override fun onBindViewHolder(holder: TestsViewHolder, position: Int) {
        holder.bind(tests[position])
    }

    override fun getItemCount(): Int = tests.size

    fun updateTests(newTests: List<Test>) {
        tests = newTests
        notifyDataSetChanged()
    }
}