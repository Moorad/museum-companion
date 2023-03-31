package com.example.cs306coursework1

import android.os.Parcelable
import androidx.compose.ui.text.toLowerCase
import kotlinx.parcelize.Parcelize


@Parcelize
data class UserDetails(
    var id: String?,
    var username: String,
    var email: String?,
    var accountType: AccountType
) : Parcelable

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
    }
}
