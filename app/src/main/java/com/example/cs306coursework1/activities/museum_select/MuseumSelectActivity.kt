package com.example.cs306coursework1.activities.museum_select

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.cs306coursework1.R
import com.example.cs306coursework1.activities.login.LoginActivity
import com.example.cs306coursework1.activities.museum_upsert.MuseumUpsertActivity
import com.example.cs306coursework1.data.AccountType
import com.example.cs306coursework1.data.UpsertMode
import com.example.cs306coursework1.data.UserSingleton
import com.example.cs306coursework1.helpers.DB
import com.example.cs306coursework1.helpers.Misc
import com.google.android.material.appbar.MaterialToolbar
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.ktx.Firebase

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
            Firebase.auth.signOut()
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }

        topAppBar.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                R.id.action_add -> {
                    val intent = Intent(this, MuseumUpsertActivity::class.java)
                    intent.putExtra("mode", UpsertMode.INSERT)
                    startActivity(intent)
                    true
                }
                else -> false
            }
        }

        if (UserSingleton.getAccountType() != AccountType.CURATOR) {
            topAppBar.menu.findItem(R.id.action_add).isVisible = false
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

            val adapter = MuseumsAdapter(this, cardArrayList, constraintLayout)
            recyclerView.adapter = adapter
        }.addOnFailureListener { exception ->
            Misc.displaySnackBar(constraintLayout, exception.message.toString())
        }
    }

    private fun populateList(documents: MutableList<DocumentSnapshot>): ArrayList<MuseumsModel> {
        val list = ArrayList<MuseumsModel>()

        documents.forEach { doc ->
            val card = MuseumsModel()
            card.setName(doc["name"].toString())
            card.setCuratorName(doc["owner_name"].toString())
            card.setDescription(doc["description"].toString())
            card.setID(doc.id)
            list.add(card)
        }

        return list
    }
}