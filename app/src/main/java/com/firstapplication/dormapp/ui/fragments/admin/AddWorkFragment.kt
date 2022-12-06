package com.firstapplication.dormapp.ui.fragments.admin

import android.annotation.SuppressLint
import android.content.ContentResolver
import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.Spinner
import androidx.annotation.ArrayRes
import androidx.annotation.DrawableRes
import androidx.core.net.toUri
import androidx.core.view.isInvisible
import androidx.fragment.app.viewModels
import com.firstapplication.dormapp.DormApp
import com.firstapplication.dormapp.R
import com.firstapplication.dormapp.databinding.FragmentAddWorkBinding
import com.firstapplication.dormapp.sealed.CorrectResult
import com.firstapplication.dormapp.sealed.ErrorResult
import com.firstapplication.dormapp.sealed.ProgressResult
import com.firstapplication.dormapp.ui.activity.MainActivity
import com.firstapplication.dormapp.ui.fragments.BasicFragment
import com.firstapplication.dormapp.ui.models.NewsModel
import com.firstapplication.dormapp.ui.viewmodels.AdminViewModel
import com.firstapplication.dormapp.ui.viewmodels.factories.AdminViewModelFactory
import com.google.android.material.bottomnavigation.BottomNavigationView
import java.lang.Exception
import javax.inject.Inject

class AddWorkFragment : BasicFragment() {

    private var isEdit = false

    private lateinit var binding: FragmentAddWorkBinding

    @Inject
    lateinit var factory: AdminViewModelFactory.Factory

    private val viewModel: AdminViewModel by viewModels {
        factory.create(activity?.application as DormApp)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        (activity as MainActivity).activityComponent?.inject(this)

        binding = FragmentAddWorkBinding.inflate(inflater, container, false)
        requireActivity().findViewById<BottomNavigationView>(R.id.studentBottomView).isInvisible = true

        binding.btnSave.setOnClickListener {
            saveClick()
        }

        viewModel.createResult.observe(viewLifecycleOwner) { event ->
            when (val result = event.getValue()) {
                is ErrorResult -> {
                    Log.e(this::class.java.simpleName, result.message)
                    toast(getStringFromRes(R.string.error))
                }
                is CorrectResult -> {
                    toast(getStringFromRes(R.string.record_added))
                    binding.btnSave.isEnabled = true
                }
                is ProgressResult -> {
                    binding.btnSave.isEnabled = false
                }
                else -> return@observe
            }
        }

        initSpinner(R.array.time_types, binding.timeTypeSpinner)
        initSpinner(R.array.hours, binding.hoursSpinner, true)

        val news = arguments?.getParcelable<NewsModel>(NEWS_TAG)
        if (news != null) {
            isEdit = true
            initEditScreen(news = news)
        } else {
            turnOnBottomNavView(R.id.adminBottomView)
        }

        binding.etTime.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun afterTextChanged(p0: Editable?) {
                if (resources.getStringArray(R.array.hours).contains(p0.toString())) {
                    setHoursSpinnerSelection(p0.toString())
                } else {
                    binding.hoursSpinner.setSelection(0)
                }
            }
        })

        return binding.root
    }

    private fun setHoursSpinnerSelection(text: String) {
        try {
            val n = text.toInt()
            binding.hoursSpinner.setSelection(n)
        } catch (e: Exception) {
            binding.hoursSpinner.setSelection(0)
        }
    }

    @SuppressLint("ResourceType")
    private fun initEditScreen(news: NewsModel) = with(binding) {
        etTitle.setText(news.title)
        etTime.setText(news.hours.toString())
        etDescription.setText(news.description)

        when (news.timeType) {
            getStringFromRes(R.string.hours) -> {
                timeTypeSpinner.setSelection(0)
            }
            getStringFromRes(R.string.minutes) -> {
                timeTypeSpinner.setSelection(1)
            }
        }

        if (news.imgSrc.toUri() == getUriFromDrawable(R.drawable.ic_baseline_newspaper)) {
            radioGroup.check(R.id.rbNews)
        }

        if (resources.getStringArray(R.array.hours).contains(news.hours.toString())) {
            setHoursSpinnerSelection(news.hours.toString())
        } else {
            etTime.setText(news.hours.toString())
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
                if (binding.hoursSpinner.selectedItem.toString() == getStringFromRes(R.string.custom)) {
                    return
                }

                if (binding.etTime.text.toString() != binding.hoursSpinner.selectedItem.toString()) {
                    binding.etTime.text = Editable.Factory.getInstance()
                        .newEditable(adapter?.selectedItem.toString())
                }
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {}
        }
    }

    private fun saveClick() {
        if (!isBlankEditText(binding.etTitle)) return
        else if (!isBlankEditText(binding.etTime)) return

        val drawableRes = getDrawableByCheckedRadioButton()
        viewModel.createNews(
            title = binding.etTitle.text.toString(),
            img = getUriFromDrawable(drawableRes),
            time = binding.etTime.text.toString(),
            timeType = binding.etTimeType.text.toString(),
            description = binding.etDescription.text.toString()
        )

        setDefaultParameters()
    }

    private fun setDefaultParameters() {
        binding.etTitle.setText("")
        binding.etDescription.setText("")
        binding.radioGroup.check(R.id.rbWork)
        binding.hoursSpinner.setSelection(0)
        binding.timeTypeSpinner.setSelection(0)
    }

    private fun getDrawableByCheckedRadioButton() = if (binding.rbWork.isChecked)
        R.drawable.ic_baseline_work_active
    else
        R.drawable.ic_baseline_newspaper

    private fun getUriFromDrawable(@DrawableRes drawableId: Int): Uri {
        return Uri.parse(
            ContentResolver.SCHEME_ANDROID_RESOURCE
                    + "://" + requireContext().resources.getResourcePackageName(drawableId)
                    + '/' + requireContext().resources.getResourceTypeName(drawableId)
                    + '/' + requireContext().resources.getResourceEntryName(drawableId)
        )
    }

    private fun isBlankEditText(editText: EditText): Boolean {
        val etText = editText.text.toString()
        if (etText.isBlank()) {
            editText.error = getStringFromRes(R.string.cannot_be_empty)
            return false
        }

        return true
    }

    companion object {
        private const val NEWS_TAG = "NEWS"

        @JvmStatic
        fun newInstance(news: NewsModel? = null): AddWorkFragment {
            val fragment = AddWorkFragment()
            val args = Bundle()
            args.putParcelable(NEWS_TAG, news)

            fragment.arguments = args
            return fragment
        }
    }
}