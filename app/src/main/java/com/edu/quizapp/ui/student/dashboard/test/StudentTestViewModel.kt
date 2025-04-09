package com.edu.quizapp.ui.student.dashboard.test

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.edu.quizapp.data.models.Test
import com.edu.quizapp.data.repository.StudentRepository
import com.edu.quizapp.data.repository.TestRepository
import com.edu.quizapp.data.repository.ClassRepository
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch

class StudentTestViewModel : ViewModel() {

    private val testRepository = TestRepository()
    private val studentRepository = StudentRepository()
    private val classRepository = ClassRepository()

    private val _tests = MutableLiveData<List<Test>>()
    val tests: LiveData<List<Test>> = _tests

    private val _isLoading = MutableLiveData<Boolean>(false)
    val isLoading: LiveData<Boolean> = _isLoading

    fun fetchTestsForStudent() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                testRepository.updateTestsWithClassId()
                
                val currentUserId = FirebaseAuth.getInstance().currentUser?.uid
                Log.d("StudentTestViewModel", "Current user ID: $currentUserId")
                
                currentUserId?.let { userId ->
                    val student = studentRepository.getStudentById(userId)
                    Log.d("StudentTestViewModel", "Student data: $student")
                    
                    student?.let { s ->
                        val allTests = mutableListOf<Test>()
                        
                        // First try to get tests from the student's classes list
                        val classes = s.classes ?: emptyList()
                        Log.d("StudentTestViewModel", "Student classes: $classes")
                        
                        for (classId in classes) {
                            val testsInClass = testRepository.getTestsByClassId(classId)
                            Log.d("StudentTestViewModel", "Tests for class $classId: $testsInClass")
                            allTests.addAll(testsInClass)
                        }
                        
                        // Also try to find tests for classes where the student's studentsId is in the students array
                        if (s.studentsId.isNotEmpty()) {
                            Log.d("StudentTestViewModel", "Searching for tests with studentsId: ${s.studentsId}")
                            val allClasses = classRepository.getAllClasses()
                            val classesWithStudent = allClasses.filter { it.students.contains(s.studentsId) }
                            Log.d("StudentTestViewModel", "Found ${classesWithStudent.size} classes with studentsId ${s.studentsId}")
                            
                            for (classItem in classesWithStudent) {
                                val testsInClass = testRepository.getTestsByClassId(classItem.classId)
                                Log.d("StudentTestViewModel", "Tests for class ${classItem.classId} (found by studentsId): $testsInClass")
                                
                                // Add any tests not already in the list
                                for (test in testsInClass) {
                                    if (!allTests.any { it.testId == test.testId }) {
                                        allTests.add(test)
                                    }
                                }
                                
                                // Update the student's classes list to include this class
                                if (!classes.contains(classItem.classId)) {
                                    val updatedClasses = classes.toMutableList()
                                    updatedClasses.add(classItem.classId)
                                    studentRepository.updateStudent(
                                        userId,
                                        s.copy(classes = updatedClasses)
                                    )
                                }
                            }
                        }
                        
                        Log.d("StudentTestViewModel", "Total tests found: ${allTests.size}")
                        _tests.value = allTests
                    } ?: run {
                        Log.e("StudentTestViewModel", "Student data is null")
                        _tests.value = emptyList()
                    }
                } ?: run {
                    Log.e("StudentTestViewModel", "Current user ID is null")
                    _tests.value = emptyList()
                }
            } catch (e: Exception) {
                Log.e("StudentTestViewModel", "Error fetching tests: ${e.message}")
                _tests.value = emptyList()
            } finally {
                _isLoading.value = false
            }
        }
    }
} 