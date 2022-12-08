package com.firstapplication.dormapp.ui.fragments.admin

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.firstapplication.dormapp.databinding.FragmentRespondingStudentsListBinding
import com.firstapplication.dormapp.ui.fragments.BasicFragment

class RespondingStudentsListFragment : BasicFragment() {

    private lateinit var binding: FragmentRespondingStudentsListBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentRespondingStudentsListBinding.inflate(inflater, container, false)

        return binding.root
    }

    companion object {
        @JvmStatic
        fun newInstance(): RespondingStudentsListFragment {
            return RespondingStudentsListFragment()
        }
    }
}