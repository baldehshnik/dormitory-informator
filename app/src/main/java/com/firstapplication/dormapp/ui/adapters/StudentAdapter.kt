package com.firstapplication.dormapp.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.firstapplication.dormapp.R
import com.firstapplication.dormapp.databinding.StudentItemBinding
import com.firstapplication.dormapp.ui.interfacies.OnStudentItemClickListener
import com.firstapplication.dormapp.ui.models.StudentModel

class StudentAdapter(
    private val listener: OnStudentItemClickListener
) : ListAdapter<StudentModel, StudentAdapter.StudentViewHolder>(StudentDiffUtil()) {

    class StudentViewHolder(
        private val binding: StudentItemBinding,
        private val listener: OnStudentItemClickListener
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(student: StudentModel) = with(binding) {
            twRoomNumber.text = student.roomNumber.toString()
            twStudentName.text = student.fullName

            btnMenu.setOnClickListener { view ->
                initMenu(view, student)
            }
        }

        private fun initMenu(view: View, student: StudentModel) {
            val menu = PopupMenu(view.context, view)
            menu.menuInflater.inflate(R.menu.student_item_menu, menu.menu)

            menu.setOnMenuItemClickListener { menuItem ->
                when (menuItem.itemId) {
                    R.id.itemConfirm -> {
                        listener.onConfirmClick(student)
                    }
                    R.id.itemCancel -> {
                        listener.onCancelClick(student.passNumber)
                    }
                }
                true
            }

            menu.show()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StudentViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return StudentViewHolder(StudentItemBinding.inflate(inflater, parent, false), listener)
    }

    override fun onBindViewHolder(holder: StudentViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

}

class StudentDiffUtil : DiffUtil.ItemCallback<StudentModel>() {
    override fun areItemsTheSame(oldItem: StudentModel, newItem: StudentModel): Boolean {
        return oldItem.passNumber == newItem.passNumber
    }

    override fun areContentsTheSame(oldItem: StudentModel, newItem: StudentModel): Boolean {
        return oldItem == newItem
    }
}