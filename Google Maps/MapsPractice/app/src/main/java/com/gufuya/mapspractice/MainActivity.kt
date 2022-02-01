package com.gufuya.mapspractice

import android.annotation.SuppressLint
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Looper
import android.text.SpannableString
import android.text.style.TextAppearanceSpan
import android.view.MenuItem
import androidx.annotation.RequiresApi
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.gms.location.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.google.android.material.navigation.NavigationView
import com.gufuya.mapspractice.databinding.ActivityMainBinding
import com.google.android.gms.location.LocationServices
import kotlinx.coroutines.*
import com.google.android.gms.maps.model.Marker

import com.google.android.gms.maps.GoogleMap.OnInfoWindowClickListener

const val REQUEST_PERMISSION_CODE = 1000

class MainActivity : AppCompatActivity(),
    OnInfoWindowClickListener,
    OnMapReadyCallback,
    NavigationView.OnNavigationItemSelectedListener {

    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityMainBinding

    private lateinit var drawerLayout: DrawerLayout
    private lateinit var mFusedLocationClient: FusedLocationProviderClient
    private var mLatitude = 0.0
    private var mLongitude = 0.0
    private lateinit var locationRequest: LocationRequest
    private lateinit var locationCallback: LocationCallback
    private var markerList: MutableList<Marker> = ArrayList()

    private var visibleMarkerList: MutableList<Marker> = ArrayList()
    private lateinit var locations: List<Location>

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Set the toolbar of the layout
        setSupportActionBar(binding.appBarMain.toolbar)

        drawerLayout = binding.drawerLayout
        val navView: NavigationView = binding.navView

        val mainOptions = listOf(R.id.ver, R.id.ocultar)
        val menu = navView.menu
        val secOptions = listOf(R.id.verKarts, R.id.verRestaurantes, R.id.ocultarTodo)

        for(option in mainOptions){
            val tools = menu.findItem(option)
            val s = SpannableString(tools.title)
            s.setSpan(TextAppearanceSpan(this, R.style.MainOptionStyle), 0, s.length, 0)
            tools.title = s
        }

        for(option in secOptions){
            val tools = menu.findItem(option)
            val s = SpannableString(tools.title)
            s.setSpan(TextAppearanceSpan(this, R.style.SecondaryOptionStyle), 0, s.length, 0)
            tools.title = s
        }

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val fragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        fragment.getMapAsync(this)

        val mActionBarDrawerToggle = ActionBarDrawerToggle(this,
            drawerLayout, binding.appBarMain.toolbar, R.string.drawer_opened, R.string.drawer_closed )
        mActionBarDrawerToggle.syncState()

        navView.setNavigationItemSelectedListener(this)

    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        mMap.setOnInfoWindowClickListener {
            marker ->
                onInfoWindowClick(marker)
        }

        locations = Location.readLocations(this)

        // Add my townhall location and zoom
        val townHall = LatLng(39.436746, -0.4660131)
        mMap.moveCamera(CameraUpdateFactory.newLatLng(townHall))
        mMap.moveCamera(CameraUpdateFactory.zoomTo(12.0f))

        //Disable my location button, two finger rotation and zoom controls
        mMap.uiSettings.setMyLocationButtonEnabled(false)
        mMap.uiSettings.setRotateGesturesEnabled(false)
        mMap.uiSettings.setZoomControlsEnabled(false)

        //Setting the style of the map
        mMap.setMapStyle (MapStyleOptions.loadRawResourceStyle (this, R.raw.style_json))


    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        val scope = CoroutineScope(Job() + Dispatchers.Main)
        return when (item.itemId) {
            R.id.cerrarMenu -> {
                //Close the drawers
                drawerLayout.closeDrawers()
                true
            }
            R.id.salirApp -> {
                //Finish the app
                finish()
                System.exit(0)
                true
            }
            R.id.ver -> {

                scope.launch{
                    //Here, we get the current location
                    getLocation()
                    //And now we request the locations
                    requestLocations()
                }

                true
            }

            R.id.ocultar -> {
                scope.cancel()
                removeLocations()
                for(locationMarker: Marker in markerList){
                    locationMarker.remove()
                }
                true
            }

            R.id.verKarts -> {
                seeMarkers("kart")
                true
            }

            R.id.verRestaurantes -> {
                seeMarkers("restaurante")
                true
            }

            R.id.ocultarTodo -> {
                for(m in visibleMarkerList){
                    m.remove()
                }
                true
            }
            else -> false
        }

    }

    @SuppressLint("MissingPermission")
    private fun requestLocations(){
        if (ActivityCompat.checkSelfPermission(this@MainActivity,
                android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this@MainActivity,
                android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(this@MainActivity,
                arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION,
                    android.Manifest.permission.ACCESS_COARSE_LOCATION),
                REQUEST_PERMISSION_CODE)
        } else {
            mFusedLocationClient!!.requestLocationUpdates(locationRequest!!, locationCallback!!, Looper.getMainLooper())
        }
    }

    private fun getLocation(){
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        locationRequest = LocationRequest.create()
        locationRequest!!.priority = LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY
        locationRequest!!.interval = 16000 //16seconds
        locationRequest!!.fastestInterval = 8000//8seconds
        locationRequest.smallestDisplacement = 0f

        locationCallback = object:LocationCallback(){
            override fun onLocationResult(locationResult: LocationResult) {
                super.onLocationResult(locationResult)
                for ( location in locationResult.locations) {
                    if (location != null) {
                        mLatitude = location.latitude
                        mLongitude = location.longitude

                        //we make a marker and add it to the map
                        val marker = mMap.addMarker(MarkerOptions().position(LatLng(mLatitude,mLongitude)).title("My Location").icon(BitmapDescriptorFactory.fromResource(R.mipmap.my_location_pic)))
                        markerList.add(marker)
                        mMap.moveCamera(CameraUpdateFactory.newLatLng(marker.position))
                        mMap.moveCamera(CameraUpdateFactory.zoomTo(12.0f))
                    }
                }
            }
        }
    }

    private fun removeLocations() {
        mFusedLocationClient!!.removeLocationUpdates(locationCallback!!)
    }

    override fun onInfoWindowClick(marker: Marker?) {
        if(marker!=null){
            if (marker.tag?.equals("kart") == true){
                var url: String = ""
                for(location in locations){
                    if (marker.title.equals(location.title)){
                        url = location.fragment
                    }
                }
                val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                startActivity(browserIntent)
            } else if (marker.tag?.equals("restaurante") == true) {
                var tlf: String = ""
                for(location in locations){
                    if (marker.title.equals(location.title)){
                        tlf = location.fragment
                    }
                }
                val intent = Intent(Intent.ACTION_DIAL,Uri.parse("tel:$tlf"))
                startActivity(intent)
            }
            else{
                //we get the marker
                var markerPos : LatLng = marker.position

                //we create the dialog
                val customDialog = AlertDialog.Builder(this).setCancelable(false)

                //Add the info like text and tittle
                customDialog.setTitle("MapsPractice")
                customDialog.setMessage("Su ubicación actual es \nLat: ${markerPos.latitude} Lon: ${markerPos.longitude}")
                customDialog.setIcon(R.mipmap.ic_launcher_foreground)

                //Now, we add the button which closes the application
                val positiveButtonClick = { dialog: DialogInterface, which: Int ->
                    dialog.dismiss()
                }
                customDialog.setPositiveButton("OK", DialogInterface.OnClickListener(function = positiveButtonClick))

                //And finally create the dialog
                customDialog.create()
                customDialog.show()
            }
        }
    }

    fun seeMarkers(tag:String){
        for(location in locations){
            if(location.tag.equals("kart") && tag.equals("kart")){
                val marker = mMap.addMarker(MarkerOptions().position(LatLng(location.latitude,location.longitude)).title(location.title).snippet(location.fragment).icon(BitmapDescriptorFactory.fromResource(R.mipmap.kart_icon)))
                marker.setTag("kart")
                visibleMarkerList.add(marker)
            }
            else if(location.tag.equals("restaurante") && tag.equals("restaurante")){
                val marker = mMap.addMarker(MarkerOptions().position(LatLng(location.latitude,location.longitude)).title(location.title).snippet(location.fragment).icon(BitmapDescriptorFactory.fromResource(R.mipmap.food_icon)))
                marker.setTag("restaurante")
                visibleMarkerList.add(marker)
            }
        }
    }
}




