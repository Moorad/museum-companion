package com.example.cs306coursework1.activities.submission.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.LinearLayout
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.cs306coursework1.R
import com.example.cs306coursework1.data.AccountType
import com.example.cs306coursework1.data.SubmissionType
import com.example.cs306coursework1.data.UserSingleton
import com.example.cs306coursework1.helpers.DB
import com.example.cs306coursework1.helpers.Misc
import com.google.android.gms.tasks.TaskCompletionSource
import com.google.firebase.Timestamp
import com.google.firebase.firestore.QueryDocumentSnapshot
import com.google.firebase.firestore.QuerySnapshot

class PendingFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_pending, container, false)

        val frameLayout = view.findViewById<FrameLayout>(R.id.frameLayout)

        val userID = if (UserSingleton.getAccountType() != AccountType.CURATOR) {
            UserSingleton.getID()
        } else {
            null
        }

        DB.getSubmissionsByStatus(
            SubmissionType.PENDING,
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
                    val submissions = getSubmissions(users, docs)

                    val layoutManager = LinearLayoutManager(view.context)
                    recyclerView.layoutManager = layoutManager
                    val mAdapter = SubmissionAdapter(submissions, view.context, frameLayout)
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

    private fun getSubmissions(
        users: QuerySnapshot,
        docs: QuerySnapshot
    ): ArrayList<SubmissionModal> {
        val models = ArrayList<SubmissionModal>()

        docs.sortedByDescending { (it["last_updated"] as Timestamp).toDate().time }.forEach { doc ->
            val model = SubmissionModal()
            val userName =
                users.find { user -> user["uid"] == doc["created_by"] }?.get("name").toString()
            model.setName(userName)
            model.setLastUpdated(
                Misc.toTimeAgo(
                    (doc["last_updated"] as Timestamp).toDate()
                ).toString()
            )
            model.setImage(R.drawable.ah)
            model.setLevel(1.5F)
            model.setArtefactID(doc["artefact_id"].toString())
            models.add(model)
        }

        return models
    }

}