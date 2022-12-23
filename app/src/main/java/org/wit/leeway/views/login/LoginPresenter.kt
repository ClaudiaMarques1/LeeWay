package org.wit.leeway.views.login

import android.content.Intent
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import com.google.firebase.auth.FirebaseAuth
import org.wit.leeway.main.LeewayApp
import org.wit.leeway.models.HabitFireStore
import org.wit.leeway.views.habitlist.HabitListView

class LoginPresenter (val view: LoginView)  {
    private lateinit var loginIntentLauncher : ActivityResultLauncher<Intent>
    var app: LeewayApp = view.application as LeewayApp
    var auth: FirebaseAuth = FirebaseAuth.getInstance()
    var fireStore: HabitFireStore? = null

    init{
        registerLoginCallback()
        if (app.habits is HabitFireStore) {
            fireStore = app.habits as HabitFireStore
        }
    }


    fun doLogin(email: String, password: String) {
        view.showProgress()
        auth.signInWithEmailAndPassword(email, password).addOnCompleteListener(view) { task ->
            if (task.isSuccessful) {
                if (fireStore != null) {
                    fireStore!!.fetchHabits {
                        view?.hideProgress()
                        val launcherIntent = Intent(view, HabitListView::class.java)
                        loginIntentLauncher.launch(launcherIntent)
                    }
                } else {
                    view?.hideProgress()
                    val launcherIntent = Intent(view, HabitListView::class.java)
                    loginIntentLauncher.launch(launcherIntent)
                }
            } else {
                view?.hideProgress()
                view.showSnackBar("Login failed: ${task.exception?.message}")
            }
            view.hideProgress()
        }

    }

    fun doSignUp(email: String, password: String) {
        view.showProgress()
        auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(view) { task ->
            if (task.isSuccessful) {
                fireStore!!.fetchHabits {
                    view?.hideProgress()
                    val launcherIntent = Intent(view, HabitListView::class.java)
                    loginIntentLauncher.launch(launcherIntent)
                }
            } else {
                view.showSnackBar("Login failed: ${task.exception?.message}")
            }
            view.hideProgress()
        }
    }
    private fun registerLoginCallback(){
        loginIntentLauncher =
            view.registerForActivityResult(ActivityResultContracts.StartActivityForResult())
            {  }
    }
}