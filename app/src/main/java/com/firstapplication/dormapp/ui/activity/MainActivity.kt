package com.firstapplication.dormapp.ui.activity

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.firstapplication.dormapp.R
import com.firstapplication.dormapp.databinding.ActivityLoginBinding
import com.firstapplication.dormapp.di.ActivitySubComponent
import com.firstapplication.dormapp.extensions.appComponent
import com.firstapplication.dormapp.ui.fragments.admin.AddWorkFragment
import com.firstapplication.dormapp.ui.fragments.login.MainLoginFragment
import com.firstapplication.dormapp.ui.fragments.student.AccountFragment

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    var activityComponent: ActivitySubComponent? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        activityComponent = appComponent.activityComponentBuilder().build()

        if (savedInstanceState == null) {
            val sharedPreferences = getSharedPreferences(LOGIN_USER_PREF, MODE_PRIVATE)
            if (sharedPreferences.getString(LOGIN_KEY, null) == null) {
                openLoginFragment()
            } else {
                openUserFragment(sharedPreferences.getString(LOGIN_KEY, "")!!)
            }
        }

    }

    override fun onDestroy() {
        super.onDestroy()
        activityComponent = null
    }

    private fun openUserFragment(key: String) = when {
            key.isBlank() -> {
                openLoginFragment()
            }
            key == resources.getString(R.string.admin_key) -> {
                openAdminFragment()
            }
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
            .add(R.id.fragmentContainer, AddWorkFragment.newInstance())
            .commit()
    }

    private fun openStudentFragment(key: Int) {
        supportFragmentManager.beginTransaction()
            .add(R.id.fragmentContainer, AccountFragment.newInstance(key = key))
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
    }
}