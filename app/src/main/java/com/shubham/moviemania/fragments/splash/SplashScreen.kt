package com.shubham.moviemania.fragments.splash

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.shubham.moviemania.R
import com.shubham.moviemania.activities.MainActivity
import com.shubham.moviemania.databinding.FragmentSplashScreenBinding


@SuppressLint("CustomSplashScreen")
class SplashScreen : Fragment() {

    private val binding: FragmentSplashScreenBinding by lazy {
        FragmentSplashScreenBinding.inflate(layoutInflater)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return binding.root
    }

    override fun onStart() {
        super.onStart()
        (activity as MainActivity).topBarVisibility(View.GONE)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val handler = Handler(Looper.getMainLooper())
        handler.postDelayed({
            findNavController().navigate(R.id.action_splashScreen_to_homeFragment)
        }, 3000)
    }
}