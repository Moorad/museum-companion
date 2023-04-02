package com.example.cs306coursework1

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import androidx.appcompat.app.AppCompatDelegate
import com.example.cs306coursework1.data.MuseumDetails
import com.example.cs306coursework1.data.UserDetails
import com.example.cs306coursework1.helpers.Misc
import com.example.cs306coursework1.login.LoginActivity
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class BrowseActivity : AppCompatActivity() {

    private var auth = Firebase.auth

    lateinit var loginActivityIntent: Intent
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        setContentView(R.layout.activity_browse)

        loginActivityIntent = Intent(this, LoginActivity::class.java)

        val userDetails =
            Misc.getParcelableFromIntent(intent, "user_details", UserDetails::class.java)
        val museumDetails =
            Misc.getParcelableFromIntent(intent, "museum_details", MuseumDetails::class.java)

        Log.println(Log.INFO, "user_details", userDetails.toString())
        Log.println(Log.INFO, "museum_details", museumDetails.toString())

        val signOutButton = findViewById<Button>(R.id.signOutButton)

        signOutButton.setOnClickListener {
            signOutUser()
        }
    }

    private fun signOutUser() {
        auth.signOut()

        startActivity(loginActivityIntent)
    }
}