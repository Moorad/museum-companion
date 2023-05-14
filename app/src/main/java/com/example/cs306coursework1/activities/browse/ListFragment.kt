package com.example.cs306coursework1.activities.browse

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.cs306coursework1.R
import com.example.cs306coursework1.data.UserSingleton
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

        if (UserSingleton.getSelectedMuseumID() != null) {
            DB.getArtefactsOfMuseum(UserSingleton.getSelectedMuseumID().toString())
                .addOnSuccessListener { documents ->
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

    private fun populateList(documents: MutableList<DocumentSnapshot>): ArrayList<ListModel> {
        val list = ArrayList<ListModel>()

        documents.sortedBy { str ->
            str["label"].toString().takeWhile { c -> c.isDigit() }.toIntOrNull() ?: Int.MAX_VALUE
        }.forEach { doc ->
            val card = ListModel()
            card.setID(doc.id)
            card.setLabel(doc["label"].toString())
            card.setCreatedAt(doc["created_at"] as Timestamp)

            card.setName(doc["title"].toString())
            card.setDescription(doc["short_desc"].toString())
            card.setTags(doc["tags"] as ArrayList<String>)
            list.add(card)
        }


        return list
    }
}