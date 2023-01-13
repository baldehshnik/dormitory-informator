package com.firstapplication.dormapp.ui.fragments.admin

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import com.firstapplication.dormapp.R
import com.firstapplication.dormapp.databinding.FragmentNewsListAdminBinding
import com.firstapplication.dormapp.ui.activity.MainActivity
import com.firstapplication.dormapp.ui.adapters.NewsAdminAdapter
import com.firstapplication.dormapp.ui.fragments.BasicFragment
import com.firstapplication.dormapp.ui.interfacies.OnAdminNewsItemClickListener
import com.firstapplication.dormapp.ui.models.NewsModel
import com.firstapplication.dormapp.ui.viewmodels.NewsListAdminViewModel
import com.firstapplication.dormapp.ui.viewmodels.factories.AdminVMFactory
import javax.inject.Inject

class NewsListAdminFragment : BasicFragment(), OnAdminNewsItemClickListener {

    private lateinit var binding: FragmentNewsListAdminBinding

    @Inject
    lateinit var factory: AdminVMFactory

    private val viewModel: NewsListAdminViewModel by viewModels { factory }

    @SuppressLint("CutPasteId")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        (activity as MainActivity).activityComponent.also { it?.inject(this) }

        binding = FragmentNewsListAdminBinding.inflate(inflater, container, false)
        switchBottomNavViewVisibility(R.id.adminBottomView, VISIBLE)

        val adapter = NewsAdminAdapter(this)
        binding.rwNewsAdmin.adapter = adapter

        viewModel.news.observe(viewLifecycleOwner) { models ->
            adapter.submitList(models)
            binding.progressBarNewsAdmin.isVisible = false
            binding.rwNewsAdmin.isVisible = true
        }

        return binding.root
    }

    override fun onFullItemClick(news: NewsModel) {
        parentFragmentManager.beginTransaction()
            .addToBackStack(null)
            .replace(R.id.fragmentContainer, NewsInfoFragment.newInstance(news = news))
            .commit()
    }

    override fun onEditClick(news: NewsModel) {
        parentFragmentManager.beginTransaction()
            .addToBackStack(null)
            .replace(
                R.id.fragmentContainer,
                AddWorkFragment.newInstance(news = news)
            )
            .commit()
    }

    companion object {
        @JvmStatic
        fun newInstance(): NewsListAdminFragment {
            return NewsListAdminFragment()
        }
    }
}