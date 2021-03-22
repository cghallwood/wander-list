package com.colehallwood.wanderlist.fragments

import android.app.AlertDialog
import android.os.Bundle
import android.text.TextUtils
import android.view.*
import androidx.fragment.app.Fragment
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.colehallwood.wanderlist.R
import com.colehallwood.wanderlist.databinding.FragmentUpdateBinding
import com.colehallwood.wanderlist.models.Place
import com.colehallwood.wanderlist.viewmodels.PlaceViewModel

class UpdateFragment : Fragment() {

    // View binding replaces findViewById()
    private var binding: FragmentUpdateBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val fragmentUpdateBinding get() = binding!!

    // Get fragment arguments
    private val args by navArgs<UpdateFragmentArgs>()

    // UpdateFragment attributes
    private lateinit var placeViewModel: PlaceViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentUpdateBinding.inflate(inflater, container, false)

        // Initialize view model
        placeViewModel = ViewModelProvider(this).get(PlaceViewModel::class.java)

        // Set editable fields to the current place information
        fragmentUpdateBinding.etUpdateName.setText(args.currentPlace.name)
        fragmentUpdateBinding.etUpdateLatitude.setText(args.currentPlace.latitude.toString())
        fragmentUpdateBinding.etUpdateLongitude.setText(args.currentPlace.longitude.toString())

        // Set button to update item information
        fragmentUpdateBinding.btUpdateSubmit.setOnClickListener {
            updateItem()
        }

        // Add menu
        setHasOptionsMenu(true)

        return fragmentUpdateBinding.root
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.delete_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.menu_delete) {
            deleteItem()
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }

    /**
     * Delete the current item in the place database
     */
    private fun deleteItem() {
        val builder = AlertDialog.Builder(requireContext())
        builder.setPositiveButton(getString(R.string.alert_positive_button)) { _, _ ->
            placeViewModel.deletePlace(args.currentPlace)
            Toast.makeText(
                requireContext(),
                getString(R.string.confirm_input),
                Toast.LENGTH_SHORT
            ).show()

            // Navigate back
            findNavController().navigate(R.id.action_updateFragment_to_listFragment)
        }
        builder.setNegativeButton(getString(R.string.alert_negative_button)) { _, _ -> }
        builder.setTitle(String.format(
            getString(R.string.alert_delete_title), args.currentPlace.name)
        )
        builder.setMessage(String.format(
            getString(R.string.alert_delete_message), args.currentPlace.name)
        )
        builder.create().show()
    }

    /**
     * Update the current item in the place database
     */
    private fun updateItem() {
        // Retrieve text from editable fields
        val placeName = fragmentUpdateBinding.etUpdateName.text.toString()
        val placeLat = fragmentUpdateBinding.etUpdateLatitude.text.toString()
        val placeLong = fragmentUpdateBinding.etUpdateLongitude.text.toString()

        // Check for empty fields
        if (!emptyCheck(placeName, placeLat, placeLong)) {
            // Create new place object with same ID
            val newPlace = Place(
                args.currentPlace.placeId,
                placeName,
                "",
                placeLat.toDouble(),
                placeLong.toDouble()
            )

            // Update current place
            placeViewModel.updatePlace(newPlace)
            Toast.makeText(
                requireContext(),
                getString(R.string.confirm_input),
                Toast.LENGTH_SHORT
            ).show()

            // Navigate back
            findNavController().navigate(R.id.action_updateFragment_to_listFragment)

        } else {
            // Inform user of failed input
            Toast.makeText(
                requireContext(),
                getString(R.string.error_input),
                Toast.LENGTH_SHORT
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