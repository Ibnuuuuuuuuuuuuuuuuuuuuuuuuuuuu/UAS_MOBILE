package com.example.kopipedia.domain.repository

import com.example.kopipedia.domain.model.Coffee
import kotlinx.coroutines.flow.Flow

interface CoffeeRepository {
    // Fungsi untuk mendapatkan data kopi dengan mekanisme offline-first
    fun getCoffees(): Flow<List<Coffee>>

    // Fungsi untuk mendapatkan data favorit saja
    fun getFavoriteCoffees(): Flow<List<Coffee>>

    // Fungsi untuk mengubah status favorit
    suspend fun setFavorite(coffee: Coffee, isFavorite: Boolean)
}