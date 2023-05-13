package com.example.cs306coursework1.activities.museum_upsert

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.example.cs306coursework1.R
import com.example.cs306coursework1.activities.museum_select.MuseumSelectActivity
import com.example.cs306coursework1.data.UpsertMode
import com.example.cs306coursework1.data.UserSingleton
import com.example.cs306coursework1.helpers.DB
import com.example.cs306coursework1.helpers.Misc
import com.example.cs306coursework1.helpers.Storage
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.textfield.TextInputEditText

class MuseumUpsertActivity : AppCompatActivity() {
    private var mode: UpsertMode? = null
    private var museumID: String? = null

    private lateinit var constraintLayout: ConstraintLayout

    private lateinit var toolbar: MaterialToolbar

    private lateinit var museumNameEditText: TextInputEditText
    private lateinit var museumDescriptionEditText: TextInputEditText

    private lateinit var submitButton: Button


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_museum_upsert)

        mode = UpsertMode.INSERT

        mode = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getSerializableExtra("mode", UpsertMode::class.java)
        } else {
            intent.getSerializableExtra("mode") as UpsertMode?
        }

        if (mode == UpsertMode.UPDATE || mode == UpsertMode.VIEW) {
            museumID = intent.getStringExtra("museum_id")
        }

        setupViews()

        if (mode == UpsertMode.UPDATE || mode == UpsertMode.VIEW) {
            readMuseumAndUpdateViews()
        }
    }

    private fun setupViews() {
        constraintLayout = findViewById(R.id.constraintLayout)

        toolbar = findViewById(R.id.toolbar)

        toolbar.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        museumNameEditText = findViewById(R.id.titleEditText)
        museumDescriptionEditText = findViewById(R.id.descriptionEditText)

        if (mode == UpsertMode.UPDATE) {
            toolbar.title = "Edit museum"
        } else if (mode == UpsertMode.VIEW) {
            toolbar.title = "View museum"
            submitButton.visibility = View.GONE
        }

        submitButton = findViewById(R.id.submitButton)

        submitButton.setOnClickListener {
            val infoData = getInformationData()
            updateMuseumDBEntry(infoData)
        }
    }

    private fun getInformationData(): HashMap<String, Any?> {

        return hashMapOf(
            "name" to museumNameEditText.text.toString(),
            "description" to museumDescriptionEditText.text.toString(),
            "owner_id" to UserSingleton.getID(),
            "owner_name" to UserSingleton.getUsername()
        )
    }

    private fun readMuseumAndUpdateViews() {
        DB.getMuseumByID(museumID.toString()).addOnSuccessListener { doc ->
            museumNameEditText.setText(doc["name"].toString(), TextView.BufferType.EDITABLE)
            museumDescriptionEditText.setText(
                doc["description"].toString(),
                TextView.BufferType.EDITABLE
            )
        }.addOnFailureListener { exception ->
            Misc.displaySnackBar(constraintLayout, exception.message.toString())
        }
    }

    private fun updateMuseumDBEntry(
        infoData: HashMap<String, Any?>
    ) {

        if (mode == UpsertMode.INSERT) {
            DB.createMuseum(infoData).addOnSuccessListener {
                val intent = Intent(this, MuseumSelectActivity::class.java)
                startActivity(intent)
            }.addOnFailureListener { exception ->
                Misc.displaySnackBar(constraintLayout, exception.message.toString())
            }
        } else if (mode == UpsertMode.UPDATE) {
            DB.updateMuseum(museumID.toString(), infoData).addOnSuccessListener {
                val intent = Intent(this, MuseumSelectActivity::class.java)
                startActivity(intent)
            }.addOnFailureListener { exception ->
                Misc.displaySnackBar(constraintLayout, exception.message.toString())
            }
        }
    }
}