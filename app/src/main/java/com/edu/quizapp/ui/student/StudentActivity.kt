package com.edu.quizapp.ui.student

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.edu.quizapp.R
import com.edu.quizapp.databinding.ActivityStudentHomeBinding
import com.google.android.material.bottomnavigation.BottomNavigationView

class StudentActivity : AppCompatActivity() {

    private lateinit var binding: ActivityStudentHomeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityStudentHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupListeners()
    }

    private fun setupListeners() {
        binding.featureLayout.getChildAt(0).setOnClickListener {
            // Xử lý khi người dùng chọn "Danh mục"
            // Ví dụ: Mở danh sách danh mục
        }

        binding.featureLayout.getChildAt(1).setOnClickListener {
            // Xử lý khi người dùng chọn "Bài kiểm tra"
            // Ví dụ: Mở danh sách bài kiểm tra
        }

        binding.featureLayout.getChildAt(2).setOnClickListener {
            // Xử lý khi người dùng chọn "Lớp học"
            // Ví dụ: Mở danh sách lớp học
        }

        binding.bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navigation_home -> {
                    // Xử lý khi người dùng chọn "Trang chủ"
                    true
                }
                R.id.navigation_home-> {
                    // Xử lý khi người dùng chọn "Bảng điều khiển"
                    true
                }
                R.id.navigation_notifications -> {
                    // Xử lý khi người dùng chọn "Thông báo"
                    true
                }
                else -> false
            }
        }
    }
}