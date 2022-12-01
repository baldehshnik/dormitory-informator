package com.firstapplication.dormapp.ui.fragments.student

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.Toolbar
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import com.firstapplication.dormapp.DormApp
import com.firstapplication.dormapp.R
import com.firstapplication.dormapp.databinding.FragmentNewsListBinding
import com.firstapplication.dormapp.ui.activity.MainActivity
import com.firstapplication.dormapp.ui.adapters.NewsAdapter
import com.firstapplication.dormapp.ui.fragments.BasicFragment
import com.firstapplication.dormapp.ui.viewmodels.NewsListViewModel
import com.firstapplication.dormapp.ui.viewmodels.factories.StudentViewModelFactory
import com.google.android.material.bottomnavigation.BottomNavigationView
import javax.inject.Inject

class NewsListFragment : BasicFragment() {

    private lateinit var binding: FragmentNewsListBinding

    @Inject
    lateinit var factory: StudentViewModelFactory.Factory

    private val viewModel: NewsListViewModel by viewModels {
        factory.create(activity?.application as DormApp)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        (activity as MainActivity).activityComponent.also { it?.inject(this) }
        binding = FragmentNewsListBinding.inflate(inflater, container, false)

        viewModel.readNewsFromDB()

        requireActivity().findViewById<Toolbar>(R.id.toolbar).isVisible = false
        requireActivity().findViewById<BottomNavigationView>(R.id.studentBottomView).isVisible = true

        /////
//        Firebase.database.reference.child("news").child("fhjdlkfanf")
//            .setValue(NewsEntity(
//                id = "fhjdlkfanf",
//                imgSrc = "some image src",
//                title = "some news title",
//                hours = 1.1,
//                description = "some description",
//                isActive = false
//            ))
        /////

        val adapter = NewsAdapter()
        binding.rwNews.adapter = adapter

        viewModel.news.observe(viewLifecycleOwner) { newsModels ->
            adapter.submitList(newsModels)
            binding.newsListProgressBar.isVisible = false
            binding.rwNews.isVisible = true
            requireActivity().findViewById<Toolbar>(R.id.toolbar).isVisible = true
        }

        return binding.root
    }

    companion object {
        @JvmStatic
        fun newInstance(): NewsListFragment {
            return NewsListFragment()
        }
    }
}