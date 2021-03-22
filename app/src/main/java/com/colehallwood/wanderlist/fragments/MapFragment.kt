package com.colehallwood.wanderlist.fragments

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.colehallwood.wanderlist.R
import com.colehallwood.wanderlist.databinding.FragmentMapBinding
import com.colehallwood.wanderlist.models.Place
import com.colehallwood.wanderlist.viewmodels.PlaceViewModel
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import kotlinx.coroutines.*
import java.io.IOException
import java.lang.StringBuilder
import java.util.*


class MapFragment : Fragment() {

    // View binding replaces findViewById()
    private var binding: FragmentMapBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val fragmentMapBinding get() = binding!!

    // MapFragment attributes
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private val REQUEST_LOCATION_PERMISSION = 1
    private var tempMarker: Marker? = null
    private lateinit var tempPlace: Place
    private lateinit var placeViewModel: PlaceViewModel
    private lateinit var map: GoogleMap

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View {
        // Inflate view and set as binding
        binding = FragmentMapBinding.inflate(inflater, container, false)

        // Initialize view model
        placeViewModel = ViewModelProvider(this).get(PlaceViewModel::class.java)

        // Hide place info layout
        fragmentMapBinding.llMapPlaceInfo.visibility = View.GONE

        // Enable options menu
        setHasOptionsMenu(true)

        // Set FAB button to navigate to list fragment
        fragmentMapBinding.btMapList.setOnClickListener {
            findNavController().navigate(R.id.action_mapFragment_to_listFragment)
        }

        // Set add button to navigate to add fragment
        fragmentMapBinding.btMapPlaceAdd.setOnClickListener {
            val action = MapFragmentDirections.actionMapFragmentToAddFragment(tempPlace)
            findNavController().navigate(action)
        }

        return fragmentMapBinding.root
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.map_options, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
        // Change the map type based on the user's selection.
        R.id.normal_map -> {
            map.mapType = GoogleMap.MAP_TYPE_NORMAL
            true
        }
        R.id.hybrid_map -> {
            map.mapType = GoogleMap.MAP_TYPE_HYBRID
            true
        }
        else -> super.onOptionsItemSelected(item)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment?.getMapAsync(callback)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the cameraLocation.
     * If Google Play services is not installed on the device, the user will be prompted to
     * install it inside the SupportMapFragment. This method will only be triggered once the
     * user has installed Google Play services and returned to the app.
     */
    private val callback = OnMapReadyCallback { googleMap ->
        // Setup map
        map = googleMap
        enableMyLocation()
        redrawMap()

        // Long click listener for quick map marking
        map.setOnMapLongClickListener { location ->
            // Create (or replace) a dropped marker
            tempMarker?.remove()
            tempMarker = map.addMarker(MarkerOptions().position(location).title(getString(R.string.dropped_marker_title)))
            tempPlace = Place(
                    0,
                    getString(R.string.dropped_marker_title),
                    "",
                    location.latitude,
                    location.longitude
            )
            // Get and display address information
            fetchAddress(location.latitude, location.longitude)
        }

        // Point of interest click listener to display place info
        map.setOnPoiClickListener { poi ->
            // Create (or replace) a dropped marker
            tempMarker?.remove()

            // Place temporary marker on th POI location
            tempMarker = map.addMarker(MarkerOptions().position(poi.latLng).title(poi.name))
            tempPlace = Place(
                    0,
                    poi.name,
                    "",
                    poi.latLng.latitude,
                    poi.latLng.longitude,
            )
            // Get and display address information
            fetchAddress(poi.latLng.latitude, poi.latLng.longitude)
        }

        // Map click listener to remove a dropped marker
        map.setOnMapClickListener {
            if (fragmentMapBinding.llMapPlaceInfo.visibility == View.VISIBLE) {
                fragmentMapBinding.llMapPlaceInfo.visibility = View.GONE
            }
            tempMarker?.remove()
        }
    }

    /**
     * Fetch address information from location and display in view
     */
    private fun fetchAddress(lat: Double, long: Double) = CoroutineScope(Dispatchers.Main).launch {
        val result = getAddress(lat, long)
        Log.i("Result", result)
        displayPlaceInfoWindow(result)
    }

    /**
     * Returns geocoded address as a string, runs in a background thread
     */
    private suspend fun getAddress(lat: Double, long: Double) = withContext(Dispatchers.IO) {
        val result = StringBuilder()

        // Use Geocoder to parse latitude and longitude
        try {
            val geocode = Geocoder(requireContext(), Locale.getDefault())
            val addresses = geocode.getFromLocation(lat, long, 1)

            // Get the first address result and append to a string
            if (addresses.size > 0) {
                val address = addresses[0]
                result.append(address.getAddressLine(0))
            }
        } catch (e: IOException) {
            Log.e("Geocode Error", e.toString())
        }
        // Return address result
        result.toString()
    }

    /**
     * Show place information from the temporary marker
     */
    private fun displayPlaceInfoWindow(address: String) {
        fragmentMapBinding.llMapPlaceInfo.visibility = View.VISIBLE
        fragmentMapBinding.tvMapPlaceTitle.text = tempMarker?.title
        fragmentMapBinding.tvMapPlaceLocation.text = address
    }

    /**
     * Draws markers from place database
     */
    private fun redrawMap() {
        // Clean map to remove recently deleted/updated markers
        map.clear()

        // Retrieve markers from database and place on map
        placeViewModel.readAllData.observe(viewLifecycleOwner, { places ->
            for (item in places) {
                map.addMarker(MarkerOptions()
                        .position(LatLng(item.latitude, item.longitude))
                        .title(item.name)
                )
            }
        })
    }

    /**
     * Returns true if location permission has been granted, false otherwise
     */
    private fun isPermissionGranted() : Boolean {
        return ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
    }

    /**
     * Requests location permission if necessary, otherwise enable location tracking
     */
    @SuppressLint("MissingPermission")
    private fun enableMyLocation() {
        // Check that location permission has been granted
        if (isPermissionGranted()) {
            // Enable current location tracking
            map.isMyLocationEnabled = true

            // Set map position to current location upon initial load
            fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())
            fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
                if (location != null && tempMarker == null) {
                    map.moveCamera(
                            CameraUpdateFactory.newLatLng(
                                    LatLng(location.latitude, location.longitude)
                            )
                    )
                }
            }
        } else {
            // Request location permission
            ActivityCompat.requestPermissions(
                    requireActivity(),
                    arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                    REQUEST_LOCATION_PERMISSION
            )
        }
    }

    override fun onRequestPermissionsResult(
            requestCode: Int,
            permissions: Array<String>,
            grantResults: IntArray
    ) {
        if (requestCode == REQUEST_LOCATION_PERMISSION) {
            if (grantResults.contains(PackageManager.PERMISSION_GRANTED)) {
                enableMyLocation()
            }
        }
    }
}
