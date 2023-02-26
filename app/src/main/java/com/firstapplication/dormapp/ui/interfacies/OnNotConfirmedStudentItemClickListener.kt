package com.firstapplication.dormapp.ui.interfacies

import com.firstapplication.dormapp.ui.models.StudentModel

interface OnNotConfirmedStudentItemClickListener {
    fun onItemClick(model: StudentModel, position: Int)
    fun onConfirmClick(model: StudentModel, position: Int)
    fun onCancelClick(pass: String, position: Int)
}