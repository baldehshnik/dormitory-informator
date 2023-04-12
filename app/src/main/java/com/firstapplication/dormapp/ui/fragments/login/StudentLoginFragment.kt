package com.firstapplication.dormapp.ui.fragments.login

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ScrollView
import androidx.annotation.StringRes
import androidx.appcompat.app.AlertDialog
import androidx.core.content.res.ResourcesCompat
import androidx.core.os.bundleOf
import androidx.fragment.app.viewModels
import com.bumptech.glide.Glide
import com.firstapplication.dormapp.Encryptor
import com.firstapplication.dormapp.R
import com.firstapplication.dormapp.databinding.FragmentStudentLoginBinding
import com.firstapplication.dormapp.extensions.saveStudent
import com.firstapplication.dormapp.sealed.*
import com.firstapplication.dormapp.ui.activity.LOGIN_USER_PREF
import com.firstapplication.dormapp.ui.activity.MainActivity
import com.firstapplication.dormapp.ui.fragments.BasicFragment
import com.firstapplication.dormapp.ui.fragments.login.MainLoginFragment.Companion.STUDENT_INIT
import com.firstapplication.dormapp.ui.fragments.login.MainLoginFragment.Companion.STUDENT_LOGIN_KEY
import com.firstapplication.dormapp.ui.viewmodels.StudentLoginViewModel
import com.firstapplication.dormapp.ui.viewmodels.factories.StudentViewModelFactory
import javax.inject.Inject

class StudentLoginFragment : BasicFragment() {

    private lateinit var binding: FragmentStudentLoginBinding

    @Inject
    lateinit var viewModelFactory: StudentViewModelFactory

    private val viewModel: StudentLoginViewModel by viewModels { viewModelFactory }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentStudentLoginBinding.inflate(inflater, container, false)
        (activity as MainActivity).activityComponent.also { it?.inject(this) }

        val scrollView = ScrollView(requireContext())
        scrollView.addView(binding.root)

        Glide.with(requireContext())
            .load(ResourcesCompat.getDrawable(resources, R.drawable.dormitory_text, null))
            .into(binding.dormitoryTextImage)

        with(binding) {
            btnConfirm.setOnClickListener {
                val passStr = etPassNumber.text?.toString() ?: ""
                val roomStr = etRoomNumber.text?.toString() ?: ""
                val password = etPassword.text?.toString() ?: ""
                clickConfirm(passStr, roomStr, password)
                switchEditTexts(false)
            }
            btnBack.setOnClickListener {
                parentFragmentManager.popBackStack()
            }
        }

        viewModel.verifiedUser.observe(viewLifecycleOwner) {
            checkVerifiedUser(it)
        }

        return scrollView
    }

    private fun switchEditTexts(value: Boolean) = with(binding) {
        etPassNumber.isEnabled = value
        etRoomNumber.isEnabled = value
        etPassword.isEnabled = value
    }

    private fun checkVerifiedUser(result: LoginStudentResult) {
        when (result) {
            ProgressLoginResult -> {
                return
            }
            DeletedLoginResult -> {
                showNotVerifiedAlertDialog(R.string.not_verified_deleted)
            }
            NotVerifiedResult -> {
                showNotVerifiedAlertDialog(R.string.not_verified_info)
            }
            CorrectLoginResult -> {
                val sharedPreferences = requireActivity().getSharedPreferences(
                    LOGIN_USER_PREF,
                    Context.MODE_PRIVATE
                )

                val passStr = binding.etPassNumber.text.toString()
                val roomStr = binding.etRoomNumber.text.toString()
                val password = Encryptor().encrypt(binding.etPassword.text.toString())

                if (password == null) {
                    toast(getStringFromRes(R.string.not_encrypted))
                    return
                }

                sharedPreferences.saveStudent(passStr, roomStr, password)

                parentFragmentManager.setFragmentResult(
                    STUDENT_LOGIN_KEY, bundleOf(
                        STUDENT_INIT to true, PASS_KEY to passStr.toInt()
                    )
                )
                parentFragmentManager.popBackStack()
            }
            NotFoundResult -> {
                snackBar(binding.etPassNumber, getStringFromRes(R.string.not_verified))
            }
            DbErrorResult -> {
                snackBar(binding.etPassNumber, getStringFromRes(R.string.database_error))
            }
        }

        switchEditTexts(true)
    }

    private fun showNotVerifiedAlertDialog(@StringRes message: Int) {
        AlertDialog.Builder(requireContext())
            .setIcon(R.drawable.ic_baseline_info)
            .setTitle(R.string.not_verified_title)
            .setMessage(message)
            .setPositiveButton(R.string.ok, null)
            .create()
            .show()
    }

    private fun clickConfirm(passStr: String, roomStr: String, password: String) {
        try {
            val passNumber = passStr.toInt()
            val roomNumber = roomStr.toInt()
            viewModel.checkUser(passNumber, roomNumber, Encryptor().encrypt(password) ?: "")
        } catch (e: Exception) {
            toast(getStringFromRes(R.string.date_not_correct))
        }
    }

    companion object {
        const val ROOM_KEY = "ROOM_KEY"
        const val PASSWORD_KEY = "PASSWORD_KEY"
        const val PASS_KEY = "PASS_KEY"

        @JvmStatic
        fun newInstance(): StudentLoginFragment {
            return StudentLoginFragment()
        }
    }
}