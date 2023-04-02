package com.example.cs306coursework1.museum_select

class MuseumsModal {
    private var museumID: String? = null;
    private var museumName: String? = null;
    private var museumDescription: String? = null
    private var museumCuratorName: String? = null


    fun getID(): String {
        return museumID.toString()
    }

    fun setID(uid: String) {
        this.museumID = uid
    }

    fun getName(): String {
        return museumName.toString()
    }

    fun setName(name: String) {
        this.museumName = name
    }

    fun getDescription(): String {
        return museumDescription.toString()
    }

    fun setDescription(description: String) {
        this.museumDescription = description
    }

    fun getCuratorName(): String {
        return museumCuratorName.toString()
    }

    fun setCuratorName(curatorName: String) {
        this.museumCuratorName = curatorName
    }
}