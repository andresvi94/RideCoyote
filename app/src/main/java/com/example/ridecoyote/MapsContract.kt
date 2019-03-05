package com.example.ridecoyote

interface MapsContract {
    fun checkHasPermissions(): Boolean
    fun requestPermission()
    fun generateMap()
    fun findLastLocation()
}