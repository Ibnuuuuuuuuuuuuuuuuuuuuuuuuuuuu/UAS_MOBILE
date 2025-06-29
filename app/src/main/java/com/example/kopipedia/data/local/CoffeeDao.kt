package com.example.kopipedia.data.local

import androidx.room.*
import com.example.kopipedia.data.model.CoffeeEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface CoffeeDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllCoffees(coffees: List<CoffeeEntity>)

    @Query("SELECT * FROM coffees")
    fun getAllCoffees(): Flow<List<CoffeeEntity>>

    @Query("SELECT * FROM coffees WHERE isFavorite = 1")
    fun getFavoriteCoffees(): Flow<List<CoffeeEntity>>

    @Update
    suspend fun updateCoffee(coffee: CoffeeEntity)
}