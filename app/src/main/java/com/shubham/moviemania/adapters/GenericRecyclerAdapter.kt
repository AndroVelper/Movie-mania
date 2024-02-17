package com.shubham.moviemania.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.shubham.moviemania.R
import com.shubham.moviemania.communicator.AdapterListener
import com.shubham.moviemania.databinding.ItemMovieDataBinding
import com.shubham.moviemania.models.MovieData


class MovieAdapter(private var list: ArrayList<MovieData>, private val listener: AdapterListener) :
    RecyclerView.Adapter<MovieAdapter.ViewHolder>() {


    @SuppressLint("NotifyDataSetChanged")
    fun updateData(newList: ArrayList<MovieData>) {
        list = newList
        notifyDataSetChanged()
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MovieAdapter.ViewHolder {
        return ViewHolder(
            ItemMovieDataBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: MovieAdapter.ViewHolder, position: Int) {
        val singleItem = list[position]
        holder.binding.apply {
            updateTheUi(this, singleItem, position)
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }

    inner class ViewHolder(val binding: ItemMovieDataBinding) :
        RecyclerView.ViewHolder(binding.root)


    private fun updateTheUi(item: ItemMovieDataBinding, data: MovieData, position: Int) {
        item.apply {
            Glide.with(movieImage.context).load(data.poster).into(movieImage)
            movieName.text = data.title
            movieYear.text = data.year
            movieType.text = data.type

            val drawable = if (data.isInFavourite) {
                R.drawable.ic_favourite_active
            } else {
                R.drawable.ic_favourite_inactive
            }

            favouriteListener.setImageDrawable(ContextCompat.getDrawable(root.context, drawable))
            favouriteListener.setOnClickListener {
                listener.onFavouriteButtonClicked(
                    position,
                    item
                )
            }
            shareListener.setOnClickListener { listener.onShareButtonClicked(position) }
        }
    }

}