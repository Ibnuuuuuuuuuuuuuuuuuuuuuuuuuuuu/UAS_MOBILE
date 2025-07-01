package com.example.kopipedia.data.repository

import com.example.kopipedia.data.api.CoffeeApiService
import com.example.kopipedia.data.local.CoffeeDao
import com.example.kopipedia.data.model.CoffeeDto
import com.example.kopipedia.data.model.CoffeeEntity
import com.example.kopipedia.domain.model.Coffee
import com.example.kopipedia.domain.repository.CoffeeRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import android.util.Log // Import Log

private fun getIndonesianTitle(originalTitle: String?): String? {
    return when (originalTitle) {
        "Svart Te" -> "Teh Hitam"
        "Islatte" -> "Iced Latte"
        "Islatte Mocha" -> "Iced Mocha Latte"
        "Frapino Caramel" -> "Frappuccino Caramel"
        "Frapino Mocka" -> "Frappuccino Mocha"
        "Apelsinjuice" -> "Jus Jeruk"
        "Frozen Lemonade" -> "Iced Lemonade"
        "Lemonad" -> "Lemonade"
        else -> originalTitle
    }
}

private fun getIndonesianDescription(title: String?, originalDescription: String?): String? {
    return when (title) {
        "Black Coffee" -> "Kopi hitam pekat tanpa tambahan gula atau susu, menonjolkan rasa asli biji kopi."
        "Latte" -> "Espresso yang dipadukan dengan susu steam dalam jumlah banyak dan lapisan busa tipis di atasnya."
        "Cappuccino" -> "Perpaduan seimbang antara espresso, susu steam, dan busa susu yang tebal dan lembut."
        "Americano" -> "Satu shot espresso yang diencerkan dengan air panas, menghasilkan kopi yang ringan namun tetap kaya rasa."
        "Espresso" -> "Ekstrak kopi murni yang kental dan pekat, disajikan dalam cangkir kecil."
        "Doppio" -> "Dua shot espresso, memberikan tendangan kafein dan rasa yang lebih intens."
        "Caramel Latte" -> "Latte klasik dengan tambahan sirup karamel manis dan lezat."
        "Macchiato" -> "Espresso yang diberi sedikit busa susu, memberikan sentuhan creamy pada kopi pekat."
        "Mocha" -> "Perpaduan harmonis antara espresso, cokelat, dan susu steam, menciptakan rasa kopi cokelat yang kaya."
        "Hot Chocolate" -> "Minuman cokelat hangat dan kaya, sempurna untuk menghangatkan diri."
        "Chai Latte" -> "Teh hitam pedas yang dicampur dengan susu steam dan rempah-rempah aromatik."
        "Matcha Latte" -> "Minuman teh hijau matcha yang dicampur dengan susu steam, kaya akan rasa umami dan sedikit manis."
        "Seasonal Brew" -> "Sajian kopi spesial musiman, rasanya bervariasi tergantung ketersediaan biji kopi terbaik di musim tersebut."
        "Svart te" -> "Teh hitam tradisional, disajikan hangat dengan rasa yang kuat dan menyegarkan."
        "Islatte" -> "Latte dingin yang menyegarkan, dibuat dengan espresso dan susu dingin di atas es."
        "Islatte mocha" -> "Latte dingin dengan perpaduan espresso, cokelat, susu, dan es."
        "Frapino Caramel" -> "Minuman kopi blend dingin dengan rasa karamel yang kaya, cocok untuk pecinta manis."
        "Frapino Mocha" -> "Minuman kopi blend dingin dengan sentuhan cokelat, sangat menyegarkan dan manis."
        "Apelsinjuice" -> "Jus jeruk segar yang kaya vitamin C."
        "Frozen Lemonade" -> "Minuman lemon beku yang sangat menyegarkan, manis, dan asam."
        "Lemonad" -> "Minuman lemon segar, perpaduan manis dan asam yang pas."
        "Latte Noisette" -> "Latte dengan sentuhan rasa hazelnut yang lembut."
        "Latte Amaretto" -> "Latte dengan aroma dan rasa amaretto yang khas."
        "Latte Violette" -> "Latte dengan infus rasa bunga violet yang unik."
        "Latte Choco Noisette" -> "Latte yang memadukan rasa cokelat dan hazelnut."
        "Latte Amande" -> "Latte dengan rasa almond yang creamy dan manis."
        else -> originalDescription
    }
}

private fun CoffeeEntity.toDomain(): Coffee {
    return Coffee(id, title, description, imageUrl, isFavorite)
}

private fun CoffeeDto.toEntity(): CoffeeEntity {
    val translatedTitle = getIndonesianTitle(this.title)
    val translatedDescription = getIndonesianDescription(this.title, this.description)

    return CoffeeEntity(
        id = this.id ?: 0,
        title = translatedTitle ?: this.title ?: "",
        description = translatedDescription ?: this.description ?: "",
        imageUrl = this.imageUrl ?: "",
        isFavorite = false
    )
}

class CoffeeRepositoryImpl(
    private val apiService: CoffeeApiService,
    private val coffeeDao: CoffeeDao
) : CoffeeRepository {

    override fun getCoffees(): Flow<List<Coffee>> {
        return coffeeDao.getAllCoffees()
            .map { entities ->
                val domainList = entities.map { it.toDomain() }
                Log.d("RepoDebug", "Emitting from DB flow (Size: ${domainList.size}) BEFORE DISTINCT:")
                domainList.forEachIndexed { index, coffee ->
                    Log.d("RepoDebug", "  $index: ${coffee.title} (ID: ${coffee.id})")
                }
                // --- Tambahkan distinctBy di sini untuk deduplikasi berdasarkan judul ---
                val distinctList = domainList.distinctBy { it.title }
                Log.d("RepoDebug", "Emitting from DB flow (Size: ${distinctList.size}) AFTER DISTINCT:")
                distinctList.forEachIndexed { index, coffee ->
                    Log.d("RepoDebug", "  $index: ${coffee.title} (ID: ${coffee.id})")
                }
                distinctList
            }
            .onStart {
                try {
                    Log.d("RepoDebug", "onStart: Fetching from network.")
                    val networkCoffeesDto = apiService.getHotCoffees()
                    val localCoffeeEntities = coffeeDao.getAllCoffees().first()

                    val coffeeEntitiesToInsert = networkCoffeesDto.map { dto ->
                        val existing = localCoffeeEntities.find { it.id == dto.id }
                        dto.toEntity().copy(isFavorite = existing?.isFavorite ?: false)
                    }
                    Log.d("RepoDebug", "onStart: Inserting ${coffeeEntitiesToInsert.size} entities into DB.")
                    coffeeDao.insertAllCoffees(coffeeEntitiesToInsert)
                } catch (e: Exception) {
                    Log.e("RepoDebug", "Network fetch failed: ${e.message}")
                    e.printStackTrace()
                }
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