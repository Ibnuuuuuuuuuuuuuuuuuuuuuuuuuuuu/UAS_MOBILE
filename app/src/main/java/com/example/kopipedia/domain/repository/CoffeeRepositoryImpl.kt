package com.example.kopipedia.data.repository

import com.example.kopipedia.data.api.CoffeeApiService
import com.example.kopipedia.data.local.CoffeeDao
import com.example.kopipedia.data.model.CoffeeDto
import com.example.kopipedia.data.model.CoffeeEntity
import com.example.kopipedia.domain.model.Coffee
import com.example.kopipedia.domain.repository.CoffeeRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map

// Buat sebuah Mapper untuk mengubah DTO/Entity menjadi model domain
private fun CoffeeEntity.toDomain(): Coffee {
    return Coffee(id, title, description, imageUrl, isFavorite)
}

private fun CoffeeDto.toEntity(): CoffeeEntity {
    return CoffeeEntity(id ?: 0, title ?: "", description ?: "", imageUrl ?: "", false)
}

class CoffeeRepositoryImpl(
    private val apiService: CoffeeApiService,
    private val coffeeDao: CoffeeDao
) : CoffeeRepository {

    override fun getCoffees(): Flow<List<Coffee>> = flow {
        // 1. Emit data dari database terlebih dahulu
        val localCoffees = coffeeDao.getAllCoffees().first().map { it.toDomain() }
        emit(localCoffees)

        // 2. Coba ambil data dari network
        try {
            val networkCoffeesDto = apiService.getHotCoffees()
            val localCoffeeEntities = coffeeDao.getAllCoffees().first()

            // 3. Konversi DTO ke Entity, sambil mempertahankan status favorit yang ada
            val coffeeEntities = networkCoffeesDto.map { dto ->
                val existing = localCoffeeEntities.find { it.id == dto.id }
                dto.toEntity().copy(isFavorite = existing?.isFavorite ?: false)
            }

            // 4. Simpan ke database
            coffeeDao.insertAllCoffees(coffeeEntities)

            // 5. Emit data terbaru dari database
            val updatedLocalCoffees = coffeeDao.getAllCoffees().first().map { it.toDomain() }
            emit(updatedLocalCoffees)

        } catch (e: Exception) {
            // Jika network gagal, flow akan berhenti di sini,
            // dan UI sudah menampilkan data dari cache (langkah 1).
            e.printStackTrace()
        }
    }

    override fun getFavoriteCoffees(): Flow<List<Coffee>> {
        return coffeeDao.getFavoriteCoffees().map { list ->
            list.map { it.toDomain() }
        }
    }

    override suspend fun setFavorite(coffee: Coffee, isFavorite: Boolean) {
        val entity = coffee.run {
            CoffeeEntity(id, title, description, imageUrl, isFavorite)
        }
        coffeeDao.updateCoffee(entity)
    }
}