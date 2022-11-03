package com.firstapplication.dormapp.ui.fragments.login

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.firstapplication.dormapp.R
import com.firstapplication.dormapp.databinding.FragmentMainLoginBinding

class MainLoginFragment : Fragment(R.layout.fragment_main_login) {

    private lateinit var binding: FragmentMainLoginBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMainLoginBinding.inflate(layoutInflater, container, false)
        binding.btnAdmin.setOnClickListener { openAdminLoginFragment() }
        binding.btnStudent.setOnClickListener { openStudentLoginGFragment() }

        return binding.root
    }

    private fun openAdminLoginFragment() {
        val adminLoginDialogFragment = AdminLoginDialogFragment()
        adminLoginDialogFragment.show(parentFragmentManager, AdminLoginDialogFragment.TAG)
    }

    private fun openStudentLoginGFragment() {
        parentFragmentManager.beginTransaction()
            .addToBackStack(null)
            .replace(R.id.fragmentLoginContainer, StudentLoginFragment())
            .commit()
    }

}