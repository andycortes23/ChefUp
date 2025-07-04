package com.example.test3.mealplanner

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Download
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import android.widget.Toast
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.graphics.Color

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MealDetailScreen(recipe: Recipe, onBack: () -> Unit) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(recipe.title) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    val context = LocalContext.current
                    IconButton(onClick = {
                        val success = SavedRecipeManager.saveRecipe(context, recipe)
                        if (success) {
                            Toast.makeText(context, "Recipe saved for offline use!", Toast.LENGTH_SHORT).show()
                        } else {
                            Toast.makeText(context, "Failed to save recipe.", Toast.LENGTH_SHORT).show()
                        }
                    }) {
                        Icon(Icons.Default.Download, contentDescription = "Download")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFFB0E57C))
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .padding(16.dp)
        ) {
            Divider(modifier = Modifier.padding(vertical = 8.dp))
            Text(recipe.description, fontSize = 18.sp)
            Spacer(modifier = Modifier.height(16.dp))

            Text("Ingredients:", style = MaterialTheme.typography.titleLarge.copy(color = Color(0xFF388E3C)))
            recipe.ingredients.forEach { ingredient ->
                Text("- $ingredient")
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text("Instructions:", style = MaterialTheme.typography.titleLarge.copy(color = Color(0xFF388E3C)))
            Text(recipe.instructions)
        }
    }
}