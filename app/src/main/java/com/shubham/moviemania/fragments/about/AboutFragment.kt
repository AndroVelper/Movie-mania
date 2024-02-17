package com.shubham.moviemania.fragments.about

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.shubham.moviemania.R
import com.shubham.moviemania.activities.MainActivity
import com.shubham.moviemania.databinding.FragmentAboutBinding
import com.shubham.moviemania.utility.Utility.Companion.changeStatusBarColor


class AboutFragment : Fragment() {


    private val binding: FragmentAboutBinding by lazy {
        FragmentAboutBinding.inflate(layoutInflater)
    }

    /*
    * Get the instance of the class.
    * */
    companion object {
        /*
        * Why JVM static as this annotation describes the JVM that this method is static in nature
        * as companion object are not static but behave like static but this annotation tell the JVM to
        * treat this method as static.
        * */
        @JvmStatic
        fun aboutUsInstance() = AboutFragment()  // higher Order functions
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_about, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (activity != null) {
            changeStatusBarColor((activity as MainActivity).window, requireContext(), R.color.black)
        }
    }

}