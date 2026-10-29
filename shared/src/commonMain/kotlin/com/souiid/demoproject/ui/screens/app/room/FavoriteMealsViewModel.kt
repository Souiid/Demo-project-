package com.souiid.demoproject.ui.screens.app.room

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.souiid.demoproject.ktor.domain.model.Meal
import com.souiid.demoproject.room.domain.usecase.ObserveFavoriteMealsUseCase
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn

class FavoriteMealsViewModel(
    observeFavoriteMealsUseCase: ObserveFavoriteMealsUseCase
) : ViewModel() {

    val favoriteMeals = observeFavoriteMealsUseCase()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = emptyList<Meal>()
        )
}
