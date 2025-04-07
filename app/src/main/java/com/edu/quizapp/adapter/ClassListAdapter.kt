package com.edu.quizapp.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.edu.quizapp.R
import com.edu.quizapp.data.models.Classes

class ClassListAdapter(private val classes: List<Classes>, private val onItemClick: (Classes) -> Unit) :
    RecyclerView.Adapter<ClassListAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val classIcon: ImageView = view.findViewById(R.id.class_icon)
        val className: TextView = view.findViewById(R.id.class_name)
        val classDescription: TextView = view.findViewById(R.id.class_description)
        val detailsButton: Button = view.findViewById(R.id.details_button)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.class_list_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val classItem = classes[position]
        holder.className.text = classItem.className
        holder.classDescription.text = "Mô tả lớp học"
        holder.detailsButton.setOnClickListener {
            onItemClick(classItem)
        }
    }

    override fun getItemCount() = classes.size
}