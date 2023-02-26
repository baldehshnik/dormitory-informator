package com.firstapplication.dormapp.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.firstapplication.dormapp.R
import com.firstapplication.dormapp.databinding.StudentItemBinding
import com.firstapplication.dormapp.ui.interfacies.OnStudentItemClickListener
import com.firstapplication.dormapp.ui.models.StudentModel
import com.firstapplication.dormapp.ui.models.StudentModel.Companion.NAME_DELIMITER

class StudentAdapter(
    private val listener: OnStudentItemClickListener,
    private val students: MutableList<StudentModel>
) : ListAdapter<StudentModel, StudentAdapter.StudentViewHolder>(StudentDiffUtil()) {

    class StudentViewHolder(
        private val binding: StudentItemBinding,
        private val listener: OnStudentItemClickListener
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(student: StudentModel) = with(binding) {
            twRoomNumber.text = student.roomNumber.toString()
            twStudentName.text = student.fullName.split(NAME_DELIMITER).joinToString(" ")

            Glide.with(imgStudentIcon)
                .load(student.imgSrc)
                .placeholder(R.drawable.ic_baseline_no_image)
                .error(R.drawable.ic_baseline_no_image)
                .into(imgStudentIcon)

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
                        listener.onConfirmClick(student, adapterPosition)
                    }
                    R.id.itemCancel -> {
                        listener.onCancelClick(student.passNumber, adapterPosition)
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
        holder.bind(students[position])
    }

    override fun getItemCount(): Int {
        return students.size
    }

    fun removeSelectedItem(selectedItem: Int) {
        if (selectedItem >= 0 && selectedItem < students.size) {
            students.removeAt(selectedItem)
            notifyItemRemoved(selectedItem)
        }
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