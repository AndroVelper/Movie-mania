package com.shubham.moviemania.fragments.home

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.shubham.moviemania.Repository
import com.shubham.moviemania.enums.HomeEnums
import com.shubham.moviemania.models.MovieModal
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class HomeViewModel(application: Application) : AndroidViewModel(application = application) {


    /*
    * handle the ui update.
    * */
    private val getTheMovies: MutableLiveData<MovieModal> = MutableLiveData()

    /* deals with all the error in the api class*/
    private val isErrorHere: MutableLiveData<String> = MutableLiveData()

    /*MediatorLiveData to merge the Mutable live Data*/
    private var _homeScreenData: MediatorLiveData<Pair<Any, Int>> = MediatorLiveData()

    /*LiveDat that has to be observed in the fragment*/
    val homeScreenData: LiveData<Pair<Any, Int>> = _homeScreenData

    companion object {
        private val repository = Repository.getInstance()
    }

    init {
        _homeScreenData.addSource(getTheMovies) { movieModal ->
            _homeScreenData.postValue(Pair(movieModal, HomeEnums.SEARCH.ordinal))
        }
        _homeScreenData.addSource(isErrorHere) {
            _homeScreenData.postValue(Pair(it, HomeEnums.ERROR.ordinal))
        }
    }


    /*
    * need to do the network operation on the background thread.
    * */
    fun getTheMoviesForDashBoard(page: String, search: String) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val result = repository.giveMovies(page, search)
                if (result.code() == 200) {
                    getTheMovies.postValue(result.body())
                } else {
                    isErrorHere.postValue("Wrong Response")
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}