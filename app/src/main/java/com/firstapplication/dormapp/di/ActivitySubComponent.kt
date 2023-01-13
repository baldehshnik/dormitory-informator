package com.firstapplication.dormapp.di

import com.firstapplication.dormapp.ui.fragments.admin.*
import com.firstapplication.dormapp.ui.fragments.login.StudentLoginFragment
import com.firstapplication.dormapp.ui.fragments.login.StudentRegisterFragment
import com.firstapplication.dormapp.ui.fragments.student.AccountFragment
import com.firstapplication.dormapp.ui.fragments.student.NewsListFragment
import dagger.Subcomponent

@Subcomponent
@ActivityScope
interface ActivitySubComponent {

    fun inject(fragment: StudentLoginFragment)
    fun inject(fragment: AccountFragment)
    fun inject(fragment: NewsListFragment)
    fun inject(fragment: StudentRegisterFragment)
    fun inject(fragment: NewsListAdminFragment)
    fun inject(fragment: AddWorkFragment)
    fun inject(fragment: NewsInfoFragment)
    fun inject(fragment: RespondingStudentsListFragment)
    fun inject(fragment: ConfirmStudentsFragment)

    @Subcomponent.Builder
    interface Builder {
        fun build(): ActivitySubComponent
    }
}