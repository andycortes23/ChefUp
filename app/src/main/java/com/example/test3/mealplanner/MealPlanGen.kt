package com.example.test3.mealplanner

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SmallTopAppBar
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.test3.mealplanner.Recipe
import com.example.test3.mealplanner.MealPlanViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MealPlanGenScreen(
    onRecipeSelected: (Recipe) -> Unit,
    onOfflineClick: () -> Unit,
    viewModel: MealPlanViewModel = viewModel()
) {
    val context = LocalContext.current
    val query by viewModel.query.collectAsState()
    val recipesToShow by viewModel.recipes.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    // same pill‐shape + colors as StoreFinder
    val pillShape  = RoundedCornerShape(24.dp)
    val pillColors = ButtonDefaults.buttonColors(
        containerColor = Color.Black,
        contentColor   = Color.White
    )

    // exact header color from your Add‐Ingredient screen
    val headerBg = Color(0xFFD4FF99)

    Scaffold(
        topBar = {
            SmallTopAppBar(
                title = {
                    Text(
                        "Meal Plan Generator",
                        style = MaterialTheme.typography.titleLarge
                            .copy(fontWeight = FontWeight.Bold)
                    )
                },
                colors = TopAppBarDefaults.smallTopAppBarColors(
                    containerColor = headerBg
                )
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
        ) {
            item {
                OutlinedTextField(
                    value         = query,
                    onValueChange = { viewModel.setQuery(it) },
                    label         = { Text("Ask for a recipe") },
                    modifier      = Modifier.fillMaxWidth()
                )

                Spacer(Modifier.height(12.dp))

                Button(
                    onClick  = onOfflineClick,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    shape  = pillShape,
                    colors = pillColors
                ) {
                    Text("Offline Recipes")
                }

                Spacer(Modifier.height(8.dp))

                Button(
                    onClick  = { viewModel.fetchRecipes(context) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    shape  = pillShape,
                    colors = pillColors
                ) {
                    Text("Search")
                }

                Spacer(Modifier.height(16.dp))
            }

            if (isLoading) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }
            }

            items(recipesToShow) { recipe ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 6.dp)
                        .clickable { onRecipeSelected(recipe) },
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant
                    )
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            recipe.title,
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp
                        )
                        Text(
                            recipe.description,
                            modifier = Modifier.padding(vertical = 4.dp)
                        )
                    }
                }
            }
        }
    }
}
