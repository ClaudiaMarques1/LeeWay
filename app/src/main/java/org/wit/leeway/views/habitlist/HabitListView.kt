package org.wit.leeway.views.habitlist

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.*
import org.wit.leeway.R
import org.wit.leeway.databinding.ActivityHabitListBinding
import org.wit.leeway.main.LeewayApp
import org.wit.leeway.models.HabitModel
import timber.log.Timber.i

class HabitListView : AppCompatActivity(), HabitListener {

    lateinit var app: LeewayApp
    lateinit var binding: ActivityHabitListBinding
    lateinit var presenter: HabitListPresenter

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        binding = ActivityHabitListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //update Toolbar title
        binding.toolbar.title = title
        val user = FirebaseAuth.getInstance().currentUser
        if (user != null) {
            binding.toolbar.title = "${title}: ${user.email}"
        }
        setSupportActionBar(binding.toolbar)

        presenter = HabitListPresenter(this)
        val layoutManager = LinearLayoutManager(this)
        binding.recyclerView.layoutManager = layoutManager
        updateRecyclerView()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onResume() {

        //update the view
        super.onResume()
        updateRecyclerView()
        binding.recyclerView.adapter?.notifyDataSetChanged()
        i("recyclerView onResume")

    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.item_add -> { presenter.doAddHabit() }
            R.id.item_map -> { presenter.doShowHabitsMap() }
            R.id.item_logout -> {
                GlobalScope.launch(Dispatchers.IO) {
                    presenter.doLogout()
                }
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onHabitClick(habit: HabitModel) {
        presenter.doEditHabit(habit)

    }

    private fun updateRecyclerView(){
        GlobalScope.launch(Dispatchers.Main){
            binding.recyclerView.adapter =
                HabitAdapter(presenter.getHabits(), this@HabitListView)
        }
    }

}