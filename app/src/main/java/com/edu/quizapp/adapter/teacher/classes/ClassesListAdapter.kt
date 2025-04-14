package com.edu.quizapp.adapter.teacher.classes

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.edu.quizapp.R
import com.edu.quizapp.data.models.Classes

class ClassesListAdapter(
    private var classes: List<Classes>,
    private val onDetailsClick: (Classes) -> Unit // Listener cho nút chi tiết
) :
    RecyclerView.Adapter<ClassesListAdapter.ClassesViewHolder>() {

    class ClassesViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val classNameTextView: TextView = itemView.findViewById(R.id.class_name)
        val detailsButton: ImageButton = itemView.findViewById(R.id.details_button)

        fun bind(classes: Classes) {
            Log.d("ClassesListAdapter", "Binding class: ${classes.className}")
            classNameTextView.text = classes.className
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ClassesViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.class_list_item, parent, false)
        return ClassesViewHolder(view)
    }

    override fun onBindViewHolder(holder: ClassesViewHolder, position: Int) {
        Log.d("ClassesListAdapter", "Binding position: $position")
        try {
            holder.bind(classes[position])
            holder.detailsButton.setOnClickListener { // Xử lý click nút chi tiết
                onDetailsClick(classes[position])
            }
        } catch (e: Exception) {
            Log.e("ClassesListAdapter", "Error binding position $position: ${e.message}")
        }
    }

    override fun getItemCount(): Int = classes.size

    fun updateClasses(newClasses: List<Classes>) {
        classes = newClasses
        notifyDataSetChanged()
    }
}