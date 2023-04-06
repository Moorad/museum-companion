package com.example.cs306coursework1.information

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.example.cs306coursework1.R
import com.example.cs306coursework1.browse.ListAdapter
import com.example.cs306coursework1.helpers.DB
import com.example.cs306coursework1.helpers.Err
import com.example.cs306coursework1.helpers.Misc
import com.google.android.material.appbar.CollapsingToolbarLayout
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.squareup.picasso.Picasso

class InformationActivity : AppCompatActivity() {

    lateinit var containerLayout: LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_information)

        containerLayout = findViewById<LinearLayout>(R.id.containerLayout)
        val heroImage = findViewById<ImageView>(R.id.heroImage)

        val descriptionText = findViewById<TextView>(R.id.descriptionText)
        val descriptionWikiButton = findViewById<Button>(R.id.descriptionWikiButton)

        val galleryRecyclerView = findViewById<RecyclerView>(R.id.galleryRecyclerView)
        val linksRecyclerView = findViewById<RecyclerView>(R.id.linksRecyclerView)
        val originCountryName = findViewById<TextView>(R.id.originCountryName)
        val originDescription = findViewById<TextView>(R.id.originDescription)
        val originContinentImage = findViewById<ImageView>(R.id.originContinentImage)
        val toolbarLayout =
            findViewById<CollapsingToolbarLayout>(R.id.collpasingToolbarLayout)
        val artefactID = intent.getStringExtra("artefact_id").toString()


        DB.getArtefactByID(artefactID).addOnSuccessListener { document ->

            val details = document.get("details") as Map<String, Any>

            // Set title of artefact in toolbar
            toolbarLayout.title = document["title"].toString()

            // Display hero image
            Misc.setImageFromURL(details["hero_image"].toString(), heroImage)

            // Set description
            val descriptionDetails = details["description"] as Map<String, Any>
            descriptionText.text = descriptionDetails["text"].toString()

            // Set wiki button link
            descriptionWikiButton.setOnClickListener {
                val browserIntent =
                    Intent(
                        Intent.ACTION_VIEW,
                        Uri.parse(descriptionDetails["wikipedia_url"].toString())
                    )

                startActivity(browserIntent)
            }

            // Set origin of item
            val history = details["history"] as Map<String, String>
            originCountryName.text = history["origin_country"].toString()

            originDescription.text =
                "The " + document["title"].toString() + " was first invented by " + history["company_name"].toString() + " in " + history["release_date"] + "."

            originContinentImage.setImageResource(getContinentDrawable(history["continent"].toString()))

            // Set gallery images
            val imageURLs = details["gallery"] as ArrayList<String>
            val galleryLayoutManager = GridLayoutManager(this, 3)
            galleryRecyclerView.layoutManager = galleryLayoutManager
            val adapter = GalleryAdapter(imageURLs)
            galleryRecyclerView.adapter = adapter

            // Set related links
            val links = details["related_links"] as ArrayList<Map<String, String>>
            val modals = populateLinks(links)
            val linksLayoutManager = LinearLayoutManager(this)
            linksRecyclerView.layoutManager = linksLayoutManager
            val linksAdapter = LinksAdapter(this, modals)
            linksRecyclerView.adapter = linksAdapter

        }.addOnFailureListener { exception ->
            Err.displaySnackBar(containerLayout, exception.message.toString())
        }
    }

    private fun populateLinks(data: ArrayList<Map<String, String>>): ArrayList<LinksModal> {
        val links = ArrayList<LinksModal>()
        data.forEach { link ->
            val modal = LinksModal()
            modal.setLinkTitle(link["title"].toString())
            modal.setLinkURL(link["url"].toString())

            links.add(modal)
        }

        return links
    }

    private fun getContinentDrawable(continentCode: String): Int {
        return when (continentCode) {
            "AS" -> R.drawable.graph_as
            "EU" -> R.drawable.graph_eu
            "NA" -> R.drawable.graph_na
            "SA" -> R.drawable.graph_sa
            "AF" -> R.drawable.graph_af
            "OC" -> R.drawable.graph_oc
            else -> R.drawable.graph_none
        }
    }

}