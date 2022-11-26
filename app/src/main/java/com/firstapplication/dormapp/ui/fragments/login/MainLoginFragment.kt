package com.firstapplication.dormapp.ui.fragments.login

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResultListener
import com.firstapplication.dormapp.R
import com.firstapplication.dormapp.databinding.FragmentMainLoginBinding
import com.firstapplication.dormapp.ui.fragments.admin.AddWorkFragment
import com.firstapplication.dormapp.ui.fragments.login.StudentLoginFragment.Companion.PASS_KEY
import com.firstapplication.dormapp.ui.fragments.student.AccountFragment

class MainLoginFragment : Fragment(R.layout.fragment_main_login) {

    private lateinit var binding: FragmentMainLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setFragmentResultListener(ADMIN_LOGIN_KEY) { _, bundle ->
            changeDefaultFragmentToAdmin(key = bundle.getBoolean(ADMIN_INIT, false))
        }
        setFragmentResultListener(STUDENT_LOGIN_KEY) { _, bundle ->
            changeDefaultFragmentToStudent(
                key = bundle.getBoolean(STUDENT_INIT, false),
                userKey = bundle.getInt(PASS_KEY, -1)
            )
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentMainLoginBinding.inflate(layoutInflater, container, false)
        binding.btnAdmin.setOnClickListener { openAdminLoginFragment() }
        binding.btnStudent.setOnClickListener { openStudentLoginFragment() }

        return binding.root
    }

    private fun changeDefaultFragmentToAdmin(key: Boolean) {
        if (key) parentFragmentManager.beginTransaction()
            .add(R.id.fragmentContainer, AddWorkFragment.newInstance())
            .remove(this@MainLoginFragment)
            .commit()
    }

    private fun changeDefaultFragmentToStudent(key: Boolean, userKey: Int) {
        if (key) parentFragmentManager.beginTransaction()
            .add(R.id.fragmentContainer, AccountFragment.newInstance(userKey))
            .remove(this@MainLoginFragment)
            .commit()
    }

    private fun openAdminLoginFragment() {
        val adminLoginDialogFragment = AdminLoginDialogFragment.newInstance()
        adminLoginDialogFragment.show(parentFragmentManager, AdminLoginDialogFragment.TAG)
    }

    private fun openStudentLoginFragment() {
        parentFragmentManager.beginTransaction()
            .addToBackStack(null)
            .replace(R.id.fragmentContainer, StudentLoginFragment.newInstance())
            .commit()
    }

    companion object {
        const val ADMIN_LOGIN_KEY = "ADMIN_FRAGMENT_LISTENER"
        const val STUDENT_LOGIN_KEY = "STUDENT_FRAGMENT_LISTENER"
        const val ADMIN_INIT = "ADMIN_INIT"
        const val STUDENT_INIT = "STUDENT_INIT"

        fun newInstance(): MainLoginFragment {
            return MainLoginFragment()
        }
    }

}