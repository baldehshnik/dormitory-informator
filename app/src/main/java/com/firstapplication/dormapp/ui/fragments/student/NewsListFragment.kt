package com.firstapplication.dormapp.ui.fragments.student

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import com.firstapplication.dormapp.R
import com.firstapplication.dormapp.databinding.FragmentNewsListBinding
import com.firstapplication.dormapp.sealed.*
import com.firstapplication.dormapp.ui.activity.MainActivity
import com.firstapplication.dormapp.ui.adapters.NewsAdapter
import com.firstapplication.dormapp.ui.fragments.BasicFragment
import com.firstapplication.dormapp.ui.models.NewsModel
import com.firstapplication.dormapp.ui.viewmodels.NewsListViewModel
import com.firstapplication.dormapp.ui.viewmodels.factories.StudentViewModelFactory
import javax.inject.Inject

class NewsListFragment : BasicFragment() {

    private lateinit var binding: FragmentNewsListBinding
    private lateinit var adapter: NewsAdapter

    @Inject
    lateinit var factory: StudentViewModelFactory

    private val viewModel: NewsListViewModel by viewModels { factory }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        (activity as MainActivity).activityComponent.also { it?.inject(this) }
        binding = FragmentNewsListBinding.inflate(inflater, container, false)

        viewModel.readNewsFromDB()

        switchBottomNavViewVisibility(R.id.studentBottomView, VISIBLE)

        adapter = NewsAdapter()
        binding.rwNews.adapter = adapter

        viewModel.news.observe(viewLifecycleOwner) { event ->
            handleSelectedNewsResult(event)
        }

        return binding.root
    }

    @Suppress("UNCHECKED_CAST")
    private fun handleSelectedNewsResult(result: SelectResult) {
        when (result) {
            is ProgressSelect -> {
                changeUiVisibility(toolBarVisibility = GONE, newsListVisibility = false)
            }
            is ErrorSelect -> {
                toast(getStringFromRes(R.string.error))
                changeUiVisibility(progressBarVisibility = false)
            }
            is Empty -> {
                toast(getStringFromRes(R.string.empty))
                changeUiVisibility(progressBarVisibility = false)
            }
            is CorrectSelect<*>  -> {
                adapter.submitList(result.value as MutableList<NewsModel>)
                changeUiVisibility(progressBarVisibility = false)
            }
        }
    }

    private fun changeUiVisibility(
        toolBarVisibility: Int = VISIBLE,
        progressBarVisibility: Boolean = true,
        newsListVisibility: Boolean = true
    ) {
        binding.newsListProgressBar.isVisible = progressBarVisibility
        binding.rwNews.isVisible = newsListVisibility
        switchToolBarVisibility(toolBarVisibility)
    }

    companion object {
        @JvmStatic
        fun newInstance(): NewsListFragment {
            return NewsListFragment()
        }
    }
}