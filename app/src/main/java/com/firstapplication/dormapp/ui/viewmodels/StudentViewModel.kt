package com.firstapplication.dormapp.ui.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.firstapplication.dormapp.data.interfacies.StudentRepository
import com.firstapplication.dormapp.data.models.SingleEvent
import com.firstapplication.dormapp.data.models.StudentModel
import com.firstapplication.dormapp.data.repositories.StudentRepositoryImpl
import com.firstapplication.dormapp.di.ActivityScope
import com.firstapplication.dormapp.ui.models.StudentVerifyModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

@ActivityScope
class StudentViewModel(
    application: Application,
    private val repository: StudentRepository
) : AndroidViewModel(application) {

    val userDataAccount: StateFlow<SingleEvent<StudentModel>> get() = (repository as StudentRepositoryImpl).userDataAccount

    fun getVerifiedUser(studentVerifyModel: StudentVerifyModel) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.getVerifiedUser(studentModel = studentVerifyModel.migrateToStudentModel())
        }
    }

}