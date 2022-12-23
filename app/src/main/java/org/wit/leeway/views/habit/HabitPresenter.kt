package org.wit.leeway.views.habit

import android.annotation.SuppressLint
import android.content.Intent
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import org.wit.leeway.helpers.checkLocationPermissions
import org.wit.leeway.helpers.createDefaultLocationRequest
import org.wit.leeway.helpers.showImagePicker
import org.wit.leeway.main.LeewayApp
import org.wit.leeway.models.Location
import org.wit.leeway.models.HabitModel
import org.wit.leeway.views.location.EditLocationView
import timber.log.Timber
import timber.log.Timber.i

class HabitPresenter(private val view: HabitView) {
    private val locationRequest = createDefaultLocationRequest()
    var map: GoogleMap? = null
    var habit = HabitModel()
    var app: LeewayApp = view.application as LeewayApp
    var locationManuallyChanged = false;
    //location service
    var locationService: FusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(view)
    private lateinit var imageIntentLauncher : ActivityResultLauncher<Intent>
    private lateinit var mapIntentLauncher : ActivityResultLauncher<Intent>
    private lateinit var requestPermissionLauncher: ActivityResultLauncher<String>
    var edit = false;
    private val location = Location(52.245696, -7.139102, 15f)

    init {

        doPermissionLauncher()
        registerImagePickerCallback()
        registerMapCallback()

        if (view.intent.hasExtra("habit_edit")) {
            edit = true
            habit = view.intent.extras?.getParcelable("habit_edit")!!
            view.showHabit(habit)
        }
        else {

            if (checkLocationPermissions(view)) {
                doSetCurrentLocation()
            }
            habit.location.lat = location.lat
            habit.location.lng = location.lng
        }

    }


    suspend fun doAddOrSave(title: String, description: String) {
        habit.title = title
        habit.description = description
        if (edit) {
            app.habits.update(habit)
        } else {
            app.habits.create(habit)
        }

        view.finish()

    }

    fun doCancel() {
        view.finish()

    }

    suspend fun doDelete() {
        app.habits.delete(habit)
        view.finish()

    }

    fun doSelectImage() {
        showImagePicker(imageIntentLauncher)
    }

    fun doSetLocation() {
        locationManuallyChanged = true;

        if (habit.location.zoom != 0f) {

            location.lat =  habit.location.lat
            location.lng = habit.location.lng
            location.zoom = habit.location.zoom
            locationUpdate(habit.location.lat, habit.location.lng)
        }
        val launcherIntent = Intent(view, EditLocationView::class.java)
            .putExtra("location", location)
        mapIntentLauncher.launch(launcherIntent)
    }

    @SuppressLint("MissingPermission")
    fun doSetCurrentLocation() {

        locationService.lastLocation.addOnSuccessListener {
            locationUpdate(it.latitude, it.longitude)
        }
    }

    @SuppressLint("MissingPermission")
    fun doRestartLocationUpdates() {
        var locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult?) {
                if (locationResult != null && locationResult.locations != null) {
                    val l = locationResult.locations.last()
                    if(!locationManuallyChanged){
                        locationUpdate(l.latitude, l.longitude)
                    }
                }
            }
        }
        if (!edit) {
            locationService.requestLocationUpdates(locationRequest, locationCallback, null)
        }
    }

    fun doConfigureMap(m: GoogleMap) {
        map = m
        locationUpdate(habit.location.lat, habit.location.lng)
    }

    fun locationUpdate(lat: Double, lng: Double) {
        habit.location = location
        map?.clear()
        map?.uiSettings?.setZoomControlsEnabled(true)
        val options = MarkerOptions().title(habit.title).position(LatLng(habit.location.lat, habit.location.lng))
        map?.addMarker(options)
        map?.moveCamera(CameraUpdateFactory.newLatLngZoom(LatLng(habit.location.lat, habit.location.lng), habit.location.zoom))
        view.showHabit(habit)
    }

    fun cacheHabit (title: String, description: String) {
        habit.title = title;
        habit.description = description
    }

    private fun registerImagePickerCallback() {

        imageIntentLauncher =
            view.registerForActivityResult(ActivityResultContracts.StartActivityForResult())
            { result ->
                when(result.resultCode){
                    AppCompatActivity.RESULT_OK -> {
                        if (result.data != null) {
                            Timber.i("Got Result ${result.data!!.data}")
                            habit.image = result.data!!.data!!.toString()
                            view.updateImage(habit.image)
                        }
                    }
                    AppCompatActivity.RESULT_CANCELED -> { } else -> { }
                }

            }
    }

    private fun registerMapCallback() {
        mapIntentLauncher =
            view.registerForActivityResult(ActivityResultContracts.StartActivityForResult())
            { result ->
                when (result.resultCode) {
                    AppCompatActivity.RESULT_OK -> {
                        if (result.data != null) {
                            Timber.i("Got Location ${result.data.toString()}")
                            val location = result.data!!.extras?.getParcelable<Location>("location")!!
                            Timber.i("Location == $location")
                            habit.location = location
                        } // end of if
                    }
                    AppCompatActivity.RESULT_CANCELED -> { } else -> { }
                }

            }
    }

    private fun doPermissionLauncher() {
        i("permission check called")
        requestPermissionLauncher =
        view.registerForActivityResult(ActivityResultContracts.RequestPermission())
        { isGranted: Boolean ->
            if (isGranted) {
                doSetCurrentLocation()
            } else {
                locationUpdate(location.lat, location.lng)
            }
        }
    }
}