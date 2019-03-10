package com.example.ridecoyote

import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.tasks.OnSuccessListener
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.widget.Autocomplete
import com.google.android.libraries.places.widget.AutocompleteActivity
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode
import kotlinx.android.synthetic.main.search_layout.*


class MapsActivity : AppCompatActivity(), OnMapReadyCallback, MapsContract {

    private lateinit var map: GoogleMap
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private lateinit var presenter: MapsPresenter
    private lateinit var lastLocation: Location
    private var isOrigin = false
    //    private lateinit var placesClient: PlacesClient

    companion object {
        private var TAG = MapsActivity::class.java.simpleName
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1
        private const val AUTOCOMPLETE_REQUEST_CODE = 2
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)

        if (!Places.isInitialized()) {
            Places.initialize(applicationContext, BuildConfig.GoogleMapsKey)
        }

//        placesClient = Places.createClient(this)

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
        presenter = MapsPresenter(this)
        presenter.requestPermissions()

        origin_button.text = getString(R.string.origin_text, "")
        destination_button.text = getString(R.string.destination_text, "")
    }

    fun launchAutoCompleteActivity(v: View) {
        val fields = arrayListOf(Place.Field.ID, Place.Field.NAME)
        val intent = Autocomplete.IntentBuilder(AutocompleteActivityMode.FULLSCREEN, fields)
            .build(this)
        isOrigin = (v.id == origin_button.id)
        startActivityForResult(intent, AUTOCOMPLETE_REQUEST_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when (requestCode) {
            AUTOCOMPLETE_REQUEST_CODE -> {
                when (resultCode) {
                    RESULT_OK -> {
                        val place = Autocomplete.getPlaceFromIntent(data!!)
                        if (isOrigin) {
                            origin_button.text = getString(R.string.origin_text, place.name)
                            isOrigin = false
                        } else {
                            destination_button.text = getString(R.string.destination_text, place.name)
                        }
                    }
                    AutocompleteActivity.RESULT_ERROR -> {
                        val status = Autocomplete.getStatusFromIntent(data!!)
                        Log.d(TAG, "${status.statusMessage}")
                    }
                    AutocompleteActivity.RESULT_CANCELED -> {
                        // user cancelled
                    }
                }
            }
        }
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera.
     */
    @SuppressLint("MissingPermission")
    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap
        map.isMyLocationEnabled = true
        presenter.onMapReady()
    }


    override fun checkHasPermissions() =
        (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED)

    override fun requestPermission() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION),
            LOCATION_PERMISSION_REQUEST_CODE
        )
    }

    override fun generateMap() {
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        when (requestCode) {
            LOCATION_PERMISSION_REQUEST_CODE -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.d(TAG, "OnRequestPermissionsResult: permission granted")
                    generateMap()
                } else {
//                    val view: View = findViewById(R.id.map)
//                    Snackbar.make(view, "Location Permission Not Granted", Snackbar.LENGTH_INDEFINITE).show()
                    Toast.makeText(this, "Location Permission Not Granted", Toast.LENGTH_LONG).show()
                }
                return
            }
            else -> {
                //do nothing
            }
        }
    }

    @SuppressLint("MissingPermission")
    override fun findLastLocation() {
        fusedLocationProviderClient.lastLocation.addOnSuccessListener { location ->
            if (location != null) {
                lastLocation = location
                presenter.setLatLng(location)
            }
        }

    }

    override fun scrollMapTo(latitude: Double, longitude: Double) {
        val bounds = LatLng(latitude, longitude)
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(bounds, 17F))
    }


}
