package com.example.kopipedia.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.kopipedia.domain.model.Coffee
import com.example.kopipedia.domain.usecase.GetFavoriteCoffeesUseCase
import com.example.kopipedia.domain.usecase.GetHotCoffeesUseCase
import com.example.kopipedia.domain.usecase.SetFavoriteUseCase
import com.example.kopipedia.presentation.ui.util.ResultState
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class CoffeeViewModel(
    getHotCoffeesUseCase: GetHotCoffeesUseCase,
    val getFavoriteCoffeesUseCase: GetFavoriteCoffeesUseCase,
    private val setFavoriteUseCase: SetFavoriteUseCase
) : ViewModel() {

    // Mengambil daftar kopi dan mengubahnya menjadi StateFlow
    val coffees: StateFlow<ResultState<List<Coffee>>> =
        getHotCoffeesUseCase()
            .map<List<Coffee>, ResultState<List<Coffee>>> { ResultState.Success(it) }
            .catch { emit(ResultState.Error(it.message.toString())) }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000L),
                initialValue = ResultState.Loading
            )

    fun setFavorite(coffee: Coffee, isFavorite: Boolean) {
        viewModelScope.launch {
            setFavoriteUseCase(coffee, isFavorite)
        }
    }
}
