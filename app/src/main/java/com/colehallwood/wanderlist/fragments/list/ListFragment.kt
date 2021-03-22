package com.colehallwood.wanderlist.fragments.list

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.colehallwood.wanderlist.R
import com.colehallwood.wanderlist.viewmodels.PlaceViewModel
import com.colehallwood.wanderlist.databinding.FragmentListBinding

class ListFragment : Fragment() {

    // View binding replaces findViewById()
    private var binding: FragmentListBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val fragmentListBinding get() = binding!!

    // ListFragment attributes
    private lateinit var placeViewModel: PlaceViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentListBinding.inflate(inflater, container, false)

        // Set up the recycler view
        val recyclerView = fragmentListBinding.rvListPlaces

        // Attach list adapter to recycler view to describe how the items are displayed
        val adapter = ListAdapter()
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        // Initialize view model
        placeViewModel = ViewModelProvider(this).get(PlaceViewModel::class.java)

        // Set data to be adapted in the list
        placeViewModel.readAllData.observe(viewLifecycleOwner, { places ->
            adapter.setData(places)
        })

        // Set FAB button to navigate to add fragment
        fragmentListBinding.btListMap.setOnClickListener {
            findNavController().navigate(R.id.action_listFragment_to_mapFragment)
        }

        return fragmentListBinding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }
}