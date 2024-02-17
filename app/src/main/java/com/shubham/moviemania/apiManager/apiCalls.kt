package com.shubham.moviemania.apiManager

import com.google.android.gms.common.api.Response
import com.shubham.moviemania.models.MovieModal
import retrofit2.http.GET
import retrofit2.http.HTTP
import retrofit2.http.Query


interface ApiCalls {

    @GET( "/")
   suspend  fun searchMovie(@Query("apiKey") apiKey : String , @Query("page")page : String , @Query("s") searchedMovie : String ) : retrofit2.Response<MovieModal>
}