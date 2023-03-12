package com.firstapplication.dormapp.ui.fragments.student

import android.animation.LayoutTransition
import android.annotation.SuppressLint
import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.Toolbar
import androidx.core.os.bundleOf
import androidx.core.view.children
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import com.bumptech.glide.Glide
import com.firstapplication.dormapp.R
import com.firstapplication.dormapp.databinding.FragmentAccountBinding
import com.firstapplication.dormapp.sealed.*
import com.firstapplication.dormapp.ui.activity.LOGIN_USER_PREF
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
import javax.inject.Inject

class AccountFragment : BasicFragment() {

    private var studentInfo: StudentModel? = null

    private lateinit var binding: FragmentAccountBinding

    @Inject
    lateinit var viewModelFactory: StudentViewModelFactory

    private val viewModel: AccountViewModel by viewModels { viewModelFactory }

    @SuppressLint("SetTextI18n")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAccountBinding.inflate(inflater, container, false)
        (activity as MainActivity).activityComponent.also { it?.inject(this) }

        switchToolBarVisibility(GONE)
        changeScreenVisibility(isLoadingMode = true)

        if (savedInstanceState == null) {
            initUser(arguments?.getInt(USER_KEY, -1) ?: -1)
        } else {
            studentInfo = savedInstanceState.getParcelable(SAVED_USER)
            if (studentInfo == null) initUser(arguments?.getInt(USER_KEY, -1) ?: -1)
            else initUI(studentInfo!!)
        }

        switchBottomNavViewVisibility(R.id.studentBottomView, VISIBLE)

        viewModel.userDataAccount.observe(viewLifecycleOwner) {
            dbResponse(it)
        }

        val adapter = NewsAdapter()
        binding.rwSavedNews.adapter = adapter

        viewModel.savedNews.observe(viewLifecycleOwner) { newsModels ->
            adapter.submitList(newsModels)
            binding.rwSavedNews.isVisible = true
            binding.progressBarNews.isVisible = false
        }

        binding.root.layoutTransition.enableTransitionType(LayoutTransition.CHANGING)

        binding.myBTN.setOnClickListener {
            val sharedPreferences = context?.getSharedPreferences(LOGIN_USER_PREF, MODE_PRIVATE)
            sharedPreferences?.edit()?.clear()?.apply()
        }

        return binding.root
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putParcelable(SAVED_USER, studentInfo)
        studentInfo = null
    }

    private fun dbResponse(result: DatabaseResult) {
        when (result) {
            Progress -> changeScreenVisibility(isLoadingMode = true)
            is Error -> {
                toast(getStringFromRes(result.message))
                changeScreenVisibility(isLoadingMode = false)
            }
            is Correct<*> -> {
                val student = result.value as StudentModel
                studentInfo = student
                viewModel.addSavedNewsListener()
                initUI(student)
                changeScreenVisibility(isLoadingMode = false)
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
            LOGIN_USER_PREF,
            MODE_PRIVATE
        )
        sharedPreferences.edit()
            .clear()
            .apply()

        parentFragmentManager.beginTransaction()
            .add(R.id.fragmentContainer, MainLoginFragment.newInstance())
            .remove(this)
            .commit()

        val activity = activity
        if (activity is MainActivity) activity.setNewCurrentUserType(this, NoOne)
    }

    private fun initUser(key: Int) {
        if (key == -1) setLoginFragment()

        val sharedPreferences = requireActivity().getSharedPreferences(
            LOGIN_USER_PREF,
            MODE_PRIVATE
        )
        val room = sharedPreferences.getInt(ROOM_KEY, -1)
        val password = sharedPreferences.getString(PASSWORD_KEY, "")!!

        viewModel.getVerifiedUser(StudentVerifyModel(key, room, password))
    }

    private fun changeScreenVisibility(isLoadingMode: Boolean) {
        binding.root.children.forEach { it.isVisible = !isLoadingMode }
        binding.progressBar.isVisible = isLoadingMode
    }

    companion object {
        @JvmStatic
        private val USER_KEY = "USER_KEY"

        @JvmStatic
        private val SAVED_USER = "SAVED_USER_KEY"

        @JvmStatic
        fun newInstance(key: Int): AccountFragment {
            val fragment = AccountFragment()
            fragment.arguments = bundleOf(USER_KEY to key)
            return fragment
        }
    }
}