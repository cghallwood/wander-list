package com.colehallwood.wanderlist.fragments

import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.colehallwood.wanderlist.R
import com.colehallwood.wanderlist.viewmodels.PlaceViewModel
import com.colehallwood.wanderlist.databinding.FragmentAddBinding
import com.colehallwood.wanderlist.models.Place

class AddFragment : Fragment() {

    // View binding replaces findViewById()
    private var binding: FragmentAddBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val fragmentAddBinding get() = binding!!

    // Get fragment arguments
    private val args by navArgs<AddFragmentArgs>()

    // AddFragment attributes
    private lateinit var placeViewModel: PlaceViewModel

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentAddBinding.inflate(inflater, container, false)

        // Initialize view model
        placeViewModel = ViewModelProvider(this).get(PlaceViewModel::class.java)

        // Set editable fields to the new place information
        fragmentAddBinding.etAddName.setText(args.newPlace.name)
        fragmentAddBinding.etAddLatitude.setText(args.newPlace.latitude.toString())
        fragmentAddBinding.etAddLongitude.setText(args.newPlace.longitude.toString())

        // Submit given information to database
        fragmentAddBinding.btAddSubmit.setOnClickListener {
            insertDataToDatabase()
        }
        return fragmentAddBinding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }

    /**
     * Insert user input to place database
     */
    private fun insertDataToDatabase() {
        val name = fragmentAddBinding.etAddName.text.toString()
        val lat = fragmentAddBinding.etAddLatitude.text.toString()
        val long = fragmentAddBinding.etAddLongitude.text.toString()

        // Check for empty fields
        if (!emptyCheck(name, lat, long)) {
            // Create new place object
            val place = Place(0, name, "Test", lat.toDouble(), long.toDouble())

            // Add place to database
            placeViewModel.addPlace(place)
            Toast.makeText(
                requireContext(),
                getString(R.string.confirm_input),
                Toast.LENGTH_LONG
            ).show()

            // Navigate back
            findNavController().navigate(R.id.action_addFragment_to_listFragment)
        } else {
            // Inform user of failed input
            Toast.makeText(
                requireContext(),
                getString(R.string.error_input),
                Toast.LENGTH_LONG
            ).show()
        }
    }

    /**
     * Returns true if the [placeName] is empty, false otherwise.
     */
    private fun emptyCheck(placeName: String, latitude: String, longitude: String): Boolean {
        return (TextUtils.isEmpty(placeName) ||
                TextUtils.isEmpty(latitude) ||
                TextUtils.isEmpty(longitude))
    }
}
