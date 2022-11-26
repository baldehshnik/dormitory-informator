package com.firstapplication.dormapp.ui.fragments.student

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.firstapplication.dormapp.databinding.FragmentNewsListBinding
import com.firstapplication.dormapp.ui.fragments.BasicFragment

class NewsListFragment : BasicFragment() {

    private lateinit var binding: FragmentNewsListBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentNewsListBinding.inflate(inflater, container, false)


        return binding.root
    }

    companion object {
        @JvmStatic
        fun newInstance(): NewsListFragment {
            return NewsListFragment()
        }
    }

}