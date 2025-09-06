package com.agendally.app

import android.app.Application
import android.os.Build
import androidx.annotation.RequiresApi

class AcademicAllyApplication : Application() {
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate() {
        super.onCreate()
    }

}