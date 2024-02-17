package com.shubham.moviemania.models

import com.google.gson.annotations.SerializedName

data class MovieModal(
    @SerializedName("Search")
    val search: List<MovieData>,
    @SerializedName("totalResults")
    val totalResults: Int,
    @SerializedName("Response")
    val answer: String,

    )

data class MovieData(
    @SerializedName("Title")
    val title: String,
    @SerializedName("Year")
    val year: String,
    @SerializedName("imdbID")
    val id: String,
    @SerializedName("Type")
    val type: String,
    @SerializedName("Poster")
    val poster: String?,
    var isVisible: Boolean = false,
    var isInFavourite: Boolean = true
)
