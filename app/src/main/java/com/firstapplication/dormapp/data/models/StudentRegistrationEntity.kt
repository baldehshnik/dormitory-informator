package com.firstapplication.dormapp.data.models

import com.firstapplication.dormapp.enums.ConfirmedRegistration

data class StudentRegistrationEntity(
    val fullName: String = "None None None",
    val imgSrc: String = "android.resource//drawable/ic_baseline_no_image",
    val passNumber: Int = -1,
    val roomNumber: Int = -1,
    val hours: Double = 0.0,
    val password: String = "",
    val confirmed: Int = ConfirmedRegistration.EMPTY.value
) {

    constructor(studentEntity: StudentEntity): this(
        studentEntity.fullName, studentEntity.imgSrc,
        studentEntity.passNumber, studentEntity.roomNumber,
        studentEntity.hours, studentEntity.password
    )

    fun toStudentEntity(): StudentEntity {
        return StudentEntity(fullName, imgSrc, passNumber, roomNumber, hours, password)
    }

    fun getStudentVerifyEntity(): StudentVerifyEntity {
        return StudentVerifyEntity(passNumber, roomNumber, password)
    }
}