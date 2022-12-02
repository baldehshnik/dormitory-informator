package com.firstapplication.dormapp.ui.fragments.admin

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.Toolbar
import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import com.firstapplication.dormapp.DormApp
import com.firstapplication.dormapp.R
import com.firstapplication.dormapp.databinding.FragmentNewsListAdminBinding
import com.firstapplication.dormapp.ui.activity.MainActivity
import com.firstapplication.dormapp.ui.adapters.NewsAdminAdapter
import com.firstapplication.dormapp.ui.fragments.BasicFragment
import com.firstapplication.dormapp.ui.viewmodels.NewsListAdminViewModel
import com.firstapplication.dormapp.ui.viewmodels.factories.AdminViewModelFactory
import com.google.android.material.bottomnavigation.BottomNavigationView
import javax.inject.Inject

class NewsListAdminFragment : BasicFragment() {

    private lateinit var binding: FragmentNewsListAdminBinding

    @Inject
    lateinit var factory: AdminViewModelFactory.Factory

    private val viewModel: NewsListAdminViewModel by viewModels {
        factory.create(activity?.application as DormApp)
    }

    @SuppressLint("CutPasteId")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        (activity as MainActivity).activityComponent.also { it?.inject(this) }

        binding = FragmentNewsListAdminBinding.inflate(layoutInflater, container, false)
        turnOnBottomNavView(R.id.adminBottomView)
        requireActivity().findViewById<Toolbar>(R.id.toolbar).isVisible = false

        val adapter = NewsAdminAdapter()
        binding.rwNewsAdmin.adapter = adapter

        viewModel.news.observe(viewLifecycleOwner) { models ->
            adapter.submitList(models)
            binding.progressBarNewsAdmin.isVisible = false
            binding.rwNewsAdmin.isVisible = true
            requireActivity().findViewById<Toolbar>(R.id.toolbar).isVisible = true
            requireActivity().findViewById<BottomNavigationView>(R.id.studentBottomView).isInvisible = true
        }

        return binding.root
    }

    companion object {
        @JvmStatic
        fun newInstance(): NewsListAdminFragment {
            return NewsListAdminFragment()
        }
    }
}