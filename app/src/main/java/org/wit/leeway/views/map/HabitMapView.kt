package org.wit.leeway.views.map

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.Marker
import com.squareup.picasso.Picasso
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.wit.leeway.databinding.ActivityHabitMapsBinding
import org.wit.leeway.databinding.ContentHabitMapsBinding
import org.wit.leeway.main.LeewayApp
import org.wit.leeway.models.HabitModel

class HabitMapView : AppCompatActivity() , GoogleMap.OnMarkerClickListener{

    private lateinit var binding: ActivityHabitMapsBinding
    private lateinit var contentBinding: ContentHabitMapsBinding
    lateinit var app: LeewayApp
    lateinit var presenter: HabitMapPresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        app = application as LeewayApp
        binding = ActivityHabitMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)

        presenter = HabitMapPresenter(this)

        contentBinding = ContentHabitMapsBinding.bind(binding.root)

        contentBinding.mapView.onCreate(savedInstanceState)
        contentBinding.mapView.getMapAsync{
            GlobalScope.launch(Dispatchers.Main) {
                presenter.doPopulateMap(it)
            }
        }
    }
    override fun onMarkerClick(marker: Marker): Boolean {
        GlobalScope.launch(Dispatchers.Main) {
            presenter.doMarkerSelected(marker)
        }
        return true
    }
    fun showHabit(habit: HabitModel) {
        contentBinding.currentTitle.text = habit.title
        contentBinding.currentDescription.text = habit.description
        Picasso.get()
            .load(habit.image)
            .into(contentBinding.imageView2)
    }

    override fun onDestroy() {
        super.onDestroy()
        contentBinding.mapView.onDestroy()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        contentBinding.mapView.onLowMemory()
    }

    override fun onPause() {
        super.onPause()
        contentBinding.mapView.onPause()
    }

    override fun onResume() {
        super.onResume()
        contentBinding.mapView.onResume()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        contentBinding.mapView.onSaveInstanceState(outState)
    }


}