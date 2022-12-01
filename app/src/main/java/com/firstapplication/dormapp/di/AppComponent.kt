package com.firstapplication.dormapp.di

import android.content.Context
import dagger.BindsInstance
import dagger.Component
import javax.inject.Singleton

@Component(modules = [AppBindModule::class, AppModule::class])
@Singleton
interface AppComponent {

    fun activityComponentBuilder(): ActivitySubComponent.Builder

    @Component.Builder
    interface Builder {
        @BindsInstance
        fun context(context: Context): Builder
        fun build(): AppComponent
    }

}