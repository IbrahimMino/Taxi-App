package com.mir.mytaxi


import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.ColorStateList
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.location.*
import android.nfc.Tag
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.*
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.mir.mytaxi.databinding.ActivityMapsBinding
import java.lang.Exception
import java.util.*


class MapsActivity : AppCompatActivity(), OnMapReadyCallback,GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener,
    LocationListener, GoogleMap.OnCameraMoveListener, GoogleMap.OnCameraMoveStartedListener, GoogleMap.OnCameraIdleListener{

    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityMapsBinding
    lateinit var toggle: ActionBarDrawerToggle
    private val REQUEST_CODE = 100
    private val GPS_REQUEST_CODE = 101
    lateinit var mLocationClient: FusedLocationProviderClient


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        checkPermission()

        toggle = ActionBarDrawerToggle(this, binding.drawerLayout, R.string.open, R.string.close)
        binding.drawerLayout.addDrawerListener(toggle)
        toggle.syncState()
         checkPermission()
        mLocationClient = FusedLocationProviderClient(this)
        onClickView()

    }


         private fun onClickView() {
        binding.navigationView.setNavigationItemSelectedListener {
            when(it.itemId){
                R.id.tripHistory->{
                    val intent = Intent(applicationContext,TripHistory::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(intent)
                    binding.drawerLayout.close()
                }
                R.id.wallet->{
                    Toast.makeText(applicationContext, "Wallet Click", Toast.LENGTH_SHORT).show()
                }
                R.id.halfStart->{
                    Toast.makeText(applicationContext, "Half Start Click", Toast.LENGTH_SHORT).show()
                }
            }
            true
        }

        binding.cardImg.setOnClickListener {
        }

        binding.btnNavi.setOnClickListener{
            binding.drawerLayout.open()
        }



        binding.fabLocation.setOnClickListener {
            if (isGPSenable()){
                getCurrentLoc()
            }
        }


             binding.cardWhere.setOnClickListener {
                 Toast.makeText(applicationContext, "Куда?", Toast.LENGTH_SHORT).show()
             }

    }
         @SuppressLint("MissingPermission")
         private fun getCurrentLoc() {
             mLocationClient.lastLocation.addOnCompleteListener{task->
                 if (task.isSuccessful){
                     val location: Location = task.result
                     gotoLocation(location.latitude,location.longitude)
                 }
             }
         }
         private fun gotoLocation(latitude: Double, longitude: Double) {
             val lat = LatLng(latitude,longitude)
             val cameraUpdate = CameraUpdateFactory.newLatLngZoom(lat,18f)
             mMap.moveCamera(cameraUpdate)
             mMap.mapType = GoogleMap.MAP_TYPE_NORMAL
         }
         override fun onOptionsItemSelected(item: MenuItem): Boolean {
       if (toggle.onOptionsItemSelected(item)){
           return true
       }
        return super.onOptionsItemSelected(item)
    }
         @SuppressLint("MissingPermission", "ResourceType")
         override fun onMapReady(googleMap: GoogleMap) {
           mMap = googleMap
             mMap.isMyLocationEnabled = true
             mMap.setOnCameraMoveListener(this)
             mMap.setOnCameraMoveStartedListener(this)
             mMap.setOnCameraIdleListener(this)
             mMap.uiSettings.isMyLocationButtonEnabled = false
    }
         private fun checkPermission(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            if (ContextCompat.checkSelfPermission(this,android.Manifest.permission.ACCESS_FINE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED){
                requestPermissions(arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION),REQUEST_CODE)
                return
            }
        }
        isGPSenable()
    }
         private fun isGPSenable():Boolean {
             val locationManager = getSystemService(LOCATION_SERVICE) as LocationManager
             val providerEnable = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
             if (providerEnable){
                 return true
             }else{
                  val builder = AlertDialog.Builder(this)
                  builder.setTitle("GPS Permission")
                         .setMessage("GPS is required for this app to work. Please")
                         .setPositiveButton("Ok") { dialog, which ->
                              val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                         startActivityForResult(intent,GPS_REQUEST_CODE)
                         }
                         .setCancelable(false)

                    val alertDialog = builder.create()
                   alertDialog.show()
             }
             return false
         }
         override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        when(requestCode){
            REQUEST_CODE->{
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED){
                   isGPSenable()

                    Toast.makeText(applicationContext, "Permission have", Toast.LENGTH_SHORT).show()
                }else{
                    Toast.makeText(applicationContext,"Permision have not!", Toast.LENGTH_SHORT).show()
                }
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }
         @SuppressLint("WrongConstant")
         override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
             super.onActivityResult(requestCode, resultCode, data)
             when(requestCode){
                 GPS_REQUEST_CODE ->{
                     val locManager = getSystemService(LOCATION_SERVICE) as LocationManager
                     val providerEnable = locManager.isProviderEnabled(LocationManager.GPS_PROVIDER)

                     if (providerEnable){
                         getCurrentLoc()
                         Toast.makeText(applicationContext, "GPS is enable", Toast.LENGTH_SHORT).show()
                     }else{
                         Toast.makeText(applicationContext, "GPS is not enable", Toast.LENGTH_SHORT).show()
                     }

                 }
             }
         }
         override fun onConnected(p0: Bundle?) {

         }
         override fun onConnectionSuspended(p0: Int) {

         }
         override fun onConnectionFailed(p0: ConnectionResult) {

         }
         private fun geoLocate(){
             val localName = "Namangan"
             val geoCoder = Geocoder(this, Locale.getDefault())

             try {
                 val adressList: List<Address> = geoCoder.getFromLocationName(localName,1)
                 if (adressList.isNotEmpty()){
                     val addres = adressList[0]
                     gotoLocation(addres.latitude,addres.longitude)
                     mMap.addMarker(MarkerOptions().position(LatLng(addres.latitude,addres.longitude)))
                     
                 }
             }catch (ex:Exception){ex.printStackTrace()}

         }
         override fun onLocationChanged(location: Location) {
             val geoCoder = Geocoder(this, Locale.getDefault())
             var adressList:List<Address>? = null
             try {
                 adressList = geoCoder.getFromLocation(location.latitude,location.longitude,1)
                 if (adressList.isNotEmpty()){
                     val addres = adressList[0]
                     gotoLocation(addres.latitude,addres.longitude)
                     mMap.addMarker(MarkerOptions().position(LatLng(addres.latitude,addres.longitude)))

                 }
             }catch (ex:Exception){ex.printStackTrace()}
             setAddres(adressList!![0])
         }
         private fun setAddres(address: Address) {
         if (address != null){
             if (address.getAddressLine(0) != null){
                 binding.momentLocation.text = address.getAddressLine(0)
             }
             if (address.getAddressLine(0) != null){
                 binding.momentLocation.text = "${binding.momentLocation.text}"

             }
         }
         }
         override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {
             super.onStatusChanged(provider, status, extras)
         }
         override fun onCameraMove() {

    }
         override fun onCameraMoveStarted(p0: Int) {
    }
         override fun onCameraIdle() {
        var address:List<Address>? = null
        val geoCoder = Geocoder(this, Locale.getDefault())
        try {
            address = geoCoder.getFromLocation(mMap.cameraPosition.target.latitude, mMap.cameraPosition.target.longitude,1)
            setAddres(address[0])
        }catch (ex:Exception){ex.printStackTrace()}

    }

}