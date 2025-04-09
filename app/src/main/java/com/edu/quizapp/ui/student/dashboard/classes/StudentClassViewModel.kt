package com.edu.quizapp.ui.student.dashboard.classes

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.edu.quizapp.data.models.Classes // Thêm dòng import này
import com.edu.quizapp.data.repository.ClassRepository
import com.edu.quizapp.data.repository.StudentRepository
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.launch

class StudentClassViewModel : ViewModel() {

    private val classRepository = ClassRepository()
    private val studentRepository = StudentRepository()

    private val _classes = MutableLiveData<List<Classes>>()
    val classes: LiveData<List<Classes>> = _classes

    fun loadStudentClasses(studentId: String) {
        viewModelScope.launch {
            try {
                val student = studentRepository.getStudentById(studentId)
                if (student != null) {
                    Log.d("StudentClassViewModel", "Student found: $student")
                    Log.d("StudentClassViewModel", "Student studentsId: ${student.studentsId}")
                    
                    // First try to get classes from the student's classes list
                    val studentClasses = mutableListOf<Classes>()
                    
                    // If student has classes list, use it
                    if (student.classes.isNotEmpty()) {
                        Log.d("StudentClassViewModel", "Using student's classes list: ${student.classes}")
                        val deferredClasses = student.classes.map { classId ->
                            async {
                                try {
                                    val classData = classRepository.getClassById(classId)
                                    Log.d("StudentClassViewModel", "Class data for $classId: $classData")
                                    classData
                                } catch (e: Exception) {
                                    Log.e("StudentClassViewModel", "Error fetching class with id $classId: ${e.message}")
                                    null
                                }
                            }
                        }
                        studentClasses.addAll(deferredClasses.awaitAll().filterNotNull())
                    }
                    
                    // Also try to find classes where the student's studentsId is in the students array
                    if (student.studentsId.isNotEmpty()) {
                        Log.d("StudentClassViewModel", "Searching for classes with studentsId: ${student.studentsId}")
                        val allClasses = classRepository.getAllClasses()
                        val classesWithStudent = allClasses.filter { it.students.contains(student.studentsId) }
                        Log.d("StudentClassViewModel", "Found ${classesWithStudent.size} classes with studentsId ${student.studentsId}")
                        
                        // Add any classes not already in the list
                        for (classItem in classesWithStudent) {
                            if (!studentClasses.any { it.classId == classItem.classId }) {
                                studentClasses.add(classItem)
                                
                                // Update the student's classes list to include this class
                                if (!student.classes.contains(classItem.classId)) {
                                    val updatedClasses = student.classes.toMutableList()
                                    updatedClasses.add(classItem.classId)
                                    studentRepository.updateStudent(
                                        studentId,
                                        student.copy(classes = updatedClasses)
                                    )
                                }
                            }
                        }
                    }
                    
                    _classes.value = studentClasses
                    Log.d("StudentClassViewModel", "Total classes found: ${studentClasses.size}")
                } else {
                    Log.d("StudentClassViewModel", "Student not found for id: $studentId")
                    _classes.value = emptyList()
                }
            } catch (e: Exception) {
                Log.e("StudentClassViewModel", "Error loading student classes: ${e.message}")
                _classes.value = emptyList()
            }
        }
    }
}