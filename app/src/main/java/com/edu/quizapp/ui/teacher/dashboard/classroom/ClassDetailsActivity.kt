package com.edu.quizapp.ui.teacher.dashboard.classroom

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.edu.quizapp.R
import com.edu.quizapp.data.models.Classes
import com.edu.quizapp.data.models.Test
import com.edu.quizapp.data.models.User
import com.edu.quizapp.data.repository.ClassRepository
import com.edu.quizapp.data.repository.TestRepository
import com.edu.quizapp.data.repository.UserRepository
import com.edu.quizapp.databinding.ActivityClassDetailBinding
import com.edu.quizapp.ui.teacher.dashboard.TeacherDashboardActivity
import com.edu.quizapp.ui.teacher.profile.TeacherProfileActivity
import kotlinx.coroutines.launch

class ClassDetailsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityClassDetailBinding
    private val classRepository = ClassRepository()
    private val userRepository = UserRepository()
    private val testRepository = TestRepository()
    private var currentClass: Classes? = null // Lưu lớp học hiện tại
    private var classId: String? = null // Lưu classId

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityClassDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupBottomNavigationTeacher()

        val classId = intent.getStringExtra("CLASS_ID")
        if (classId != null) {
            loadClassDetails(classId)
        }

        binding.backButton.setOnClickListener {
            finish()
        }

        binding.deleteButton.setOnClickListener {
            currentClass?.classId?.let { classId ->
                deleteClass(classId) // Gọi deleteClass() với classId
            }
        }

        binding.editButton.setOnClickListener {
            currentClass?.let {
                val intent = Intent(this, EditClassActivity::class.java)
                intent.putExtra("CLASS_ID", it.classId)
                startActivity(intent)
            }
        }
    }

    override fun onResume() {
        super.onResume()
        classId?.let { loadClassDetails(it) } // Tải lại dữ liệu khi resume
    }

    private fun loadClassDetails(classId: String) {
        lifecycleScope.launch {
            try {
                val classes = classRepository.getClassById(classId)
                if (classes != null) {
                    currentClass = classes
                    displayClassDetails(classes)
                } else {
                    Log.e("ClassDetailsActivity", "Class not found")
                }
            } catch (e: Exception) {
                Log.e("ClassDetailsActivity", "Error loading class details: ${e.message}")
            }
        }
    }

    private fun displayClassDetails(classes: Classes) {
        binding.className.text = classes.className
        binding.classCode.text = "Mã lớp: ${classes.classCode}"

        lifecycleScope.launch {
            try {
                val teacher = userRepository.getUserById(classes.teacherId)
                binding.teacherName.text = "Giáo viên: ${teacher?.name ?: "Không xác định"}"
            } catch (e: Exception) {
                Log.e("ClassDetailsActivity", "Error loading teacher name: ${e.message}")
            }
        }

        binding.studentCount.text = "Số lượng học sinh: ${classes.students.size}"

        lifecycleScope.launch {
            try {
                val tests = testRepository.getTestsByClassId(classes.classId)
                binding.testCount.text = "Số lượng bài kiểm tra hiện có: ${tests.size}"
            } catch (e: Exception) {
                Log.e("ClassDetailsActivity", "Error loading test count: ${e.message}")
            }
        }
    }

    private fun setupBottomNavigationTeacher() {
        binding.bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {

                R.id.navigation_home -> {
                    val intent = Intent(this, TeacherDashboardActivity::class.java)
                    startActivity(intent)
                    true
                }
                R.id.navigation_profile -> {
                    val intent = Intent(this, TeacherProfileActivity::class.java)
                    startActivity(intent)
                    true
                }
                else -> false
            }
        }
    }

    private fun deleteClass(classId: String) { // Nhận classId làm tham số
        AlertDialog.Builder(this)
            .setTitle("Xác nhận xóa lớp")
            .setMessage("Bạn có chắc chắn muốn xóa lớp này?")
            .setPositiveButton("Xóa") { _, _ ->
                // Người dùng nhấn "Xóa", tiến hành xóa lớp
                lifecycleScope.launch {
                    try {
                        val success = classRepository.deleteClass(classId)
                        if (success) {
                            Toast.makeText(this@ClassDetailsActivity, "Xóa lớp thành công", Toast.LENGTH_SHORT).show()
                            finish() // Quay lại màn hình trước đó
                        } else {
                            Toast.makeText(this@ClassDetailsActivity, "Xóa lớp thất bại", Toast.LENGTH_SHORT).show()
                        }
                    } catch (e: Exception) {
                        Log.e("EditClassActivity", "Error deleting class: ${e.message}")
                        Toast.makeText(this@ClassDetailsActivity, "Xóa lớp thất bại", Toast.LENGTH_SHORT).show()
                    }
                }
            }
            .setNegativeButton("Hủy", null) // Người dùng nhấn "Hủy", không làm gì cả
            .show()
    }
}