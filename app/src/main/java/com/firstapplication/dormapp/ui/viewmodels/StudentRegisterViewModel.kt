package com.firstapplication.dormapp.ui.viewmodels

import android.app.Application
import androidx.annotation.StringRes
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.firstapplication.dormapp.Encryptor
import com.firstapplication.dormapp.R
import com.firstapplication.dormapp.data.interfacies.StudentRepository
import com.firstapplication.dormapp.data.repositories.StudentRepositoryImpl
import com.firstapplication.dormapp.di.ActivityScope
import com.firstapplication.dormapp.sealed.ChangeResponse
import com.firstapplication.dormapp.sealed.Correct
import com.firstapplication.dormapp.ui.models.StudentModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.transformWhile
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@ActivityScope
class StudentRegisterViewModel(
    application: Application,
    private val repository: StudentRepository
) : AndroidViewModel(application) {

    private val _registerResponse = MutableLiveData<ChangeResponse>()
    val registerResponse: LiveData<ChangeResponse> get() = _registerResponse

    fun registerStudent(
        surname: String,
        name: String,
        patronymic: String,
        passNumber: String,
        roomNumber: String,
        password: String
    ) {
        viewModelScope.launch {
            val data = listOf(surname, name, patronymic, passNumber, roomNumber, password)
            data.forEach {
                if (it.isEmpty()) {
                    setErrorResponse(R.string.fields_must_be_filled)
                    return@launch
                }
            }

            if (checkRegistrationData(passNumber, roomNumber, password)) {
                val encryptedPassword: String? = Encryptor().toEncrypt(password)
                if (encryptedPassword.isNullOrEmpty()) {
                    setErrorResponse(R.string.password_encrypt_error)
                    return@launch
                }

                registerStudentInDatabase(
                    studentModel = StudentModel(
                        fullName = "$surname $name $patronymic",
                        password = encryptedPassword,
                        passNumber = passNumber.toInt(),
                        roomNumber = roomNumber.toInt(),
                        hours = 0.0
                    )
                )
            }
        }
    }

    private fun registerStudentInDatabase(studentModel: StudentModel) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.registerStudent(studentModel.migrateToStudentEntity())
            setRegisteredStudentResponseListener()
        }
    }

    private suspend fun setErrorResponse(@StringRes message: Int) {
        withContext(Dispatchers.Main) {
            _registerResponse.value = com.firstapplication.dormapp.sealed.Error(message)
        }
    }

    private suspend fun setRegisteredStudentResponseListener() {
        (repository as StudentRepositoryImpl).registerResponse.transformWhile { value ->
            emit(value)
            value !is Correct && value !is Error
        }.collect {
            withContext(Dispatchers.Main) {
                _registerResponse.value = it
            }
        }
    }

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