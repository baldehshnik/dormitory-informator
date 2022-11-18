package com.firstapplication.dormapp.di

import com.firstapplication.dormapp.ui.fragments.login.StudentLoginFragment
import com.firstapplication.dormapp.ui.fragments.student.AccountFragment
import dagger.Subcomponent

@Subcomponent
@ActivityScope
interface ActivitySubComponent {

    fun inject(fragment: StudentLoginFragment)
    fun inject(fragment: AccountFragment)

    @Subcomponent.Builder
    interface Builder {
        fun build(): ActivitySubComponent
    }

}