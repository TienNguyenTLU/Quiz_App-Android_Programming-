package com.edu.quizapp.ui.teacher.dashboard

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.edu.quizapp.data.models.Student
import com.edu.quizapp.data.models.Teacher
import com.edu.quizapp.data.models.User
import com.edu.quizapp.data.repository.TeacherRepository
import com.edu.quizapp.data.repository.UserRepository
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch

class TeacherDashboardViewModel: ViewModel(){
    private val userRepository = UserRepository()
    private val studentRepository = TeacherRepository()
    private val _userData = MutableLiveData<User?>()
    val userData: LiveData<User?> = _userData
    private val _teacherdata = MutableLiveData<Teacher?>()
    val teacherdata: LiveData<Student?> = _teacherdata
    init {
        loadData()
    }

    fun loadData() {
        viewModelScope.launch {
            val currentUser = FirebaseAuth.getInstance().currentUser
            currentUser?.uid?.let { uid ->
                try {
                    val user = userRepository.getUserByUid(uid)
                    _userData.value = user

                    if (user != null) {
                        val existingStudent = studentRepository.getTeacherById(uid)
                        if (existingStudent == null) {
                            // Tạo mới học sinh với thông tin từ user
                            val newTeacher = Teacher(
                                uid = uid,
                                name = user.name,
                                phone = "",
                                address = ""
                            )
                            studentRepository.createTeacher(newTeacher)
                            _teacherdata.value = newTeacher
                            Log.d("DashboardViewModel", "Created new student for uid: $uid")
                        } else {
                            _teacherdata.value = existingStudent
                            Log.d("DashboardViewModel", "Student already exists for uid: $uid")
                        }
                    } else {
                        Log.e("DashboardViewModel", "User not found for uid: $uid")
                    }
                } catch (e: Exception) {
                    Log.e("DashboardViewModel", "Error loading data: ${e.message}")
                }
            }
        }
    }

    fun createStudentIfNotExist() {
        viewModelScope.launch {
            val currentUser = FirebaseAuth.getInstance().currentUser
            currentUser?.uid?.let { uid ->
                try {
                    val user = userRepository.getUserByUid(uid)
                    if (user != null) {
                        val existingStudent = studentRepository.getStudentById(uid)
                        if (existingStudent == null) {
                            val newStudent = Student(
                                uid = uid,
                                studentsId = "",
                                name = user.name,
                                phone = "",
                                address = "",
                                profileImageUrl = "",
                                email = user.email
                            )
                            studentRepository.createStudent(newStudent)
                            _studentData.value = newStudent
                            Log.d("DashboardViewModel", "Created new student for uid: $uid")
                        } else {
                            Log.d("DashboardViewModel", "Student already exists for uid: $uid")
                        }
                    } else {
                        Log.e("DashboardViewModel", "User not found for uid: $uid")
                    }
                } catch (e: Exception) {
                    Log.e("DashboardViewModel", "Error creating student: ${e.message}")
                }
            }
        }
    }
}