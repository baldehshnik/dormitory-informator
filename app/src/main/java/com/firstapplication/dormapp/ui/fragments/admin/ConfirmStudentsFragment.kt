package com.firstapplication.dormapp.ui.fragments.admin

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.core.view.isVisible
import androidx.core.view.size
import androidx.fragment.app.viewModels
import com.firstapplication.dormapp.R
import com.firstapplication.dormapp.databinding.FragmentConfirmStudentsBinding
import com.firstapplication.dormapp.sealed.*
import com.firstapplication.dormapp.ui.activity.MainActivity
import com.firstapplication.dormapp.ui.adapters.NotConfirmedStudentsAdapter
import com.firstapplication.dormapp.ui.fragments.BasicFragment
import com.firstapplication.dormapp.ui.interfacies.OnNotConfirmedStudentItemClickListener
import com.firstapplication.dormapp.ui.models.StudentModel
import com.firstapplication.dormapp.ui.models.StudentModel.Companion.NAME_DELIMITER
import com.firstapplication.dormapp.ui.viewmodels.ConfirmStudentsViewModel
import com.firstapplication.dormapp.ui.viewmodels.factories.AdminVMFactory
import com.google.android.material.snackbar.Snackbar
import javax.inject.Inject

class ConfirmStudentsFragment : BasicFragment(), OnNotConfirmedStudentItemClickListener {

    private var selectedItem = -1
    private var onConfirmClick: Boolean? = null

    private lateinit var binding: FragmentConfirmStudentsBinding
    private lateinit var adapter: NotConfirmedStudentsAdapter

    @Inject
    lateinit var factory: AdminVMFactory

    private val viewModel: ConfirmStudentsViewModel by viewModels { factory }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        (activity as MainActivity).activityComponent?.inject(this)
        binding = FragmentConfirmStudentsBinding.inflate(inflater, container, false)

        switchBottomNavViewVisibility(R.id.adminBottomView, VISIBLE)
        switchBottomNavViewVisibility(R.id.studentBottomView, INVISIBLE)

        viewModel.readNotConfirmedStudents()

        viewModel.notSavedStudentsResult.observe(viewLifecycleOwner) {
            handleReadStudentsResult(it)
        }

        viewModel.confirmResult.observe(viewLifecycleOwner) {
            handleConfirmResult(it)
        }

        return binding.root
    }

    @Suppress("UNCHECKED_CAST")
    private fun handleReadStudentsResult(result: SelectResult) {
        when (result) {
            is ErrorSelect -> {
                Snackbar.make(
                    binding.rwNotConfirmedStudents,
                    getStringFromRes(R.string.error),
                    Snackbar.LENGTH_SHORT
                ).show()
                changeLayoutVisibility(isProgress = false)
            }
            is ProgressSelect -> {
                changeLayoutVisibility(isProgress = true)
            }
            is CorrectSelect<*> -> {
                adapter = NotConfirmedStudentsAdapter(
                    this, result.value as MutableList<StudentModel>
                )
                binding.rwNotConfirmedStudents.adapter = adapter
                changeLayoutVisibility(isProgress = false)
            }
            is Empty -> {
                if (binding.rwNotConfirmedStudents.size == 0) {
                    changeLayoutVisibility(isProgress = false, isEmpty = true)
                } else {
                    changeLayoutVisibility(isProgress = false)
                }
            }
        }
    }

    private fun handleConfirmResult(result: ChangeResult) {
        when (result) {
            is CorrectResult -> {
                adapter.removeSelectedItem(selectedItem)
                changeLayoutVisibility(isProgress = false)
                binding.rwNotConfirmedStudents.isClickable = true

                if (onConfirmClick == true) {
                    toast(getStringFromRes(R.string.student_confirmed))
                } else if (onConfirmClick == false) {
                    toast(getStringFromRes(R.string.student_not_confirmed))
                }
            }
            is ErrorResult -> {
                changeLayoutVisibility(isProgress = false)
                binding.rwNotConfirmedStudents.isClickable = true
                toast(getStringFromRes(R.string.database_error))
            }
            is ProgressResult -> {
                binding.rwNotConfirmedStudents.isClickable = false
            }
        }

        selectedItem = -1
        onConfirmClick = null
    }

    override fun onItemClick(model: StudentModel, position: Int) {
        selectedItem = position
        val nameParams = model.fullName.split(NAME_DELIMITER)
        AlertDialog.Builder(requireContext())
            .setCancelable(true)
            .setPositiveButton(R.string.validate) { _, _ ->
                onConfirmClick(model, position)
            }
            .setNegativeButton(R.string.invalidate) { _, _ ->
                onCancelClick(model.passNumber.toString(), position)
            }
            .setNeutralButton(R.string.ok, null)
            .setIcon(R.drawable.ic_baseline_info)
            .setTitle(R.string.student_info)
            .setMessage(
                resources.getString(
                    R.string.student_info_format, nameParams[0], nameParams[1], nameParams[2],
                    model.passNumber.toString(), model.roomNumber.toString(), model.hours.toString()
                )
            )
            .create()
            .show()
    }

    override fun onConfirmClick(model: StudentModel, position: Int) {
        selectedItem = position
        onConfirmClick = true
        viewModel.confirmStudent(model)
    }

    override fun onCancelClick(pass: String, position: Int) {
        selectedItem = position
        onConfirmClick = false
        viewModel.cancelStudent(pass)
    }

    private fun changeLayoutVisibility(isProgress: Boolean, isEmpty: Boolean = false) {
        with(binding) {
            progressBar.isVisible = isProgress
            imgEmpty.isVisible = isEmpty
            rwNotConfirmedStudents.isVisible = if (isEmpty) isProgress else !isProgress
        }
    }

    companion object {
        @JvmStatic
        fun newInstance(): ConfirmStudentsFragment {
            return ConfirmStudentsFragment()
        }
    }
}