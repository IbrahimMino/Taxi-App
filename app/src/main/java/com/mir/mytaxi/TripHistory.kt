package com.mir.mytaxi

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.widget.Toast
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.PolylineOptions
import com.mir.mytaxi.databinding.ActivityTripHistoryBinding

class TripHistory : AppCompatActivity() {

    private lateinit var binding:ActivityTripHistoryBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTripHistoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewOnClick()


    }

    private fun viewOnClick() {
        binding.btnCardBack.setOnClickListener {
            val intent = Intent(applicationContext, MapsActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
        }
        binding.btnCardItem2.setOnClickListener {
            val intent = Intent(applicationContext, TripDetails::class.java)
            startActivity(intent)
        }
    }


    override fun onStart() {
        super.onStart()

        Handler().postDelayed({
            binding.frameShimmerLayout.stopShimmerAnimation()
            binding.frameShimmerLayout.visibility = View.GONE
            binding.scrollView.visibility = View.VISIBLE
        },1000)
    }

}