package com.shubham.moviemania.fragments.home

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.get
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.shubham.moviemania.R
import com.shubham.moviemania.activities.MainActivity
import com.shubham.moviemania.adapters.FavouriteItemsAdapter
import com.shubham.moviemania.adapters.MovieAdapter
import com.shubham.moviemania.communicator.AdapterListener
import com.shubham.moviemania.communicator.FavItemCommunicator
import com.shubham.moviemania.databinding.FragmentHomeBinding
import com.shubham.moviemania.databinding.ItemMovieDataBinding
import com.shubham.moviemania.enums.HomeEnums
import com.shubham.moviemania.models.MovieData
import com.shubham.moviemania.models.MovieModal
import com.shubham.moviemania.utility.PreferenceManager
import com.shubham.moviemania.utility.Utility
import com.shubham.moviemania.utility.Utility.Companion.shareItemData


class HomeFragment : Fragment(), AdapterListener, FavItemCommunicator {


    companion object {
        val movieDataArray: ArrayList<MovieData> by lazy {
            ArrayList()
        }
    }

    private val binding: FragmentHomeBinding by lazy {
        FragmentHomeBinding.inflate(layoutInflater)
    }

    private val viewModel: HomeViewModel by lazy {
        ViewModelProvider(this).get()
    }

    private lateinit var adapter: MovieAdapter
    private val favMovieList: ArrayList<MovieData> by lazy {
        PreferenceManager.getInstance().getInsertedFavArray(requireContext())
    }
    private lateinit var favAdapter: FavouriteItemsAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return binding.root
    }

    override fun onResume() {
        super.onResume()
        (activity as MainActivity).topBarVisibility(View.VISIBLE)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initViews()
        handleObservers()
        initializeRecyclerView()
        hitTheApi()
    }

    private fun initViews() {
        binding.apply {
            favouriteItem.isVisible = favMovieList.isNotEmpty()
        }
    }


    private fun handleObservers() {
        viewModel.homeScreenData.observe(viewLifecycleOwner) {
            when (it.second) {
                HomeEnums.SEARCH.ordinal -> {
                    movieDataArray.clear()
                    binding.progress.visibility = View.GONE
                    val data = it.first as MovieModal
                    updateFavouriteListAccordingly(data)
                }

                HomeEnums.ERROR.ordinal -> {
                    Utility.showToast(requireContext(), it.first as String, true)
                }
            }
        }
    }

    private fun updateFavouriteListAccordingly(data: MovieModal) {

        try {
            data.search.forEach { singleItem ->
                if (favMovieList.contains(singleItem)) {
                    singleItem.isInFavourite = true
                }
                movieDataArray.add(singleItem)
            }
            adapter.updateData(movieDataArray)
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }


    private fun hitTheApi() {
        movieDataArray.clear()
        adapter.updateData(movieDataArray)
        viewModel.getTheMoviesForDashBoard("1", "abc")
    }


    /*
    * this will initialize the recycler views
    *
    * */
    private fun initializeRecyclerView() {
        binding.moviesRecycler.layoutManager = GridLayoutManager(requireContext() , 2)
        binding.moviesRecycler.hasFixedSize()
        adapter = MovieAdapter(movieDataArray, this)
        binding.moviesRecycler.adapter = adapter

        binding.favouriteItemRecyclerView.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        binding.favouriteItemRecyclerView.hasFixedSize()
        favAdapter = FavouriteItemsAdapter(favMovieList, this)
        binding.favouriteItemRecyclerView.adapter = favAdapter

    }


    /*
    * Handle the movie recycler view.
    * As this is a generic adapter can be used anyWhere with more readability and usage
    * */

    override fun onFavouriteButtonClicked(position: Int, item: ItemMovieDataBinding) {
        val data = movieDataArray[position]
        if (isFavouriteListContainItem(data)) {
            favMovieList.add(data)
            item.favouriteListener.setImageDrawable(
                ContextCompat.getDrawable(
                    requireContext(),
                    R.drawable.ic_favourite_active
                )
            )
            favAdapter.notifyItemInserted(favMovieList.size - 1)
            removeFavourite()
        } else {
            findAndRemoveElement(data, item)
        }
    }

    private fun isFavouriteListContainItem(data: MovieData): Boolean {
        for(i in favMovieList){
            if(i.title == data.title && i.year == data.year){
                return false
            }
        }
        return true
    }


    /*
    this will help the user
    **/
    override fun onShareButtonClicked(position: Int) {
        shareItemData(movieDataArray[position], requireContext())
    }


    /*
    * find the element and if found and update the element and ui
    * */
    private fun findAndRemoveElement(data: MovieData, item: ItemMovieDataBinding) {
        for ((position, favMovie) in favMovieList.withIndex())
            if (favMovie.id == data.id) {
                favMovieList.remove(favMovie)
                removeFavourite()
                item.favouriteListener.setImageDrawable(
                    ContextCompat.getDrawable(
                        requireContext(),
                        R.drawable.ic_favourite_inactive
                    )
                )
                favAdapter.notifyItemRemoved(position)
                return
            } else {
                Utility.debugLogs("Item not found")
            }
    }

    private fun removeFavourite() {
        binding.favouriteItem.isVisible = favMovieList.isNotEmpty()

    }


    private fun findDataInMovieListAndRemove(data: MovieData) {
        for ((position, singleItem) in movieDataArray.withIndex()) {
            if (singleItem.id == data.id) {
                movieDataArray[position].isInFavourite = false;
                adapter.notifyItemChanged(position)
                return
            }
        }

    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onFavouriteItemClick(position: Int) {
        val singleItem = favMovieList[position]
        Utility.showCustomDialog(
            getString(R.string.remove_from_list),
            getString(R.string.cancel),
            singleItem.title,
            getString(
                R.string.hi_have_you_watched_named_release_in_with_imdb_id,
                singleItem.type,
                singleItem.title,
                singleItem.year,
                singleItem.id,
            ), layoutInflater, requireContext(), position, favMovieList,
            { data ->
                favMovieList.remove(data)
                findDataInMovieListAndRemove(data)
                favAdapter.notifyDataSetChanged()
                removeFavourite()

            }
        ) {}
    }


    override fun onPause() {
        super.onPause()
        PreferenceManager.getInstance().insertFavArray(requireContext(), favMovieList)
    }

}