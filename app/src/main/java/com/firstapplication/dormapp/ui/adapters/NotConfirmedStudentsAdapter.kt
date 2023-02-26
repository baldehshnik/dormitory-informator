package com.firstapplication.dormapp.ui.adapters

import android.app.AlertDialog
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.firstapplication.dormapp.R
import com.firstapplication.dormapp.databinding.NotConfirmedStudentItemBinding
import com.firstapplication.dormapp.ui.interfacies.OnNotConfirmedStudentItemClickListener
import com.firstapplication.dormapp.ui.models.StudentModel
import com.firstapplication.dormapp.ui.models.StudentModel.Companion.NAME_DELIMITER

class NotConfirmedStudentsAdapter(
    private val listener: OnNotConfirmedStudentItemClickListener,
    private val students: MutableList<StudentModel>
) : RecyclerView.Adapter<NotConfirmedStudentsAdapter.NotConfirmedStudentViewHolder>() {

    class NotConfirmedStudentViewHolder(
        private val binding: NotConfirmedStudentItemBinding,
        private val listener: OnNotConfirmedStudentItemClickListener
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(student: StudentModel) = with(binding) {
            twStudentName.text = student.fullName.split(NAME_DELIMITER).joinToString(" ")
            twRoomNumber.text = student.roomNumber.toString()

            Glide.with(imgStudentIcon)
                .load(student.imgSrc)
                .fallback(R.drawable.ic_baseline_no_image)
                .placeholder(R.drawable.ic_baseline_no_image)
                .into(imgStudentIcon)

            val pass = student.passNumber.toString()
            val context = root.context
            btnConfirm.setOnClickListener {
                showDialog(
                    context,
                    context.resources.getString(R.string.to_confirm_reg_dialog, student.fullName),
                    model = student
                )
            }
            btnCancel.setOnClickListener {
                showDialog(
                    context,
                    context.resources.getString(R.string.to_cancel_reg_dialog, student.fullName),
                    pass = pass
                )
            }
            root.setOnClickListener { listener.onItemClick(student, adapterPosition) }
        }

        private fun showDialog(
            context: Context, message: String,
            pass: String? = null, model: StudentModel? = null
        ) {
            AlertDialog.Builder(context)
                .setIcon(R.drawable.ic_baseline_info)
                .setTitle(R.string.attention)
                .setMessage(message)
                .setPositiveButton(R.string.confirm) { _, _ ->
                    if (pass == null) {
                        listener.onConfirmClick(model ?: return@setPositiveButton, adapterPosition)
                    } else {
                        listener.onCancelClick(pass, adapterPosition)
                    }
                }
                .setNegativeButton(R.string.close, null)
                .create()
                .show()
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): NotConfirmedStudentViewHolder {
        val binding = NotConfirmedStudentItemBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )

        return NotConfirmedStudentViewHolder(binding, listener)
    }

    override fun onBindViewHolder(holder: NotConfirmedStudentViewHolder, position: Int) {
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