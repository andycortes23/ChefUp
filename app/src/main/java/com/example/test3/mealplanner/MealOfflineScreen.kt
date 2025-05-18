package com.example.test3.mealplanner

import android.widget.Toast
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MealOfflineScreen(onRecipeSelected: (Recipe) -> Unit, onBack: () -> Unit) {
    val context = LocalContext.current
    val savedRecipes = remember(context) { mutableStateOf(emptyList<Recipe>()) }

    LaunchedEffect(Unit) {
        savedRecipes.value = SavedRecipeManager.getSavedRecipes(context)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Offline Recipes") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        if (savedRecipes.value.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                Text("No offline recipes saved.")
            }
        } else {
            Column(modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
            ) {
                savedRecipes.value.forEach { recipe ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 6.dp)
                            .clickable { onRecipeSelected(recipe) },
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(
                            modifier = Modifier
                                .weight(1f)
                                .padding(8.dp)
                        ) {
                            Text(recipe.title, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                            Text(recipe.description, modifier = Modifier.padding(top = 4.dp))
                        }
                        IconButton(onClick = {
                            val removed = SavedRecipeManager.removeRecipe(context, recipe)
                            if (removed) {
                                savedRecipes.value = SavedRecipeManager.getSavedRecipes(context)
                                Toast.makeText(context, "Recipe removed", Toast.LENGTH_SHORT).show()
                            } else {
                                Toast.makeText(context, "Failed to remove recipe", Toast.LENGTH_SHORT).show()
                            }
                        }) {
                            Icon(Icons.Default.Delete, contentDescription = "Delete")
                        }
                    }
                }
            }
        }
    }
}
