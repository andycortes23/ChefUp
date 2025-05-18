package com.example.test3.inventory

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.test3.R
import com.example.test3.Screen
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.google.firebase.auth.FirebaseAuth

data class StoredIngredient(
    val id: String = "",
    val name: String = "",
    val quantity: String = "",
    val storage: String = "",
    val expirationDate: String = "",
    val category: String = ""
)

@Composable
fun InventoryScreen(
    currentScreen: Screen,
    onTabSelected: (Screen) -> Unit,
    onAddIngredient: () -> Unit,
    onStorageClick: (String) -> Unit,
    onCategoryClick: (String) -> Unit
) {
    val systemUiController = rememberSystemUiController()
    var searchQuery by remember { mutableStateOf("") }
    val searchResults = remember { mutableStateListOf<StoredIngredient>() }
    val userId = FirebaseAuth.getInstance().currentUser?.uid

    SideEffect {
        systemUiController.setStatusBarColor(Color(0xFFD4FF99), darkIcons = true)
    }

    Scaffold(
        contentWindowInsets = WindowInsets(0, 0, 0, 0)
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Green top section
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFFD4FF99))
            ) {
                Column {
                    Spacer(modifier = Modifier.height(32.dp))
                    TopSection(
                        searchQuery = searchQuery,
                        onSearchChange = { searchQuery = it }
                    )
                    CategorySection(onCategorySelected = onCategoryClick)
                }
            }

            // White bottom section
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White)
                    .padding(top = 16.dp)
            ) {
                StorageSection(onStorageSelected = onStorageClick)

                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopSection(
    searchQuery: String,
    onSearchChange: (String) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        OutlinedTextField(
            value = searchQuery,
            onValueChange = onSearchChange,
            placeholder = { Text("Search ingredients...") },
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White, RoundedCornerShape(12.dp)),
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
            singleLine = true,
            keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Done),
            keyboardActions = KeyboardActions(onDone = {}),
            shape = RoundedCornerShape(12.dp),
            colors = TextFieldDefaults.outlinedTextFieldColors(
                containerColor = Color.White
            )
        )
    }
}

@Composable
fun CategorySection(
    onCategorySelected: (String) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 16.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Search by Category:",
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp
            )
            Spacer(modifier = Modifier.width(8.dp))
            Icon(Icons.Default.ArrowForward, contentDescription = null)
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            listOf(
                "Fruits" to R.drawable.fruits,
                "Vegetables" to R.drawable.veggies,
                "Grains" to R.drawable.grains,
                "Proteins" to R.drawable.proteins
            ).forEach { (label, imageRes) ->
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.clickable { onCategorySelected(label) }
                ) {
                    Image(
                        painter = painterResource(id = imageRes),
                        contentDescription = label,
                        modifier = Modifier
                            .size(72.dp)
                            .clip(CircleShape)
                            .background(Color.White)
                    )
                    Text(label)
                }
            }
        }
    }
}

@Composable
fun StorageSection(
    onStorageSelected: (String) -> Unit
) {
    Column(modifier = Modifier.padding(horizontal = 16.dp)) {
        listOf("Fridge", "Freezer", "Pantry", "All Storage").forEach { title ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onStorageSelected(title) }
                    .padding(vertical = 20.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "$title:",
                    fontWeight = FontWeight.Bold,
                    fontSize = 28.sp
                )
                Icon(Icons.Default.ArrowForward, contentDescription = "$title details")
            }
        }

        Spacer(modifier = Modifier.height(20.dp))
    }
}
