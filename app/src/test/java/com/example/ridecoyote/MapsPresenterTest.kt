package com.example.ridecoyote

import com.google.android.gms.maps.model.LatLng
import org.junit.Assert
import org.junit.Test

class MapsPresenterTest {

    @Test
    fun shouldPass() {
        Assert.assertEquals(1, 1)
    }

    @Test
    fun mapsPermissionsGranted() {
        //given
        val view = MockMapsContract(true)
        //when
        val presenter = MapsPresenter(view)
        presenter.requestPermissions()
        //then
        Assert.assertEquals(true, view.passed)
    }

    @Test
    fun mapsPermissionsNotGranted() {
        //given
        val view = MockMapsContract(false)
        //when
        val presenter = MapsPresenter(view)
        presenter.requestPermissions()
        //then
        Assert.assertEquals(true, view.passed)
    }


    class MockMapsContract(val case: Boolean) : MapsContract {
        var passed = false

        override fun checkHasPermissions(): Boolean {
            return case
        }

        override fun requestPermission() {
            passed = true
        }

        override fun generateMap() {
            passed = true
        }

        override fun findLastLocation() {
        }
    }
}