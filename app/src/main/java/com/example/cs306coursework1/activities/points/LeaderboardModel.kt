package com.example.cs306coursework1.activities.points

class LeaderboardModel {
    private var modelImage: String? = null
    private var modelLevel: Double = 0.0
    private var modelName: String? = null


    fun getName(): String {
        return modelName.toString()
    }

    fun setName(name: String) {
        this.modelName = name
    }

    fun getImage(): String {
        return modelImage.toString()
    }

    fun setImage(modelImage: String) {
        this.modelImage = modelImage
    }

    fun getLevel(): Double {
        return modelLevel
    }

    fun setLevel(level: Double) {
        this.modelLevel = level
    }


}