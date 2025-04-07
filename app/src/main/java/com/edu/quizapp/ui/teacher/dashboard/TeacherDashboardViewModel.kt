package com.edu.quizapp.ui.teacher.dashboard

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.edu.quizapp.data.models.Teacher
import com.edu.quizapp.data.models.User
import com.edu.quizapp.data.repository.TeacherRepository
import com.edu.quizapp.data.repository.UserRepository
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch

class TeacherDashboardViewModel : ViewModel() {

    private val userRepository = UserRepository()
    private val teacherRepository = TeacherRepository()
    private val _userData = MutableLiveData<User?>()
    val userData: LiveData<User?> = _userData
    private val _teacherData = MutableLiveData<Teacher?>()
    val teacherData: LiveData<Teacher?> = _teacherData

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
                        val existingTeacher = teacherRepository.getTeacherById(uid)
                        if (existingTeacher == null) {
                            // Tạo mới giáo viên với thông tin từ user
                            val newTeacher = Teacher(
                                uid = uid,
                                name = user.name,
                                phone = "",
                                address = "",
                                profileImageUrl = "",
                                email = user.email
                            )
                            teacherRepository.createTeacher(newTeacher)
                            _teacherData.value = newTeacher
                            Log.d("TeacherDashboardViewModel", "Created new teacher for uid: $uid")
                        } else {
                            _teacherData.value = existingTeacher
                            Log.d("TeacherDashboardViewModel", "Teacher already exists for uid: $uid")
                        }
                    } else {
                        Log.e("TeacherDashboardViewModel", "User not found for uid: $uid")
                    }
                } catch (e: Exception) {
                    Log.e("TeacherDashboardViewModel", "Error loading data: ${e.message}")
                }
            }
        }
    }

    fun createTeacherIfNotExist() {
        viewModelScope.launch {
            val currentUser = FirebaseAuth.getInstance().currentUser
            currentUser?.uid?.let { uid ->
                try {
                    val user = userRepository.getUserByUid(uid)
                    if (user != null) {
                        val existingTeacher = teacherRepository.getTeacherById(uid)
                        if (existingTeacher == null) {
                            val newTeacher = Teacher(
                                uid = uid,
                                name = user.name,
                                phone = "",
                                address = "",
                                profileImageUrl = "",
                                email = user.email
                            )
                            teacherRepository.createTeacher(newTeacher)
                            _teacherData.value = newTeacher
                            Log.d("TeacherDashboardViewModel", "Created new teacher for uid: $uid")
                        } else {
                            Log.d("TeacherDashboardViewModel", "Teacher already exists for uid: $uid")
                        }
                    } else {
                        Log.e("TeacherDashboardViewModel", "User not found for uid: $uid")
                    }
                } catch (e: Exception) {
                    Log.e("TeacherDashboardViewModel", "Error creating teacher: ${e.message}")
                }
            }
        }
    }
}