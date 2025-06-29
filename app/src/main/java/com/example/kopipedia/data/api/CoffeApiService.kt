package com.example.kopipedia.data.api

import com.example.kopipedia.data.model.CoffeeDto
import retrofit2.http.GET

interface CoffeeApiService {
    @GET("coffee/hot")
    suspend fun getHotCoffees(): List<CoffeeDto>
}