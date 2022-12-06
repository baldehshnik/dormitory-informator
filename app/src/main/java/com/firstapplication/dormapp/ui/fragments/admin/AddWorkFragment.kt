package com.firstapplication.dormapp.ui.fragments.admin

import android.os.Bundle
import android.text.Editable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import androidx.annotation.ArrayRes
import androidx.annotation.IdRes
import androidx.core.view.isInvisible
import com.firstapplication.dormapp.R
import com.firstapplication.dormapp.databinding.FragmentAddWorkBinding
import com.firstapplication.dormapp.ui.fragments.BasicFragment
import com.google.android.material.bottomnavigation.BottomNavigationView

class AddWorkFragment : BasicFragment() {

    private lateinit var binding: FragmentAddWorkBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAddWorkBinding.inflate(inflater, container, false)
        turnOnBottomNavView(R.id.adminBottomView)
        requireActivity().findViewById<BottomNavigationView>(R.id.studentBottomView).isInvisible = true

        binding.radioGroup.setOnCheckedChangeListener { _, checkedId ->
            radioGroupListener(id = checkedId)
        }

        binding.btnSave.setOnClickListener {
            saveClick()
        }

        initSpinner(R.array.time_types, binding.timeTypeSpinner)
        initSpinner(R.array.hours, binding.hoursSpinner, true)
        return binding.root
    }

    private fun radioGroupListener(@IdRes id: Int) {
        when (id) {
            R.id.rbWork -> {}
            else -> {}
        }
    }

    private fun initSpinner(@ArrayRes arrayId: Int, spinner: Spinner, listener: Boolean = false) {
        ArrayAdapter.createFromResource(
            requireContext(),
            arrayId,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.select_dialog_singlechoice)
            spinner.adapter = adapter
        }

        if (listener) {
            setSpinnerItemSelectedListener()
        }
    }

    private fun setSpinnerItemSelectedListener() {
        binding.hoursSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(adapter: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                binding.etTime.text = Editable.Factory.getInstance()
                    .newEditable(adapter?.selectedItem.toString())
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {}
        }
    }

    private fun saveClick() {

    }

    companion object {
        @JvmStatic
        fun newInstance(): AddWorkFragment {
            return AddWorkFragment()
        }
    }
}