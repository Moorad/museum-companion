package com.example.cs306coursework1.activities.information

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Html
import android.text.Spanned
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.text.HtmlCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.cs306coursework1.R
import com.example.cs306coursework1.helpers.DB
import com.example.cs306coursework1.helpers.Misc
import com.google.android.material.appbar.CollapsingToolbarLayout
import com.google.android.material.appbar.MaterialToolbar
import com.google.firebase.firestore.QueryDocumentSnapshot

class InformationActivity : AppCompatActivity() {

    private lateinit var containerLayout: LinearLayout

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

        val dimensionWidthView = findViewById<TextView>(R.id.dimensionWidth)
        val dimensionHeightView = findViewById<TextView>(R.id.dimensionHeight)
        val dimensionDepthView = findViewById<TextView>(R.id.dimensionDepth)
        val dimensionMassView = findViewById<TextView>(R.id.dimensionMass)
        val dimensionConditionView = findViewById<TextView>(R.id.dimensionCondition)

        val toolbarLayout =
            findViewById<CollapsingToolbarLayout>(R.id.collpasingToolbarLayout)

        val toolbar = findViewById<MaterialToolbar>(R.id.toolbar)

        toolbar.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        val artefactID = intent.getStringExtra("artefact_id").toString()
        val artefactName = intent.getStringExtra("artefact_name").toString()


        DB.getArtefactDetailsByID(artefactID).addOnSuccessListener { documents ->
            val details = documents.first()

            // Set title of artefact in toolbar
            toolbarLayout.title = artefactName

            // Display hero image
            if (existsIn(details, "hero_image")) {
                Misc.setImageFromURL(details["hero_image"].toString(), heroImage)
            } else {
                // Hide if the field is not present
                heroImage.visibility = View.GONE
            }


            // Set description
            if (existsIn(details, "description")) {
                val descriptionDetails = details["description"] as Map<String, Any>


                // Set description text
                if (existsIn(descriptionDetails, "text")) {
                    descriptionText.text = descriptionDetails["text"].toString()
                } else {
                    descriptionText.visibility = View.GONE
                }

                // Set wiki button link
                if (existsIn(descriptionDetails, "wikipedia_url")) {
                    descriptionWikiButton.setOnClickListener {
                        val browserIntent =
                            Intent(
                                Intent.ACTION_VIEW,
                                Uri.parse(descriptionDetails["wikipedia_url"].toString())
                            )

                        startActivity(browserIntent)
                    }
                } else {
                    descriptionWikiButton.visibility = View.GONE
                }
            } else {
                // Hide if the field is not present
                findViewById<LinearLayout>(R.id.descriptionContainer).visibility = View.GONE
            }

            // Set origin of item
            if (existsIn(details, "history")) {
                val history = details["history"] as Map<String, String>
                originCountryName.text = history["origin_country"].toString()

                originDescription.text =
                    "The " + artefactName + " was first invented by " + history["company_name"].toString() + " in " + history["release_date"] + "."

                originContinentImage.setImageResource(getContinentDrawable(history["continent"].toString()))
            } else {
                findViewById<LinearLayout>(R.id.originContainer).visibility = View.GONE
            }

            // Set dimensions of item
            if (existsIn(details, "dimensions")) {
                val dimensions = details["dimensions"] as Map<String, Float>

                if (existsIn(dimensions, "width")) {
                    dimensionWidthView.text =
                        formatMeasure("Width", dimensions["width"].toString(), "cm")
                    dimensionWidthView.visibility = View.VISIBLE
                }

                if (existsIn(dimensions, "height")) {
                    dimensionHeightView.text =
                        formatMeasure("Height", dimensions["height"].toString(), "cm")
                    dimensionHeightView.visibility = View.VISIBLE
                }

                if (existsIn(dimensions, "depth")) {
                    dimensionDepthView.text =
                        formatMeasure("Depth", dimensions["depth"].toString(), "cm")
                    dimensionDepthView.visibility = View.VISIBLE
                }

                if (existsIn(dimensions, "mass")) {
                    dimensionMassView.text =
                        formatMeasure("Mass", dimensions["mass"].toString(), "kg")
                    dimensionMassView.visibility = View.VISIBLE
                }

                if (existsIn(dimensions, "condition")) {
                    dimensionConditionView.text =
                        formatMeasure("Condition", dimensions["condition"].toString(), null)
                    dimensionConditionView.visibility = View.VISIBLE
                }

            } else {
                findViewById<LinearLayout>(R.id.dimensionsContainer).visibility = View.GONE
            }


            // Set gallery images
            if (existsIn(details, "gallery")) {
                val imageURLs = details["gallery"] as ArrayList<String>
                val galleryLayoutManager = GridLayoutManager(this, 3)
                galleryRecyclerView.layoutManager = galleryLayoutManager
                val adapter = GalleryAdapter(imageURLs)
                galleryRecyclerView.adapter = adapter
            } else {
                findViewById<LinearLayout>(R.id.galleryContainer).visibility = View.GONE
            }

            // Set related links
            if (existsIn(details, "related_links")) {
                val links = details["related_links"] as ArrayList<Map<String, String>>
                val modals = populateLinks(links)
                val linksLayoutManager = LinearLayoutManager(this)
                linksRecyclerView.layoutManager = linksLayoutManager
                val linksAdapter = LinksAdapter(this, modals)
                linksRecyclerView.adapter = linksAdapter
            } else {
                findViewById<LinearLayout>(R.id.linksContainer).visibility = View.GONE
            }

        }.addOnFailureListener { exception ->
            Misc.displaySnackBar(containerLayout, exception.message.toString())
        }
    }

    private fun populateLinks(data: ArrayList<Map<String, String>>): ArrayList<LinksModel> {
        val links = ArrayList<LinksModel>()
        data.forEach { link ->
            val modal = LinksModel()
            modal.setLinkTitle(link["title"].toString())
            modal.setLinkURL(link["url"].toString())

            links.add(modal)
        }

        return links
    }

    private fun formatMeasure(label: String, value: String, unit: String?): Spanned {
        var str = "<b>$label:</b> $value "

        if (unit != null) {
            str += unit
        }
        return Html.fromHtml(str, HtmlCompat.FROM_HTML_MODE_LEGACY)
    }

    private fun existsIn(hashMap: Map<String, Any>, entry: String): Boolean {
        return hashMap.get(entry) != null && hashMap.get(entry) != ""
    }

    private fun existsIn(
        hashMap: QueryDocumentSnapshot, entry: String
    ): Boolean {
        return hashMap.get(entry) != null && hashMap.get(entry) != ""
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