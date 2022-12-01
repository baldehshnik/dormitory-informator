package com.firstapplication.dormapp.di

import com.firstapplication.dormapp.ui.fragments.login.StudentLoginFragment
import com.firstapplication.dormapp.ui.fragments.student.AccountFragment
import com.firstapplication.dormapp.ui.fragments.student.NewsListFragment
import dagger.Subcomponent

@Subcomponent
@ActivityScope
interface ActivitySubComponent {

    fun inject(fragment: StudentLoginFragment)
    fun inject(fragment: AccountFragment)
    fun inject(fragment: NewsListFragment)

    @Subcomponent.Builder
    interface Builder {
        fun build(): ActivitySubComponent
    }

}