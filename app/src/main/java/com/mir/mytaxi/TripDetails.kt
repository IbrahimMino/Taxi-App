package com.mir.mytaxi

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.PolylineOptions
import com.mir.mytaxi.databinding.ActivityMapsBinding
import com.mir.mytaxi.databinding.ActivityTripDetailsBinding

class TripDetails : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap


    private lateinit var binding:ActivityTripDetailsBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTripDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        viewOnClicked()

        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map2) as SupportMapFragment
        mapFragment.getMapAsync(this)


    }

    private fun viewOnClicked() {
     binding.cardBtnBack.setOnClickListener {
         val intent = Intent(applicationContext,TripHistory::class.java)
         intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_CLEAR_TASK
         startActivity(intent)
     }
    }


    //Chizish oxirgi oyna uchun
    private fun polyLine(){

        val polylineOptions = PolylineOptions()
            .add(LatLng(41.3156848,69.2509706))
            .add(LatLng(41.316664,69.2407741))
        mMap.addPolyline(polylineOptions)
    }

    override fun onMapReady(p0: GoogleMap) {
         mMap = p0
        val marker = LatLng(41.3156848,69.2509706)
        val marker2 = LatLng(41.316664,69.2407741)
        mMap.addMarker(MarkerOptions().position(marker).title("Xaqllar do'stligi").anchor(0.5f,1.0f))
        mMap.addMarker(MarkerOptions().position(marker2).title("Samarqand darvoza").anchor(0.5f,1.0f))


        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(marker,14f))
        polyLine()
    }
}