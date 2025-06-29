package com.example.kopipedia.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "coffees")
data class CoffeeEntity(
    @PrimaryKey
    val id: Int,
    val title: String,
    val description: String,
    val imageUrl: String,
    var isFavorite: Boolean = false
)