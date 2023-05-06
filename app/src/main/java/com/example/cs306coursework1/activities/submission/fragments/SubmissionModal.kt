package com.example.cs306coursework1.activities.submission.fragments

class SubmissionModal {
    var modelName: String? = null;
    private var modelImage: Int = 0
    private var modelLevel: Float = 0.0F
    private var artefactID: String? = null
    private var lastUpdated: String? = null

    fun getName(): String {
        return modelName.toString()
    }

    fun setName(name: String) {
        this.modelName = name
    }

    fun getLastUpdated(): String {
        return lastUpdated.toString()
    }

    fun setLastUpdated(lastUpdated: String) {
        this.lastUpdated = lastUpdated
    }


    fun getImage(): Int {
        return modelImage
    }

    fun setImage(image_drawable: Int) {
        this.modelImage = image_drawable
    }

    fun getLevel(): Float {
        return modelLevel
    }

    fun setLevel(level: Float) {
        this.modelLevel = level
    }

    fun getArtefactID(): String {
        return artefactID.toString()
    }

    fun setArtefactID(id: String) {
        this.artefactID = id
    }
}