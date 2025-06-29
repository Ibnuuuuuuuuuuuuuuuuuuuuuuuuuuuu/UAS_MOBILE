package com.example.kopipedia.domain.usecase

import com.example.kopipedia.domain.repository.CoffeeRepository

class GetFavoriteCoffeesUseCase(private val repository: CoffeeRepository) {
    operator fun invoke() = repository.getFavoriteCoffees()
}