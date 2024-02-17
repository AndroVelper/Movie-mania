package com.shubham.moviemania.communicator

import com.shubham.moviemania.databinding.ItemSearchBinding

interface SearchCommunicator {
    fun searchCommunicator(item: ItemSearchBinding, position: Int, lastPosition: Int)
    fun searchFavouriteListener(position : Int , item: ItemSearchBinding)
}