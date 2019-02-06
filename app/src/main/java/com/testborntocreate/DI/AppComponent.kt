package com.testborntocreate.DI

import com.testborntocreate.Activities.MainActivity
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = arrayOf(AppModule::class, NetworkModule::class))
interface AppComponent {

    fun inject(activity: MainActivity)
}