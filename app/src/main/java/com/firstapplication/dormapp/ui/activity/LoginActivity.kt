package com.firstapplication.dormapp.ui.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.firstapplication.dormapp.R
import com.firstapplication.dormapp.databinding.ActivityLoginBinding
import com.firstapplication.dormapp.ui.fragments.admin.AddWorkFragment
import com.firstapplication.dormapp.ui.fragments.login.MainLoginFragment
import com.firstapplication.dormapp.ui.fragments.student.AccountFragment

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (savedInstanceState == null) {
            val sharedPreferences = getSharedPreferences(LOGIN_USER_PREF, MODE_PRIVATE)
            if (sharedPreferences.getString(LOGIN_KEY, null) == null) {
                openLoginFragment()
            } else {
                openUserFragment(sharedPreferences.getString(LOGIN_KEY, "")!!)
            }
        }
    }

    private fun openUserFragment(key: String) = when {
            key.isBlank() -> {
                openLoginFragment()
            }
            key == resources.getString(R.string.admin_key) -> {
                openAdminFragment()
            }
            else -> {
                openStudentFragment()
            }
        }

    private fun openAdminFragment() {
        supportFragmentManager.beginTransaction()
            .add(R.id.fragmentLoginContainer, AddWorkFragment())
            .commit()
    }

    private fun openStudentFragment() {
        supportFragmentManager.beginTransaction()
            .add(R.id.fragmentLoginContainer, AccountFragment())
            .commit()
    }

    private fun openLoginFragment() {
        supportFragmentManager.beginTransaction()
            .add(R.id.fragmentLoginContainer, MainLoginFragment())
            .commit()
    }

    companion object {
        const val LOGIN_USER_PREF = "LOGIN_USER"
        const val LOGIN_KEY = "LOGIN_KEY"
    }
}