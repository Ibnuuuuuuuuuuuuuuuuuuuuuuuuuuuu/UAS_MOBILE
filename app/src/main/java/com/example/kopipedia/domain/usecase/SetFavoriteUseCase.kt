package com.example.kopipedia.domain.usecase

import com.example.kopipedia.domain.model.Coffee
import com.example.kopipedia.domain.repository.CoffeeRepository

class SetFavoriteUseCase(private val repository: CoffeeRepository) {
    suspend operator fun invoke(coffee: Coffee, isFavorite: Boolean) {
        repository.setFavorite(coffee, isFavorite)
    }
}