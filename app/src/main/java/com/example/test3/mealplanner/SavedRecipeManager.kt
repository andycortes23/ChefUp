package com.example.test3.mealplanner

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringSetPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.google.gson.Gson
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking

private val Context.dataStore by preferencesDataStore(name = "saved_recipes")

object SavedRecipeManager {
    private val gson = Gson()
    private val key = stringSetPreferencesKey("recipes")

    fun saveRecipe(context: Context, recipe: Recipe): Boolean {
        return try {
            runBlocking {
                val current = context.dataStore.data.first()[key] ?: emptySet()
                val newSet = current.toMutableSet()
                val json = gson.toJson(recipe)
                println("Saving recipe JSON: $json") // 🔍 LOG HERE
                newSet.add(json)
                context.dataStore.edit { prefs ->
                    prefs[key] = newSet
                }
            }
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    fun getSavedRecipes(context: Context): List<Recipe> {
        return runBlocking {
            val stored = context.dataStore.data.first()[key] ?: emptySet()
            stored.mapNotNull { json ->
                try {
                    println("Parsing JSON: $json") // Optional debug log
                    gson.fromJson(json, Recipe::class.java)
                } catch (e: Exception) {
                    e.printStackTrace()
                    null
                }
            }
        }
    }

    fun removeRecipe(context: Context, recipe: Recipe): Boolean {
        return try {
            runBlocking {
                val current = context.dataStore.data.first()[key] ?: emptySet()
                val jsonToRemove = gson.toJson(recipe)
                val newSet = current.toMutableSet().apply { remove(jsonToRemove) }
                context.dataStore.edit { prefs ->
                    prefs[key] = newSet
                }
            }
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }
}