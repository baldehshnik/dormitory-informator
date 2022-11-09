package com.firstapplication.dormapp.di

import dagger.Component
import javax.inject.Singleton

@Component(modules = [AppBindModule::class])
@Singleton
interface AppComponent {

    fun activityComponentBuilder(): ActivitySubComponent.Builder

    @Component.Builder
    interface Builder {
        fun build(): AppComponent
    }

}