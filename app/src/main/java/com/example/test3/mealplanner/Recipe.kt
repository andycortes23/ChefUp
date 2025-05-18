package com.example.test3.mealplanner

data class Recipe(
    val title: String,
    val description: String,
    val ingredients: List<String>,
    val instructions: String,
    val matchLevel: Int
)