package org.wit.leeway.views.habitlist

import android.content.Intent
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.wit.leeway.main.LeewayApp
import org.wit.leeway.models.HabitModel
import org.wit.leeway.views.login.LoginView
import org.wit.leeway.views.habit.HabitView
import org.wit.leeway.views.map.HabitMapView

class HabitListPresenter(private val view: HabitListView) {

    var app: LeewayApp = view.application as LeewayApp
    private lateinit var refreshIntentLauncher : ActivityResultLauncher<Intent>
    private lateinit var editIntentLauncher : ActivityResultLauncher<Intent>

    init {
        registerEditCallback()
        registerRefreshCallback()
    }

    suspend fun getHabits() = app.habits.findAll()

    fun doAddHabit() {
        val launcherIntent = Intent(view, HabitView::class.java)
        editIntentLauncher.launch(launcherIntent)
    }

    fun doEditHabit(habit: HabitModel) {
        val launcherIntent = Intent(view, HabitView::class.java)
        launcherIntent.putExtra("habit_edit", habit)
        editIntentLauncher.launch(launcherIntent)
    }

    fun doShowHabitsMap() {
        val launcherIntent = Intent(view, HabitMapView::class.java)
        editIntentLauncher.launch(launcherIntent)
    }
    suspend fun doLogout(){
        FirebaseAuth.getInstance().signOut()
        app.habits.clear()
        val launcherIntent = Intent(view, LoginView::class.java)
        editIntentLauncher.launch(launcherIntent)
    }
    private fun registerRefreshCallback() {
        refreshIntentLauncher =
            view.registerForActivityResult(ActivityResultContracts.StartActivityForResult())
            {
                GlobalScope.launch(Dispatchers.Main){
                    getHabits()
                }
            }
    }

    private fun registerEditCallback() {
        editIntentLauncher =
            view.registerForActivityResult(ActivityResultContracts.StartActivityForResult())
            {  }

    }
}