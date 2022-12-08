package com.firstapplication.dormapp.ui.fragments.admin

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.firstapplication.dormapp.DormApp
import com.firstapplication.dormapp.R
import com.firstapplication.dormapp.data.remote.ROOM_KEY
import com.firstapplication.dormapp.databinding.FragmentNewsInfoBinding
import com.firstapplication.dormapp.sealed.*
import com.firstapplication.dormapp.ui.activity.MainActivity
import com.firstapplication.dormapp.ui.fragments.BasicFragment
import com.firstapplication.dormapp.ui.models.NewsModel
import com.firstapplication.dormapp.ui.viewmodels.NewsInfoViewModel
import com.firstapplication.dormapp.ui.viewmodels.factories.AllUsersViewModelFactory
import javax.inject.Inject

class NewsInfoFragment : BasicFragment() {

    private var isAdmin = true

    private lateinit var binding: FragmentNewsInfoBinding

    @Inject
    lateinit var factory: AllUsersViewModelFactory.Factory

    private val viewModel: NewsInfoViewModel by viewModels {
        factory.create(activity?.application as DormApp)
    }

    @SuppressLint("SetTextI18n")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        (activity as MainActivity).activityComponent?.inject(this)
        binding = FragmentNewsInfoBinding.inflate(inflater, container, false)
        switchBottomNavViewVisibility(R.id.adminBottomView, GONE)
        switchBottomNavViewVisibility(R.id.studentBottomView, GONE)

        val sharedPreferences = requireActivity().getSharedPreferences(
            MainActivity.LOGIN_USER_PREF,
            Context.MODE_PRIVATE
        )

        isAdmin = sharedPreferences.getBoolean(IS_ADMIN, true)
        if (isAdmin) {
            binding.btnRespond.isVisible = false
            binding.btnShowList.isVisible = true
        } else {
            binding.btnRespond.isVisible = true
            binding.btnShowList.isVisible = false
        }

        val news = arguments?.getParcelable<NewsModel>(NEWS_TAG)!!
        binding.twTitle.text = news.title
        binding.twTime.text = "${news.hours} ${news.timeType}"
        binding.twDescription.text = news.description

        binding.btnRespond.setOnClickListener {
            onRespondClick(news.id, sharedPreferences.getInt(ROOM_KEY, 0))
        }

        binding.btnShowList.setOnClickListener {
            onShowListClick()
        }

        viewModel.responseResult.observe(viewLifecycleOwner) { response ->
            checkResponse(response)
        }

        return binding.root
    }

    private fun checkResponse(response: ResponseResult) {
        when (response) {
            is ErrorResponse -> {
                toast(getStringFromRes(R.string.database_error))
                binding.btnRespond.isEnabled = true
            }
            is ProgressResponse -> {
                binding.btnRespond.isEnabled = false
            }
            is AlreadyRegisteredResponse -> {
                toast(getStringFromRes(R.string.already_registered))
                binding.btnRespond.isEnabled = true
            }
            is CorrectResponse -> {
                toast(getStringFromRes(R.string.added))
                binding.btnRespond.isEnabled = true
            }
        }
    }

    private fun onShowListClick() {
        parentFragmentManager.beginTransaction()
            .addToBackStack(null)
            .replace(R.id.fragmentContainer, RespondingStudentsListFragment.newInstance())
            .commit()
    }

    private fun onRespondClick(id: String, userPassKey: Int) {
        viewModel.addWorker(id, userPassKey)
    }

    companion object {
        private const val NEWS_TAG = "NEWS"
        private const val IS_ADMIN = "IS ADMIN"

        @JvmStatic
        fun newInstance(news: NewsModel, isAdmin: Boolean = true): Fragment {
            val fragment = NewsInfoFragment()
            val args = Bundle()
            args.putParcelable(NEWS_TAG, news)
            args.putBoolean(IS_ADMIN, isAdmin)

            fragment.arguments = args
            return fragment
        }
    }
}