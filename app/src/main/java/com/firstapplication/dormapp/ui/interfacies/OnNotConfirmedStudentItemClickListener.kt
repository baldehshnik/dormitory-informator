package com.firstapplication.dormapp.ui.interfacies

interface OnNotConfirmedStudentItemClickListener {
    fun onItemClick(pass: String, position: Int)
    fun onConfirmClick(pass: String, position: Int)
    fun onCancelClick(pass: String, position: Int)
}