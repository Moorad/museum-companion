package com.example.cs306coursework1

import android.os.Bundle
import android.widget.Button
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.animation.fadeOut
import androidx.core.view.WindowCompat
import androidx.fragment.app.FragmentTransaction
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import com.example.cs306coursework1.databinding.ActivityLoginBinding
import com.google.android.material.appbar.MaterialToolbar

class LoginActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val loginFragment = LoginFragment()

        val loginOptionButton = findViewById<Button>(R.id.loginOptionButton)

        loginOptionButton.setOnClickListener {
            val transaction = supportFragmentManager.beginTransaction()
            transaction.setCustomAnimations(
                R.anim.slide_in,
                R.anim.fade_out,
                R.anim.fade_in,
                R.anim.slide_out
            )
            transaction.replace(R.id.flFragment, loginFragment)
            transaction.addToBackStack(null);
            transaction.commit()
        }
    }
}