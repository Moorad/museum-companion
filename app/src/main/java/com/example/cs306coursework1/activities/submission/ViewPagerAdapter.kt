package com.example.cs306coursework1.activities.submission

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.cs306coursework1.activities.submission.fragments.ApprovedFragment
import com.example.cs306coursework1.activities.submission.fragments.DeniedFragment
import com.example.cs306coursework1.activities.submission.fragments.PendingFragment
import com.example.cs306coursework1.activities.upsert.LinksModel

class ViewPagerAdapter(
    private val listFragment: ArrayList<Fragment>,
    fm: FragmentManager,
    lifecycle: Lifecycle
) : FragmentStateAdapter(fm, lifecycle) {

    override fun getItemCount(): Int {
        return listFragment.size
    }

    override fun createFragment(position: Int): Fragment {
        return listFragment[position]
    }
}