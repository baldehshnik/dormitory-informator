package com.firstapplication.dormapp.ui.interfacies

import com.firstapplication.dormapp.ui.models.StudentModel

interface OnStudentItemClickListener {
    fun onConfirmClick(student: StudentModel)
    fun onCancelClick(passNumber: Int)
}