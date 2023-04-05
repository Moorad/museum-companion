package com.example.cs306coursework1

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import com.example.cs306coursework1.helpers.DB
import com.example.cs306coursework1.helpers.Err
import com.google.android.material.appbar.CollapsingToolbarLayout
import com.google.android.material.appbar.MaterialToolbar
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
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

        val toolbarLayout =
            findViewById<CollapsingToolbarLayout>(R.id.collpasingToolbarLayout)
        val artefactID = intent.getStringExtra("artefact_id").toString()


        DB.getArtefactByID(artefactID).addOnSuccessListener { document ->

            toolbarLayout.title = document["title"].toString()
            val details = document.get("details") as Map<String, Any>

            // Display hero image
            getImageAndDisplay(details["hero_image"].toString(), heroImage)

            val descriptionDetails = details["description"] as Map<String, Any>
            descriptionText.text = descriptionDetails["text"].toString()

            descriptionWikiButton.setOnClickListener {
                val browserIntent =
                    Intent(
                        Intent.ACTION_VIEW,
                        Uri.parse(descriptionDetails["wikipedia_url"].toString())
                    )

                startActivity(browserIntent)
            }

        }.addOnFailureListener { exception ->
            Err.displaySnackBar(containerLayout, exception.message.toString())
        }
    }

    private fun getImageAndDisplay(storageLocation: String, imageView: ImageView) {
        val imageRef =
            Firebase.storage.getReferenceFromUrl(storageLocation)

        imageRef.downloadUrl.addOnSuccessListener { uri ->
            Picasso.get().load(uri.toString()).into(imageView)
        }.addOnFailureListener { exception ->
            Err.displaySnackBar(containerLayout, exception.message.toString())
        }
    }

}