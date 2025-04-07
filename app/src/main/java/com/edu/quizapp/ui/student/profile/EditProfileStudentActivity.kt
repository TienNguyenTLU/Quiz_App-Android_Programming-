package com.edu.quizapp.ui.student.profile

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.edu.quizapp.R
import com.edu.quizapp.data.models.Student
import com.edu.quizapp.databinding.ActivityEditProfileStudentBinding
import kotlinx.coroutines.launch

class EditProfileStudentActivity : AppCompatActivity() {

    private lateinit var binding: ActivityEditProfileStudentBinding
    private lateinit var viewModel: ProfileSettingsStudentViewModel
    private var selectedImageUri: Uri? = null
    private val PICK_IMAGE_REQUEST = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditProfileStudentBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel = ViewModelProvider(this)[ProfileSettingsStudentViewModel::class.java]

        lifecycleScope.launch {
            viewModel.studentData.observe(this@EditProfileStudentActivity) { student ->
                if (student != null) {
                    if (student.profileImageUrl.isNotEmpty()) {
                        Glide.with(this@EditProfileStudentActivity)
                            .load(student.profileImageUrl)
                            .placeholder(R.drawable.ic_profile_placeholder)
                            .into(binding.profileImage)
                    } else {
                        binding.profileImage.setImageResource(R.drawable.ic_profile_placeholder)
                    }

                    binding.editName.setText(student.name)
                    binding.editEmail.setText(student.email)
                    binding.editPhone.setText(student.phone)
                    binding.editAddress.setText(student.address)
                    binding.editStudentsId.setText(student.studentsId)
                }
            }

            viewModel.user.observe(this@EditProfileStudentActivity){ user ->
                if(user != null){
                    binding.editName.setText(user.name)
                    binding.editEmail.setText(user.email)
                }
            }
        }

        binding.profileImage.setOnClickListener {
            openImageChooser()
        }

        binding.saveButton.setOnClickListener {
            lifecycleScope.launch {
                val student = viewModel.studentData.value
                val user = viewModel.user.value
                if(student != null && user != null){
                    val updatedStudent = Student(
                        name = user.name,
                        email = user.email,
                        phone = binding.editPhone.text.toString(),
                        address = binding.editAddress.text.toString(),
                        studentsId = binding.editStudentsId.text.toString()
                    )

                    viewModel.updateStudent(updatedStudent, selectedImageUri)
                        .observe(this@EditProfileStudentActivity) { isSuccess ->
                            if (isSuccess) {
                                Toast.makeText(this@EditProfileStudentActivity, "Cập nhật thành công", Toast.LENGTH_SHORT).show()
                                finish()
                            } else {
                                Toast.makeText(this@EditProfileStudentActivity, "Cập nhật thất bại", Toast.LENGTH_SHORT).show()
                            }
                        }
                } else {
                    Toast.makeText(this@EditProfileStudentActivity, "Không thể lấy thông tin người dùng", Toast.LENGTH_SHORT).show()
                }
            }
        }

        binding.backButton.setOnClickListener {
            finish()
        }
    }

    private fun openImageChooser() {
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(Intent.createChooser(intent, "Chọn ảnh"), PICK_IMAGE_REQUEST)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null) {
            selectedImageUri = data.data
            binding.profileImage.setImageURI(selectedImageUri)
        }
    }
}