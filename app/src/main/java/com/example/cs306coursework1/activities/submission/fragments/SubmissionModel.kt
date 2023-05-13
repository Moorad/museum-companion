package com.example.cs306coursework1.activities.submission.fragments

class SubmissionModel {
    var modelName: String? = null;
    private var modelUserID: String? = null
    private var modelImage: Int = 0
    private var modelLevel: Double = 0.0
    private var artefactID: String? = null
    private var lastUpdated: String? = null

    fun getUserID(): String {
        return modelUserID.toString()
    }

    fun setUserID(id: String) {
        this.modelUserID = id
    }

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

    fun getLevel(): Double {
        return modelLevel
    }

    fun setLevel(level: Double) {
        this.modelLevel = level
    }

    fun getArtefactID(): String {
        return artefactID.toString()
    }

    fun setArtefactID(id: String) {
        this.artefactID = id
    }
}