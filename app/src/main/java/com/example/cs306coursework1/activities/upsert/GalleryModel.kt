package com.example.cs306coursework1.activities.upsert

class GalleryModel {
    private var imageURL: String? = null
    private var imageName: String? = null

    fun getImageURL(): String {
        return imageURL.toString()
    }

    fun setImageURL(url: String) {
        this.imageURL = url
    }

    fun getImageName(): String {
        return imageName.toString()
    }

    fun setImageName(name: String) {
        this.imageName = name
    }

}