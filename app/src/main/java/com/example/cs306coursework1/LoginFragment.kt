package com.example.cs306coursework1

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button

/**
 * A simple [Fragment] subclass.
 */
class LoginFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_login, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val loginBackButton = view.findViewById<Button>(R.id.loginBackButton)

        // Make the back button on the top left corner
        // function the same as pressing the system back button
        loginBackButton?.setOnClickListener {
            activity?.onBackPressedDispatcher?.onBackPressed()
        }
    }

}