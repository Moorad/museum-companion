package com.example.cs306coursework1.data

enum class AccountType(val type: String) {
    GUEST("guest"),
    STANDARD("standard"),
    CURATOR("curator");


    // Additional helper methods for this enum
    companion object {

        // Get the enum that corresponds with the string
        fun getTypeFromString(type: String): AccountType {

            return values().find {
                it.type == type.lowercase()
            }
                ?: throw java.lang.IllegalArgumentException("Expected a valid account type but received $type")
        }

        // Compares two enums and returns whether they are equal or not
        fun isEqual(value_1: AccountType, value_2: AccountType): Boolean {
            if (value_1 == value_2) {
                return true
            }

            return false
        }

        // Overloading for an AccountType to string comparison
        fun isEqual(value_1: AccountType, value_2: String): Boolean {
            if (value_1.type == value_2.lowercase()) {
                return true
            }

            return false
        }
    }
}