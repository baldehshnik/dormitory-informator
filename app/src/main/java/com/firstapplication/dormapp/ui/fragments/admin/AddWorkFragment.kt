package com.firstapplication.dormapp.ui.fragments.admin

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.firstapplication.dormapp.databinding.FragmentAddWorkBinding
import com.firstapplication.dormapp.ui.fragments.BasicFragment

class AddWorkFragment : BasicFragment() {

    private lateinit var binding: FragmentAddWorkBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAddWorkBinding.inflate(inflater, container, false)

        return binding.root
    }

    companion object {
        fun newInstance(): AddWorkFragment {
            return AddWorkFragment()
        }
    }
}