package com.firstapplication.dormapp.ui.fragments.login

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.core.os.bundleOf
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.firstapplication.dormapp.DormApp
import com.firstapplication.dormapp.Encryptor
import com.firstapplication.dormapp.R
import com.firstapplication.dormapp.databinding.FragmentStudentLoginBinding
import com.firstapplication.dormapp.extensions.saveStudent
import com.firstapplication.dormapp.ui.activity.MainActivity
import com.firstapplication.dormapp.ui.fragments.BasicFragment
import com.firstapplication.dormapp.ui.fragments.login.MainLoginFragment.Companion.STUDENT_INIT
import com.firstapplication.dormapp.ui.fragments.login.MainLoginFragment.Companion.STUDENT_LOGIN_KEY
import com.firstapplication.dormapp.ui.viewmodels.StudentLoginViewModel
import com.firstapplication.dormapp.ui.viewmodels.factories.StudentViewModelFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class StudentLoginFragment : BasicFragment() {

    private lateinit var binding: FragmentStudentLoginBinding

    @Inject
    lateinit var viewModelFactory: StudentViewModelFactory.Factory

    private val viewModel: StudentLoginViewModel by viewModels {
        viewModelFactory.create(activity?.application as DormApp)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentStudentLoginBinding.inflate(inflater, container, false)
        (activity as MainActivity).activityComponent.also { it?.inject(this) }

        with(binding) {
            btnConfirm.setOnClickListener {
                val passStr = binding.etPassNumber.text?.toString() ?: ""
                val roomStr = binding.etRoomNumber.text?.toString() ?: ""
                val password =binding.etPassword.text?.toString() ?: ""
                clickConfirm(passStr = passStr, roomStr = roomStr, password = password)
                switchEditTexts(value = false)
            }
            btnCancel.setOnClickListener {
                parentFragmentManager.popBackStack()
            }
        }

        lifecycleScope.launchWhenCreated {
            viewModel.verifiedUser.collect {
                withContext(Dispatchers.Main) {
                    binding.progressBar.visibility = View.GONE
                }

                val result = it.getValue() ?: return@collect
                checkVerifiedUser(result)
            }
        }

        return binding.root
    }

    private fun switchEditTexts(value: Boolean) = with(binding) {
        etPassNumber.isEnabled = value
        etRoomNumber.isEnabled = value
        etPassword.isEnabled = value
    }

    private fun checkVerifiedUser(result: Int) {
        when (result) {
            2 -> {
                AlertDialog.Builder(requireContext())
                    .setIcon(R.drawable.ic_baseline_info)
                    .setTitle(R.string.not_verified_title)
                    .setMessage(R.string.not_verified_info)
                    .setPositiveButton(R.string.ok, null)
                    .create()
                    .show()
            }
            1 -> {
                val sharedPreferences = requireActivity().getSharedPreferences(
                    MainActivity.LOGIN_USER_PREF,
                    Context.MODE_PRIVATE
                )

                val passStr = binding.etPassNumber.text.toString()
                val roomStr = binding.etRoomNumber.text.toString()
                val password = Encryptor().toEncrypt(
                    binding.etPassword.text.toString()
                ) ?: getStringFromRes(R.string.not_encrypted)

                sharedPreferences.saveStudent(passStr, roomStr, password)

//                sharedPreferences.edit()
//                    .putString(MainActivity.LOGIN_KEY, passStr)
//                    .putInt(ROOM_KEY, roomStr.toInt())
//                    .putString(PASSWORD_KEY, Encryptor().toEncrypt(password) ?: getStringFromRes(R.string.not_encrypted))
//                    .apply()

                parentFragmentManager.setFragmentResult(STUDENT_LOGIN_KEY, bundleOf(
                    STUDENT_INIT to true, PASS_KEY to passStr.toInt())
                )
                parentFragmentManager.popBackStack()
            }
            -1 -> {
                snackBar(binding.etPassNumber, getStringFromRes(R.string.not_verified))
            }
            -2 -> {
                snackBar(binding.etPassNumber, getStringFromRes(R.string.database_error))
            }
        }

        switchEditTexts(true)
    }

    private fun clickConfirm(passStr: String, roomStr: String, password: String) {
        try {
            val passNumber = passStr.toInt()
            val roomNumber = roomStr.toInt()
            viewModel.checkUser(passNumber, roomNumber, Encryptor().toEncrypt(password) ?: "")
            binding.progressBar.visibility = View.VISIBLE
        } catch (e: Exception) {
            toast(getStringFromRes(R.string.date_not_correct))
        }
    }

    companion object {
        const val ROOM_KEY = "ROOM_KEY"
        const val PASSWORD_KEY = "PASSWORD_KEY"
        const val PASS_KEY = "PASS_KEY"

        fun newInstance(): StudentLoginFragment {
            return StudentLoginFragment()
        }
    }
}