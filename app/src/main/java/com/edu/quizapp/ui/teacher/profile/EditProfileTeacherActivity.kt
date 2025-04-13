package com.edu.quizapp.ui.teacher.profile

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.edu.quizapp.R
import com.edu.quizapp.data.models.Teacher
import com.edu.quizapp.databinding.ActivityEditProfileTeacherBinding
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch

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
    }

    private fun setupObservers() {
        lifecycleScope.launch {
            viewModel.user.observe(this@EditProfileTeacherActivity) { user ->
                if (user != null) {
                    binding.editNameLayout.editText?.setText(user.name)
                    binding.editEmailLayout.editText?.setText(user.email)
                } else {
                    Toast.makeText(this@EditProfileTeacherActivity, "Không thể tải thông tin người dùng.", Toast.LENGTH_SHORT).show()
                }
            }

            viewModel.teacherData.observe(this@EditProfileTeacherActivity) { teacher ->
                if (teacher != null) {
                    binding.editPhoneLayout.editText?.setText(teacher.phone)
                    binding.editAddressLayout.editText?.setText(teacher.address)
                    binding.editImageUrlLayout.editText?.setText(teacher.profileImageUrl)

                    if (!teacher.profileImageUrl.isNullOrEmpty()) {
                        Glide.with(this@EditProfileTeacherActivity)
                            .load(teacher.profileImageUrl)
                            .placeholder(R.drawable.ic_profile_placeholder)
                            .error(R.drawable.ic_profile_placeholder)
                            .into(binding.profileImage)
                    }
                } else {
                    Toast.makeText(this@EditProfileTeacherActivity, "Không thể tải thông tin giáo viên.", Toast.LENGTH_SHORT).show()
                }
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
        lifecycleScope.launch {
            val teacher = viewModel.teacherData.value
            val user = viewModel.user.value
            if (teacher != null && user != null) {
                val updatedTeacher = Teacher(
                    uid = user.uid ?: return@launch,
                    name = user.name,
                    email = user.email,
                    phone = binding.editPhoneLayout.editText?.text.toString(),
                    address = binding.editAddressLayout.editText?.text.toString(),
                    profileImageUrl = binding.editImageUrlLayout.editText?.text.toString()
                )

                viewModel.updateTeacher(updatedTeacher, selectedImageUri)
                    .observe(this@EditProfileTeacherActivity) { isSuccess ->
                        if (isSuccess) {
                            Toast.makeText(this@EditProfileTeacherActivity, "Cập nhật thành công", Toast.LENGTH_SHORT).show()
                            viewModel.loadTeacherData() // Load lại dữ liệu từ Firestore
                            finish()
                        } else {
                            Toast.makeText(this@EditProfileTeacherActivity, "Cập nhật thất bại", Toast.LENGTH_SHORT).show()
                        }
                    }
            } else {
                Toast.makeText(this@EditProfileTeacherActivity, "Không thể lấy thông tin người dùng", Toast.LENGTH_SHORT).show()
            }
        }
    }
}