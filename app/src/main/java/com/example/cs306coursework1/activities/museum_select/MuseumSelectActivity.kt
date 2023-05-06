package com.example.cs306coursework1.activities.museum_select

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.cs306coursework1.R
import com.example.cs306coursework1.data.UserSingleton
import com.example.cs306coursework1.helpers.DB
import com.example.cs306coursework1.helpers.Misc
import com.google.android.material.appbar.MaterialToolbar
import com.google.firebase.firestore.DocumentSnapshot

class MuseumSelectActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_museum_select)

        val noMuseumView = findViewById<LinearLayout>(R.id.noMuseum)
        val topAppBar = findViewById<MaterialToolbar>(R.id.topAppBar)
        val constraintLayout = findViewById<ConstraintLayout>(R.id.constraintLayout)
        val recyclerView = findViewById<RecyclerView>(R.id.recycler_view)
        val layoutManager = LinearLayoutManager(this)
        recyclerView.layoutManager = layoutManager

        topAppBar.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        Misc.displaySnackBar(
            constraintLayout,
            "Signed in as " + UserSingleton.getUsername(),
        )

        DB.getAvailableMuseums().addOnSuccessListener { documents ->

            // If there is no museums display the "No museums added" text
            // and skip the reset of this code
            if (documents.size() == 0) {
                noMuseumView.visibility = View.VISIBLE
                return@addOnSuccessListener
            }

            val cardArrayList = populateList(documents.documents)

            val adapter = MuseumsAdapter(this, cardArrayList)
            recyclerView.adapter = adapter
        }.addOnFailureListener { exception ->
            Misc.displaySnackBar(constraintLayout, exception.message.toString())
        }
    }

    private fun populateList(documents: MutableList<DocumentSnapshot>): ArrayList<MuseumsModal> {
        val list = ArrayList<MuseumsModal>()

        documents.forEach { doc ->
            val card = MuseumsModal()
            card.setName(doc["name"].toString())
            card.setCuratorName(doc["owner_name"].toString())
            card.setDescription(doc["description"].toString())
            card.setID(doc.id)
            list.add(card)
        }

        return list
    }
}