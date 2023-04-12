package com.example.cs306coursework1.activities.upsert

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import androidx.constraintlayout.widget.ConstraintLayout
import com.example.cs306coursework1.R
import com.example.cs306coursework1.data.UpsertMode
import com.example.cs306coursework1.helpers.DB
import com.example.cs306coursework1.helpers.Misc
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.textfield.MaterialAutoCompleteTextView
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.Timestamp

class UpsertActivity : AppCompatActivity() {
    private lateinit var museumID: String
    private lateinit var mode: UpsertMode

    private lateinit var constraintLayout: ConstraintLayout

    private lateinit var titleEditText: TextInputEditText
    private lateinit var shortDescEditText: TextInputEditText
    private lateinit var labelEditText: TextInputEditText

    private lateinit var descriptionTextEditText: TextInputEditText
    private lateinit var descriptionWikiEditText: TextInputEditText

    private lateinit var originCompanyEditText: TextInputEditText
    private lateinit var originCountryEditText: TextInputEditText
    private lateinit var originContinentEditText: MaterialAutoCompleteTextView
    private lateinit var originReleaseEditText: TextInputEditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_upsert)

        mode = UpsertMode.INSERT
        museumID = "R3wYiI9r3XVsUbhA2wAN"

        constraintLayout = findViewById<ConstraintLayout>(R.id.constraintLayout)
        val toolbar = findViewById<MaterialToolbar>(R.id.toolbar)

        toolbar.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        titleEditText = findViewById<TextInputEditText>(R.id.titleEditText)
        makeRequired(titleEditText)
        shortDescEditText = findViewById<TextInputEditText>(R.id.shortDescEditText)
        makeRequired(shortDescEditText)
        labelEditText = findViewById<TextInputEditText>(R.id.labelEditText)
        makeRequired(labelEditText)

        descriptionTextEditText = findViewById<TextInputEditText>(R.id.descriptionTextEditText)
        descriptionWikiEditText = findViewById<TextInputEditText>(R.id.descriptionWikiEditText)

        originCompanyEditText = findViewById<TextInputEditText>(R.id.originCompanyEditText)
        originCountryEditText = findViewById<TextInputEditText>(R.id.originCountryEditText)
        originContinentEditText =
            findViewById<MaterialAutoCompleteTextView>(R.id.originContinentEditText)
        originReleaseEditText = findViewById<TextInputEditText>(R.id.originReleaseEditText)

        val submitButton = findViewById<Button>(R.id.submitButton)

        submitButton.setOnClickListener {
            val artefactBasicData = getBasicData()
            val artefactDetailedData = getDetailedData()

            addArtefactDBEntry(artefactBasicData, artefactDetailedData)
        }
    }

    private fun getBasicData(): HashMap<String, Any?> {
        return hashMapOf(
            "museum_id" to museumID,
            "title" to titleEditText.text.toString(),
            "short_desc" to shortDescEditText.text.toString(),
            "label" to labelEditText.text.toString(),
            "created_at" to Timestamp.now(),
            "tags" to listOf("Test", "Test 2", "Test 3")
        )
    }

    private fun getDetailedData(): HashMap<String, Any?> {
        val data = HashMap<String, Any?>()

        if (descriptionTextEditText.text.toString() != "" || descriptionWikiEditText.text.toString() != "") {
            data["description"] = hashMapOf(
                "text" to descriptionTextEditText.text.toString(),
                "wikipedia_url" to descriptionWikiEditText.text.toString()
            )
        }

        if (originCompanyEditText.text.toString() != "" ||
            originCountryEditText.text.toString() != "" ||
            originContinentEditText.text.toString() != "" ||
            originReleaseEditText.text.toString() != ""
        ) {
            data["history"] = hashMapOf(
                "company_name" to originCompanyEditText.text.toString(),
                "origin_country" to originCountryEditText.text.toString(),
                "continent" to getContinentCode(originContinentEditText.text.toString()),
                "release_date" to originReleaseEditText.text.toString()
            )
        }

        return data

    }

    private fun addArtefactDBEntry(
        basicData: HashMap<String, Any?>,
        detailedData: HashMap<String, Any?>
    ) {
        DB.createBasicArtefact(basicData).addOnSuccessListener { documentRef ->
            detailedData["artefact_id"] = documentRef.id.toString()

            DB.createArtefactDetails(detailedData).addOnSuccessListener {
                Misc.displaySnackBar(
                    constraintLayout,
                    "The artefact has been added successfully"
                )
            }.addOnFailureListener { exception ->
                Misc.displaySnackBar(constraintLayout, exception.message.toString())
            }
            Log.println(Log.INFO, "db status", "added")
        }.addOnFailureListener { exception ->
            Misc.displaySnackBar(constraintLayout, exception.message.toString())
        }
    }

    private fun makeRequired(editText: TextInputEditText) {
        editText.setOnFocusChangeListener { view, isFocused ->

            if (!isFocused) {
                if (editText.text.toString() == "") {
                    editText.error = "This field is required"
                }
            }
        }
    }

    private fun getContinentCode(continentName: String): String {
        return when (continentName) {
            "Asia" -> "AS"
            "Europe" -> "EU"
            "North America" -> "NA"
            "South America" -> "SA"
            "Africa" -> "AF"
            "Oceania" -> "OC"
            else -> "UN"
        }
    }
}