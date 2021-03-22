package com.colehallwood.wanderlist.fragments.list

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.colehallwood.wanderlist.models.Place
import com.colehallwood.wanderlist.databinding.ItemRowBinding

class ListAdapter: RecyclerView.Adapter<ListAdapter.ItemViewHolder>() {

    private var placeList = emptyList<Place>()

    class ItemViewHolder(var viewBinding: ItemRowBinding) : RecyclerView.ViewHolder(viewBinding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val binding = ItemRowBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ItemViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        // Get place selected by the user
        val currentItem = placeList[position]

        // Put current item data into the view holder
        holder.viewBinding.tvItemRowTitle.text = currentItem.name

        // TODO: Item click listener to show detailed information
        // Currently navigates to update fragment
        holder.viewBinding.clItemContainer.setOnClickListener {
            val action = ListFragmentDirections.actionListFragmentToUpdateFragment(currentItem)
            holder.itemView.findNavController().navigate(action)
        }
    }

    override fun getItemCount() = placeList.size

    /**
     * Set a [List] of [places] into the adapter
     */
    fun setData(places: List<Place>) {
        this.placeList = places
        notifyDataSetChanged()
    }
}