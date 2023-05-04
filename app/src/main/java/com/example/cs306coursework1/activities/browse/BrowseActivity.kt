package com.example.cs306coursework1.activities.browse

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.Fragment
import com.example.cs306coursework1.R
import com.example.cs306coursework1.activities.submission.SubmissionsActivity
import com.example.cs306coursework1.data.MuseumDetails
import com.example.cs306coursework1.data.UserDetails
import com.example.cs306coursework1.helpers.Misc
import com.example.cs306coursework1.activities.login.LoginActivity
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class BrowseActivity : AppCompatActivity() {

    private var auth = Firebase.auth
    private var isMapView = true

    private var museumDetails: MuseumDetails? = null

    lateinit var submissionActivityIntent: Intent
    lateinit var loginActivityIntent: Intent
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        setContentView(R.layout.activity_browse)

        loginActivityIntent = Intent(this, LoginActivity::class.java)
        submissionActivityIntent = Intent(this, SubmissionsActivity::class.java)

        val userDetails =
            Misc.getParcelableFromIntent(intent, "user_details", UserDetails::class.java)
        museumDetails =
            Misc.getParcelableFromIntent(intent, "museum_details", MuseumDetails::class.java)

        Log.println(Log.INFO, "user_details", userDetails.toString())
        Log.println(Log.INFO, "museum_details", museumDetails.toString())


        val mapFragment = MapFragment()
        val listFragment = ListFragment()
        val switchViewButton =
            findViewById<ExtendedFloatingActionButton>(R.id.switchViewFloatingButton)

        // Show map fragment by default
        showFragment(listFragment)

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


        val navigationBarView = findViewById<BottomNavigationView>(R.id.bottom_navigation)
        navigationBarView.selectedItemId = R.id.action_artefacts
        navigationBarView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.action_artefacts -> {
                    true
                }
                R.id.action_submissions -> {
                    submissionActivityIntent.putExtra("user_details", userDetails)
                    startActivity(submissionActivityIntent)
                    overridePendingTransition(0, 0)
                    true
                }
                else -> false
            }
        }
//        switchViewButton.setOnClickListener {
//            switchViewButton.setCompoundDrawablesWithIntrinsicBounds(
//                R.drawable.ic_map_view,
//                0,
//                0,
//                0
//            )
//        }
//        val signOutButton = findViewById<Button>(R.id.signOutButton)

//        signOutButton.setOnClickListener {
//            signOutUser()
//        }
    }

//    private fun signOutUser() {
//        auth.signOut()
//
//        startActivity(loginActivityIntent)
//    }

    private fun showFragment(fragment: Fragment) {
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.flFragment, fragment)
        transaction.addToBackStack(null);
        transaction.commit()
    }

    fun getMuseumDetails(): MuseumDetails? {
        return this.museumDetails
    }
}