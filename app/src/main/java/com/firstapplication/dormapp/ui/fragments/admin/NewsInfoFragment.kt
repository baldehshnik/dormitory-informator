package com.firstapplication.dormapp.ui.fragments.admin

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.firstapplication.dormapp.databinding.FragmentNewsInfoBinding
import com.firstapplication.dormapp.ui.fragments.BasicFragment
import com.firstapplication.dormapp.ui.models.NewsModel

class NewsInfoFragment : BasicFragment() {

    private lateinit var binding: FragmentNewsInfoBinding

    @SuppressLint("SetTextI18n")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentNewsInfoBinding.inflate(inflater, container, false)

        val news = arguments?.getParcelable<NewsModel>(NEWS_TAG)!!
        binding.twTitle.text = news.title
        binding.twTime.text = "${news.hours} ${news.timeType}"
        binding.twDescription.text = news.description

        return binding.root
    }

    companion object {
        private const val NEWS_TAG = "NEWS"

        @JvmStatic
        fun newInstance(news: NewsModel): Fragment {
            val fragment = NewsInfoFragment()
            val args = Bundle()
            args.putParcelable(NEWS_TAG, news)

            fragment.arguments = args
            return fragment
        }
    }
}