package com.example.cs306coursework1.activities.submission.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.cs306coursework1.R
import com.example.cs306coursework1.data.SubmissionType
import com.example.cs306coursework1.helpers.DB
import com.example.cs306coursework1.helpers.Misc
import com.google.android.gms.tasks.TaskCompletionSource
import com.google.firebase.firestore.QuerySnapshot

class PendingFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_pending, container, false)

        DB.getSubmissionsByStatus(SubmissionType.PENDING).addOnSuccessListener { docs ->
            val userIDs = ArrayList<String>()
            val artefactIDs = ArrayList<String>()

            docs.forEach { doc ->
                userIDs.add(doc["created_by"].toString())
                artefactIDs.add(doc["artefact_id"].toString())
            }

            DB.getUsersByUIDs(userIDs).addOnSuccessListener { users ->
                val submissions = getSubmissions(users, artefactIDs)
                val recyclerView = view.findViewById<View>(R.id.recyclerView) as RecyclerView
                val layoutManager = LinearLayoutManager(view.context)
                recyclerView.layoutManager = layoutManager

                val mAdapter = SubmissionAdapter(submissions, view.context)
                recyclerView.adapter = mAdapter
            }
        }.addOnFailureListener { exception ->
            Misc.displaySnackBar(view, exception.message.toString())
        }

        return view
    }

    private fun getSubmissions(
        users: QuerySnapshot,
        artefactIDs: ArrayList<String>
    ): ArrayList<SubmissionModal> {
        val models = ArrayList<SubmissionModal>()

        users.forEachIndexed { i, user ->
            val model = SubmissionModal()
            model.setName(user["name"].toString())
            model.setImage(R.drawable.ah)
            model.setLevel(1.5F)
            model.setArtefactID(artefactIDs[i])
            models.add(model)
        }

        return models
    }

}