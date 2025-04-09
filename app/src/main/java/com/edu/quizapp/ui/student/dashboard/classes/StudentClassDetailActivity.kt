package com.edu.quizapp.ui.student.dashboard.classes

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.edu.quizapp.R
import com.edu.quizapp.adapter.student.classes.StudentTestListAdapter
import com.edu.quizapp.data.models.Classes
import com.edu.quizapp.data.models.Test
import com.edu.quizapp.data.repository.ClassRepository
import com.edu.quizapp.data.repository.TeacherRepository
import com.edu.quizapp.data.repository.TestRepository
import com.edu.quizapp.databinding.ActivityClassDetailStudentBinding
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class StudentClassDetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityClassDetailStudentBinding
    private val classRepository = ClassRepository()
    private val teacherRepository = TeacherRepository()
    private val testRepository = TestRepository()
    private lateinit var testListAdapter: StudentTestListAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityClassDetailStudentBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val classId = intent.getStringExtra("classId")

        if (classId != null) {
            loadClassDetails(classId)
        } else {
            Log.e("StudentClassDetailActivity", "ClassId is null")
            finish()
        }

        binding.testsRecyclerView.layoutManager = LinearLayoutManager(this)
        testListAdapter = StudentTestListAdapter(emptyList())
        binding.testsRecyclerView.adapter = testListAdapter

        binding.backButton.setOnClickListener {
            finish() // Quay về form trước đó
        }
    }

    private fun loadClassDetails(classId: String) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val classData = classRepository.getClassById(classId)
                withContext(Dispatchers.Main) {
                    if (classData != null) {
                        displayClassDetails(classData)
                        loadTests(classId)
                    } else {
                        Log.e("StudentClassDetailActivity", "Class data is null")
                        finish()
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Log.e("StudentClassDetailActivity", "Error loading class details: ${e.message}")
                    finish()
                }
            }
        }
    }

    private fun displayClassDetails(classData: Classes) {
        binding.classNameDetail.text = classData.className
        binding.classCodeDetail.text = "Mã lớp: ${classData.classCode}"

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val teacher = teacherRepository.getTeacherById(classData.teacherId)
                withContext(Dispatchers.Main) {
                    binding.teacherNameDetail.text = "Giáo viên: ${teacher?.name ?: "Không xác định"}"
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    binding.teacherNameDetail.text = "Giáo viên: Lỗi"
                    Log.e("StudentClassDetailActivity", "Error loading teacher: ${e.message}")
                }
            }
        }

        binding.studentCountDetail.text = "Số lượng học sinh: ${classData.students?.size ?: 0}"
    }

    private fun loadTests(classId: String) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val classData = classRepository.getClassById(classId)
                if (classData != null) {
                    Log.d("ClassCode", "ClassCode: ${classData.classCode}")
                    val tests = testRepository.getTestsByClassCode(classData.classCode)
                    Log.d("TestCount","${tests.size}")
                    withContext(Dispatchers.Main) {
                        displayTests(tests)
                    }
                } else {
                    withContext(Dispatchers.Main) {
                        Log.e("StudentClassDetailActivity", "Class data is null")
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Log.e("StudentClassDetailActivity", "Error loading tests: ${e.message}")
                }
            }
        }
    }

    private fun displayTests(tests: List<Test>) {
        if (tests.isNotEmpty()) {
            testListAdapter.updateData(tests)
            binding.testCountDetail.text = "Số lượng bài kiểm tra hiện có: ${tests.size}"
        } else {
            binding.testCountDetail.text = "Số lượng bài kiểm tra hiện có: 0"
        }
    }
}

