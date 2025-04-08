package com.edu.quizapp.ui.teacher.dashboard.classroom

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.edu.quizapp.R
import com.edu.quizapp.data.models.Classes
import com.edu.quizapp.data.repository.ClassRepository
import com.edu.quizapp.data.repository.StudentRepository
import com.edu.quizapp.databinding.ActivityEditClassBinding
import com.edu.quizapp.ui.teacher.dashboard.TeacherDashboardActivity
import com.edu.quizapp.ui.teacher.profile.TeacherProfileActivity
import kotlinx.coroutines.launch
import androidx.appcompat.app.AlertDialog

class EditClassActivity : AppCompatActivity() {

    private lateinit var binding: ActivityEditClassBinding
    private val classRepository = ClassRepository()
    private val studentRepository = StudentRepository()
    private var classId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditClassBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupBottomNavigationTeacher()

        classId = intent.getStringExtra("CLASS_ID")
        classId?.let {
            loadClassDetails(it)
        }

        binding.saveChangesButton.setOnClickListener {
            saveChanges()
        }

        binding.backButtonHeader.setOnClickListener {
            finish()
        }

    }

    private fun loadClassDetails(classId: String) {
        lifecycleScope.launch {
            try {
                val classes = classRepository.getClassById(classId)
                if (classes != null) {
                    displayClassDetails(classes)
                } else {
                    Log.e("EditClassActivity", "Class not found")
                }
            } catch (e: Exception) {
                Log.e("EditClassActivity", "Error loading class details: ${e.message}")
            }
        }
    }

    private fun displayClassDetails(classes: Classes) {
        binding.editClassName.setText(classes.className)
        binding.editClassCode.setText(classes.classCode)
        binding.editStudentCount.setText(classes.students.size.toString())
    }

    private fun saveChanges() {
        val className = binding.editClassName.text.toString()
        val classCode = binding.editClassCode.text.toString()
        val studentsId = binding.editAddStudent.text.toString()

        Log.d("EditClassActivity", "Attempting to add student with studentsId: $studentsId to classId: $classId")

        if (className.isEmpty() || classCode.isEmpty()) {
            Toast.makeText(this, "Vui lòng nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show()
            return
        }

        classId?.let { classId ->
            lifecycleScope.launch {
                try {
                    val classes = classRepository.getClassById(classId)
                    if (classes != null) {
                        val updatedClass = classes.copy(className = className, classCode = classCode)
                        classRepository.createClass(updatedClass)

                        if (studentsId.isNotEmpty()) {
                            Log.d("EditClassActivity", "Fetching student with studentsId: $studentsId")
                            val student = studentRepository.getStudentByStudentsId(studentsId)
                            Log.d("EditClassActivity", "Student found: $student")

                            if (student != null) {
                                val addStudentSuccess = classRepository.addStudentToClass(classId, studentsId)
                                Log.d("EditClassActivity", "addStudentSuccess: $addStudentSuccess")

                                if (addStudentSuccess) {
                                    Toast.makeText(this@EditClassActivity, "Cập nhật và thêm sinh viên thành công", Toast.LENGTH_SHORT).show()
                                } else {
                                    Toast.makeText(this@EditClassActivity, "Cập nhật thành công, thêm sinh viên thất bại", Toast.LENGTH_SHORT).show()
                                }
                            } else {
                                Toast.makeText(this@EditClassActivity, "Cập nhật thành công, không tìm thấy sinh viên", Toast.LENGTH_SHORT).show()
                            }
                        } else {
                            Toast.makeText(this@EditClassActivity, "Cập nhật thành công", Toast.LENGTH_SHORT).show()
                        }

                        val intent = Intent(this@EditClassActivity, EditClassSuccessActivity::class.java)
                        startActivity(intent)
                        finish()
                    } else {
                        Toast.makeText(this@EditClassActivity, "Lớp không tồn tại", Toast.LENGTH_SHORT).show()
                    }
                } catch (e: Exception) {
                    Log.e("EditClassActivity", "Error updating class: ${e.message}")
                    Toast.makeText(this@EditClassActivity, "Cập nhật thất bại", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun setupBottomNavigationTeacher() {
        binding.bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navigation_notifications -> {
                    true
                }
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
}