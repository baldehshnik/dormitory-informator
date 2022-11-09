package com.firstapplication.dormapp.ui.fragments.login

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import com.firstapplication.dormapp.DormApp
import com.firstapplication.dormapp.databinding.FragmentStudentLoginBinding
import com.firstapplication.dormapp.di.ActivitySubComponent
import com.firstapplication.dormapp.extensions.appComponent
import com.firstapplication.dormapp.ui.activity.MainActivity
import com.firstapplication.dormapp.ui.viewmodels.AdminViewModel
import com.firstapplication.dormapp.ui.viewmodels.StudentLoginViewModel
import com.firstapplication.dormapp.ui.viewmodels.factories.AdminViewModelFactory
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import javax.inject.Inject

class StudentLoginFragment : Fragment() {

    private lateinit var binding: FragmentStudentLoginBinding

    @Inject
    lateinit var factory: AdminViewModelFactory.Factory

    private val viewModel: AdminViewModel by viewModels {
        factory.create(activity?.application as DormApp)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        (activity as MainActivity).activityComponent.also { it?.inject(this) }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentStudentLoginBinding.inflate(inflater, container, false)

        with(binding) {
            btnCancel.setOnClickListener { parentFragmentManager.popBackStack() }
        }

        return binding.root
    }

}