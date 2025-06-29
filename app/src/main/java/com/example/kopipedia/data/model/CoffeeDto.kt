package com.example.kopipedia.data.model

import com.google.gson.annotations.SerializedName

data class CoffeeDto(
    @SerializedName("id")
    val id: Int? = null,

    @SerializedName("title")
    val title: String? = null,

    @SerializedName("description")
    val description: String? = null,

    @SerializedName("ingredients")
    val ingredients: List<String>? = null,

    @SerializedName("image")
    val imageUrl: String? = null
)