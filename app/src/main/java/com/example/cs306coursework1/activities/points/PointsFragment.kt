package com.example.cs306coursework1.activities.points

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.cs306coursework1.R
import com.example.cs306coursework1.helpers.DB
import com.example.cs306coursework1.helpers.Misc
import com.google.android.material.appbar.MaterialToolbar
import com.google.firebase.firestore.DocumentSnapshot

class PointsFragment(private val AppBar: MaterialToolbar) : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_points, container, false)

        AppBar.title = "Trust level leaderboard"

        val frameLayout = view.findViewById<FrameLayout>(R.id.frameLayout)
        val recyclerView = view.findViewById<RecyclerView>(R.id.recycler_view)
        val layoutManager = LinearLayoutManager(context)
        recyclerView.layoutManager = layoutManager

        DB.getAllUsers().addOnSuccessListener { documents ->
            val cardArrayList = populateList(documents.documents)


            val adapter = context?.let { LeaderboardAdapter(it, cardArrayList) }
            recyclerView.adapter = adapter
        }.addOnFailureListener { exception ->
            Misc.displaySnackBar(frameLayout, exception.message.toString())
        }

        return view
    }

    private fun populateList(docs: MutableList<DocumentSnapshot>): ArrayList<LeaderboardModel> {
        val list = ArrayList<LeaderboardModel>()

        docs.sortedByDescending { it["level"] as Double }
            .forEach { doc ->
                val card = LeaderboardModel()
                card.setName(doc["name"].toString())
                card.setLevel(doc["level"] as Double)
                card.setImage(doc["profile_image"].toString())
                list.add(card)
            }

        return list
    }
}