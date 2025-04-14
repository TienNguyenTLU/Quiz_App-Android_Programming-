package com.edu.quizapp.ui.teacher.dashboard.classroom

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.doAfterTextChanged
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.edu.quizapp.R
import com.edu.quizapp.adapter.teacher.classes.ClassesListAdapter
import com.edu.quizapp.data.repository.UserRepository
import com.edu.quizapp.databinding.ActivityClassManagementBinding
import com.edu.quizapp.ui.shared.SharedUserViewModel
import com.edu.quizapp.ui.shared.SharedUserViewModelFactory
import com.edu.quizapp.ui.teacher.dashboard.TeacherDashboardActivity
import com.edu.quizapp.ui.teacher.profile.TeacherProfileActivity

class ClassManagementActivity : AppCompatActivity() {

    private lateinit var binding: ActivityClassManagementBinding
    private lateinit var classManagementViewModel: ClassManagementViewModel
    private lateinit var sharedUserViewModel: SharedUserViewModel
    private lateinit var classesListAdapter: ClassesListAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityClassManagementBinding.inflate(layoutInflater)
        setContentView(binding.root)

        classManagementViewModel = ViewModelProvider(this)[ClassManagementViewModel::class.java]

        classesListAdapter = ClassesListAdapter(emptyList()) { classes ->
            val intent = Intent(this, ClassDetailsActivity::class.java)
            intent.putExtra("CLASS_ID", classes.classId)
            startActivity(intent)
        }
        binding.classListRecyclerView.adapter = classesListAdapter
        binding.classListRecyclerView.layoutManager = LinearLayoutManager(this)

        observeViewModel()
        setupBottomNavigationTeacher()
        setupSearchBar() // Thêm hàm setupSearchBar

        binding.addClassButton.setOnClickListener {
            startActivity(Intent(this, AddClassActivity::class.java))
        }

        binding.backButton.setOnClickListener {
            finish()
        }

        val userRepository = UserRepository()
        val factory = SharedUserViewModelFactory(userRepository)
        sharedUserViewModel = ViewModelProvider(this, factory)[SharedUserViewModel::class.java]
        observeSharedUserViewModel()
    }

    override fun onResume() {
        super.onResume()
        classManagementViewModel.loadClasses()
    }

    private fun setupSearchBar() {
        binding.searchBar.doAfterTextChanged { text ->
            classManagementViewModel.searchClasses(text.toString())
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

    private fun observeViewModel() {
        classManagementViewModel.classList.observe(this) { classes ->
            if (classes.isNullOrEmpty()) {
                binding.classListRecyclerView.visibility = View.GONE
                // Thêm TextView thông báo rỗng nếu cần
                // binding.emptyClassesTextView.visibility = View.VISIBLE
            } else {
                binding.classListRecyclerView.visibility = View.VISIBLE
                // Ẩn TextView thông báo rỗng nếu cần
                // binding.emptyClassesTextView.visibility = View.GONE
                classesListAdapter.updateClasses(classes)
            }
        }
    }

    private fun setupBottomNavigationTeacher() {
        binding.bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navigation_home -> {
                    val intent = Intent(this, TeacherDashboardActivity::class.java)
                    startActivity(intent)
                    true
                }
                R.id.navigation_profile -> {
                    val intent = Intent(this, TeacherProfileActivity::class.java)
                    startActivity(intent)
                    true
                }
                else -> false
            }
        }
    }
}