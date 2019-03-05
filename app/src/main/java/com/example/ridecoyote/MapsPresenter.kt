package com.example.ridecoyote

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
}
