package com.edu.quizapp.adapter.teacher.category

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.edu.quizapp.R
import com.edu.quizapp.data.models.Category

class CategoryListAdapter(
    private var categories: List<Category>,
    private val onClassesClick: (Category) -> Unit,
    private val onTestsClick: (Category) -> Unit
) : RecyclerView.Adapter<CategoryListAdapter.CategoryViewHolder>() {

    inner class CategoryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val categoryNameTextView: TextView = itemView.findViewById(R.id.category_name_text_view)
        val viewClassesButton: Button = itemView.findViewById(R.id.view_classes_button)
        val viewTestsButton: Button = itemView.findViewById(R.id.view_tests_button)

        fun bind(category: Category) {
            categoryNameTextView.text = category.categoryName
            viewClassesButton.setOnClickListener { onClassesClick(category) }
            viewTestsButton.setOnClickListener { onTestsClick(category) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_category, parent, false)
        return CategoryViewHolder(view)
    }

    override fun onBindViewHolder(holder: CategoryViewHolder, position: Int) {
        holder.bind(categories[position])
    }

    override fun getItemCount(): Int = categories.size

    fun updateCategories(newCategories: List<Category>) {
        categories = newCategories
        notifyDataSetChanged()
    }
}