package com.example.test3.mealplanner

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun MealPlanGenScreen(
    onRecipeSelected: (Recipe) -> Unit,
    onOfflineClick: () -> Unit,
    viewModel: MealPlanViewModel = androidx.lifecycle.viewmodel.compose.viewModel()
) {
    val query by viewModel.query.collectAsState()
    val recipesToShow by viewModel.recipes.collectAsState()
    val context = LocalContext.current

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        item {
            OutlinedTextField(
                value = query,
                onValueChange = { viewModel.setQuery(it) },
                label = { Text("Ask for a recipe") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(12.dp))

            Button(
                onClick = onOfflineClick,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(50)
            ) {
                Text("Offline Recipes")
            }

            Spacer(modifier = Modifier.height(8.dp))

            Button(
                onClick = {
                    viewModel.fetchRecipes(context)
                },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(50)
            ) {
                Text("Search")
            }

            Spacer(modifier = Modifier.height(16.dp))
        }

        items(recipesToShow) { recipe ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 6.dp)
                    .clickable { onRecipeSelected(recipe) },
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(recipe.title, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                    Text(recipe.description, modifier = Modifier.padding(vertical = 4.dp))
                }
            }
        }
    }
}