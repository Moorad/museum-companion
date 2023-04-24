package com.example.cs306coursework1.activities.upsert

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.cs306coursework1.R
import com.example.cs306coursework1.activities.information.InformationActivity
import com.example.cs306coursework1.data.UpsertMode
import com.example.cs306coursework1.helpers.DB
import com.example.cs306coursework1.helpers.Misc
import com.example.cs306coursework1.helpers.Storage
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.textfield.MaterialAutoCompleteTextView
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.Timestamp

class UpsertActivity : AppCompatActivity() {
    private lateinit var museumID: String
    private lateinit var mode: UpsertMode

    private val requiredFields = ArrayList<EditText>()

    private lateinit var constraintLayout: ConstraintLayout

    private lateinit var titleEditText: TextInputEditText
    private lateinit var shortDescEditText: TextInputEditText
    private lateinit var labelEditText: TextInputEditText

    private lateinit var heroSelectButton: Button
    private lateinit var heroRemoveButton: Button
    private lateinit var heroPreview: ImageView

    private lateinit var descriptionTextEditText: TextInputEditText
    private lateinit var descriptionWikiEditText: TextInputEditText

    private lateinit var originCompanyEditText: TextInputEditText
    private lateinit var originCountryEditText: TextInputEditText
    private lateinit var originContinentEditText: MaterialAutoCompleteTextView
    private lateinit var originReleaseEditText: TextInputEditText

    private lateinit var dimensionWidthEditText: TextInputEditText
    private lateinit var dimensionHeightEditText: TextInputEditText
    private lateinit var dimensionDepthEditText: TextInputEditText
    private lateinit var dimensionMassEditText: TextInputEditText
    private lateinit var dimensionConditionEditText: MaterialAutoCompleteTextView

    private lateinit var gallerySelectButton: Button

    private lateinit var linkSelectButton: Button

    private lateinit var tagAddButton: Button

    private lateinit var linkAdapter: LinksAdapter
    private lateinit var tagAdapter: TagAdapter

    private var uploadedImages = hashMapOf<String, Any?>(
        "hero" to null,
        "gallery" to ArrayList<String>()
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_upsert)

        mode = UpsertMode.INSERT
        museumID = "R3wYiI9r3XVsUbhA2wAN"

        setupViews()

        setupTagRecyclerView()
        setupHeroSelect()
        setupGalleryRecyclerView()
        setupLinksRecyclerView()

        val submitButton = findViewById<Button>(R.id.submitButton)

        submitButton.setOnClickListener {
            if (isRequiredFieldsFilled()) {
                val artefactBasicData = getBasicData()
                val artefactDetailedData = getDetailedData()

                addArtefactDBEntry(artefactBasicData, artefactDetailedData)
            } else {
                showErrorOnRequiredFields()
                Misc.displaySnackBar(constraintLayout, "Required fields cannot be left blank")
            }
        }
    }

    private fun setupViews() {
        constraintLayout = findViewById(R.id.constraintLayout)
        val toolbar = findViewById<MaterialToolbar>(R.id.toolbar)

        toolbar.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        titleEditText = findViewById(R.id.titleEditText)
        makeRequired(titleEditText)
        shortDescEditText = findViewById(R.id.shortDescEditText)
        makeRequired(shortDescEditText)
        labelEditText = findViewById(R.id.labelEditText)
        makeRequired(labelEditText)

        heroPreview = findViewById(R.id.heroPreview)
        heroSelectButton = findViewById(R.id.heroSelectButton)
        heroRemoveButton = findViewById(R.id.heroRemoveButton)

        descriptionTextEditText = findViewById(R.id.descriptionTextEditText)
        descriptionWikiEditText = findViewById(R.id.descriptionWikiEditText)

        originCompanyEditText = findViewById(R.id.originCompanyEditText)
        originCountryEditText = findViewById(R.id.originCountryEditText)
        originContinentEditText = findViewById(R.id.originContinentEditText)
        originReleaseEditText = findViewById(R.id.originReleaseEditText)

        dimensionWidthEditText = findViewById(R.id.dimensionWidthEditText)
        dimensionHeightEditText = findViewById(R.id.dimensionHeightEditText)
        dimensionDepthEditText = findViewById(R.id.dimensionDepthEditText)
        dimensionMassEditText = findViewById(R.id.dimensionMassEditText)
        dimensionConditionEditText = findViewById(R.id.dimensionConditionEditText)

        gallerySelectButton = findViewById(R.id.gallerySelectButton)

        linkSelectButton = findViewById(R.id.linkSelectButton)

        tagAddButton = findViewById(R.id.tagAddButton)

    }

    private fun getBasicData(): HashMap<String, Any?> {
        return hashMapOf(
            "museum_id" to museumID,
            "title" to titleEditText.text.toString(),
            "short_desc" to shortDescEditText.text.toString(),
            "label" to labelEditText.text.toString(),
            "created_at" to Timestamp.now(),
            "tags" to tagAdapter.getTags()
        )
    }

    private fun setupTagRecyclerView() {
        val tagRecyclerView = findViewById<RecyclerView>(R.id.tagRecyclerView)

        val tagLayoutManager = LinearLayoutManager(this)
        tagRecyclerView.layoutManager = tagLayoutManager

        tagAdapter = TagAdapter(ArrayList())
        tagRecyclerView.adapter = tagAdapter

        tagAddButton.setOnClickListener {
            tagAdapter.addItem("")
        }
    }

    private fun setupLinksRecyclerView() {
        val linksRecyclerView = findViewById<RecyclerView>(R.id.linksRecyclerView)

        val linksLayoutManager = LinearLayoutManager(this)
        linksRecyclerView.layoutManager = linksLayoutManager

        linkAdapter = LinksAdapter(ArrayList())
        linksRecyclerView.adapter = linkAdapter

        linkSelectButton.setOnClickListener {
            linkAdapter.addItem(LinksModel())
        }
    }

    private fun setupGalleryRecyclerView() {
        val galleryRecyclerView = findViewById<RecyclerView>(R.id.galleryRecyclerView)

        val galleryLayoutManager = LinearLayoutManager(this)
        galleryRecyclerView.layoutManager = galleryLayoutManager

        val models = imageURLsToModels(uploadedImages["gallery"] as ArrayList<String>)
        val adapter = GalleryAdapter(models)
        galleryRecyclerView.adapter = adapter

        adapter.setOnRemoveListener { pos ->
            Log.println(Log.INFO, "gallery", pos.toString())
            (uploadedImages["gallery"] as ArrayList<String>).removeAt(pos)
            Log.println(Log.INFO, "gallery", uploadedImages["gallery"].toString())
        }

        val multipleSelectPhotoMedia =
            registerForActivityResult(ActivityResultContracts.PickMultipleVisualMedia()) { uris ->
                uris.forEach { uri ->
                    if (uri != null) {
                        Storage.uploadImage(uri, "gallery_images", constraintLayout)
                            .addOnCompleteListener { task ->
                                if (task.isSuccessful) {
                                    val downloadURL = task.result.toString()

                                    val model = GalleryModel()
                                    model.setImageURL(downloadURL)
                                    model.setImageName("ABC")
                                    adapter.addItem(model)

                                    (uploadedImages["gallery"] as ArrayList<String>).add(downloadURL)
                                } else {
                                    Misc.displaySnackBar(
                                        constraintLayout,
                                        task.exception.toString()
                                    )
                                }
                            }
                    }
                }
            }

        gallerySelectButton.setOnClickListener {
            multipleSelectPhotoMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        }
    }

    private fun setupHeroSelect() {
        val singleSelectPhotoMedia =
            registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->

                if (uri != null) {
                    Storage.uploadImage(uri, "hero_images", constraintLayout)
                        .addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                val downloadURL = task.result.toString()

                                Misc.setImageFromURL(downloadURL, heroPreview)

                                uploadedImages["hero"] = downloadURL

                                heroPreview.visibility = View.VISIBLE
                                heroRemoveButton.visibility = View.VISIBLE
                            } else {
                                Misc.displaySnackBar(constraintLayout, task.exception.toString())
                            }
                        }
                }
            }

        heroSelectButton.setOnClickListener {
            Log.println(Log.INFO, "hero", "button clicked")
            singleSelectPhotoMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        }

        heroRemoveButton.setOnClickListener {
            if (uploadedImages["hero"] != null) {
                Storage.deleteImage(uploadedImages["hero"].toString()).addOnSuccessListener {
                    heroPreview.setImageResource(0)
                    heroRemoveButton.visibility = View.GONE

                    uploadedImages["hero"] = null
                }.addOnFailureListener { exception ->
                    Misc.displaySnackBar(constraintLayout, exception.message.toString())
                }
            }
        }
    }

    private fun getDetailedData(): HashMap<String, Any?> {
        val data = HashMap<String, Any?>()

        if (uploadedImages["hero"] != null) {
            data["hero_image"] = uploadedImages["hero"].toString()
        }

        if (descriptionTextEditText.text.toString() != "" || descriptionWikiEditText.text.toString() != "") {
            data["description"] = hashMapOf(
                "text" to descriptionTextEditText.text.toString(),
                "wikipedia_url" to descriptionWikiEditText.text.toString()
            )
        }

        // If any origin input box is filled, push all the data even if empty
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

        if (dimensionWidthEditText.text.toString() != "" ||
            dimensionHeightEditText.text.toString() != "" ||
            dimensionDepthEditText.text.toString() != "" ||
            dimensionMassEditText.text.toString() != "" ||
            dimensionConditionEditText.text.toString() != ""
        ) {
            data["dimensions"] = hashMapOf(
                "width" to dimensionWidthEditText.text.toString(),
                "height" to dimensionHeightEditText.text.toString(),
                "depth" to dimensionDepthEditText.text.toString(),
                "mass" to dimensionMassEditText.text.toString(),
                "condition" to dimensionConditionEditText.text.toString()

            )
        }

        if ((uploadedImages["gallery"] as ArrayList<String>).isNotEmpty()) {
            data["gallery"] = uploadedImages["gallery"] as ArrayList<String>
        }

        if (linkAdapter.getModels().isNotEmpty()) {
            data["related_links"] = ArrayList<HashMap<String, String>>()
            linkAdapter.getModels().forEach { model ->
                if ((model.getLinkText() != "" || model.getLinkText() != "null") && (model.getLinkURL() != "" || model.getLinkURL() != "null")) {
                    (data["related_links"] as ArrayList<HashMap<String, String>>).add(
                        hashMapOf(
                            "title" to model.getLinkText(),
                            "url" to model.getLinkURL()
                        )
                    )
                }
            }
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
                val intent = Intent(this, InformationActivity::class.java)
                intent.putExtra("artefact_name", basicData["title"].toString())
                intent.putExtra("artefact_id", documentRef.id)

                startActivity(intent)

            }.addOnFailureListener { exception ->
                Misc.displaySnackBar(constraintLayout, exception.message.toString())
            }

        }.addOnFailureListener { exception ->
            Misc.displaySnackBar(constraintLayout, exception.message.toString())
        }
    }

    private fun makeRequired(editText: TextInputEditText) {
        requiredFields.add(editText)

        editText.setOnFocusChangeListener { view, isFocused ->
            if (!isFocused) {
                if (editText.text.toString() == "") {
                    editText.error = "This field is required"

                }
            }
        }
    }

    private fun isRequiredFieldsFilled(): Boolean {
        return requiredFields.all {
            it.text.toString() != ""
        }
    }

    private fun showErrorOnRequiredFields() {
        requiredFields.forEach {
            if (it.text.toString() == "") {
                it.error = "This field is required"
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

    private fun imageURLsToModels(urls: ArrayList<String>): ArrayList<GalleryModel> {
        val models = ArrayList<GalleryModel>()
        urls.forEach { url ->
            val model = GalleryModel()

            model.setImageURL(url)
            model.setImageName(url)

            models.add(model)
        }

        return models
    }

}