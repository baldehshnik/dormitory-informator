package com.firstapplication.dormapp.ui.fragments.login

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.isVisible
import androidx.fragment.app.setFragmentResultListener
import com.bumptech.glide.Glide
import com.firstapplication.dormapp.R
import com.firstapplication.dormapp.databinding.FragmentMainLoginBinding
import com.firstapplication.dormapp.sealed.Administrator
import com.firstapplication.dormapp.sealed.Student
import com.firstapplication.dormapp.sealed.UserType
import com.firstapplication.dormapp.ui.activity.MainActivity
import com.firstapplication.dormapp.ui.fragments.BasicFragment
import com.firstapplication.dormapp.ui.fragments.admin.ConfirmStudentsFragment
import com.firstapplication.dormapp.ui.fragments.login.StudentLoginFragment.Companion.PASS_KEY
import com.firstapplication.dormapp.ui.fragments.student.AccountFragment

class MainLoginFragment : BasicFragment() {

    private lateinit var binding: FragmentMainLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setFragmentResultListener(ADMIN_LOGIN_KEY) { _, bundle ->
            changeDefaultFragmentToAdmin(bundle.getBoolean(ADMIN_INIT, false))
        }
        setFragmentResultListener(STUDENT_LOGIN_KEY) { _, bundle ->
            changeDefaultFragmentToStudent(
                bundle.getBoolean(STUDENT_INIT, false),
                bundle.getInt(PASS_KEY, -1)
            )
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentMainLoginBinding.inflate(layoutInflater, container, false)
        switchToolBarVisibility(GONE)

        Glide.with(requireContext())
            .load(ResourcesCompat.getDrawable(resources, R.drawable.hello_icon, null))
            .into(binding.helloImage)

        binding.btnStudent.setOnClickListener { openStudentLoginFragment() }
        binding.btnAdmin.setOnClickListener { openAdminLoginFragment() }
        binding.btnRegister.setOnClickListener { openStudentRegisterFragment() }

        binding.btnLogin.setOnClickListener { changeVisibility(false) }
        binding.btnBack.setOnClickListener { changeVisibility(true) }

        return binding.root
    }

    private fun changeVisibility(visibility: Boolean) = with(binding) {
        btnLogin.isVisible = visibility
        btnRegister.isVisible = visibility
        btnStudent.isVisible = !visibility
        btnAdmin.isVisible = !visibility
        btnBack.isVisible = !visibility
    }

    private fun changeDefaultFragmentToAdmin(key: Boolean) {
        if (key) {
            parentFragmentManager.beginTransaction()
                .add(R.id.fragmentContainer, ConfirmStudentsFragment.newInstance())
                .remove(this@MainLoginFragment)
                .commit()

            setNewCurrentUserType(Administrator)
        }
    }

    private fun changeDefaultFragmentToStudent(key: Boolean, userKey: Int) {
        if (key) {
            parentFragmentManager.beginTransaction()
                .add(R.id.fragmentContainer, AccountFragment.newInstance(userKey))
                .remove(this@MainLoginFragment)
                .commit()

            setNewCurrentUserType(Student)
        }
    }

    private fun setNewCurrentUserType(userType: UserType) {
        val activity = activity
        if (activity is MainActivity) activity.setNewCurrentUserType(this, userType)
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

        @JvmStatic
        fun newInstance(): MainLoginFragment {
            return MainLoginFragment()
        }
    }
}