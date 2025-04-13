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

    fun joinClass(studentsId: String, classId: String) {
        viewModelScope.launch {
            try {
                val classroom = classRepository.getClassById(classId)
                if (classroom != null) {
                    if (classroom.students.contains(studentsId)) {
                        _joinClassResult.value = "Bạn đã gia nhập lớp này rồi."
                    } else {
                        classRepository.addStudentToClass(classId, FirebaseAuth.getInstance().currentUser?.uid ?: "")
                        val notification = Notification(
                            notificationId = UUID.randomUUID().toString(),
                            senderId = FirebaseAuth.getInstance().currentUser?.uid ?: "",
                            message = "Học sinh đã gia nhập lớp học.",
                            timestamp = System.currentTimeMillis()
                        )
                        classRepository.addNotification(classId, notification)
                        _joinClassResult.value = "Gia nhập lớp thành công."
                    }
                } else {
                    _joinClassResult.value = "Lớp học không tồn tại."
                }
            } catch (e: Exception) {
                Log.e("DashboardViewModel", "Error joining class: ${e.message}")
                _joinClassResult.value = "Có lỗi xảy ra, vui lòng thử lại sau."
            }
        }
    }

    private val _joinClassResult = MutableLiveData<String>()
    val joinClassResult: LiveData<String> = _joinClassResult
}