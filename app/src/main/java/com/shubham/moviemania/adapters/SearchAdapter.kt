package com.shubham.moviemania.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.shubham.moviemania.R
import com.shubham.moviemania.communicator.SearchCommunicator
import com.shubham.moviemania.databinding.ItemSearchBinding
import com.shubham.moviemania.models.MovieData


class SearchAdapter(
    private var data: ArrayList<MovieData>,
    private val listener: SearchCommunicator
) :
    RecyclerView.Adapter<SearchAdapter.MyViewAttached>() {

    companion object {
        var lastPosition = 0;
    }

    @SuppressLint("NotifyDataSetChanged")
    fun updateData(newList: ArrayList<MovieData>) {
        data = newList
        notifyDataSetChanged()
    }


    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): MyViewAttached {
        return MyViewAttached(ItemSearchBinding.inflate(LayoutInflater.from(parent.context),  parent ,false) )
    }

    override fun onBindViewHolder(holder: MyViewAttached, position: Int) {
        val singleItem = data[position]

        holder.binding.apply {
            dialogMessage.isVisible = singleItem.isVisible
            updateTheUi(this, singleItem, position)
            root.setOnClickListener {
                listener.searchCommunicator(
                    this,
                    position,
                    lastPosition = lastPosition
                )
            }

        }
    }

    private fun updateTheUi(
        circularItemsBinding: ItemSearchBinding,
        singleItem: MovieData,
        position: Int
    ) {
        circularItemsBinding.apply {
            Glide.with(dialogImage.context).load(singleItem.poster).circleCrop().placeholder(
                ContextCompat.getDrawable(dialogImage.context, R.drawable.no_result_found)
            ).into(dialogImage)
            dialogTitle.text = singleItem.title
            "Hi , have you watched the ${singleItem.type} named ${singleItem.title} relased in ${singleItem.year} with imdb id ${singleItem.id}"
                .also {
                    dialogMessage.text = it
                }
            favouriteParent.setOnClickListener {
                listener.searchFavouriteListener(position , circularItemsBinding)
            }
            if(singleItem.isInFavourite){
                favouriteListener.setImageDrawable(ContextCompat.getDrawable(root.context , R.drawable.ic_favourite_active))
            }else{
                favouriteListener.setImageDrawable(ContextCompat.getDrawable(root.context , R.drawable.ic_favourite_inactive))
            }
        }
    }

    override fun getItemCount(): Int {
        return data.size
    }

    class MyViewAttached(val binding: ItemSearchBinding) : RecyclerView.ViewHolder(binding.root)
}