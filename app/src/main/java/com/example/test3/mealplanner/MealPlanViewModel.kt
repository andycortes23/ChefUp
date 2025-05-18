package com.example.test3.mealplanner

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class MealPlanViewModel : ViewModel() {

    private val _query = MutableStateFlow("")
    val query: StateFlow<String> get() = _query

    private val _recipes = MutableStateFlow<List<Recipe>>(emptyList())
    val recipes: StateFlow<List<Recipe>> get() = _recipes

    fun setQuery(newQuery: String) {
        _query.value = newQuery
    }

    fun fetchRecipes(context: Context) {
        val currentQuery = _query.value
        if (currentQuery.isNotBlank()) {
            viewModelScope.launch(Dispatchers.IO) {
                try {
                    val result = RecipeFetcher.fetchRecipes(context, currentQuery)
                    _recipes.value = result
                } catch (e: Exception) {
                    _recipes.value = emptyList()
                }
            }
        }
    }
}