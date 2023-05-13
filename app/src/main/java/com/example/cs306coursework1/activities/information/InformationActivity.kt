package com.example.cs306coursework1.activities.information

import android.content.Intent
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.text.Html
import android.text.Spanned
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.text.HtmlCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.cs306coursework1.R
import com.example.cs306coursework1.activities.main.MainActivity
import com.example.cs306coursework1.activities.upsert.UpsertActivity
import com.example.cs306coursework1.data.AccountType
import com.example.cs306coursework1.data.Constants
import com.example.cs306coursework1.data.UpsertMode
import com.example.cs306coursework1.data.UserSingleton
import com.example.cs306coursework1.helpers.DB
import com.example.cs306coursework1.helpers.Misc
import com.google.android.material.appbar.CollapsingToolbarLayout
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import okhttp3.MediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import org.json.JSONObject
import java.io.IOException
import java.util.concurrent.TimeUnit

class InformationActivity : AppCompatActivity() {

    private lateinit var containerLayout: LinearLayout
    private lateinit var upsertActivityIntent: Intent

    private var voiceFilePresent = false
    private var voiceFileURL: String? = null

    private lateinit var voiceButton: Button
    private lateinit var artefactID: String
    private lateinit var artefactName: String


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_information)

        containerLayout = findViewById<LinearLayout>(R.id.containerLayout)

        upsertActivityIntent = Intent(this, UpsertActivity::class.java)

        val heroImage = findViewById<ImageView>(R.id.heroImage)

        val descriptionText = findViewById<TextView>(R.id.descriptionText)
        val descriptionWikiButton = findViewById<Button>(R.id.descriptionWikiButton)

        voiceButton = findViewById(R.id.voiceButton)

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
            findViewById<CollapsingToolbarLayout>(R.id.collapsingToolbarLayout)

        val toolbar = findViewById<MaterialToolbar>(R.id.toolbar)

        toolbar.setNavigationOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }


        artefactID = intent.getStringExtra("artefact_id").toString()

        toolbar.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                R.id.action_edit -> {
                    upsertActivityIntent.putExtra("mode", UpsertMode.UPDATE)
                    upsertActivityIntent.putExtra("artefact_id", artefactID)
                    startActivity(upsertActivityIntent)
                    true
                }
                R.id.action_delete -> {
                    // USE "R.string.thing" HERE PLS
                    MaterialAlertDialogBuilder(
                        this,
                        com.google.android.material.R.style.ThemeOverlay_Material3_MaterialAlertDialog_Centered
                    )
                        .setTitle("Permanently delete?")
                        .setIcon(R.drawable.ic_delete)
                        .setMessage("Are you sure you want to delete this item? You will not be able to restore this after deleting.")
                        .setNegativeButton("Cancel", null)
                        .setPositiveButton("Delete") { _, _ ->
                            DB.deleteArtefactByID(artefactID).addOnSuccessListener {
                                val intent = Intent(this, MainActivity::class.java)
                                startActivity(intent)
                            }
                        }
                        .show()
                    true
                }
                R.id.action_share -> {
                    val sendIntent = Intent().apply {
                        action = Intent.ACTION_SEND
                        putExtra(
                            Intent.EXTRA_TEXT,
                            "Learn more about $artefactName on the ${resources.getString(R.string.app_name)}"
                        )
                        type = "text/plain"
                    }

                    val shareIntent = Intent.createChooser(sendIntent, null)
                    startActivity(shareIntent)

                    true
                }
                else -> false
            }
        }

        if (UserSingleton.getAccountType() != AccountType.CURATOR) {
            toolbar.menu.findItem(R.id.action_delete).isVisible = false
            toolbar.menu.findItem(R.id.action_edit).isVisible = false
        }


        DB.getArtefactBasicByID(artefactID).addOnSuccessListener { doc ->
            toolbarLayout.title = doc["title"].toString()
            artefactName = doc["title"].toString()

            DB.getArtefactDetailsByID(artefactID).addOnSuccessListener { documents ->
                val details = documents.first()

                // Set title of artefact in toolbar


                // Display hero image
                if (Misc.existsIn(details, "hero_image")) {
                    Misc.setImageFromURL(details["hero_image"].toString(), heroImage)
                } else {
                    // Hide if the field is not present
                    heroImage.visibility = View.GONE
                }


                // Set description
                if (Misc.existsIn(details, "description")) {
                    val descriptionDetails = details["description"] as Map<String, Any>


                    // Set description text
                    if (Misc.existsIn(descriptionDetails, "text")) {
                        descriptionText.text = descriptionDetails["text"].toString()

                        voiceFilePresent = Misc.existsIn(descriptionDetails, "voice_file")

                        if (voiceFilePresent) {
                            voiceFileURL = descriptionDetails["voice_file"].toString()
                        }

                        voiceButton.setOnClickListener {
                            if (voiceFilePresent) {
                                playVoiceFile(voiceFileURL.toString())
                            } else {
                                voiceButton.text = "Generating..."
                                voiceButton.compoundDrawablePadding = 10
                                generateVoiceFileAndStore(descriptionDetails)
                            }
                        }

                    } else {
                        voiceButton.visibility = View.GONE
                        descriptionText.visibility = View.GONE
                    }

                    // Set wiki button link
                    if (Misc.existsIn(descriptionDetails, "wikipedia_url")) {
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
                if (Misc.existsIn(details, "history")) {
                    val history = details["history"] as Map<String, String>
                    originCountryName.text = history["origin_country"].toString()

                    val str =
                        "The <b>${artefactName}</b> was first invented by <b>${history["company_name"].toString()}</b> in <b>${history["release_date"].toString()} </b>."

                    originDescription.text = Html.fromHtml(str, HtmlCompat.FROM_HTML_MODE_LEGACY)


                    originContinentImage.setImageResource(getContinentDrawable(history["continent"].toString()))
                } else {
                    findViewById<LinearLayout>(R.id.originContainer).visibility = View.GONE
                }

                // Set dimensions of item
                if (Misc.existsIn(details, "dimensions")) {
                    val dimensions = details["dimensions"] as Map<String, Float>

                    if (Misc.existsIn(dimensions, "width")) {
                        dimensionWidthView.text =
                            formatMeasure("Width", dimensions["width"].toString(), "cm")
                        dimensionWidthView.visibility = View.VISIBLE
                    }

                    if (Misc.existsIn(dimensions, "height")) {
                        dimensionHeightView.text =
                            formatMeasure("Height", dimensions["height"].toString(), "cm")
                        dimensionHeightView.visibility = View.VISIBLE
                    }

                    if (Misc.existsIn(dimensions, "depth")) {
                        dimensionDepthView.text =
                            formatMeasure("Depth", dimensions["depth"].toString(), "cm")
                        dimensionDepthView.visibility = View.VISIBLE
                    }

                    if (Misc.existsIn(dimensions, "mass")) {
                        dimensionMassView.text =
                            formatMeasure("Mass", dimensions["mass"].toString(), "kg")
                        dimensionMassView.visibility = View.VISIBLE
                    }

                    if (Misc.existsIn(dimensions, "condition")) {
                        dimensionConditionView.text =
                            formatMeasure("Condition", dimensions["condition"].toString(), null)
                        dimensionConditionView.visibility = View.VISIBLE
                    }

                } else {
                    findViewById<LinearLayout>(R.id.dimensionsContainer).visibility = View.GONE
                }


                // Set gallery images
                if (Misc.existsIn(details, "gallery")) {
                    val imageURLs = details["gallery"] as ArrayList<String>
                    val galleryLayoutManager = GridLayoutManager(this, 3)
                    galleryRecyclerView.layoutManager = galleryLayoutManager
                    val adapter = GalleryAdapter(imageURLs)
                    galleryRecyclerView.adapter = adapter
                } else {
                    findViewById<LinearLayout>(R.id.galleryContainer).visibility = View.GONE
                }

                // Set related links
                if (Misc.existsIn(details, "related_links")) {
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

    private fun playVoiceFile(url: String) {
        val media = MediaPlayer()

        media.setAudioAttributes(
            AudioAttributes.Builder().setContentType(AudioAttributes.CONTENT_TYPE_SPEECH).build()
        )

        try {
            media.setDataSource(url)

            media.prepare()
            media.start()
        } catch (e: IOException) {
            Misc.displaySnackBar(containerLayout, e.message.toString())
            e.printStackTrace()
        }
    }

    private fun generateVoiceFileAndStore(map: Map<String, Any>) {
        Thread(Runnable {
            val client = OkHttpClient().newBuilder().readTimeout(1, TimeUnit.MINUTES).build()
            val JSON = JSONObject()
            JSON.put("text", map["text"].toString())
            JSON.put("voice", Constants.AI_VOICE)

            val mediaType = MediaType.parse("application/json")
            val body =
                RequestBody.create(
                    mediaType,
                    JSON.toString().toByteArray()
                )

            val request = Request.Builder()
                .url("https://play.ht/api/v2/tts")
                .post(body)
                .addHeader("accept", "text/event-stream")
                .addHeader("content-type", "application/json")
                .addHeader("Authorization", "Bearer " + "d332fd5eada94a23be50428e8118117e")
                .addHeader("X-User-Id", "bYnwqOBCYZQcPA7OuXYdMFKeerl2")
                .build()

            val response = client.newCall(request).execute()

            response.body()?.let {
                val eventStreamOutput = it.string().dropLast(4)

                val lastLineBreak = eventStreamOutput.lastIndexOf("\n")
                val line = eventStreamOutput.substring(lastLineBreak)

                val JSONstring = line.substring(line.indexOf("{"))

                val obj = JSONObject(JSONstring)

                voiceFilePresent = true
                voiceFileURL = obj.getString("url")

                runOnUiThread {
                    voiceButton.text = ""
                    voiceButton.compoundDrawablePadding = 0

                    val data = HashMap(map)
                    data["voice_file"] = voiceFileURL

                    DB.updateArtefactDetails(
                        artefactID, hashMapOf(
                            "description" to data
                        )
                    ).addOnSuccessListener {
                        playVoiceFile(voiceFileURL.toString())
                    }.addOnFailureListener { exception ->
                        Misc.displaySnackBar(containerLayout, exception.message.toString())
                    }
                }
            }
        }).start()

    }

}