package com.example.cs306coursework1

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AutoCompleteTextView
import android.widget.Button

/**
 * A simple [Fragment] subclass.
 */
class RegisterFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_register, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val registerBackButton = view.findViewById<Button>(R.id.registerBackButton)

        // Make the back button on the top left corner
        // function the same as pressing the system back button
        registerBackButton?.setOnClickListener {
            activity?.onBackPressedDispatcher?.onBackPressed()
        }

        // Set the first account type ("Standard") in the string array as the default
        val dropdownMenu = view.findViewById<AutoCompleteTextView>(R.id.dropdownMenu)

        dropdownMenu.setText(resources.getStringArray(R.array.account_types)[0])

    }

}