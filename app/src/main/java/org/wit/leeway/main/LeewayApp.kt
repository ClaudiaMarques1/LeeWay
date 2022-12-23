package org.wit.leeway.main

import android.app.Application
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import org.wit.leeway.R
import org.wit.leeway.models.HabitFireStore
import org.wit.leeway.models.HabitStore
import org.wit.leeway.room.HabitStoreRoom
import timber.log.Timber

class LeewayApp : Application() {

    lateinit var habits: HabitStore

    override fun onCreate() {
        super.onCreate()
        Timber.plant(Timber.DebugTree())
        habits = HabitFireStore(applicationContext)
        Timber.i("Leeway started")
    }
}