package com.example.test3.mealplanner

import androidx.compose.ui.text.font.FontWeight
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.CardDefaults

import android.widget.Toast
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
                    RecipeCard(recipe = recipe) {
                        onRecipeSelected(recipe)
                    }
                }
            }
        }
    }
}
@Composable
fun RecipeCard(recipe: Recipe, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp)
            .clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(recipe.title, fontWeight = FontWeight.Bold, fontSize = 18.sp)
            Text(recipe.description, modifier = Modifier.padding(top = 4.dp))
        }
    }
}