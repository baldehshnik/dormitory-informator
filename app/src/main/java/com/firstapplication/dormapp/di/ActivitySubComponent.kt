package com.firstapplication.dormapp.di

import com.firstapplication.dormapp.ui.fragments.login.StudentLoginFragment
import dagger.Subcomponent

@Subcomponent
@ActivityScope
interface ActivitySubComponent {

    fun inject(fragment: StudentLoginFragment)

    @Subcomponent.Builder
    interface Builder {
        fun build(): ActivitySubComponent
    }

}