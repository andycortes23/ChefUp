package com.example.test3.mealplanner

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

import android.content.Context
import android.util.Log
import okhttp3.RequestBody.Companion.toRequestBody
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.*
import org.json.JSONArray
import org.json.JSONObject
import java.io.IOException
import okhttp3.MediaType.Companion.toMediaTypeOrNull

object RecipeFetcher {

    suspend fun fetchRecipes(context: Context, query: String): List<Recipe> = withContext(Dispatchers.IO) {
        val config = com.google.firebase.remoteconfig.FirebaseRemoteConfig.getInstance()
        val apiKey = config.getString("openai_api_key")
        if (apiKey.isBlank()) {
            Log.e("RecipeFetcher", "API key not found in Remote Config.")
            return@withContext emptyList()
        }

        // Fetch user inventory from Firestone
        val uid = FirebaseAuth.getInstance().currentUser?.uid
        val firestore = FirebaseFirestore.getInstance()
        val inventorySnapshot = uid?.let {
            firestore.collection("users").document(it).collection("ingredients").get().await()
        }

        val inventoryItems = inventorySnapshot?.documents?.mapNotNull {
            it.getString("name")
        } ?: emptyList()

        val inventorySet = inventoryItems.map { it.lowercase() }.toSet()

        val inventoryString = inventoryItems.joinToString(", ")

        val requestBody = JSONObject().apply {
            put("model", "gpt-3.5-turbo")
            put("messages", JSONArray().put(JSONObject().apply {
                put("role", "user")
                put(
                    "content",
                    "Suggest 4 meal recipes based on \"$query\". I may have the following ingredients at home: $inventoryString. Prioritize using these ingredients if possible, but it’s okay if the recipe requires other items. " +
                            "Return JSON with fields: title, description, ingredients (list), and instructions. The instructions must be clearly numbered with each step starting with '1.', '2.', etc. on a new line."
                )
            }))
        }

        val client = OkHttpClient()
        val request = Request.Builder()
            .url("https://api.openai.com/v1/chat/completions")
            .post(requestBody.toString().toRequestBody("application/json".toMediaTypeOrNull()))
            .addHeader("Authorization", "Bearer $apiKey")
            .addHeader("Content-Type", "application/json")
            .build()

        try {
            client.newCall(request).execute().use { response ->
                if (!response.isSuccessful) {
                    Log.e("RecipeFetcher", "Failed response: ${response.code}")
                    return@withContext emptyList()
                }

                val body = response.body?.string() ?: return@withContext emptyList()
                val json = JSONObject(body)
                val content = json.getJSONArray("choices")
                    .getJSONObject(0)
                    .getJSONObject("message")
                    .getString("content")

                val contentObj = JSONObject(content.trim())
                val recipesArray = contentObj.getJSONArray("recipes")
                val recipes = mutableListOf<Recipe>()
                for (i in 0 until recipesArray.length()) {
                    val obj = recipesArray.getJSONObject(i)
                    val ingredientsList = obj.getJSONArray("ingredients").let { arr ->
                        List(arr.length()) { index -> arr.getString(index) }
                    }

                    val matchCount = ingredientsList.count { recipeIng ->
                        inventorySet.any { inv -> recipeIng.lowercase().contains(inv) || inv.contains(recipeIng.lowercase()) }
                    }
                    val totalCount = ingredientsList.size
                    val missingIngredients = ingredientsList.filter { it.lowercase() !in inventorySet }
                    val matchRatio = matchCount.toDouble() / totalCount

                    val matchNote = when {
                        matchCount == totalCount -> "You have all the ingredients! Let’s Cook!"
                        matchRatio >= 0.5 -> "You have the majority of items for this recipe. Let’s Cook!"
                        else -> "You do not have much or any ingredients for this recipe."
                    }

                    recipes.add(
                        Recipe(
                            title = obj.getString("title"),
                            description = buildString {
                                append(obj.getString("description"))
                                append("\n\n$matchNote")
                                if (missingIngredients.isNotEmpty()) {
                                    append("\n\nIngredients not found in inventory:\n")
                                    append(missingIngredients.joinToString(", "))
                                }
                            },
                            ingredients = ingredientsList,
                            instructions = obj.getString("instructions")
                                .replace(Regex("""^\["|"\]$"""), "") // Remove leading [" and trailing "]
                                .replace(Regex("""","""), "\n\n")   // Replace step delimiters with newlines
                                .replace(Regex("""(?<=\d\.)\s*"""), " ") // Ensure a single space after step numbers
                                .replace(Regex("""\s*(\d\.)"""), "\n\n$1") // Add spacing before numbered steps
                                .trim(),
                            matchLevel = totalCount - matchCount
                        )
                    )
                }
                return@withContext recipes
            }
        } catch (e: IOException) {
            Log.e("RecipeFetcher", "Network error: ${e.message}")
            return@withContext emptyList()
        } catch (e: Exception) {
            Log.e("RecipeFetcher", "Unexpected error: ${e.message}")
            return@withContext emptyList()
        }
    }
}
