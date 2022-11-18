package com.firstapplication.dormapp.ui.fragments.student

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.firstapplication.dormapp.DormApp
import com.firstapplication.dormapp.R
import com.firstapplication.dormapp.databinding.FragmentAccountBinding
import com.firstapplication.dormapp.ui.activity.MainActivity
import com.firstapplication.dormapp.ui.fragments.login.MainLoginFragment
import com.firstapplication.dormapp.ui.fragments.login.StudentLoginFragment.Companion.PASSWORD_KEY
import com.firstapplication.dormapp.ui.fragments.login.StudentLoginFragment.Companion.ROOM_KEY
import com.firstapplication.dormapp.ui.models.StudentVerifyModel
import com.firstapplication.dormapp.ui.viewmodels.StudentViewModel
import com.firstapplication.dormapp.ui.viewmodels.factories.StudentViewModelFactory
import javax.inject.Inject

class AccountFragment : Fragment() {

    private lateinit var binding: FragmentAccountBinding

    @Inject
    lateinit var viewModelFactory: StudentViewModelFactory.Factory

    private val viewModel: StudentViewModel by viewModels {
        viewModelFactory.create(activity?.application as DormApp)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        (activity as MainActivity).activityComponent.also { it?.inject(this) }
    }

    @SuppressLint("SetTextI18n")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAccountBinding.inflate(inflater, container, false)

        initUser(arguments?.getInt(USER_KEY, -1) ?: -1)

        lifecycleScope.launchWhenCreated {
            viewModel.userDataAccount.collect {
                val value = it.getValue() ?: return@collect
                if (value.passNumber == -1) return@collect

                when (value.passNumber) {
                    -2 -> {
                        Toast.makeText(requireContext(), "connection failed", Toast.LENGTH_SHORT).show()
                        setLoginFragment()
                    }
                    0 -> {
                        Toast.makeText(requireContext(), "User not found", Toast.LENGTH_SHORT).show()
                        setLoginFragment()
                    }
                    else -> {
                        binding.progressBar.visibility = View.GONE
                        binding.viewLayout.visibility = View.VISIBLE

                        Toast.makeText(requireContext(), "verified", Toast.LENGTH_SHORT).show()
                        binding.textView.text = "Room number is ${value.roomNumber}"
                    }
                }
            }
        }

        return binding.root
    }

    private fun setLoginFragment() {
        val sharedPreferences = requireActivity().getSharedPreferences(MainActivity.LOGIN_USER_PREF, Context.MODE_PRIVATE)
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

        val sharedPreferences = requireActivity().getSharedPreferences(MainActivity.LOGIN_USER_PREF, Context.MODE_PRIVATE)
        val room = sharedPreferences.getInt(ROOM_KEY, -1)
        val password = sharedPreferences.getString(PASSWORD_KEY, "")!!

        viewModel.getVerifiedUser(StudentVerifyModel(key, room, password))
    }

    companion object {
        private const val USER_KEY = "USER_KEY"

        fun newInstance(key: Int): AccountFragment {
            val fragment = AccountFragment()
            fragment.arguments = bundleOf(USER_KEY to key)
            return fragment
        }
    }

}