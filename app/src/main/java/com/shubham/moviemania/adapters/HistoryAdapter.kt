package com.shubham.moviemania.adapters

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.shubham.moviemania.communicator.HistoryCommunicator
import com.shubham.moviemania.databinding.ItemHistoryBinding

class HistoryAdapter(
    private var data: ArrayList<String>,
    private val listener: HistoryCommunicator
) : RecyclerView.Adapter<HistoryAdapter.MyViewAttached>() {



    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): MyViewAttached {
        return MyViewAttached(
            ItemHistoryBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: MyViewAttached, position: Int) {
        Log.e("CheckingData", "onBindViewHolder: position $position" )
        val singleItem = data[position]
        holder.binding.apply {
            movieName.text = singleItem
            root.setOnClickListener { listener.historyCommunicator(holder.adapterPosition) }
            historyCancel.setOnClickListener { listener.cancelButtonClicked(holder.adapterPosition) }
        }
    }


    override fun getItemCount(): Int {
        return data.size
    }

    class MyViewAttached(val binding: ItemHistoryBinding) : RecyclerView.ViewHolder(binding.root)
}