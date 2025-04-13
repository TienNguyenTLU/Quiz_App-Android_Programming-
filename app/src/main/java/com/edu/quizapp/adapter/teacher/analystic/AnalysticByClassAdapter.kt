package com.edu.quizapp.adapter.teacher.analystic

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.edu.quizapp.R
import com.edu.quizapp.data.models.Classes

class AnalysticByClassAdapter(
    private var classes: List<Classes>,
    private val onItemClick: (Classes) -> Unit // Thêm listener
) : RecyclerView.Adapter<AnalysticByClassAdapter.AnalysticByClassViewHolder>() {

    class AnalysticByClassViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val className: TextView = itemView.findViewById(R.id.class_name)
        val classCode: TextView = itemView.findViewById(R.id.class_code)

        fun bind(classes: Classes) {
            className.text = "Lớp: ${classes.className}"
            classCode.text = "Mã lớp: ${classes.classCode}"
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AnalysticByClassViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.analystic_class_item, parent, false)
        return AnalysticByClassViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: AnalysticByClassViewHolder, position: Int) {
        holder.bind(classes[position])
        holder.itemView.setOnClickListener {
            onItemClick(classes[position]) // Gọi listener khi item được click
        }
    }

    override fun getItemCount(): Int = classes.size

    fun updateData(newList: List<Classes>) {
        classes = newList
        notifyDataSetChanged()
    }
}