package com.example.cs306coursework1.activities.upsert

import android.content.Intent
import android.graphics.drawable.Drawable
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.content.res.AppCompatResources
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.graphics.drawable.DrawableCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.cs306coursework1.R
import com.example.cs306coursework1.activities.information.InformationActivity
import com.example.cs306coursework1.data.AccountType
import com.example.cs306coursework1.data.Constants
import com.example.cs306coursework1.data.UpsertMode
import com.example.cs306coursework1.data.UserSingleton
import com.example.cs306coursework1.helpers.DB
import com.example.cs306coursework1.helpers.Misc
import com.example.cs306coursework1.helpers.Storage
import com.google.android.gms.tasks.Task
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.textfield.MaterialAutoCompleteTextView
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.Timestamp

class UpsertActivity : AppCompatActivity() {
    private var mode: UpsertMode? = null
    private var artefactID: String? = null

    private val requiredFields = ArrayList<EditText>()

    private lateinit var constraintLayout: ConstraintLayout
    private lateinit var toolbar: MaterialToolbar

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

    private lateinit var linkAddButton: Button

    private lateinit var tagAddButton: Button

    private lateinit var galleryAdapter: GalleryAdapter
    private lateinit var linkAdapter: LinksAdapter
    private lateinit var tagAdapter: TagAdapter

    private lateinit var submitButton: Button

    private var uploadedImages = hashMapOf<String, Any?>(
        "hero" to null,
        "gallery" to ArrayList<String>()
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_upsert)

        mode = UpsertMode.INSERT

        mode = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getSerializableExtra("mode", UpsertMode::class.java)
        } else {
            intent.getSerializableExtra("mode") as UpsertMode?
        }

        if (mode == UpsertMode.UPDATE || mode == UpsertMode.VIEW) {
            artefactID = intent.getStringExtra("artefact_id")
        }

        setupViews()
        setupTagRecyclerView()
        setupHeroSelect()
        setupGalleryRecyclerView()
        setupLinksRecyclerView()

        if (mode == UpsertMode.UPDATE || mode == UpsertMode.VIEW) {
            readArtefactAndUpdateViews()
        }
    }

    private fun setupViews() {
        constraintLayout = findViewById(R.id.constraintLayout)
        toolbar = findViewById(R.id.toolbar)

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

        linkAddButton = findViewById(R.id.linkSelectButton)

        tagAddButton = findViewById(R.id.tagAddButton)

        submitButton = findViewById(R.id.submitButton)

        submitButton.setOnClickListener {
            if (isRequiredFieldsFilled()) {
                val artefactBasicData = getBasicData()
                val artefactDetailedData = getDetailedData()

                upsertArtefactDBEntry(artefactBasicData, artefactDetailedData)

            } else {
                showErrorOnRequiredFields()
                Misc.displaySnackBar(constraintLayout, "Required fields cannot be left blank")
            }
        }

        if (mode == UpsertMode.UPDATE) {
            toolbar.title = "Edit artefact"
        } else if (mode == UpsertMode.VIEW) {
            toolbar.title = "View artefact"
            submitButton.visibility = View.GONE
        }

    }

    private fun getBasicData(): HashMap<String, Any?> {
        val submitAsApproved = UserSingleton.getAccountType() == AccountType.CURATOR

        return hashMapOf(
            "museum_id" to UserSingleton.getSelectedMuseumID().toString(),
            "isApproved" to submitAsApproved,
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

        linkAddButton.setOnClickListener {
            linkAdapter.addItem(LinksModel())
        }
    }

    private fun setupGalleryRecyclerView() {
        val galleryRecyclerView = findViewById<RecyclerView>(R.id.galleryRecyclerView)

        val galleryLayoutManager = LinearLayoutManager(this)
        galleryRecyclerView.layoutManager = galleryLayoutManager

        val models = imageURLsToModels(uploadedImages["gallery"] as ArrayList<String>)
        galleryAdapter = GalleryAdapter(models)
        galleryRecyclerView.adapter = galleryAdapter

        galleryAdapter.setOnRemoveListener { pos ->
            (uploadedImages["gallery"] as ArrayList<String>).removeAt(pos)
        }

        val multipleSelectPhotoMedia =
            registerForActivityResult(ActivityResultContracts.PickMultipleVisualMedia()) { uris ->
                uris.forEachIndexed { i, uri ->
                    if (uri != null) {
                        Storage.uploadImage(uri, "gallery_images", constraintLayout)
                            .addOnCompleteListener { task ->
                                if (task.isSuccessful) {
                                    val downloadURL = task.result.toString()

                                    val model = GalleryModel()
                                    model.setImageURL(downloadURL)
                                    model.setImageName("Image ${galleryAdapter.itemCount}")
                                    galleryAdapter.addItem(model)

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

    private fun readArtefactAndUpdateViews() {
        DB.getArtefactBasicByID(artefactID.toString()).addOnSuccessListener { doc ->
            titleEditText.setText(doc["title"].toString(), TextView.BufferType.EDITABLE)
            shortDescEditText.setText(doc["short_desc"].toString(), TextView.BufferType.EDITABLE)
            labelEditText.setText(doc["label"].toString(), TextView.BufferType.EDITABLE)

            if (Misc.existsIn(doc, "tags")) {
                (doc["tags"] as ArrayList<String>).forEach { tag ->
                    tagAdapter.addItem(tag)
                }
            }

        }.addOnFailureListener { exception ->
            Misc.displaySnackBar(constraintLayout, exception.message.toString())
        }

        DB.getArtefactDetailsByID(artefactID.toString()).addOnSuccessListener { documents ->
            val doc = documents.first()

            // Hero image
            if (Misc.existsIn(doc, "hero_image")) {
                Misc.setImageFromURL(doc["hero_image"].toString(), heroPreview)
                uploadedImages["hero"] = doc["hero_image"].toString()

                heroPreview.visibility = View.VISIBLE
                heroRemoveButton.visibility = View.VISIBLE
            }

            // Description

            if (Misc.existsIn(doc, "description")) {
                val description = doc["description"] as HashMap<String, HashMap<String, String>>
                descriptionTextEditText.setText(
                    description["text"].toString(),
                    TextView.BufferType.EDITABLE
                )

                descriptionWikiEditText
                    .setText(
                        description["wikipedia_url"].toString(),
                        TextView.BufferType.EDITABLE
                    )
            }

            // Origin
            if (Misc.existsIn(doc, "history")) {
                val history = doc["history"] as HashMap<String, HashMap<String, String>>
                originCompanyEditText.setText(
                    history["company_name"].toString(),
                    TextView.BufferType.EDITABLE
                )
                originCountryEditText.setText(
                    history["origin_country"].toString(),
                    TextView.BufferType.EDITABLE
                )
                originContinentEditText.setText(
                    getContinentName(history["continent"].toString()),
                    false
                )
                originReleaseEditText.setText(
                    history["release_date"].toString(),
                    TextView.BufferType.EDITABLE
                )
            }

            // Dimensions
            if (Misc.existsIn(doc, "dimensions")) {
                val dimensions = doc["dimensions"] as HashMap<String, HashMap<String, String>>
                dimensionWidthEditText.setText(
                    dimensions["width"].toString(),
                    TextView.BufferType.EDITABLE
                )
                dimensionHeightEditText.setText(
                    dimensions["height"].toString(),
                    TextView.BufferType.EDITABLE
                )
                dimensionDepthEditText.setText(
                    dimensions["depth"].toString(),
                    TextView.BufferType.EDITABLE
                )
                dimensionMassEditText.setText(
                    dimensions["mass"].toString(),
                    TextView.BufferType.EDITABLE
                )
                dimensionConditionEditText.setText(
                    dimensions["condition"].toString(),
                    false
                )
            }

            // Gallery
            if (Misc.existsIn(doc, "gallery")) {
                (doc["gallery"] as ArrayList<String>).forEachIndexed { i, imageURL ->
                    val model = GalleryModel()
                    model.setImageURL(imageURL)
                    model.setImageName("Image $i")
                    galleryAdapter.addItem(model)

                    (uploadedImages["gallery"] as ArrayList<String>).add(imageURL)
                }
            }

            // Related links
            if (Misc.existsIn(doc, "related_links")) {
                (doc["related_links"] as ArrayList<HashMap<String, String>>).forEach { relatedLink ->
                    var model = LinksModel()
                    model.setLinkText(relatedLink["title"].toString())
                    model.setLinkURL(relatedLink["url"].toString())
                    linkAdapter.addItem(model)
                }
            }
        }.addOnFailureListener { exception ->
            Misc.displaySnackBar(constraintLayout, exception.message.toString())
        }

        // Disable all edit texts and buttons if in view mode
        if (mode == UpsertMode.VIEW) {
            val titleInputLayout = (titleEditText.parent as ViewGroup).parent as TextInputLayout
            titleInputLayout.isEnabled = false

            val shortDescInputLayout =
                (shortDescEditText.parent as ViewGroup).parent as TextInputLayout
            shortDescInputLayout.isEnabled = false

            val labelInputLayout = (labelEditText.parent as ViewGroup).parent as TextInputLayout
            labelInputLayout.isEnabled = false

            tagAdapter.disableAllViews()
            tagAddButton.isEnabled = false

            heroSelectButton.isEnabled = false
            heroRemoveButton.isEnabled = false

            val descriptionTextInputLayout =
                (descriptionTextEditText.parent as ViewGroup).parent as TextInputLayout
            descriptionTextInputLayout.isEnabled = false

            val descriptionWikiInputLayout =
                (descriptionWikiEditText.parent as ViewGroup).parent as TextInputLayout
            descriptionWikiInputLayout.isEnabled = false

            val originCompanyInputLayout =
                (originCompanyEditText.parent as ViewGroup).parent as TextInputLayout
            originCompanyInputLayout.isEnabled = false

            val originCountryInputLayout =
                (originCountryEditText.parent as ViewGroup).parent as TextInputLayout
            originCountryInputLayout.isEnabled = false

            val originContinentInputLayout =
                (originContinentEditText.parent as ViewGroup).parent as TextInputLayout
            originContinentInputLayout.isEnabled = false

            val originReleaseInputLayout =
                (originReleaseEditText.parent as ViewGroup).parent as TextInputLayout
            originReleaseInputLayout.isEnabled = false


            val dimensionWidthInputLayout =
                (dimensionWidthEditText.parent as ViewGroup).parent as TextInputLayout
            dimensionWidthInputLayout.isEnabled = false

            val dimensionHeightInputLayout =
                (dimensionHeightEditText.parent as ViewGroup).parent as TextInputLayout
            dimensionHeightInputLayout.isEnabled = false

            val dimensionDepthInputLayout =
                (dimensionDepthEditText.parent as ViewGroup).parent as TextInputLayout
            dimensionDepthInputLayout.isEnabled = false

            val dimensionMassInputLayout =
                (dimensionMassEditText.parent as ViewGroup).parent as TextInputLayout
            dimensionMassInputLayout.isEnabled = false

            val dimensionConditionInputLayout =
                (dimensionConditionEditText.parent as ViewGroup).parent as TextInputLayout
            dimensionConditionInputLayout.isEnabled = false


            galleryAdapter.disableAllViews()
            gallerySelectButton.isEnabled = false

            linkAdapter.disableAllViews()
            linkAddButton.isEnabled = false
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

    private fun upsertArtefactDBEntry(
        basicData: HashMap<String, Any?>,
        detailedData: HashMap<String, Any?>
    ) {
        if (mode == UpsertMode.INSERT) {
            DB.createBasicArtefact(basicData).addOnSuccessListener { docBasicRef ->
                detailedData["artefact_id"] = docBasicRef.id
                DB.createArtefactDetails(detailedData)
                    .addOnSuccessListener { docDetailedRef ->
                        var levelGain = Constants.SUBMISSION_LEVEL_GAIN
                        var status = ""
                        if (UserSingleton.getAccountType() == AccountType.CURATOR) {
                            status = "approved"
                            levelGain = Constants.CURATOR_SUBMISSION_LEVEL_GAIN
                        } else {
                            status = "pending"
                        }
                        val submissionData: HashMap<String, Any?> = hashMapOf(
                            "artefact_id" to docBasicRef.id,
                            "created_by" to UserSingleton.getID(),
                            "last_updated" to Timestamp.now(),
                            "museum_id" to UserSingleton.getSelectedMuseumID().toString(),
                            "status" to status
                        )

                        DB.createSubmission(submissionData).addOnSuccessListener {
                            DB.getUserByUID(UserSingleton.getID()).addOnSuccessListener { docRefs ->
                                val user = docRefs.first()

                                val data: HashMap<String, Any?> = hashMapOf(
                                    "level" to (user["level"] as Double) + levelGain
                                )

                                DB.updateUser(UserSingleton.getID(), data).addOnSuccessListener {
                                    val intent = Intent(this, InformationActivity::class.java)
                                    intent.putExtra("artefact_id", docBasicRef.id)
                                    startActivity(intent)
                                }.addOnFailureListener { exception ->
                                    Misc.displaySnackBar(
                                        constraintLayout,
                                        exception.message.toString()
                                    )
                                }
                            }.addOnFailureListener { exception ->
                                Misc.displaySnackBar(constraintLayout, exception.message.toString())
                            }
                        }.addOnFailureListener { exception ->
                            Misc.displaySnackBar(constraintLayout, exception.message.toString())
                        }
                    }.addOnFailureListener { exception ->
                        Misc.displaySnackBar(constraintLayout, exception.message.toString())
                    }

            }.addOnFailureListener { exception ->
                Misc.displaySnackBar(constraintLayout, exception.message.toString())
            }
        } else if (mode == UpsertMode.UPDATE) {
            DB.updateBasicArtefact(artefactID.toString(), basicData)
                .addOnSuccessListener {
                    DB.updateArtefactDetails(artefactID.toString(), detailedData)
                        .addOnSuccessListener {

                            var status =
                                if (UserSingleton.getAccountType() == AccountType.CURATOR) {
                                    "approved"
                                } else {
                                    "pending"
                                }
                            val submissionData: HashMap<String, Any?> = hashMapOf(
                                "artefact_id" to artefactID.toString(),
                                "created_by" to UserSingleton.getID(),
                                "last_updated" to Timestamp.now(),
                                "museum_id" to UserSingleton.getSelectedMuseumID().toString(),
                                "status" to status
                            )

                            DB.updateSubmissions(submissionData).addOnSuccessListener {
                                val intent = Intent(this, InformationActivity::class.java)
                                intent.putExtra("artefact_id", artefactID.toString())
                                startActivity(intent)
                            }.addOnFailureListener { exception ->
                                Misc.displaySnackBar(constraintLayout, exception.message.toString())
                            }
                        }.addOnFailureListener { exception ->
                            Misc.displaySnackBar(constraintLayout, exception.message.toString())
                        }
                }.addOnFailureListener { exception ->
                    Misc.displaySnackBar(constraintLayout, exception.message.toString())
                }
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

    private fun getContinentName(continentCode: String): String {
        return when (continentCode) {
            "AS" -> "Asia"
            "EU" -> "Europe"
            "NA" -> "North America"
            "SA" -> "South America"
            "AF" -> "Africa"
            "OC" -> "Oceania"
            else -> "Other/Unknown"
        }
    }

    private fun imageURLsToModels(urls: ArrayList<String>): ArrayList<GalleryModel> {
        val models = ArrayList<GalleryModel>()
        urls.forEachIndexed { i, url ->
            val model = GalleryModel()

            model.setImageURL(url)
            model.setImageName("Image $i")

            models.add(model)
        }

        return models
    }

}