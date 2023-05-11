package com.example.cs306coursework1.activities.main

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import com.example.cs306coursework1.R
import com.example.cs306coursework1.activities.browse.BrowseFragment
import com.example.cs306coursework1.activities.points.PointsFragment
import com.example.cs306coursework1.activities.profile.ProfileFragment
import com.example.cs306coursework1.activities.submission.SubmissionFragment
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottom_navigation)
        val topAppBar = findViewById<MaterialToolbar>(R.id.topAppBar)

        val fragments = arrayOf(
            BrowseFragment(topAppBar),
            SubmissionFragment(topAppBar),
            PointsFragment(topAppBar),
            ProfileFragment(topAppBar)
        )

        setCurrentFragment(fragments[0])

        bottomNavigationView.setOnItemSelectedListener {
            topAppBar.menu.clear()

            when (it.itemId) {
                R.id.action_artefacts -> setCurrentFragment(fragments[0])
                R.id.action_submissions -> setCurrentFragment(fragments[1])
                R.id.action_points -> setCurrentFragment(fragments[2])
                R.id.action_profile -> setCurrentFragment(fragments[3])
            }
            true
        }


    }

    private fun setCurrentFragment(fragment: Fragment) =
        supportFragmentManager.beginTransaction().apply {
            replace(R.id.flFragment, fragment)
            commit()
        }
}