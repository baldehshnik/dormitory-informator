package com.firstapplication.dormapp.ui.activity

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager.POP_BACK_STACK_INCLUSIVE
import com.firstapplication.dormapp.R
import com.firstapplication.dormapp.databinding.ActivityLoginBinding
import com.firstapplication.dormapp.di.ActivitySubComponent
import com.firstapplication.dormapp.extensions.appComponent
import com.firstapplication.dormapp.sealed.Administrator
import com.firstapplication.dormapp.sealed.NoOne
import com.firstapplication.dormapp.sealed.Student
import com.firstapplication.dormapp.sealed.UserType
import com.firstapplication.dormapp.ui.fragments.admin.AddWorkFragment
import com.firstapplication.dormapp.ui.fragments.admin.ConfirmStudentsFragment
import com.firstapplication.dormapp.ui.fragments.admin.NewsListAdminFragment
import com.firstapplication.dormapp.ui.fragments.login.MainLoginFragment
import com.firstapplication.dormapp.ui.fragments.login.MainLoginFragment.Companion.MAIN_LOGIN_FRAGMENT_TAG
import com.firstapplication.dormapp.ui.fragments.student.AccountFragment
import com.firstapplication.dormapp.ui.fragments.student.NewsListFragment
import com.firstapplication.dormapp.utils.UserTypeGsonConverter

const val LOGIN_USER_PREF = "LOGIN_USER"
const val LOGIN_KEY = "LOGIN_KEY"

class MainActivity : AppCompatActivity() {

    private var currentUserType: UserType = NoOne
    private var isBackPressed = false

    private lateinit var binding: ActivityLoginBinding

    var activityComponent: ActivitySubComponent? = null
        private set

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        activityComponent = appComponent.activityComponentBuilder().build()
        supportActionBar?.setDisplayShowTitleEnabled(true)

        if (savedInstanceState == null) {
            val sharedPreferences = getSharedPreferences(LOGIN_USER_PREF, MODE_PRIVATE)
            val loginKey = sharedPreferences.getString(LOGIN_KEY, null)

            if (loginKey == null) openLoginFragment()
            else openUserFragment(loginKey)
        } else {
            currentUserType = UserTypeGsonConverter().fromGson(savedInstanceState.getString(USER_TYPE).toString())
        }

        binding.studentBottomView.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.itemAccount -> navigateStudent(ACCOUNT_FRAGMENT_TAG, null)
                R.id.itemNewsList -> navigateStudent(
                    NEWS_FRAGMENT_TAG,
                    NewsListFragment.newInstance()
                )
            }

            return@setOnItemSelectedListener true
        }

        binding.adminBottomView.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.itemConfirmStudents -> navigateAdministrator(CONFIRM_STUDENTS_TAG, ConfirmStudentsFragment.newInstance())
                R.id.itemAdd -> navigateAdministrator(ADD_NEWS_TAG, AddWorkFragment.newInstance())
                R.id.itemNewsListAdmin -> navigateAdministrator(
                    NEWS_ADMIN_FRAGMENT_TAG,
                    NewsListAdminFragment.newInstance()
                )
            }

            return@setOnItemSelectedListener true
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putString(USER_TYPE, UserTypeGsonConverter().toGson(currentUserType))
        super.onSaveInstanceState(outState)
    }

    private fun studentBackPressed() {
        if (findFragmentByTag(ACCOUNT_FRAGMENT_TAG)?.isVisible == true) {
            finish()
            return
        }

        super.onBackPressed()
        when {
            findFragmentByTag(NEWS_FRAGMENT_TAG)?.isVisible == true -> {
                binding.studentBottomView.selectedItemId = R.id.itemNewsList
            }
            findFragmentByTag(ACCOUNT_FRAGMENT_TAG)?.isVisible == true -> {
                binding.studentBottomView.selectedItemId = R.id.itemAccount
            }
        }
    }

    private fun adminBackPressed() {
        if (supportFragmentManager.backStackEntryCount > 1) {
            super.onBackPressed()
            isBackPressed = true
            when {
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
        } else {
            finish()
            return
        }
    }

    override fun onBackPressed() = when (currentUserType) {
        NoOne -> super.onBackPressed()
        Student -> studentBackPressed()
        Administrator -> adminBackPressed()
    }

    override fun onDestroy() {
        super.onDestroy()
        activityComponent = null
    }

    private fun findFragmentByTag(tag: String) = supportFragmentManager.findFragmentByTag(tag)

    private fun navigateStudent(tag: String, newFragment: Fragment?) {
        val fragment = findFragmentByTag(tag)
        if (fragment != null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainer, fragment, tag)
                .commit()
        } else {
            addFragmentToBackStack(newFragment, tag)
        }
    }

    private fun navigateAdministrator(tag: String, newFragment: Fragment?) {
        if (isBackPressed) {
            isBackPressed = false
            return
        }

        val fragment = findFragmentByTag(tag)

        if (fragment != null && fragment.isVisible) return
        else if (fragment != null) supportFragmentManager.popBackStack(tag, POP_BACK_STACK_INCLUSIVE)

        addFragmentToBackStack(newFragment, tag)
    }

    private fun addFragmentToBackStack(fragment: Fragment?, tag: String) {
        supportFragmentManager.beginTransaction()
            .addToBackStack(tag)
            .replace(R.id.fragmentContainer, fragment ?: return, tag)
            .commit()
    }

    private fun openUserFragment(key: String) = when (key) {
        resources.getString(R.string.admin_key) -> {
            openAdminFragment()
            currentUserType = Administrator
        }
        else -> {
            try {
                openStudentFragment(key.toInt())
                currentUserType = Student
            } catch (e: Exception) {
                Log.e(this::class.java.simpleName, e.message ?: "")
                openLoginFragment()
            }
        }
    }

    private fun openAdminFragment() {
        supportFragmentManager.beginTransaction()
            .add(R.id.fragmentContainer, ConfirmStudentsFragment.newInstance(), CONFIRM_STUDENTS_TAG)
            .commit()
    }

    private fun openStudentFragment(key: Int) {
        supportFragmentManager.beginTransaction()
            .add(R.id.fragmentContainer, AccountFragment.newInstance(key), ACCOUNT_FRAGMENT_TAG)
            .commit()
    }

    private fun openLoginFragment() {
        supportFragmentManager.beginTransaction()
            .add(R.id.fragmentContainer, MainLoginFragment.newInstance(), MAIN_LOGIN_FRAGMENT_TAG)
            .commit()
    }

    fun setNewCurrentUserType(fragment: Fragment, userType: UserType) {
        if (fragment is MainLoginFragment || fragment is AccountFragment) currentUserType = userType
    }

    companion object {
        @JvmStatic
        private val USER_TYPE = "USER TYPE"

        @JvmStatic
        val NEWS_FRAGMENT_TAG = "NEWS_LIST_TAG"

        @JvmStatic
        val ACCOUNT_FRAGMENT_TAG = "ACCOUNT_FRAGMENT_TAG"

        @JvmStatic
        val CONFIRM_STUDENTS_TAG = "CONFIRM_STUDENT_TAG"

        @JvmStatic
        val ADD_NEWS_TAG = "ADD_NEWS_TAG"

        @JvmStatic
        val NEWS_ADMIN_FRAGMENT_TAG = "NEWS_ADMIN_FRAGMENT_TAG"
    }
}