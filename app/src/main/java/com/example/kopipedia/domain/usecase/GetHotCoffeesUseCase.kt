package com.example.kopipedia.domain.usecase

import com.example.kopipedia.domain.repository.CoffeeRepository

class GetHotCoffeesUseCase(private val repository: CoffeeRepository) {
    operator fun invoke() = repository.getCoffees()
}