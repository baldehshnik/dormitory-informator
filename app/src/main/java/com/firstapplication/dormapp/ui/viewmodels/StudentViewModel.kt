package com.firstapplication.dormapp.ui.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.firstapplication.dormapp.data.interfacies.StudentRepository
import com.firstapplication.dormapp.data.models.SingleEvent
import com.firstapplication.dormapp.data.models.StudentEntity
import com.firstapplication.dormapp.data.models.StudentVerifyEntity
import com.firstapplication.dormapp.data.repositories.StudentRepositoryImpl
import com.firstapplication.dormapp.di.ActivityScope
import com.firstapplication.dormapp.ui.models.StudentModel
import com.firstapplication.dormapp.ui.models.StudentVerifyModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

@ActivityScope
class StudentViewModel(
    application: Application,
    private val repository: StudentRepository
) : AndroidViewModel(application) {

    private val _userDataAccount = MutableStateFlow(StudentEntity().migrateToStudentModel())
    val userDataAccount: StateFlow<StudentModel> get() = _userDataAccount.asStateFlow()

    private fun userDataAccountListener() {
        viewModelScope.launch(Dispatchers.IO) {
            (repository as StudentRepositoryImpl).userDataAccount.collect {
                val value = it.getValue()?.migrateToStudentModel() ?: return@collect
                _userDataAccount.value = value
            }
        }
    }

    fun getVerifiedUser(studentVerifyModel: StudentVerifyModel) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.getVerifiedUser(studentVerifyEntity = studentVerifyModel.migrateToStudentModel())
        }
    }

    init {
        userDataAccountListener()
    }

}