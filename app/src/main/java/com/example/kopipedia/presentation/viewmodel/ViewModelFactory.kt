package com.example.kopipedia.presentation.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.kopipedia.data.api.RetrofitInstance
import com.example.kopipedia.data.local.AppDatabase
import com.example.kopipedia.data.repository.CoffeeRepositoryImpl
import com.example.kopipedia.domain.repository.CoffeeRepository
import com.example.kopipedia.domain.usecase.*

class ViewModelFactory(private val repository: CoffeeRepository) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(CoffeeViewModel::class.java) -> {
                CoffeeViewModel(
                    GetHotCoffeesUseCase(repository),
                    GetFavoriteCoffeesUseCase(repository),
                    SetFavoriteUseCase(repository)
                ) as T
            }
            // Tambahkan ViewModel lain di sini jika ada
            else -> throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
        }
    }

    companion object {
        @Volatile
        private var instance: ViewModelFactory? = null

        fun getInstance(context: Context): ViewModelFactory =
            instance ?: synchronized(this) {
                instance ?: ViewModelFactory(
                    CoffeeRepositoryImpl(
                        RetrofitInstance.api,
                        AppDatabase.getDatabase(context).coffeeDao()
                    )
                ).also { instance = it }
            }
    }
}
