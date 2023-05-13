package com.example.cs306coursework1.activities.submission

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.viewpager2.widget.ViewPager2
import com.example.cs306coursework1.R
import com.example.cs306coursework1.activities.submission.fragments.ApprovedFragment
import com.example.cs306coursework1.activities.submission.fragments.DeniedFragment
import com.example.cs306coursework1.activities.submission.fragments.PendingFragment
import com.example.cs306coursework1.activities.upsert.UpsertActivity
import com.example.cs306coursework1.data.AccountType
import com.example.cs306coursework1.data.UpsertMode
import com.example.cs306coursework1.data.UserSingleton
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.tabs.TabLayout

class SubmissionFragment(private val AppBar: MaterialToolbar) : Fragment() {
    private lateinit var tabLayout: TabLayout
    private lateinit var viewPager: ViewPager2
    private lateinit var noEntryMessage: LinearLayout

    private lateinit var upsertActivityIntent: Intent

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_submission, container, false)

        AppBar.title = "Artefact submissions"

        upsertActivityIntent = Intent(context, UpsertActivity::class.java)

        viewPager = view.findViewById(R.id.viewPager)
        tabLayout = view.findViewById(R.id.tab_layout)
        noEntryMessage = view.findViewById(R.id.noEntrySubmissions)

        if (UserSingleton.getAccountType() != AccountType.GUEST) {
            AppBar.inflateMenu(R.menu.submission_activity_menu)

            AppBar.setOnMenuItemClickListener { item ->
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

        val fragments = arrayListOf(
            ApprovedFragment(activity),
            PendingFragment(activity),
            DeniedFragment(activity)
        )
        val viewPagerAdapter = ViewPagerAdapter(fragments, childFragmentManager, lifecycle)
        viewPager.adapter = viewPagerAdapter
        viewPager.isSaveEnabled = false



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
        return view
    }
}