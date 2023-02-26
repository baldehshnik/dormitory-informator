package com.firstapplication.dormapp.ui.fragments.student

import android.animation.LayoutTransition
import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.Toolbar
import androidx.core.os.bundleOf
import androidx.core.view.children
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.firstapplication.dormapp.DormApp
import com.firstapplication.dormapp.R
import com.firstapplication.dormapp.databinding.FragmentAccountBinding
import com.firstapplication.dormapp.ui.activity.MainActivity
import com.firstapplication.dormapp.ui.adapters.NewsAdapter
import com.firstapplication.dormapp.ui.fragments.BasicFragment
import com.firstapplication.dormapp.ui.fragments.login.MainLoginFragment
import com.firstapplication.dormapp.ui.fragments.login.StudentLoginFragment.Companion.PASSWORD_KEY
import com.firstapplication.dormapp.ui.fragments.login.StudentLoginFragment.Companion.ROOM_KEY
import com.firstapplication.dormapp.ui.models.StudentModel
import com.firstapplication.dormapp.ui.models.StudentModel.Companion.NAME_DELIMITER
import com.firstapplication.dormapp.ui.models.StudentVerifyModel
import com.firstapplication.dormapp.ui.viewmodels.AccountViewModel
import com.firstapplication.dormapp.ui.viewmodels.factories.StudentViewModelFactory
import com.google.android.material.bottomnavigation.BottomNavigationView
import javax.inject.Inject

class AccountFragment : BasicFragment() {
    private var studentInfo: StudentModel? = null

    private lateinit var binding: FragmentAccountBinding

    @Inject
    lateinit var viewModelFactory: StudentViewModelFactory.Factory

    private val viewModel: AccountViewModel by viewModels {
        viewModelFactory.create(activity?.application as DormApp)
    }

    @SuppressLint("SetTextI18n")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = FragmentAccountBinding.inflate(inflater, container, false)
        (activity as MainActivity).activityComponent.also { it?.inject(this) }

        requireActivity().findViewById<Toolbar>(R.id.toolbar).isVisible = false

        binding.root.children.forEach { it.isVisible = false }
        binding.progressBar.isVisible = true

        if (savedInstanceState == null) {
            initUser(arguments?.getInt(USER_KEY, -1) ?: -1)
        } else {
            studentInfo = savedInstanceState.getParcelable(SAVED_USER)
            if (studentInfo == null) initUser(arguments?.getInt(USER_KEY, -1) ?: -1)
            else initUI(studentInfo!!)
        }

        requireActivity().findViewById<BottomNavigationView>(R.id.studentBottomView)?.visibility =
            View.VISIBLE

        lifecycleScope.launchWhenCreated {
            dbResponse()
        }

        val adapter = NewsAdapter()
        binding.rwSavedNews.adapter = adapter

        viewModel.savedNews.observe(viewLifecycleOwner) { newsModels ->
            adapter.submitList(newsModels)
            binding.rwSavedNews.isVisible = true
            binding.progressBarNews.isVisible = false
        }

        binding.root.layoutTransition.enableTransitionType(LayoutTransition.CHANGING)
        return binding.root
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putParcelable(SAVED_USER, studentInfo)
        studentInfo = null
    }

    private suspend fun dbResponse() {
        viewModel.userDataAccount.collect { value ->
            if (value.passNumber == -1) return@collect
            when (value.passNumber) {
                -2 -> {
                    toast(getStringFromRes(R.string.connection_failed))
                    setLoginFragment()
                }
                0 -> {
                    toast(getStringFromRes(R.string.user_not_found))
                    setLoginFragment()
                }
                else -> {
                    studentInfo = value
                    viewModel.addSavedNewsListener()
                    initUI(value)
                }
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private fun initUI(model: StudentModel) {
        binding.root.children.forEach { it.isVisible = true }
        binding.progressBar.isVisible = false
        binding.rwSavedNews.isVisible = false
        requireActivity().findViewById<Toolbar>(R.id.toolbar).isVisible = true

        binding.twPassNumber.text = model.passNumber.toString()
        binding.twRoomNumber.text = model.roomNumber.toString()
        binding.twHours.text = model.hours.toString()

        val nameParameters = model.fullName.split(NAME_DELIMITER)
        binding.twFirstName.text = nameParameters[0]
        binding.twLastNameAndPatronymic.text = nameParameters[1] + " " + nameParameters[2]

        Glide.with(requireContext())
            .load(model.imgSrc)
            .placeholder(R.drawable.ic_baseline_no_image)
            .into(binding.imgStudent)
    }

    private fun setLoginFragment() {
        val sharedPreferences = requireActivity().getSharedPreferences(
            MainActivity.LOGIN_USER_PREF,
            Context.MODE_PRIVATE
        )
        sharedPreferences.edit()
            .clear()
            .apply()

        parentFragmentManager.beginTransaction()
            .add(R.id.fragmentContainer, MainLoginFragment.newInstance())
            .remove(this)
            .commit()
    }

    private fun initUser(key: Int) {
        if (key == -1) setLoginFragment()

        val sharedPreferences = requireActivity().getSharedPreferences(
            MainActivity.LOGIN_USER_PREF,
            Context.MODE_PRIVATE
        )
        val room = sharedPreferences.getInt(ROOM_KEY, -1)
        val password = sharedPreferences.getString(PASSWORD_KEY, "")!!

        viewModel.getVerifiedUser(StudentVerifyModel(key, room, password))
    }

    companion object {
        private const val USER_KEY = "USER_KEY"
        private const val SAVED_USER = "SAVED_USER_KEY"

        @JvmStatic
        fun newInstance(key: Int): AccountFragment {
            val fragment = AccountFragment()
            fragment.arguments = bundleOf(USER_KEY to key)
            return fragment
        }
    }
}