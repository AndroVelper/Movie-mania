package com.shubham.moviemania.activities

import android.os.Bundle
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import com.shubham.moviemania.R
import com.shubham.moviemania.databinding.ActivityMainBinding
import com.shubham.moviemania.utility.Utility
import com.shubham.moviemania.utility.Utility.Companion.makeTransactions
import com.shubham.moviemania.utility.Utility.Companion.shareUs
import com.shubham.moviemania.utility.Utility.Companion.showCustomDialog

class MainActivity : AppCompatActivity() {


    /*
    * This is a thread safe and easy approach and initialize the variable when called first.
    * Major advantage as the lazy bind itself with the class lifecycle and as the on destroy is called the
    * lazy variables are automatically handled.
    * */

    private val binding: ActivityMainBinding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }


    /*
    * This method is called as the activity is created.
    * */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        setUpClickListeners()
        onBackPressedDispatcher.addCallback(this, callback)
    }


    private fun setUpClickListeners() {


        binding.apply {

            contactUs.setOnClickListener { shareUs() }

            searchIcon.setOnClickListener {
                topBarVisibility(View.GONE)
                findNavController(R.id.fragmentContainerView).navigate(R.id.action_homeFragment_to_searchFragment)
            }

        }
    }


    private val callback = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {

            showCustomDialog(
                "Ok",
                getString(R.string.cancel),
                getString(R.string.app_name),
                getString(R.string.sure_message),
                layoutInflater,
                this@MainActivity,
                null, null,
                {}
            ) {
                onBackPressedDispatcher.onBackPressed()
            }


        }
    }

    override fun onDestroy() {
        super.onDestroy()
        Utility.debugLogs("Main Activity is destroyed")
    }


    fun topBarVisibility(visible: Int) {
        binding.contactUs.visibility = visible
        binding.heading.visibility = visible
        binding.searchIcon.visibility = visible
    }


}

