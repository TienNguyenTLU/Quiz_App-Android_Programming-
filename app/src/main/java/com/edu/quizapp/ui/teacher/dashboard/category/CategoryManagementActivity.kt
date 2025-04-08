package com.edu.quizapp.ui.teacher.dashboard.category

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.edu.quizapp.R
import com.edu.quizapp.adapter.teacher.category.CategoryListAdapter
import com.edu.quizapp.databinding.ActivityCategoryManagementBinding
import com.edu.quizapp.ui.teacher.dashboard.TeacherDashboardActivity
import com.edu.quizapp.ui.teacher.profile.TeacherProfileActivity

class CategoryManagementActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCategoryManagementBinding
    private lateinit var viewModel: CategoryManagementViewModel
    private lateinit var adapter: CategoryListAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCategoryManagementBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel = ViewModelProvider(this)[CategoryManagementViewModel::class.java]

        setupRecyclerView()
        setupObservers()
        setupBottomNavigationTeacher()
        viewModel.loadCategories()
    }

    private fun setupRecyclerView() {
        adapter = CategoryListAdapter(emptyList(), { category ->
            // Chuyển sang ClassesByCategoryActivity để xem danh sách lớp
            val intent = Intent(this, ClassesByCategoryActivity::class.java)
            intent.putExtra("categoryId", category.categoryId)
            startActivity(intent)
        }, { category ->
            // Chuyển sang TestsByCategoryActivity để xem danh sách bài kiểm tra
            val intent = Intent(this, TestsByCategoryActivity::class.java)
            intent.putExtra("categoryId", category.categoryId)
            startActivity(intent)
        })

        binding.categoryRecyclerView.apply {
            layoutManager = LinearLayoutManager(this@CategoryManagementActivity)
            adapter = adapter
        }
    }

    private fun setupObservers() {
        viewModel.categories.observe(this) { categories ->
            adapter.updateCategories(categories)
        }

        // Không cần xử lý viewModel.classes và viewModel.tests ở đây nữa
        // Các Activity ClassesByCategoryActivity và TestsByCategoryActivity sẽ xử lý dữ liệu này
    }

    private fun setupBottomNavigationTeacher() {
        binding.bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navigation_notifications -> {
                    true
                }
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