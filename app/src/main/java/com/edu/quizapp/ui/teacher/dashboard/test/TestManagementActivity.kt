package com.edu.quizapp.ui.teacher.dashboard.test

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.edu.quizapp.R
import com.edu.quizapp.adapter.teacher.test.TestListAdapter
import com.edu.quizapp.data.repository.UserRepository
import com.edu.quizapp.databinding.ActivityTestManagementBinding
import com.edu.quizapp.ui.shared.SharedUserViewModel
import com.edu.quizapp.ui.shared.SharedUserViewModelFactory
import com.edu.quizapp.ui.teacher.dashboard.TeacherDashboardActivity
import com.edu.quizapp.ui.teacher.profile.TeacherProfileActivity

class TestManagementActivity : AppCompatActivity() {

    private lateinit var binding: ActivityTestManagementBinding
    private lateinit var viewModel: TestManagementViewModel
    private lateinit var testAdapter: TestListAdapter
    private lateinit var sharedUserViewModel: SharedUserViewModel

    companion object {
        const val REQUEST_CODE_TEST_DETAIL = 123
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTestManagementBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel = ViewModelProvider(this)[TestManagementViewModel::class.java]

        setupRecyclerView()
        setupObservers()
        setupListeners()
        setupBottomNavigationTeacher()

        binding.addTestButton.setOnClickListener {
            startActivity(Intent(this, AddTestActivity::class.java))
        }

        binding.backButton.setOnClickListener{
            finish()
        }

        // Khởi tạo SharedUserViewModel
        val userRepository = UserRepository()
        val factory = SharedUserViewModelFactory(userRepository)
        sharedUserViewModel = ViewModelProvider(this, factory)[SharedUserViewModel::class.java]
        observeSharedUserViewModel()
    }

    override fun onResume() {
        super.onResume()
        viewModel.loadTests() // Tải lại dữ liệu khi Activity được resume
    }

    private fun setupRecyclerView() {
        testAdapter = TestListAdapter(emptyList(), this) { test ->
            val intent = Intent(this, TestDetailActivity::class.java)
            intent.putExtra("testId", test.testId)
            startActivityForResult(intent, REQUEST_CODE_TEST_DETAIL)
        }
        binding.testListRecyclerView.apply {
            layoutManager = LinearLayoutManager(this@TestManagementActivity)
            adapter = testAdapter
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

    private fun setupObservers() {
        viewModel.tests.observe(this) { tests ->
            testAdapter.updateTests(tests)
        }

        viewModel.isLoading.observe(this) { isLoading ->
            // Bạn có thể thêm một ProgressBar nếu cần, ở đây không có ProgressBar trong layout.
        }

        viewModel.errorMessage.observe(this) { errorMessage ->
            errorMessage?.let {
                Toast.makeText(this, it, Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun setupListeners() {
        binding.addTestButton.setOnClickListener {
            val intent = Intent(this, AddTestActivity::class.java)
            startActivity(intent)
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE_TEST_DETAIL && resultCode == RESULT_OK) {
            // Không cần gọi viewModel.loadTests() ở đây nữa
        }
    }
}