package com.example.cs306coursework1.activities.browse

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.cs306coursework1.R
import com.example.cs306coursework1.activities.login.LoginActivity
import com.example.cs306coursework1.activities.museum_select.MuseumSelectActivity
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton

class BrowseFragment(private val AppBar: MaterialToolbar) : Fragment() {

    private var isMapView = false

    private lateinit var loginActivityIntent: Intent

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_browse, container, false)

        AppBar.menu.clear()
        AppBar.title = "Browsing artefacts"

        AppBar.inflateMenu(R.menu.browse_activity_menu)

        AppBar.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                R.id.action_switch_museum -> {
                    val museumSelectActivityIntent =
                        Intent(context, MuseumSelectActivity::class.java)
                    startActivity(museumSelectActivityIntent)
                    true
                }
                else -> false
            }
        }

        loginActivityIntent = Intent(context, LoginActivity::class.java)

        val mapFragment = MapFragment()
        val listFragment = ListFragment()
        val switchViewButton =
            view.findViewById<ExtendedFloatingActionButton>(R.id.switchViewFloatingButton)

        // Show map fragment by default
        if (isMapView) {
            showFragment(mapFragment)
        } else {
            showFragment(listFragment)
        }

        switchViewButton.setOnClickListener {
            if (isMapView) {
                showFragment(listFragment)
                switchViewButton.setIconResource(R.drawable.ic_map_view)
            } else {
                showFragment(mapFragment)
                switchViewButton.setIconResource(R.drawable.ic_list_view)
            }

            isMapView = !isMapView
        }

        return view
    }

    private fun showFragment(fragment: Fragment) {
        val transaction = parentFragmentManager.beginTransaction()
        transaction.replace(R.id.flFragment, fragment)
        transaction.addToBackStack(null);
        transaction.commit()
    }
}