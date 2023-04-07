package com.example.cs306coursework1.login

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import com.example.cs306coursework1.*
import com.example.cs306coursework1.data.UserDetails
import com.example.cs306coursework1.data.AccountType
import com.example.cs306coursework1.helpers.DB
import com.example.cs306coursework1.helpers.Misc
import com.example.cs306coursework1.museum_select.MuseumSelectActivity
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class LoginActivity : AppCompatActivity() {

    // Auth variables
    private val auth = Firebase.auth
    private val currentUser = auth.currentUser

    lateinit var constraintLayout: ConstraintLayout

    lateinit var museumsActivityIntent: Intent
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        museumsActivityIntent = Intent(this, MuseumSelectActivity::class.java)

        constraintLayout = findViewById(R.id.constraintLayout)

        // User is logged in, redirect them to the main activity
        if (currentUser != null) {
            getUserAndRedirect()
        }

        val loginFragment = LoginFragment()
        val registerFragment = RegisterFragment()

        val guestOptionButton = findViewById<Button>(R.id.guestOptionButton)
        val loginOptionButton = findViewById<Button>(R.id.loginOptionButton)
        val registerOptionButton = findViewById<Button>(R.id.registerOptionButton)

        // Go to main activity if guest button was clicked
        guestOptionButton.setOnClickListener {
            museumsActivityIntent.putExtra(
                "user_details",
                UserDetails(null, "Guest user", null, AccountType.GUEST)
            )
            startActivity(museumsActivityIntent)
        }

        // Show login fragment if sign in button was clicked
        loginOptionButton.setOnClickListener {
            showFragment(loginFragment)
        }

        // Show register fragment if sign up button was clicked
        registerOptionButton.setOnClickListener {
            showFragment(registerFragment)
        }
    }

    private fun showFragment(fragment: Fragment) {
        val transaction = supportFragmentManager.beginTransaction()
        transaction.setCustomAnimations(
            R.anim.slide_in,
            R.anim.fade_out,
            R.anim.fade_in,
            R.anim.slide_out
        )
        transaction.replace(R.id.flFragment, fragment)
        transaction.addToBackStack(null);
        transaction.commit()
    }

    private fun getUserAndRedirect() {
        DB.getUserByUID(currentUser?.uid.toString())
            .addOnSuccessListener { documents ->
                val doc = documents.first()
                museumsActivityIntent.putExtra(
                    "user_details",
                    UserDetails(
                        currentUser?.uid.toString(),
                        doc.data["name"].toString(),
                        currentUser?.email.toString(),
                        AccountType.getTypeFromString(doc.data["type"].toString())
                    )
                )

                startActivity(museumsActivityIntent)
            }.addOnFailureListener { exception ->
                Misc.displaySnackBar(constraintLayout, exception.message.toString())
            }
    }
}