package com.example.cs306coursework1.data


object UserSingleton {
    private var id: String? = null
    private var username: String? = null
    private var email: String? = null
    private var accountType: AccountType? = null
    private var selectedMuseumID: String? = null
    private var selectedMuseumName: String? = null

    fun setObject(id: String, username: String, email: String, accountType: AccountType) {
        this.id = id
        this.username = username
        this.email = email
        this.accountType = accountType
    }

    fun getID(): String {
        return id.toString()
    }

    fun setID(id: String) {
        this.id = id
    }

    fun getUsername(): String {
        return username.toString()
    }

    fun setUsername(username: String) {
        this.username = username
    }

    fun getEmail(): String {
        return email.toString()
    }

    fun setEmail(email: String) {
        this.email = email
    }

    fun getAccountType(): AccountType? {
        return accountType
    }

    fun setAccountType(accountType: AccountType) {
        this.accountType = accountType
    }

    fun getSelectedMuseumID(): String? {
        return selectedMuseumID
    }

    fun setSelectedMuseumID(museumID: String?) {
        this.selectedMuseumID = museumID
    }
    
    fun getSelectedMuseumName(): String? {
        return selectedMuseumName
    }

    fun setSelectedMuseumName(museumName: String?) {
        this.selectedMuseumName = museumName
    }
}
