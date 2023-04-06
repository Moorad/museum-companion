package com.example.cs306coursework1.information

class LinksModal {
    private var linkTitle: String? = null
    private var linkURL: String? = null

    fun getLinkTitle(): String {
        return linkTitle.toString()
    }

    fun setLinkTitle(title: String) {
        this.linkTitle = title
    }

    fun getLinkURL(): String {
        return linkURL.toString()
    }

    fun setLinkURL(URL: String) {
        this.linkURL = URL
    }
}