package kz.kuz.location

import android.Manifest
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.google.android.gms.location.*
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.LatLng

// добавлены зависимости:
// com.google.android.gms:play-services-location
// com.google.android.gms:play-services-maps
class MainFragment : Fragment() {
    private lateinit var mLocationRequest: LocationRequest
    private val UPDATE_INTERVAL = (10 * 1000).toLong() // 10 сек
    private val FASTEST_INTERVAL: Long = 2000 // 2 сек
    private val locationRequestCode = 1000
    lateinit var textView: TextView
    private val TAG = "MapDemoActivity"
    private lateinit var mFusedLocationClient: FusedLocationProviderClient

    // методы фрагмента должны быть открытыми
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //        startLocationUpdates();
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        activity?.setTitle(R.string.toolbar_title)
        val view = inflater.inflate(R.layout.fragment_main, container, false)
        mLocationRequest = LocationRequest()
        textView = view.findViewById(R.id.textView1)
        return view
    }

    // Trigger new location updates at interval
    private fun startLocationUpdates() {
        // Create the location request to start receiving updates
        mLocationRequest = LocationRequest()
        mLocationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        mLocationRequest.interval = UPDATE_INTERVAL
        mLocationRequest.fastestInterval = FASTEST_INTERVAL
        if (ActivityCompat.checkSelfPermission(context!!,
                        Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(context!!,
                        Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
//            ActivityCompat.requestPermissions(getActivity(), new String[]{
//                    Manifest.permission.ACCESS_FINE_LOCATION,
//                    Manifest.permission.ACCESS_COARSE_LOCATION}, REQUEST_FINE_LOCATION);
            return
        }

        // Create LocationSettingsRequest object using location request
        val builder = LocationSettingsRequest.Builder()
        builder.addLocationRequest(mLocationRequest)
        val locationSettingsRequest = builder.build()

        // Check whether location settings are satisfied
        // https://developers.google.com/android/reference/com/google/android/gms/location/SettingsClient
        val settingsClient = LocationServices.getSettingsClient(activity!!)
        settingsClient.checkLocationSettings(locationSettingsRequest)
        if (ActivityCompat.checkSelfPermission(context!!,
                        Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(activity!!, arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION), locationRequestCode)
        }
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(activity!!)
        mFusedLocationClient.lastLocation.addOnSuccessListener(activity!!
        ) { location: Location -> Log.e(TAG, location.toString()) }

        // new Google API SDK v11 uses getFusedLocationProviderClient(this)
        LocationServices.getFusedLocationProviderClient(activity!!).requestLocationUpdates(mLocationRequest,
                object : LocationCallback() {
                    override fun onLocationResult(locationResult: LocationResult) {
                        // do work here
                        Log.e(TAG, "Point4")
                        textView.text = "Come here!"
                        onLocationChanged(locationResult.lastLocation)
                    }
                },
                Looper.myLooper())
    }

    fun onLocationChanged(location: Location) {
        // New location has now been determined
        val msg = "Updated Location: " + java.lang.Double.toString(location.latitude) + "," +
                java.lang.Double.toString(location.longitude)
        Toast.makeText(activity, msg, Toast.LENGTH_SHORT).show()
        // You can now create a LatLng Object for use with maps
        val latLng = LatLng(location.latitude, location.longitude)
    }// GPS location can be null if GPS is switched off// TODO: Consider calling

    //    ActivityCompat#requestPermissions
    // here to request the missing permissions, and then overriding
    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
    //                                          int[] grantResults)
    // to handle the case where the user grants the permission. See the documentation
    // for ActivityCompat#requestPermissions for more details.
    // Get last known recent location using new Google Play Services SDK (v11+)
    val lastLocation: Unit
        get() {
            // Get last known recent location using new Google Play Services SDK (v11+)
            val locationClient = LocationServices.getFusedLocationProviderClient(activity!!)
            if (ActivityCompat.checkSelfPermission(activity!!,
                            Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                    && ActivityCompat.checkSelfPermission(activity!!,
                            Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return
            }
            locationClient.lastLocation.addOnSuccessListener { location -> // GPS location can be null if GPS is switched off
                location?.let { onLocationChanged(it) }
            }
                    .addOnFailureListener { e ->
                        Log.d("MapDemoActivity", "Error trying to get last GPS location")
                        e.printStackTrace()
                    }
        }

    //    @Override
    fun onMapReady(googleMap: GoogleMap) {
        if (checkPermissions()) {
            if (ActivityCompat.checkSelfPermission(activity!!,
                            Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                    && ActivityCompat.checkSelfPermission(activity!!,
                            Manifest.permission.ACCESS_COARSE_LOCATION) !=
                    PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return
            }
            googleMap.isMyLocationEnabled = true
        }
    }

    private fun checkPermissions(): Boolean {
        return if (ContextCompat.checkSelfPermission(activity!!,
                        Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            true
        } else {
            requestPermissions()
            false
        }
    }

    private fun requestPermissions() {
        ActivityCompat.requestPermissions(activity!!, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), locationRequestCode)
    }
}