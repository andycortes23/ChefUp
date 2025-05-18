package com.example.test3.mealplanner

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
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
    onOfflineClick: () -> Unit
) {
    var query by remember { mutableStateOf("") }

    // val mockRecipes = listOf(
    //     Recipe(
    //         "Classic Margherita Pizza",
    //         "A simple and classic pizza with tomato sauce, mozzarella, and fresh basil.",
    //         listOf("Pizza dough", "Tomato sauce", "Mozzarella cheese", "Fresh basil", "Olive oil"),
    //         "Preheat oven to 475°F. Roll out the dough, spread tomato sauce, add mozzarella, and bake for 10-12 minutes. Top with basil and drizzle with olive oil.",
    //         matchLevel = 0
    //     ),
    //     Recipe(
    //         "Pepperoni Pizza",
    //         "A popular pizza topped with spicy pepperoni and gooey cheese.",
    //         listOf("Pizza dough", "Tomato sauce", "Mozzarella cheese", "Pepperoni"),
    //         "Preheat oven to 475°F. Spread sauce, cheese, and pepperoni over dough. Bake for 12 minutes.",
    //         matchLevel = 1
    //     ),
    //     Recipe(
    //         "Veggie Supreme Pizza",
    //         "A delicious pizza loaded with fresh vegetables.",
    //         listOf("Pizza dough", "Tomato sauce", "Mozzarella", "Bell peppers", "Olives", "Onions"),
    //         "Preheat oven to 450°F. Spread tomato sauce and top with vegetables and cheese. Bake for 15 minutes.",
    //         matchLevel = 1
    //     ),
    //     Recipe(
    //         "White Garlic Pizza",
    //         "A pizza with creamy white sauce and garlic, topped with herbs.",
    //         listOf("Pizza dough", "Garlic", "White sauce", "Mozzarella", "Parsley"),
    //         "Preheat oven to 475°F. Spread white sauce on dough, add garlic and cheese. Bake for 10 minutes.",
    //         matchLevel = 2
    //     )
    // )

    val mockRecipes = listOf(
        Recipe(
            "Classic Margherita Pizza",
            "A simple and classic pizza with tomato sauce, mozzarella, and fresh basil.",
            listOf("Pizza dough", "Tomato sauce", "Mozzarella cheese", "Fresh basil", "Olive oil"),
            "Preheat oven to 475°F. Roll out the dough, spread tomato sauce, add mozzarella, and bake for 10-12 minutes. Top with basil and drizzle with olive oil.",
            matchLevel = 0
        ),
        Recipe(
            "Pepperoni Pizza",
            "A popular pizza topped with spicy pepperoni and gooey cheese.",
            listOf("Pizza dough", "Tomato sauce", "Mozzarella cheese", "Pepperoni"),
            "Preheat oven to 475°F. Spread sauce, cheese, and pepperoni over dough. Bake for 12 minutes.",
            matchLevel = 1
        ),
        Recipe(
            "Veggie Supreme Pizza",
            "A delicious pizza loaded with fresh vegetables.",
            listOf("Pizza dough", "Tomato sauce", "Mozzarella", "Bell peppers", "Olives", "Onions"),
            "Preheat oven to 450°F. Spread tomato sauce and top with vegetables and cheese. Bake for 15 minutes.",
            matchLevel = 1
        ),
        Recipe(
            "White Garlic Pizza",
            "A pizza with creamy white sauce and garlic, topped with herbs.",
            listOf("Pizza dough", "Garlic", "White sauce", "Mozzarella", "Parsley"),
            "Preheat oven to 475°F. Spread white sauce on dough, add garlic and cheese. Bake for 10 minutes.",
            matchLevel = 2
        )
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        OutlinedTextField(
            value = query,
            onValueChange = { query = it },
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
            onClick = { /* TODO: handle AI search */ },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(50)
        ) {
            Text("Search")
        }

        Spacer(modifier = Modifier.height(16.dp))

    val recipesToShow = if (query.isNotBlank()) mockRecipes.sortedBy { it.matchLevel } else emptyList()

        recipesToShow.forEach { recipe ->
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