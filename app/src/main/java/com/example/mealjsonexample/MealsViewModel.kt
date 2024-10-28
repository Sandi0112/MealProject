package com.example.mealjsonexample

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.StateFlow

class MealsViewModel: ViewModel() {
    private var mealsRepository = MealsRepository()

    private var categories = MutableStateFlow(mealsRepository.categoriesState)

    val categoriesState = categories.asStateFlow()

    private var meals = MutableStateFlow(mealsRepository.mealsState)

    val mealsState = meals.asStateFlow()
    private var _chosenCategoryName = MutableStateFlow("")

    val chosenCategoryName = _chosenCategoryName.asStateFlow()

    private val _areaQuery = MutableStateFlow("")
    val areaQuery: StateFlow<String> = _areaQuery
    fun setAreaQuery(query: String) {
        _areaQuery.value = query
    }

    private val _mealsByAreaState = MutableStateFlow(MealsState())
    val mealsByAreaState: StateFlow<MealsState> = _mealsByAreaState

    private val _randomMeal = MutableStateFlow(MealsState())
    val randomMeal: StateFlow<MealsState> = _randomMeal
    fun fetchRandomMeal() {
        viewModelScope.launch {
            _randomMeal.value = MealsState(isLoading = true)
            try {
                val response = mealsRepository.getRandomMeal()
                _randomMeal.value = MealsState(result = response.meals)
            } catch (e: Exception) {
                _randomMeal.value = MealsState(isError = true, error = e.message)
            }
        }
    }

    fun getAllDishesByArea(areaName: String) {
        viewModelScope.launch {
            _mealsByAreaState.value = MealsState(isLoading = true)
            try {
                val response = mealService.getAllDishesByArea(areaName)
                _mealsByAreaState.value = MealsState(result = response.meals)
            } catch (e: Exception) {
                _mealsByAreaState.value = MealsState(isError = true, error = e.message)
            }
        }
    }

    init {
        getAllCategories()
    }

    fun setChosenCategory(name: String){
        _chosenCategoryName.value = name
    }

    fun getAllDishesByCategoryName(categoryName: String){
        viewModelScope.launch {
            try {
                meals.value = meals.value.copy(
                    isLoading = true
                )
                val response = mealsRepository.getAllMealsByCategoryName(categoryName)
                meals.value = meals.value.copy(
                    isLoading = false,
                    isError = false,
                    result = response.meals
                )

            }
            catch (e: Exception){
                meals.value = meals.value.copy(
                    isError = true,
                    isLoading = false,
                    error = e.message
                )
            }
        }
    }

    private fun getAllCategories(){
        viewModelScope.launch {
            try {
                categories.value = categories.value.copy(
                    isLoading = true
                )
                val response = mealsRepository.getAllCategories()

                categories.value = categories.value.copy(
                    isLoading = false,
                    isError = false,
                    result = response.categories
                )

            }
            catch (e: Exception){
                categories.value = categories.value.copy(
                    isError = true,
                    isLoading = false,
                    error = e.message
                )
            }
        }
    }

}