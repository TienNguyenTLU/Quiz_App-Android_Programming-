package com.edu.quizapp.ui.teacher.profile

import android.net.Uri
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

class ProfileSettingsTeacherViewModel : ViewModel() {

    private val teacherRepository = TeacherRepository()
    private val _teacherData = MutableLiveData<Teacher?>()
    val teacherData: LiveData<Teacher?> = _teacherData

    private val userRepository = UserRepository()
    private val _user = MutableLiveData<User?>()
    val user: LiveData<User?> = _user

    init {
        loadTeacherData()
        loadUserData()
    }

    fun loadTeacherData() {
        viewModelScope.launch {
            val uid = FirebaseAuth.getInstance().currentUser?.uid
            if (uid != null) {
                _teacherData.value = teacherRepository.getTeacherById(uid)
            }
        }
    }

    fun loadUserData() {
        viewModelScope.launch {
            val uid = FirebaseAuth.getInstance().currentUser?.uid
            if (uid != null) {
                _user.value = userRepository.getUserByUid(uid)
            }
        }
    }

    fun updateTeacher(teacher: Teacher, imageUri: Uri?): LiveData<Boolean> {
        val result = MutableLiveData<Boolean>()
        viewModelScope.launch {
            val uid = FirebaseAuth.getInstance().currentUser?.uid
            if (uid != null) {
                result.value = teacherRepository.updateTeacher(uid, teacher, imageUri)
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