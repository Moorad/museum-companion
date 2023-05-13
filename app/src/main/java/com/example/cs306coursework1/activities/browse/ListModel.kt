package com.example.cs306coursework1.activities.browse

import com.google.firebase.Timestamp
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter

class ListModel {
    private var artefactID: String? = null
    private var artefactCreatedAt: String? = null
    private var artefactLabel: String? = null
    private var artefactName: String? = null
    private var artefactDescription: String? = null
    private var artefactTags: ArrayList<String>? = null

    fun getID(): String {
        return artefactID.toString()
    }

    fun setID(id: String) {
        this.artefactID = id
    }

    fun getLabel(): String {
        return artefactLabel.toString()
    }

    fun setLabel(label: String) {
        this.artefactLabel = label
    }

    fun getCreatedAt(): String {
        return artefactCreatedAt.toString()
    }

    fun setCreatedAt(createdAt: Timestamp) {
        val dtf = DateTimeFormatter.ofPattern("dd MMM yyyy");
        val ldt = createdAt.toDate().toInstant().atOffset(ZoneOffset.UTC).toLocalDateTime()
        this.artefactCreatedAt = ldt.format(dtf)
    }

    fun getName(): String {
        return artefactName.toString()
    }

    fun setName(name: String) {
        this.artefactName = name
    }

    fun getDescription(): String {
        return artefactDescription.toString()
    }

    fun setDescription(description: String) {
        this.artefactDescription = description
    }

    fun getTags(): ArrayList<String> {
        if (artefactTags == null) {
            return arrayListOf<String>()
        }

        return artefactTags as ArrayList<String>
    }

    fun setTags(list: ArrayList<String>) {
        this.artefactTags = list
    }

}