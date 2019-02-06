package com.testborntocreate.Activities

import android.app.Application
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.testborntocreate.DI.AppComponent
import com.testborntocreate.DI.AppModule
import com.testborntocreate.DI.DaggerAppComponent

class App: Application() {

    companion object {
        @JvmStatic
        lateinit var component: AppComponent
    }

    override fun onCreate() {
        super.onCreate()

        component = DaggerAppComponent.builder()
            .appModule(AppModule(this))
            .build()
    }
}