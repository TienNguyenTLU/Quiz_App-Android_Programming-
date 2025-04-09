package com.edu.quizapp.ui.teacher.profile

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.edu.quizapp.R
import com.edu.quizapp.data.models.Teacher
import com.edu.quizapp.databinding.ActivityEditProfileTeacherBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.util.UUID

class EditProfileTeacherActivity : AppCompatActivity() {

    private lateinit var binding: ActivityEditProfileTeacherBinding
    private lateinit var viewModel: ProfileSettingsTeacherViewModel
    private var selectedImageUri: Uri? = null

    companion object {
        private const val PICK_IMAGE_REQUEST = 1
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditProfileTeacherBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel = ViewModelProvider(this)[ProfileSettingsTeacherViewModel::class.java]

        setupObservers()
        setupClickListeners()

        loadData()
    }

    private fun loadData() {
        viewModel.loadTeacherData()
        viewModel.loadUserData()
    }

    private fun setupObservers() {
        viewModel.user.observe(this) { user ->
            if (user != null) {
                binding.editNameLayout.editText?.setText(user.name)
                binding.editEmailLayout.editText?.setText(user.email)
            } else {
                Toast.makeText(this, "Không thể tải thông tin người dùng.", Toast.LENGTH_SHORT).show()
            }
        }

        viewModel.teacherData.observe(this) { teacher ->
            if (teacher != null) {
                binding.editPhoneLayout.editText?.setText(teacher.phone)
                binding.editAddressLayout.editText?.setText(teacher.address)
                binding.editImageUrlLayout.editText?.setText(teacher.profileImageUrl)

                if (!teacher.profileImageUrl.isNullOrEmpty()) {
                    Glide.with(this)
                        .load(teacher.profileImageUrl)
                        .placeholder(R.drawable.ic_profile_placeholder)
                        .error(R.drawable.ic_profile_placeholder)
                        .into(binding.profileImage)
                }
            } else {
                Toast.makeText(this, "Không thể tải thông tin giáo viên.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun setupClickListeners() {
        binding.backButton.setOnClickListener {
            finish()
        }

        binding.profileImage.setOnClickListener {
            openImagePicker()
        }

        binding.saveButton.setOnClickListener {
            saveChanges()
        }
    }

    private fun openImagePicker() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(intent, PICK_IMAGE_REQUEST)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null) {
            selectedImageUri = data.data
            binding.profileImage.setImageURI(selectedImageUri)
        }
    }

    private fun saveChanges() {
        val phone = binding.editPhoneLayout.editText?.text.toString()
        val address = binding.editAddressLayout.editText?.text.toString()
        val imageUrl = binding.editImageUrlLayout.editText?.text.toString()

        if (phone.isBlank() || address.isBlank()) {
            Toast.makeText(this, "Vui lòng điền đầy đủ thông tin.", Toast.LENGTH_SHORT).show()
            return
        }

        saveTeacherData(phone, address, imageUrl)
    }

    private fun saveTeacherData(phone: String, address: String, imageUrl: String?) {
        val teacher = Teacher(
            uid = FirebaseAuth.getInstance().currentUser?.uid ?: "",
            name = binding.editNameLayout.editText?.text.toString(),
            email = binding.editEmailLayout.editText?.text.toString(),
            phone = phone,
            address = address,
            profileImageUrl = imageUrl ?: ""
        )

        viewModel.updateTeacher(teacher, null).observe(this) { success ->
            if (success) {
                Toast.makeText(this, "Thông tin đã được cập nhật.", Toast.LENGTH_SHORT).show()
                finish()
            } else {
                Toast.makeText(this, "Cập nhật thông tin thất bại.", Toast.LENGTH_SHORT).show()
            }
        }
    }
}