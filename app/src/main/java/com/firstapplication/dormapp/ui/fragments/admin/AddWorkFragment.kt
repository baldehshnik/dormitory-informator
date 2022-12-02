package com.firstapplication.dormapp.ui.fragments.admin

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isInvisible
import com.firstapplication.dormapp.R
import com.firstapplication.dormapp.databinding.FragmentAddWorkBinding
import com.firstapplication.dormapp.ui.fragments.BasicFragment
import com.google.android.material.bottomnavigation.BottomNavigationView

class AddWorkFragment : BasicFragment() {

    private lateinit var binding: FragmentAddWorkBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAddWorkBinding.inflate(inflater, container, false)
        turnOnBottomNavView(R.id.adminBottomView)
        requireActivity().findViewById<BottomNavigationView>(R.id.studentBottomView).isInvisible = true

        return binding.root
    }

    companion object {
        @JvmStatic
        fun newInstance(): AddWorkFragment {
            return AddWorkFragment()
        }
    }
}