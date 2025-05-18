package com.example.test3.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.example.test3.Screen

@Composable
fun BottomNavBar(
    currentScreen: Screen,
    onTabSelected: (Screen) -> Unit,
    onAddIngredient: () -> Unit
) {
    NavigationBar(
        modifier = Modifier
            .fillMaxWidth()
            .height(90.dp)
            .zIndex(1f),
        containerColor = Color.White,
        contentColor = Color.Black
    ) {
        NavigationBarItem(
            icon = {
                Icon(
                    imageVector = Icons.Default.Home,
                    contentDescription = "Home"
                )
            },
            selected = currentScreen is Screen.Home,
            onClick = { onTabSelected(Screen.Home) },
            label = { Text("Home") }
        )

        NavigationBarItem(
            icon = {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Add Ingredient"
                )
            },
            selected = currentScreen is Screen.AddIngredients,
            onClick = onAddIngredient,
            label = { Text("Add") }
        )

        NavigationBarItem(
            icon = {
                Icon(
                    imageVector = Icons.Default.Settings,
                    contentDescription = "Settings"
                )
            },
            selected = currentScreen is Screen.Settings,
            onClick = { onTabSelected(Screen.Settings) },
            label = { Text("Settings") }
        )
    }
}
