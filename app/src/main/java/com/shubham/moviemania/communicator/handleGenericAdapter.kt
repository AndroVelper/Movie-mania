package com.shubham.moviemania.communicator

import com.shubham.moviemania.databinding.ItemMovieDataBinding


interface AdapterListener {
    fun onFavouriteButtonClicked(position: Int, item: ItemMovieDataBinding)
    fun onShareButtonClicked(position: Int)
}