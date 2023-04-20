package com.firstapplication.dormapp.data.models

import com.firstapplication.dormapp.enums.ConfirmedRegistration

data class StudentRegistrationEntity(
    val fullName: String,
    val imgSrc: String,
    val passNumber: Int,
    val roomNumber: Int,
    val hours: Double,
    val password: String,
    val confirmed: Int = ConfirmedRegistration.EMPTY.value
) {

    constructor(studentEntity: StudentEntity): this(
        studentEntity.fullName, studentEntity.imgSrc,
        studentEntity.passNumber, studentEntity.roomNumber,
        studentEntity.hours, studentEntity.password
    )
}