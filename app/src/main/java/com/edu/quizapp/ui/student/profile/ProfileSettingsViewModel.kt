package com.edu.quizapp.ui.student.profile

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.edu.quizapp.data.models.Student
import com.edu.quizapp.data.models.User
import com.edu.quizapp.data.repository.StudentRepository
import com.edu.quizapp.data.repository.UserRepository
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch

class ProfileSettingsViewModel : ViewModel() {

    private val studentRepository = StudentRepository()
    private val _studentData = MutableLiveData<Student?>()
    val studentData: LiveData<Student?> = _studentData

    private val userRepository = UserRepository()
    private val _user = MutableLiveData<User?>()
    val user: LiveData<User?> = _user

    init {
        loadStudentData()
        loadUserData()
    }

    private fun loadStudentData() {
        viewModelScope.launch {
            val uid = FirebaseAuth.getInstance().currentUser?.uid
            if (uid != null) {
                _studentData.value = studentRepository.getStudentById(uid)
            }
        }
    }

    private fun loadUserData() {
        viewModelScope.launch {
            val uid = FirebaseAuth.getInstance().currentUser?.uid
            if (uid != null) {
                _user.value = userRepository.getUserByUid(uid) // Thay đổi ở đây
            }
        }
    }

    fun updateStudent(student: Student, imageUri: Uri?): LiveData<Boolean> {
        val result = MutableLiveData<Boolean>()
        viewModelScope.launch {
            val uid = FirebaseAuth.getInstance().currentUser?.uid
            if (uid != null) {
                // Cập nhật student và ảnh
                result.value = studentRepository.updateStudent(uid, student, imageUri)
            } else {
                result.value = false
            }
        }
        return result
    }

    fun logout() {
        FirebaseAuth.getInstance().signOut()
    }

    fun changePassword(oldPassword: String, newPassword: String): LiveData<Boolean> {
        val result = MutableLiveData<Boolean>()
        viewModelScope.launch {
            result.value = userRepository.changePassword(oldPassword, newPassword)
        }
        return result
    }

    fun sendPasswordResetEmail(): LiveData<Boolean> {
        val result = MutableLiveData<Boolean>()
        viewModelScope.launch {
            result.value = userRepository.sendPasswordResetEmail()
        }
        return result
    }
}