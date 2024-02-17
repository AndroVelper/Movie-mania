package com.shubham.moviemania.adapters

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.shubham.moviemania.R
import com.shubham.moviemania.communicator.FavItemCommunicator
import com.shubham.moviemania.databinding.CircularItemsBinding
import com.shubham.moviemania.models.MovieData

class FavouriteItemsAdapter(
    private val data: ArrayList<MovieData>,
    private val listener: FavItemCommunicator
) :
    RecyclerView.Adapter<FavouriteItemsAdapter.MyViewAttached>() {


    private var lastIndex = 0

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): MyViewAttached {
        return MyViewAttached(
            CircularItemsBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: MyViewAttached, position: Int) {
        val singleItem = data[position]
        holder.binding.apply {
            updateTheUi(this, singleItem, position)
            root.setOnClickListener {
                listener.onFavouriteItemClick(position)
            }
        }
    }

    private fun updateTheUi(
        circularItemsBinding: CircularItemsBinding,
        singleItem: MovieData,
        position: Int
    ) {
        circularItemsBinding.apply {
            Log.e("Poster", "updateTheUi: poster ${singleItem.poster}")
            Glide.with(circularMovieImage.context).load(singleItem.poster).circleCrop().placeholder(
                ContextCompat.getDrawable(
                    circularMovieImage.context,
                    R.drawable.no_result_found
                )
            ).into(circularMovieImage)
        }
    }

    override fun getItemCount(): Int {
        return data.size
    }

    class MyViewAttached(val binding: CircularItemsBinding) : RecyclerView.ViewHolder(binding.root)
}