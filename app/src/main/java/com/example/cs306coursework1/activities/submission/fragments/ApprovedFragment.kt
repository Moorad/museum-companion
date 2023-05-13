package com.example.cs306coursework1.activities.submission.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.LinearLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.cs306coursework1.R
import com.example.cs306coursework1.data.AccountType
import com.example.cs306coursework1.data.SubmissionType
import com.example.cs306coursework1.data.UserSingleton
import com.example.cs306coursework1.helpers.DB
import com.example.cs306coursework1.helpers.Misc


class ApprovedFragment(private val activity: FragmentActivity?) : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_approved, container, false)

        val frameLayout = view.findViewById<FrameLayout>(R.id.frameLayout)

        val userID = if (UserSingleton.getAccountType() != AccountType.CURATOR) {
            UserSingleton.getID()
        } else {
            null
        }

        DB.getSubmissionsByStatus(
            SubmissionType.APPROVED,
            UserSingleton.getSelectedMuseumID().toString(),
            userID
        ).addOnSuccessListener { docs ->

            val recyclerView = view.findViewById<View>(R.id.recyclerView) as RecyclerView

            if (!docs.isEmpty) {
                val userIDs = ArrayList<String>()
                val artefactIDs = ArrayList<String>()

                docs.forEach { doc ->
                    userIDs.add(doc["created_by"].toString())
                    artefactIDs.add(doc["artefact_id"].toString())
                }

                DB.getUsersByUIDs(userIDs).addOnSuccessListener { users ->
                    val submissions = PendingFragment.getSubmissions(users, docs)

                    val layoutManager = LinearLayoutManager(view.context)
                    recyclerView.layoutManager = layoutManager
                    val mAdapter = SubmissionAdapter(
                        submissions,
                        SubmissionType.APPROVED,
                        view.context,
                        frameLayout
                    )
                    recyclerView.adapter = mAdapter
                }
            } else {
                recyclerView.visibility = View.GONE
                val noSubmissionsView = view.findViewById<LinearLayout>(R.id.noSubmissions)

                noSubmissionsView.visibility = View.VISIBLE
            }

        }.addOnFailureListener { exception ->
            Misc.displaySnackBar(view, exception.message.toString())
        }

        return view
    }
}