package com.example.cs306coursework1.activities.points

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.cs306coursework1.R
import com.google.android.material.appbar.MaterialToolbar

class PointsFragment(private val AppBar: MaterialToolbar) : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_points, container, false)
    }
}