package com.example.cs306coursework1.upsert

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import androidx.constraintlayout.widget.ConstraintLayout
import com.example.cs306coursework1.R
import com.example.cs306coursework1.helpers.DB
import com.example.cs306coursework1.helpers.Misc
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.Timestamp

class UpsertActivity : AppCompatActivity() {
    private lateinit var museumID: String

    private lateinit var titleEditText: TextInputEditText
    private lateinit var shortDescEditText: TextInputEditText
    private lateinit var labelEditText: TextInputEditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_upsert)

        museumID = "R3wYiI9r3XVsUbhA2wAN"

        val constraintLayout = findViewById<ConstraintLayout>(R.id.constraintLayout)

        titleEditText = findViewById<TextInputEditText>(R.id.titleEditText)
        makeRequired(titleEditText)
        shortDescEditText = findViewById<TextInputEditText>(R.id.shortDescEditText)
        makeRequired(shortDescEditText)
        labelEditText = findViewById<TextInputEditText>(R.id.labelEditText)
        makeRequired(labelEditText)

        val submitButton = findViewById<Button>(R.id.submitButton)

        submitButton.setOnClickListener {
            val artefactBasicDetails = getData()

            DB.createBasicArtefact(artefactBasicDetails).addOnSuccessListener {
                Log.println(Log.INFO, "db status", "added")
            }.addOnFailureListener { exception ->
                Misc.displaySnackBar(constraintLayout, exception.message.toString())
            }
        }
    }

    private fun getData(): HashMap<String, Any?> {
        return hashMapOf(
            "museum_id" to museumID,
            "title" to titleEditText.text.toString(),
            "short_desc" to shortDescEditText.text.toString(),
            "label" to labelEditText.text.toString(),
            "created_at" to Timestamp.now(),
            "tags" to listOf("Test", "Test 2", "Test 3")
        )
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
}