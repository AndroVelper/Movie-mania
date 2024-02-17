package com.shubham.moviemania

import com.shubham.moviemania.apiManager.ApiCalls
import com.shubham.moviemania.apiManager.RetrofitManager


/*
* private constructor as we will use the repository using a singleTon pattern.
* if the reference is available then use that if not available then does not create new for that.
* */

class Repository private constructor() {

    /*
    * Why volatile as volatile variables are thread safe.
    * means updating this variable reflects immediately to all the threads.
    * */
    companion object {
        @Volatile private var instance: Repository? = null
        /*
        * Check here is instance is null then return the new instance
        * if not then return that instance
        *
        * */
        fun getInstance(): Repository = instance ?: synchronized(this) {
                instance ?: Repository().also { instance = it }
            }
    }

    /*
    * Connect the api calls with the retrofit.
    * */
    private val apiCaller: ApiCalls = RetrofitManager.apiService




    suspend fun giveMovies(page: String, search: String) = apiCaller.searchMovie("c23f0fa4" ,page, search)
}