package com.edu.quizapp.ui.teacher.dashboard.classroom

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.edu.quizapp.R
import com.edu.quizapp.data.repository.UserRepository
import com.edu.quizapp.databinding.ActivityAddClassBinding
import com.edu.quizapp.ui.shared.SharedUserViewModel
import com.edu.quizapp.ui.shared.SharedUserViewModelFactory
import com.edu.quizapp.ui.teacher.dashboard.TeacherDashboardActivity
import com.edu.quizapp.ui.teacher.profile.TeacherProfileActivity
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.util.UUID

class AddClassActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddClassBinding
    private lateinit var viewModel: ClassManagementViewModel
    private var selectedImageUri: Uri? = null
    private val PICK_IMAGE_REQUEST = 1
    private lateinit var sharedUserViewModel: SharedUserViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddClassBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel = ViewModelProvider(this)[ClassManagementViewModel::class.java]

        setupBottomNavigationTeacher()

        binding.saveClassButton.setOnClickListener {
            saveClass()
        }

        binding.backButton.setOnClickListener {
            finish()
        }

        // Khởi tạo SharedUserViewModel
        val userRepository = UserRepository()
        val factory = SharedUserViewModelFactory(userRepository)
        sharedUserViewModel = ViewModelProvider(this, factory)[SharedUserViewModel::class.java]
        observeSharedUserViewModel()
    }

    private fun openImageChooser() {
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(Intent.createChooser(intent, "Chọn ảnh"), PICK_IMAGE_REQUEST)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null) {
            selectedImageUri = data.data
        }
    }

    private fun observeSharedUserViewModel() {
        sharedUserViewModel.userData.observe(this) { user ->
            if (user != null) {
                binding.topBarInclude.userName.text = user.name
                binding.topBarInclude.welcomeMessage.text = "Chào mừng đến với 3T"
            } else {
                Toast.makeText(this, "Không thể tải thông tin người dùng.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun saveClass() {
        val className = binding.classNameEditText.text.toString()
        val classCode = binding.classCodeEditText.text.toString() // Truy xuất classCode
        val studentCount = binding.studentCountEditText.text.toString().toIntOrNull() ?: 0
        val subject = binding.subjectEditText.text.toString()

        if (className.isEmpty()) {
            Toast.makeText(this, "Vui lòng nhập tên lớp", Toast.LENGTH_SHORT).show()
            return
        }

        val imageUrl = if (selectedImageUri != null) {
            uploadImageToFirebase(selectedImageUri!!)
        } else {
            null
        }

        viewModel.addClass(className, classCode, studentCount, subject, imageUrl)
        viewModel.addClassResult.observe(this) { success ->
            if (success) {
                startActivity(Intent(this, AddClassSuccessActivity::class.java))
                finish()
            } else {
                Toast.makeText(this, "Thêm lớp học thất bại", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun uploadImageToFirebase(imageUri: Uri): String? {
        var imageUrl: String? = null
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val storageRef = FirebaseStorage.getInstance().reference
                val imageRef = storageRef.child("classes/${UUID.randomUUID()}")
                imageRef.putFile(imageUri).await()
                imageUrl = imageRef.downloadUrl.await().toString()
            } catch (e: Exception) {
                runOnUiThread {
                    Toast.makeText(this@AddClassActivity, "Upload ảnh thất bại: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
        return imageUrl
    }

    private fun setupBottomNavigationTeacher() {
        binding.bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {

                R.id.navigation_home -> {
                    // Xử lý sự kiện cho navigation_home
                    val intent = Intent(this, TeacherDashboardActivity::class.java)
                    startActivity(intent)
                    true
                }
                R.id.navigation_profile -> {
                    // Xử lý sự kiện cho navigation_profile
                    val intent = Intent(this, TeacherProfileActivity::class.java)
                    startActivity(intent)
                    true
                }
                else -> false
            }
        }
    }
}