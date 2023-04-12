package com.firstapplication.dormapp.ui.fragments.login

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ScrollView
import androidx.appcompat.app.AlertDialog
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import com.firstapplication.dormapp.R
import com.firstapplication.dormapp.databinding.FragmentRegisterStudentBinding
import com.firstapplication.dormapp.sealed.Correct
import com.firstapplication.dormapp.sealed.Error
import com.firstapplication.dormapp.sealed.Progress
import com.firstapplication.dormapp.ui.activity.MainActivity
import com.firstapplication.dormapp.ui.fragments.BasicFragment
import com.firstapplication.dormapp.ui.viewmodels.StudentRegisterViewModel
import com.firstapplication.dormapp.ui.viewmodels.factories.StudentViewModelFactory
import javax.inject.Inject

class StudentRegisterFragment : BasicFragment() {

    private lateinit var binding: FragmentRegisterStudentBinding

    @Inject
    lateinit var factory: StudentViewModelFactory

    private val viewModel: StudentRegisterViewModel by viewModels { factory }

    @SuppressLint("UnspecifiedImmutableFlag")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        (activity as MainActivity).activityComponent?.inject(this)
        binding = FragmentRegisterStudentBinding.inflate(inflater, container, false)

        val scrollView = ScrollView(requireContext())
        scrollView.addView(binding.root)

        binding.btnRegister.setOnClickListener { onRegisterClick() }
        binding.btnBack.setOnClickListener { onCancelClick() }

        viewModel.registerResponse.observe(viewLifecycleOwner) { response ->
            when (response) {
                is Error -> {
                    changeLayoutVisibility(visibility = false)
                    snackBar(binding.etSurname, getStringFromRes(response.message))
                }
                is Correct<*> -> {
                    changeLayoutVisibility(visibility = false)
                    showRegistrationInfoAlertDialog()
                }
                is Progress -> {
                    if (!binding.progressBar.isVisible) changeLayoutVisibility(visibility = true)
                }
            }
        }

        return scrollView
    }

    private fun showRegistrationInfoAlertDialog() {
        AlertDialog.Builder(requireContext())
            .setCancelable(false)
            .setTitle(R.string.registration_info_title)
            .setIcon(R.drawable.ic_baseline_info)
            .setMessage(R.string.registration_info)
            .setPositiveButton(R.string.ok) { dialog, _ ->
                dialog.cancel()
                onCancelClick()
            }
            .create()
            .show()
    }

    private fun changeLayoutVisibility(visibility: Boolean) = with(binding) {
        progressBar.isVisible = visibility
        registrationLayout.isVisible = !visibility
    }

    private fun onRegisterClick() = with(binding) {
        AlertDialog.Builder(requireContext())
            .setCancelable(true)
            .setTitle(R.string.confirm_reg_title)
            .setMessage(R.string.confirm_reg_info)
            .setIcon(R.drawable.ic_baseline_info)
            .setPositiveButton(R.string.confirm) { _, _ ->
                viewModel.registerStudent(
                    surname = etSurname.text.toString(),
                    name = etName.text.toString(),
                    patronymic = etPatronymic.text.toString(),
                    passNumber = etPassNumber.text.toString(),
                    roomNumber = etRoomNumber.text.toString(),
                    password = etPassword.text.toString()
                )
            }
            .setNegativeButton(R.string.cancel, null)
            .create()
            .show()
    }

    private fun onCancelClick() {
        parentFragmentManager.popBackStack()
    }

    companion object {
        @JvmStatic
        fun newInstance(): StudentRegisterFragment {
            return StudentRegisterFragment()
        }
    }
}