package com.firstapplication.dormapp.ui.fragments.login

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.firstapplication.dormapp.databinding.FragmentStudentLoginBinding

class StudentLoginFragment : Fragment() {

    private lateinit var binding: FragmentStudentLoginBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentStudentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

}