package com.example.cs306coursework1.data

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
