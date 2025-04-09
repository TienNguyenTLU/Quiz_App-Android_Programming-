package com.edu.quizapp.ui.student.dashboard

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.edu.quizapp.data.models.Classes
import com.edu.quizapp.data.models.Notification
import com.edu.quizapp.data.models.Student
import com.edu.quizapp.data.models.User
import com.edu.quizapp.data.repository.ClassRepository
import com.edu.quizapp.data.repository.StudentRepository
import com.edu.quizapp.data.repository.UserRepository
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch
import java.util.UUID

class StudentDashboardViewModel : ViewModel() {

    private val userRepository = UserRepository()
    private val studentRepository = StudentRepository()
    private val classRepository = ClassRepository() // Tạo instance của ClassRepository

    private val _userData = MutableLiveData<User?>()
    val userData: LiveData<User?> = _userData

    private val _studentData = MutableLiveData<Student?>()
    val studentData: LiveData<Student?> = _studentData

    private val _foundClass = MutableLiveData<Classes?>()
    val foundClass: LiveData<Classes?> = _foundClass

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
                            _studentData.value = existingStudent
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

    fun findClassByCode(classCode: String) {
        viewModelScope.launch {
            try {
                val classroom = classRepository.getAllClasses().find { it.classCode == classCode } // Sử dụng instance classRepository
                _foundClass.value = classroom
            } catch (e: Exception) {
                Log.e("DashboardViewModel", "Error finding class: ${e.message}")
                _foundClass.value = null
            }
        }
    }

    fun joinClass(userId: String, classId: String) {
        viewModelScope.launch {
            try {
                classRepository.addRequestToClass(classId, userId) // Sử dụng instance classRepository
                // Gửi thông báo đến giáo viên
                val notification = Notification(
                    notificationId = UUID.randomUUID().toString(),
                    senderId = userId,
                    message = "Yêu cầu gia nhập lớp học từ học sinh.",
                    timestamp = System.currentTimeMillis()
                )
                classRepository.addNotification(classId, notification) // Sử dụng instance classRepository
            } catch (e: Exception) {
                Log.e("DashboardViewModel", "Error joining class: ${e.message}")
            }
        }
    }
}