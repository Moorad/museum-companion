package com.example.cs306coursework1

import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import androidx.appcompat.app.AppCompatDelegate
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class MainActivity : AppCompatActivity() {

    private var auth = Firebase.auth

    lateinit var loginActivityIntent: Intent
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        setContentView(R.layout.activity_browse)

        loginActivityIntent = Intent(this, LoginActivity::class.java)

        // Get parcelable (fancy object) from activity that we were just in
        val userDetails = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            // This variant of getParcelableExtra requires API level >= 33
            intent.getParcelableExtra("user_details", UserDetails::class.java)
        } else {
            // If API level is older than Tiramisu (API level 33) use deprecated getParcelableExtra
            @Suppress("DEPRECATION")
            intent.getParcelableExtra<UserDetails>("user_details")
        }
        // They both do the same thing but the old getParcelableExtra was not type safe and the type
        // cannot be inferred so it has to be manually typed. The new getParcelableExtra you have to
        // pass in the class of the type returned making it type safe.

        Log.println(Log.INFO, "user_details", userDetails.toString())

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