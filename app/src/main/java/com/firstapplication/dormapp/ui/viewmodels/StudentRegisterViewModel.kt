package com.firstapplication.dormapp.ui.viewmodels

import androidx.annotation.StringRes
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.firstapplication.dormapp.Encryptor
import com.firstapplication.dormapp.R
import com.firstapplication.dormapp.data.interfacies.StudentRepository
import com.firstapplication.dormapp.di.ActivityScope
import com.firstapplication.dormapp.sealed.DatabaseResult
import com.firstapplication.dormapp.sealed.Error
import com.firstapplication.dormapp.sealed.Progress
import com.firstapplication.dormapp.ui.models.StudentModel
import com.firstapplication.dormapp.ui.models.StudentModel.Companion.NAME_DELIMITER
import com.firstapplication.dormapp.utils.setLiveValueWithMainContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@ActivityScope
class StudentRegisterViewModel(
    private val repository: StudentRepository
) : ViewModel() {

    private val _registerResponse = MutableLiveData<DatabaseResult>()
    val registerResponse: LiveData<DatabaseResult> get() = _registerResponse

    fun registerStudent(
        surname: String,
        name: String,
        patronymic: String,
        passNumber: String,
        roomNumber: String,
        password: String
    ) = viewModelScope.launch(Dispatchers.Default) {
        val data = listOf(surname, name, patronymic, passNumber, roomNumber, password)
        data.forEach {
            if (it.isEmpty()) {
                setErrorResponse(R.string.fields_must_be_filled)
                return@launch
            }
        }

        if (checkRegistrationData(passNumber, roomNumber, password)) {
            val encryptedPassword: String? = Encryptor().encrypt(password)
            if (encryptedPassword.isNullOrEmpty()) {
                setErrorResponse(R.string.password_encrypt_error)
                return@launch
            }

            registerStudentInDatabase(
                StudentModel(
                    fullName = "$surname$NAME_DELIMITER$name$NAME_DELIMITER$patronymic",
                    imgSrc = "android.resource//drawable/ic_baseline_no_image",
                    password = encryptedPassword,
                    passNumber = passNumber.toInt(),
                    roomNumber = roomNumber.toInt(),
                    hours = 0.0
                )
            )
        }
    }


    private fun registerStudentInDatabase(studentModel: StudentModel) {
        viewModelScope.launch(Dispatchers.IO) {
            setLiveValueWithMainContext(_registerResponse, Progress)
            val result = repository.registerStudent(studentModel.migrateToStudentEntity())
            setLiveValueWithMainContext(_registerResponse, result)
        }
    }

    private suspend fun setErrorResponse(@StringRes message: Int) {
        withContext(Dispatchers.Main) {
            _registerResponse.value = Error(message)
        }
    }

//    private suspend fun setRegisteredStudentResponseListener() {
//        (repository as StudentRepositoryImpl).registerResponse.transformWhile { value ->
//            emit(value)
//            value == Progress
//        }.collect {
//            withContext(Dispatchers.Main) {
//                _registerResponse.value = it
//            }
//        }
//    }

    private suspend fun checkRegistrationData(
        passNumber: String,
        roomNumber: String,
        password: String
    ): Boolean {

        try {
            passNumber.toInt()
            roomNumber.toInt()
        } catch (e: Exception) {
            setErrorResponse(R.string.typecast_error)
            return false
        }

        return if (password.length < 7) {
            setErrorResponse(R.string.password_is_short)
            false
        } else {
            true
        }
    }
}