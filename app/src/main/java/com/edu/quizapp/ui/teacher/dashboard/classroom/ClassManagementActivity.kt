package com.edu.quizapp.ui.teacher.dashboard.classroom

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.edu.quizapp.R
import com.edu.quizapp.adapter.teacher.category.ClassListAdapter
import com.edu.quizapp.databinding.ActivityClassManagementBinding
import com.edu.quizapp.ui.teacher.dashboard.TeacherDashboardActivity
import com.edu.quizapp.ui.teacher.profile.TeacherProfileActivity

class ClassManagementActivity : AppCompatActivity() {

    private lateinit var binding: ActivityClassManagementBinding
    private lateinit var classManagementViewModel: ClassManagementViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityClassManagementBinding.inflate(layoutInflater)
        setContentView(binding.root)

        classManagementViewModel = ViewModelProvider(this)[ClassManagementViewModel::class.java]

        setupClassRecyclerView()
        observeViewModel()
        setupBottomNavigationTeacher()

        binding.addClassButton.setOnClickListener {
            startActivity(Intent(this, AddClassActivity::class.java))
        }
    }

    override fun onResume() {
        super.onResume()
        classManagementViewModel.loadClasses()
    }

    private fun setupClassRecyclerView() {
        binding.classListRecyclerView.layoutManager = LinearLayoutManager(this)
        classManagementViewModel.classList.observe(this) { classes ->
            binding.classListRecyclerView.adapter = ClassListAdapter(classes, { selectedClass ->
                classManagementViewModel.onClassClicked(selectedClass)
            }, { selectedClass ->
                val intent = Intent(this, ClassDetailsActivity::class.java)
                intent.putExtra("CLASS_ID", selectedClass.classId)
                startActivity(intent)
            })
        }
    }

    private fun observeViewModel() {
        classManagementViewModel.navigateToClassDetails.observe(this) { classes ->
            classes?.let {
                val intent = Intent(this, ClassDetailsActivity::class.java)
                intent.putExtra("CLASS_ID", it.classId)
                startActivity(intent)
                classManagementViewModel.onNavigationToClassDetailsComplete()
            }
        }
    }

    private fun setupBottomNavigationTeacher() {
        binding.bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navigation_notifications -> {
                    // Xử lý sự kiện cho navigation_notifications
                    true
                }
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