package com.example.cs306coursework1.activities.upsert

class LinksModel {
    private var linkText: String = ""
    private var linkURL: String = ""

    fun getLinkText(): String {
        return linkText
    }

    fun setLinkText(text: String) {
        this.linkText = text
    }

    fun getLinkURL(): String {
        return linkURL
    }

    fun setLinkURL(url: String) {
        this.linkURL = url
    }
}