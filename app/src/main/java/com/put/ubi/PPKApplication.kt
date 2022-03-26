package com.put.ubi

import android.app.Application

class PPKApplication : Application() {
    val userPreferences = UserPreferences(this) // please use DI framework </3
}