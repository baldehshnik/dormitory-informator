package com.firstapplication.dormapp.ui.activity

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.firstapplication.dormapp.R
import com.firstapplication.dormapp.databinding.ActivityLoginBinding
import com.firstapplication.dormapp.di.ActivitySubComponent
import com.firstapplication.dormapp.extensions.appComponent
import com.firstapplication.dormapp.ui.fragments.admin.AddWorkFragment
import com.firstapplication.dormapp.ui.fragments.admin.ConfirmStudentsFragment
import com.firstapplication.dormapp.ui.fragments.admin.NewsListAdminFragment
import com.firstapplication.dormapp.ui.fragments.login.MainLoginFragment
import com.firstapplication.dormapp.ui.fragments.student.AccountFragment
import com.firstapplication.dormapp.ui.fragments.student.NewsListFragment

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding

    var activityComponent: ActivitySubComponent? = null
        private set

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        activityComponent = appComponent.activityComponentBuilder().build()

        if (savedInstanceState == null) {
            val sharedPreferences = getSharedPreferences(LOGIN_USER_PREF, MODE_PRIVATE)
            val loginKey = sharedPreferences.getString(LOGIN_KEY, null)
            if (loginKey == null) {
                openLoginFragment()
            } else {
                openUserFragment(loginKey)
            }
        }

        binding.studentBottomView.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.itemAccount -> navigateStudent(ACCOUNT_FRAGMENT_TAG, null)
                R.id.itemNewsList -> navigateStudent(NEWS_FRAGMENT_TAG, NewsListFragment.newInstance())
            }

            return@setOnItemSelectedListener true
        }

        binding.adminBottomView.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.itemConfirmStudents -> navigateAdmin(CONFIRM_STUDENTS_TAG, null)
                R.id.itemAdd -> navigateAdmin(ADD_NEWS_TAG, AddWorkFragment.newInstance())
                R.id.itemNewsListAdmin -> navigateAdmin(
                    NEWS_ADMIN_FRAGMENT_TAG,
                    NewsListAdminFragment.newInstance()
                )
            }

            return@setOnItemSelectedListener true
        }
    }

    override fun onBackPressed() {
        if (findFragmentByTag(ACCOUNT_FRAGMENT_TAG)?.isVisible == true) {
            finish()
            return
        }

        val isNewsAdminVisible = findFragmentByTag(NEWS_ADMIN_FRAGMENT_TAG)?.isVisible
        val isAddNewsVisible = findFragmentByTag(ADD_NEWS_TAG)?.isVisible

        if (
            supportFragmentManager.backStackEntryCount == 2 &&
            (isNewsAdminVisible == true || isAddNewsVisible == true)
        ) {
            supportFragmentManager.popBackStack()
        } else {
            super.onBackPressed()
            when {
                findFragmentByTag(NEWS_FRAGMENT_TAG)?.isVisible == true -> {
                    binding.studentBottomView.selectedItemId = R.id.itemNewsList
                }
                findFragmentByTag(ACCOUNT_FRAGMENT_TAG)?.isVisible == true -> {
                    binding.studentBottomView.selectedItemId = R.id.itemAccount
                }

                findFragmentByTag(NEWS_ADMIN_FRAGMENT_TAG)?.isVisible == true -> {
                    binding.adminBottomView.selectedItemId = R.id.itemNewsListAdmin
                }
                findFragmentByTag(ADD_NEWS_TAG)?.isVisible == true -> {
                    binding.adminBottomView.selectedItemId = R.id.itemAdd
                }
                findFragmentByTag(CONFIRM_STUDENTS_TAG)?.isVisible == true -> {
                    binding.adminBottomView.selectedItemId = R.id.itemConfirmStudents
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        activityComponent = null
    }

    private fun findFragmentByTag(tag: String): Fragment? {
        return supportFragmentManager.findFragmentByTag(tag)
    }

    private fun navigateAdmin(tag: String, newFragment: Fragment?) {
        val fragment = supportFragmentManager.findFragmentByTag(tag)
        if (fragment != null && tag != CONFIRM_STUDENTS_TAG) {
            supportFragmentManager.beginTransaction()
                .addToBackStack(null)
                .replace(R.id.fragmentContainer, fragment, tag)
                .commit()
        } else if (fragment != null && tag == CONFIRM_STUDENTS_TAG) {
            repeat(supportFragmentManager.backStackEntryCount) {
                supportFragmentManager.popBackStack()
            }
        } else {
            addFragmentToBackStack(newFragment, tag)
        }
    }

    private fun navigateStudent(tag: String, newFragment: Fragment?) {
        val fragment = supportFragmentManager.findFragmentByTag(tag)
        if (fragment != null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainer, fragment, tag)
                .commit()
        } else {
            addFragmentToBackStack(newFragment, tag)
        }
    }

    private fun addFragmentToBackStack(fragment: Fragment?, tag: String) {
        supportFragmentManager.beginTransaction()
            .addToBackStack(null)
            .replace(R.id.fragmentContainer, fragment ?: return, tag)
            .commit()
    }

    private fun openUserFragment(key: String) = when (key) {
        resources.getString(R.string.admin_key) -> openAdminFragment()
        else -> {
            try {
                openStudentFragment(key.toInt())
            } catch (e: Exception) {
                Log.e(this::class.java.simpleName, e.message ?: "")
                openLoginFragment()
            }
        }
    }

    private fun openAdminFragment() {
        supportFragmentManager.beginTransaction()
            .add(
                R.id.fragmentContainer,
                ConfirmStudentsFragment.newInstance(),
                CONFIRM_STUDENTS_TAG
            )
            .commit()
    }

    private fun openStudentFragment(key: Int) {
        supportFragmentManager.beginTransaction()
            .add(
                R.id.fragmentContainer,
                AccountFragment.newInstance(key = key),
                ACCOUNT_FRAGMENT_TAG
            )
            .commit()
    }

    private fun openLoginFragment() {
        supportFragmentManager.beginTransaction()
            .add(R.id.fragmentContainer, MainLoginFragment.newInstance())
            .commit()
    }

    companion object {
        const val LOGIN_USER_PREF = "LOGIN_USER"
        const val LOGIN_KEY = "LOGIN_KEY"

        const val NEWS_FRAGMENT_TAG = "NEWS_LIST_TAG"
        const val ACCOUNT_FRAGMENT_TAG = "ACCOUNT_FRAGMENT_TAG"

        const val CONFIRM_STUDENTS_TAG = "CONFIRM_STUDENT_TAG"
        const val ADD_NEWS_TAG = "ADD_NEWS_TAG"
        const val NEWS_ADMIN_FRAGMENT_TAG = "NEWS_ADMIN_FRAGMENT_TAG"
    }
}