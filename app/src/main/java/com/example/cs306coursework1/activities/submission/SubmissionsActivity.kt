package com.example.cs306coursework1.activities.submission

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.LinearLayout
import androidx.viewpager2.widget.ViewPager2
import com.example.cs306coursework1.R
import com.example.cs306coursework1.activities.browse.BrowseActivity
import com.example.cs306coursework1.activities.submission.fragments.ApprovedFragment
import com.example.cs306coursework1.activities.submission.fragments.DeniedFragment
import com.example.cs306coursework1.activities.submission.fragments.PendingFragment
import com.example.cs306coursework1.activities.submission.fragments.SubmissionModal
import com.example.cs306coursework1.activities.upsert.UpsertActivity
import com.example.cs306coursework1.data.AccountType
import com.example.cs306coursework1.data.UpsertMode
import com.example.cs306coursework1.data.UserDetails
import com.example.cs306coursework1.helpers.Misc
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.tabs.TabLayout

class SubmissionsActivity : AppCompatActivity() {
    private lateinit var tabLayout: TabLayout
    private lateinit var viewPager: ViewPager2
    private lateinit var noEntryMessage: LinearLayout

    private lateinit var browseActivityIntent: Intent
    private lateinit var upsertActivityIntent: Intent
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_submissions)

        val userDetails =
            Misc.getParcelableFromIntent(intent, "user_details", UserDetails::class.java)

        Log.println(Log.INFO, "user", userDetails.toString())

        browseActivityIntent = Intent(this, BrowseActivity::class.java)
        upsertActivityIntent = Intent(this, UpsertActivity::class.java)

        val toolbar = findViewById<MaterialToolbar>(R.id.toolbar)
        viewPager = findViewById(R.id.viewPager)
        tabLayout = findViewById(R.id.tab_layout)
        noEntryMessage = findViewById(R.id.noEntrySubmissions)

        toolbar.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        val navigationBarView = findViewById<BottomNavigationView>(R.id.bottom_navigation)
        navigationBarView.selectedItemId = R.id.action_submissions
        navigationBarView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.action_artefacts -> {
                    startActivity(browseActivityIntent)
                    true
                }
                R.id.action_submissions -> {
                    true
                }
                else -> false
            }
        }

        if (userDetails?.accountType != AccountType.GUEST) {
            toolbar.inflateMenu(R.menu.submission_activity_menu)

            toolbar.setOnMenuItemClickListener { item ->
                when (item.itemId) {
                    R.id.action_add -> {
                        upsertActivityIntent.putExtra("mode", UpsertMode.INSERT)
                        startActivity(upsertActivityIntent)
                        true
                    }
                    else -> false
                }
            }

            viewPager.visibility = View.VISIBLE
            tabLayout.visibility = View.VISIBLE
            noEntryMessage.visibility = View.GONE

        }

        val fragments = arrayListOf(ApprovedFragment(), PendingFragment(), DeniedFragment())
        val viewPagerAdapter = ViewPagerAdapter(fragments, supportFragmentManager, lifecycle)
        viewPager.adapter = viewPagerAdapter

        tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                viewPager.currentItem = tab.position
            }

            override fun onTabUnselected(tab: TabLayout.Tab) {

            }

            override fun onTabReselected(tab: TabLayout.Tab) {

            }
        })

        viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                tabLayout.getTabAt(position)?.select()
            }

        })
    }
}