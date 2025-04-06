package com.edu.quizapp.ui.student.profile

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.edu.quizapp.data.models.Student
import com.edu.quizapp.data.repository.StudentRepository
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch

class StudentProfileViewModel : ViewModel() {

    private val studentRepository = StudentRepository()
    private val _studentData = MutableLiveData<Student?>()
    val studentData: LiveData<Student?> = _studentData

    private val _logoutEvent = MutableLiveData<Boolean>()
    val logoutEvent: LiveData<Boolean> = _logoutEvent

    fun loadStudentData(uid: String) {
        viewModelScope.launch {
            try {
                val student = studentRepository.getStudentById(uid)
                _studentData.value = student
            } catch (e: Exception) {
                Log.e("StudentProfileViewModel", "Error loading student data: ${e.message}")
                _studentData.value = null
            }
        }
    }

    fun logout() {
        FirebaseAuth.getInstance().signOut()
        _logoutEvent.value = true
    }
}