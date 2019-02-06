package com.testborntocreate.Activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.testborntocreate.DI.AppComponent
import com.testborntocreate.DI.AppModule
import com.testborntocreate.DI.DaggerAppComponent

class App: AppCompatActivity() {

    companion object {
        @JvmStatic
        lateinit var component: AppComponent
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        component = DaggerAppComponent.builder()
            .appModule(AppModule(this))
            .build()
    }
}