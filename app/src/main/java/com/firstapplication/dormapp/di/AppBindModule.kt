package com.firstapplication.dormapp.di

import com.firstapplication.dormapp.data.interfacies.AdminRepository
import com.firstapplication.dormapp.data.interfacies.StudentRepository
import com.firstapplication.dormapp.data.repositories.AdminRepositoryImpl
import com.firstapplication.dormapp.data.repositories.StudentRepositoryImpl
import dagger.Binds
import dagger.Module

@Module(subcomponents = [ActivitySubComponent::class])
interface AppBindModule {
    @Binds
    fun bindAdminRepositoryImplToAdminRepository(adminRepositoryImpl: AdminRepositoryImpl): AdminRepository

    @Binds
    fun bindStudentRepositoryImplToStudentRepository(studentRepositoryImpl: StudentRepositoryImpl): StudentRepository
}