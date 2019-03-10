package com.example.ridecoyote

import android.location.Location

class MapsPresenter(private var mapsContract: MapsContract) {

    fun onMapReady() {
        mapsContract.findLastLocation()
    }

    fun requestPermissions() {
        if(mapsContract.checkHasPermissions()){
            mapsContract.generateMap()
        } else {
            mapsContract.requestPermission()
        }
    }

    fun setLatLng(location: Location){
        mapsContract.scrollMapTo(location.latitude, location.longitude)
    }
}
