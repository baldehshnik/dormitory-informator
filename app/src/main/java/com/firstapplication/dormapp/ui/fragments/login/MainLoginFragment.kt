package com.firstapplication.dormapp.ui.fragments.login

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResultListener
import com.firstapplication.dormapp.R
import com.firstapplication.dormapp.databinding.FragmentMainLoginBinding
import com.firstapplication.dormapp.ui.fragments.admin.ConfirmStudentsFragment
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

        binding.btnCancel.setOnClickListener { changeStudentLayout(false) }
        binding.btnStudent.setOnClickListener { changeStudentLayout(true) }

        binding.btnAdmin.setOnClickListener { openAdminLoginFragment() }
        binding.btnLogin.setOnClickListener { openStudentLoginFragment() }
        binding.btnRegister.setOnClickListener { openStudentRegisterFragment() }

        return binding.root
    }

    private fun changeStudentLayout(visibility: Boolean) = with(binding) {
        btnAdmin.isVisible = !visibility
        btnStudent.isVisible = !visibility
        btnLogin.isVisible = visibility
        btnRegister.isVisible = visibility
        btnCancel.isVisible = visibility
    }

    private fun changeDefaultFragmentToAdmin(key: Boolean) {
        if (key) parentFragmentManager.beginTransaction()
            .add(R.id.fragmentContainer, ConfirmStudentsFragment.newInstance())
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

    private fun openStudentRegisterFragment() {
        parentFragmentManager.beginTransaction()
            .addToBackStack(null)
            .replace(R.id.fragmentContainer, StudentRegisterFragment.newInstance())
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