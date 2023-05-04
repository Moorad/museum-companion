package com.example.cs306coursework1.data

enum class SubmissionType(val type: String) {
    APPROVED("approved"),
    PENDING("pending"),
    DENIED("denied");

    fun getTypeFromString(type: String): AccountType {

        return AccountType.values().find {
            it.type == type.lowercase()
        }
            ?: throw java.lang.IllegalArgumentException("Expected a valid account type but received $type")
    }

}