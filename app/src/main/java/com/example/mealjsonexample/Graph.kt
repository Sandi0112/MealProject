package com.example.mealjsonexample

object Graph {
    val mainScreen: Screen = Screen("MainScreen")
    val secondScreen: Screen = Screen("SecondScreen")
    val mealDetailScreen: Screen = Screen("mealDetail/{mealId}")
}

data class Screen(
    val route: String,
)