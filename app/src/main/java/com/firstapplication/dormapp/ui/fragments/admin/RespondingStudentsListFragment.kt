package com.firstapplication.dormapp.ui.fragments.admin

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.core.view.size
import androidx.fragment.app.viewModels
import com.firstapplication.dormapp.R
import com.firstapplication.dormapp.databinding.FragmentRespondingStudentsListBinding
import com.firstapplication.dormapp.sealed.*
import com.firstapplication.dormapp.ui.activity.MainActivity
import com.firstapplication.dormapp.ui.adapters.StudentAdapter
import com.firstapplication.dormapp.ui.fragments.BasicFragment
import com.firstapplication.dormapp.ui.interfacies.OnStudentItemClickListener
import com.firstapplication.dormapp.ui.models.StudentModel
import com.firstapplication.dormapp.ui.viewmodels.RespondingStudentListViewModel
import com.firstapplication.dormapp.ui.viewmodels.factories.RespondingStudentsListVMFactory
import javax.inject.Inject

class RespondingStudentsListFragment : BasicFragment(), OnStudentItemClickListener {

    private var selectedItem = -1
    private var currentAction: Int? = null

    private lateinit var binding: FragmentRespondingStudentsListBinding
    private lateinit var adapter: StudentAdapter

    @Inject
    lateinit var factory: RespondingStudentsListVMFactory.Factory

    private val viewModel: RespondingStudentListViewModel by viewModels {
        factory.create(arguments?.getString(NEWS_ID, "").toString())
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        (activity as MainActivity).activityComponent?.inject(this)
        binding = FragmentRespondingStudentsListBinding.inflate(inflater, container, false)

        viewModel.respondingStudentsResult.observe(viewLifecycleOwner) { result ->
            checkRespondingStudentsResult(result)
        }

        viewModel.confirmStudentResponse.observe(viewLifecycleOwner) { result ->
            handleStudentsConfirmResult(result)
        }

        return binding.root
    }

    override fun onConfirmClick(student: StudentModel, selectedItem: Int) {
        val newsId = getCurrentNews(selectedItem) ?: return
        currentAction = confirmAction
        viewModel.confirmStudentResponse(newsId, student)
    }

    override fun onCancelClick(passNumber: Int, selectedItem: Int) {
        val newsId = getCurrentNews(selectedItem) ?: return
        currentAction = cancelAction
        viewModel.cancelStudentResponse(newsId, passNumber)
    }

    private fun getCurrentNews(selectedItem: Int): String? {
        val newsId = arguments?.getString(NEWS_ID) ?: ""
        if (newsId.isBlank()) {
            toast(getStringFromRes(R.string.news_not_found))
            return null
        }
        this.selectedItem = selectedItem
        return newsId
    }

    private fun handleStudentsConfirmResult(result: DatabaseResult) {
        when (result) {
            Progress -> return
            is Error -> toast(getStringFromRes(R.string.error))
            is Correct<*> -> {
                adapter.removeSelectedItem(selectedItem)
                handleCurrentAction()
            }
        }
        selectedItem = -1
        currentAction = null
    }

    private fun handleCurrentAction() {
        toast(
            when (currentAction) {
                confirmAction -> getStringFromRes(R.string.user_confirmed)
                cancelAction -> getStringFromRes(R.string.user_canceled)
                else -> return
            }
        )
    }

    @Suppress("UNCHECKED_CAST")
    private fun checkRespondingStudentsResult(result: SelectResult) {
        when (result) {
            is ErrorSelect -> {
                changeLayout(progressBarVisibility = false)
                toast(getStringFromRes(R.string.error))
            }
            is Empty -> {
                if (binding.rwRespondingStudents.size == 0) {
                    changeLayout(progressBarVisibility = false, imgEmptyVisibility = true)
                } else {
                    changeLayout(progressBarVisibility = false)
                }
            }
            is ProgressSelect -> {
                changeLayout(rwRespondingStudentsVisibility = false)
            }
            is CorrectSelect<*> -> {
                changeLayout(progressBarVisibility = false)
                adapter = StudentAdapter(this, result.value as MutableList<StudentModel>)
                binding.rwRespondingStudents.adapter = adapter
            }
        }
    }

    private fun changeLayout(
        progressBarVisibility: Boolean = true,
        rwRespondingStudentsVisibility: Boolean = true,
        imgEmptyVisibility: Boolean = false
    ) {
        binding.progressBar.isVisible = progressBarVisibility
        binding.rwRespondingStudents.isVisible = rwRespondingStudentsVisibility
        binding.imgEmpty.isVisible = imgEmptyVisibility
    }

    companion object {
        @JvmStatic
        private val NEWS_ID = "NEWS ID"

        @JvmStatic
        private val confirmAction = 1

        @JvmStatic
        private val cancelAction = -1

        @JvmStatic
        fun newInstance(newsId: String): RespondingStudentsListFragment {
            val fragment = RespondingStudentsListFragment()

            val args = Bundle()
            args.putString(NEWS_ID, newsId)

            fragment.arguments = args
            return fragment
        }
    }
}