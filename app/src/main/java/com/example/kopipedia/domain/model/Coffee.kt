package com.example.kopipedia.domain.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Coffee(
    val id: Int,
    val title: String,
    val description: String,
    val imageUrl: String,
    val isFavorite: Boolean
) : Parcelable