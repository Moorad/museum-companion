package com.example.cs306coursework1

class SubmissionModal {
    var modelName: String? = null;
    private var modelImage: Int = 0
    private var modelLevel: Float = 0.0F

    fun getName(): String {
        return modelName.toString()
    }

    fun setName(name: String) {
        this.modelName = name
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
}