package com.edu.quizapp.ui.shared

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.edu.quizapp.data.models.User
import com.edu.quizapp.data.repository.UserRepository
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch

class SharedUserViewModel(private val userRepository: UserRepository) : ViewModel() {

    private val _userData = MutableLiveData<User?>()
    val userData: LiveData<User?> = _userData

    init {
        fetchUserData()
    }

    private fun fetchUserData() {
        viewModelScope.launch {
            val userId = FirebaseAuth.getInstance().currentUser?.uid
            if (userId != null) {
                try {
                    val user = userRepository.getUserByUid(userId)
                    _userData.value = user
                } catch (e: Exception) {
                    // Xử lý lỗi
                    _userData.value = null
                }
            } else {
                _userData.value = null
            }
        }
    }
}