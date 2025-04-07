package com.edu.quizapp.ui.teacher.dashboard.classroom

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.edu.quizapp.adapter.ClassListAdapter
import com.edu.quizapp.databinding.ActivityClassManagementBinding


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
    }

    private fun setupClassRecyclerView() {
        binding.classListRecyclerView.layoutManager = LinearLayoutManager(this)
        classManagementViewModel.classList.observe(this) { classes ->
            binding.classListRecyclerView.adapter = ClassListAdapter(classes) { selectedClass ->
                classManagementViewModel.onClassClicked(selectedClass)
            }
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
}