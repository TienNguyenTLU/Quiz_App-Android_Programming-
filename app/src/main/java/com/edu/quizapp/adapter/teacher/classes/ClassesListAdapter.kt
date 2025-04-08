package com.edu.quizapp.adapter.teacher.classes

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.edu.quizapp.R
import com.edu.quizapp.data.models.Classes

class ClassesListAdapter(private var classes: List<Classes>) :
    RecyclerView.Adapter<ClassesListAdapter.ClassesViewHolder>() {

    class ClassesViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val classNameTextView: TextView = itemView.findViewById(R.id.class_name_text_view)

        fun bind(classes: Classes) {
            classNameTextView.text = classes.className
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ClassesViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_classes, parent, false)
        return ClassesViewHolder(view)
    }

    override fun onBindViewHolder(holder: ClassesViewHolder, position: Int) {
        holder.bind(classes[position])
    }

    override fun getItemCount(): Int = classes.size

    fun updateClasses(newClasses: List<Classes>) {
        classes = newClasses
        notifyDataSetChanged()
    }
}