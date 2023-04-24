package com.example.cs306coursework1.activities.upsert

class LinksModel {
    private var linkText: String? = null
    private var linkURL: String? = null

    fun getLinkText(): String {
        return linkText.toString()
    }

    fun setLinkText(text: String) {
        this.linkText = text
    }

    fun getLinkURL(): String {
        return linkURL.toString()
    }

    fun setLinkURL(url: String) {
        this.linkURL = url
    }
}