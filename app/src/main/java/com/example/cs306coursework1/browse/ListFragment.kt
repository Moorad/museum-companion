package com.example.cs306coursework1.browse

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.cs306coursework1.R
import com.example.cs306coursework1.helpers.DB
import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentSnapshot

class ListFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_list, container, false)

        val noArtefactsView = view.findViewById<LinearLayout>(R.id.noArtefacts)
        val recyclerView = view.findViewById<RecyclerView>(R.id.recycler_view)
        val layoutManager = LinearLayoutManager(this.context)
        recyclerView.layoutManager = layoutManager

        val browseActivity = activity as BrowseActivity
        val museumDetails = browseActivity.getMuseumDetails()

        if (museumDetails != null) {
            DB.getArtefactsOfMuseum(museumDetails.id).addOnSuccessListener { documents ->
                // If there is no museums display the "No museums added" text
                // and skip the reset of this code
                if (documents.size() == 0) {
                    noArtefactsView.visibility = View.VISIBLE
                    return@addOnSuccessListener
                }

                val cardArrayList = populateList(documents.documents)

                val adapter = this.context?.let { ListAdapter(it, cardArrayList) }
                recyclerView.adapter = adapter
            }
        }

        return view
    }

    private fun populateList(documents: MutableList<DocumentSnapshot>): ArrayList<ListModal> {
        val list = ArrayList<ListModal>()

        documents.forEach { doc ->
            val card = ListModal()
            card.setID(doc.id)
            card.setLabel(doc["label"].toString())
            card.setCreatedAt(doc["created_at"] as Timestamp)
            Log.println(Log.INFO, "created_at type", doc["created_at"]!!.javaClass.name)

            card.setName(doc["title"].toString())
            card.setDescription(doc["short_desc"].toString())
            card.setTags(doc["tags"] as ArrayList<String>)
            list.add(card)
        }


        return list
    }
}