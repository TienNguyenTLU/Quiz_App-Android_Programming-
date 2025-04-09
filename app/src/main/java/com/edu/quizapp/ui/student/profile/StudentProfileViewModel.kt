package com.edu.quizapp.ui.student.profile

import android.util.Log
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

class StudentProfileViewModel : ViewModel() {

    private val studentRepository = StudentRepository()
    private val _studentData = MutableLiveData<Student?>()
    val studentData: LiveData<Student?> = _studentData

    private val userRepository = UserRepository()
    private val _user = MutableLiveData<User?>()
    val user: LiveData<User?> = _user

    private val _logoutEvent = MutableLiveData<Boolean>()
    val logoutEvent: LiveData<Boolean> = _logoutEvent

    init {
        loadStudentData()
        loadUserData()
    }

    fun loadStudentData() {
        viewModelScope.launch {
            try {
                val uid = FirebaseAuth.getInstance().currentUser?.uid
                if (uid != null) {
                    val student = studentRepository.getStudentById(uid)
                    _studentData.value = student
                }
            } catch (e: Exception) {
                Log.e("StudentProfileViewModel", "Error loading student data: ${e.message}")
                _studentData.value = null
            }
        }
    }

    private fun loadUserData() {
        viewModelScope.launch {
            try {
                val uid = FirebaseAuth.getInstance().currentUser?.uid
                if (uid != null) {
                    val user = userRepository.getUserByUid(uid)
                    _user.value = user
                }
            } catch (e: Exception) {
                Log.e("StudentProfileViewModel", "Error loading user data: ${e.message}")
                _user.value = null
            }
        }
    }

    fun logout() {
        FirebaseAuth.getInstance().signOut()
        _logoutEvent.value = true
    }
}