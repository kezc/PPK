package com.put.ubi

import android.app.Application
import com.put.ubi.data.FundsProvider

class PPKApplication : Application() {
    val userPreferences = UserPreferences(this, FundsProvider()) // please use DI framework </3
}