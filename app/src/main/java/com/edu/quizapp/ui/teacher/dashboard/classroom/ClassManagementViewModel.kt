package com.edu.quizapp.ui.teacher.dashboard.classroom

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.edu.quizapp.data.models.Classes
import com.edu.quizapp.data.repository.ClassRepository
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.UUID

class ClassManagementViewModel : ViewModel() {

    private val classRepository = ClassRepository()
    private val _classList = MutableLiveData<List<Classes>>()
    val classList: LiveData<List<Classes>> = _classList

    private val _navigateToClassDetails = MutableLiveData<Classes?>()
    val navigateToClassDetails: LiveData<Classes?> = _navigateToClassDetails

    private val _addClassResult = MutableLiveData<Boolean>()
    val addClassResult: LiveData<Boolean> = _addClassResult

    private var allClasses: List<Classes> = emptyList()

    init {
        loadClasses()
    }

    fun loadClasses() {
        viewModelScope.launch(Dispatchers.Main) {
            try {
                allClasses = withContext(Dispatchers.IO) {
                    classRepository.getAllClasses()
                }
                _classList.value = allClasses
                Log.d("ClassManagementViewModel", "Loaded classes successfully")
            } catch (e: Exception) {
                Log.e("ClassManagementViewModel", "Error loading classes: ${e.message}")
            }
        }
    }

    fun searchClasses(query: String) {
        val filteredClasses = allClasses.filter { classes ->
            classes.className.contains(query, ignoreCase = true)
        }
        _classList.value = filteredClasses
    }

    fun onClassClicked(classes: Classes) {
        _navigateToClassDetails.value = classes
    }

    fun onNavigationToClassDetailsComplete() {
        _navigateToClassDetails.value = null
    }

    fun addClass(
        className: String,
        classCode: String,
        maxStudents: Int, // <-- Sử dụng maxStudents
        subject: String,
        classImageUrl: String?
    ) {
        viewModelScope.launch {
            try {
                val teacherId = FirebaseAuth.getInstance().currentUser?.uid ?: ""
                val classId = UUID.randomUUID().toString()
                val newClass = Classes(
                    classId = classId,
                    className = className,
                    teacherId = teacherId,
                    classImageUrl = classImageUrl ?: "",
                    classCode = classCode,
                    maxStudents = maxStudents // <-- Sử dụng maxStudents
                )
                val success = withContext(Dispatchers.IO) {
                    classRepository.createClass(newClass)
                }
                _addClassResult.value = success
                if (success) {
                    loadClasses()
                    Log.d("ClassManagementViewModel", "Class added successfully")
                }
            } catch (e: Exception) {
                _addClassResult.value = false
                Log.e("ClassManagementViewModel", "Error adding class: ${e.message}")
            }
        }
    }
}