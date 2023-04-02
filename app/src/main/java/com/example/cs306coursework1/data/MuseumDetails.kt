package com.example.cs306coursework1.data

import android.os.Parcelable
import kotlinx.parcelize.Parcelize


@Parcelize
data class MuseumDetails(
    var id: String,
    var name: String,
    var description: String,
    var ownerName: String,
) : Parcelable
