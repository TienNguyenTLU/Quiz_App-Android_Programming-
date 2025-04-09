package com.edu.quizapp.adapter.student.classes

import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.edu.quizapp.R
import com.edu.quizapp.data.models.Classes
import com.edu.quizapp.data.repository.TeacherRepository
import com.edu.quizapp.ui.student.dashboard.classes.StudentClassDetailActivity // Import ClassDetailActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class StudentClassAdapter(
    private var classes: List<Classes>,
    private val teacherRepository: TeacherRepository
) : RecyclerView.Adapter<StudentClassAdapter.StudentClassViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StudentClassViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_class_student, parent, false)
        return StudentClassViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: StudentClassViewHolder, position: Int) {
        val classItem = classes[position]
        holder.bind(classItem)
    }

    override fun getItemCount(): Int = classes.size

    fun updateClasses(newClasses: List<Classes>) {
        classes = newClasses
        notifyDataSetChanged()
    }

    inner class StudentClassViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val classNameTextView: TextView = itemView.findViewById(R.id.class_name)
        private val classCodeTextView: TextView = itemView.findViewById(R.id.class_code)
        private val teacherNameTextView: TextView = itemView.findViewById(R.id.teacher_name)
        private val classImageView: ImageView = itemView.findViewById(R.id.class_image)

        fun bind(classItem: Classes) {
            classNameTextView.text = "Lớp: ${classItem.className}"
            classCodeTextView.text = "Mã lớp: ${classItem.classCode}"

            CoroutineScope(Dispatchers.IO).launch {
                try {
                    val teacher = teacherRepository.getTeacherById(classItem.teacherId)
                    withContext(Dispatchers.Main) {
                        teacherNameTextView.text = "Giáo viên: ${teacher?.name ?: "Không xác định"}"
                    }
                } catch (e: Exception) {
                    withContext(Dispatchers.Main) {
                        teacherNameTextView.text = "Giáo viên: Lỗi"
                        Log.e("StudentClassAdapter", "Lỗi khi lấy tên giáo viên: ${e.message}")
                    }
                }
            }

            if (classItem.classImageUrl.isNullOrEmpty()) {
                classImageView.setImageResource(R.drawable.ic_math)
            } else {
                Glide.with(itemView.context)
                    .load(classItem.classImageUrl)
                    .placeholder(R.drawable.ic_math)
                    .error(R.drawable.ic_math)
                    .into(classImageView)
            }

            // Thêm click listener vào item
            itemView.setOnClickListener {
                val intent = Intent(itemView.context, StudentClassDetailActivity::class.java)
                intent.putExtra("classId", classItem.classId)
                itemView.context.startActivity(intent)
            }
        }
    }
}