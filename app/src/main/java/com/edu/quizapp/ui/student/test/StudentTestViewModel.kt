package com.edu.quizapp.ui.student.test

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.edu.quizapp.data.models.Test
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class StudentTestViewModel : ViewModel() {
    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    private val _tests = MutableLiveData<List<Test>>()
    val tests: LiveData<List<Test>> = _tests

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    fun fetchTestsByClassCode(classCode: String) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                val testsSnapshot = db.collection("tests")
                    .whereEqualTo("classCode", classCode)
                    .get()
                    .await()

                val testsList = testsSnapshot.documents.mapNotNull { doc ->
                    doc.toObject(Test::class.java)
                }
                _tests.value = testsList
            } catch (e: Exception) {
                // Handle error
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun fetchTestsForStudent() {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                val currentUser = auth.currentUser ?: return@launch
                
                val testsSnapshot = db.collection("tests")
                    .get()
                    .await()

                val testsList = testsSnapshot.documents.mapNotNull { doc ->
                    doc.toObject(Test::class.java)
                }
                _tests.value = testsList
            } catch (e: Exception) {
                // Handle error
            } finally {
                _isLoading.value = false
            }
        }
    }
} 