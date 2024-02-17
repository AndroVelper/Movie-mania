package com.shubham.moviemania.utility

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.shubham.moviemania.models.MovieData

class PreferenceManager private constructor() {


    companion object {
        private const val KEY_SEARCHED_ARRAY = "search_history"
        private const val KEY_FAV_ARRAY = "favourite_array"
        private const val KEY_SHARED_PREF = "movie_mania_pref"

        @Volatile
        private var instance: PreferenceManager? = null

        @Volatile
        private var sharedPreferences: SharedPreferences? = null


        fun getInstance(): PreferenceManager {
            return instance ?: synchronized(this) {
                instance ?: PreferenceManager().also { instance = it }
            }
        }

        private fun getSharedPreference(context: Context): SharedPreferences {
            return sharedPreferences ?: synchronized(this) {
                sharedPreferences ?: context.getSharedPreferences(
                    KEY_SHARED_PREF,
                    Context.MODE_PRIVATE
                )
            }
        }


    }

     fun insertArray(context: Context, data: ArrayList<String>) {
        val result = Gson().toJson(data)
        putString(context, KEY_SEARCHED_ARRAY, result)
    }

    fun getInsertedArray(context: Context): ArrayList<String> {
        val data = getSharedPreference(context).getString(KEY_SEARCHED_ARRAY, "")
        val type = object : TypeToken<ArrayList<String>>() {}.type
        return Gson().fromJson(data, type) ?: ArrayList()
    }

    fun insertFavArray(context: Context, data: ArrayList<MovieData>) {
        val result = Gson().toJson(data)
        putString(context, KEY_FAV_ARRAY, result)
    }

    fun getInsertedFavArray(context: Context): ArrayList<MovieData> {
        val data = getSharedPreference(context).getString(KEY_FAV_ARRAY, "")
        val type = object : TypeToken<ArrayList<MovieData>>() {}.type
        return Gson().fromJson(data, type) ?: ArrayList()
    }



    private fun putString(context: Context, key: String, data: String) {
        getSharedPreference(context).edit().putString(key, data).apply()
    }

    private fun putBoolean(context: Context, key: String, data: Boolean) {
        getSharedPreference(context).edit().putBoolean(key, data).apply()
    }

}