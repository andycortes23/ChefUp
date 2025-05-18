package com.example.test3.inventory

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import androidx.compose.material.icons.filled.Delete


@Composable
fun GreenHeaderBar(
    title: String,
    onBack: () -> Unit
) {
    val systemUiController = rememberSystemUiController()
    SideEffect {
        systemUiController.setStatusBarColor(Color(0xFFC2FF87), darkIcons = true)
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFFD4FF99))
            .statusBarsPadding()
            .padding(horizontal = 16.dp, vertical = 16.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = Icons.Default.ArrowBack,
                contentDescription = "Back",
                modifier = Modifier
                    .clickable { onBack() }
                    .size(24.dp)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                text = title,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun IngredientListScreen(
    storageFilter: String,
    onBack: () -> Unit
) {
    val ingredients = remember { mutableStateListOf<StoredIngredient>() }

    LaunchedEffect(storageFilter) {
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return@LaunchedEffect
        FirebaseFirestore.getInstance()
            .collection("users")
            .document(userId)
            .collection("ingredients")
            .get()
            .addOnSuccessListener { snapshot ->
                val filtered = snapshot.documents.mapNotNull { doc ->
                    doc.toObject(StoredIngredient::class.java)?.copy(id = doc.id)
                }
                    .filter {
                    storageFilter == "All Storage" || it.storage.equals(storageFilter, ignoreCase = true)
                }

                ingredients.clear()
                ingredients.addAll(filtered)
            }
    }

    Scaffold(
        contentWindowInsets = WindowInsets(0, 0, 0, 0)
    ) { innerPadding ->
    Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .background(Color.White)
        ) {
            GreenHeaderBar(title = "$storageFilter Items", onBack = onBack)

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp)
            ) {
                if (ingredients.isEmpty()) {
                    Text(
                        text = "No items in $storageFilter",
                        fontSize = 16.sp
                    )
                } else {
                    ingredients.forEach { ingredient ->
                        IngredientCard(
                            ingredient = ingredient,
                            onDelete = { toDelete ->
                                deleteIngredientFromFirestore(toDelete) { updatedQuantity ->
                                    if (updatedQuantity <= 0) {
                                        ingredients.remove(toDelete)
                                    } else {
                                        // Update the quantity in the local list
                                        val index = ingredients.indexOfFirst { it.id == toDelete.id }
                                        if (index != -1) {
                                            ingredients[index] = toDelete.copy(quantity = updatedQuantity.toString())
                                        }
                                    }
                                }
                            }

                        )

                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun IngredientListByCategoryScreen(
    category: String,
    onBack: () -> Unit
) {
    val ingredients = remember { mutableStateListOf<StoredIngredient>() }
    val isLoading = remember { mutableStateOf(true) }


    LaunchedEffect(category) {
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return@LaunchedEffect
        FirebaseFirestore.getInstance()
            .collection("users")
            .document(userId)
            .collection("ingredients")
            .get()
            .addOnSuccessListener { snapshot ->
                val filtered = snapshot.documents.mapNotNull { doc ->
                    doc.toObject(StoredIngredient::class.java)?.copy(id = doc.id)
                }
                    .filter {
                    it.category.equals(category, ignoreCase = true)
                }

                ingredients.clear()
                ingredients.addAll(filtered)
                isLoading.value = false
            }
            .addOnFailureListener {
                isLoading.value = false
            }
    }

    Scaffold(
        contentWindowInsets = WindowInsets(0, 0, 0, 0)
    ) { innerPadding ->
    Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .background(Color.White)
        ) {
            GreenHeaderBar(title = "$category Items", onBack = onBack)

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp)
            ) {
                when {
                    isLoading.value -> {
                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            CircularProgressIndicator()
                        }
                    }

                    ingredients.isEmpty() -> {
                        Text("No items in $category", fontSize = 16.sp)
                    }

                    else -> {
                        ingredients.forEach { ingredient ->
                            IngredientCard(
                                ingredient = ingredient,
                                onDelete = { toDelete ->
                                    deleteIngredientFromFirestore(toDelete) { updatedQuantity ->
                                        if (updatedQuantity <= 0) {
                                            ingredients.remove(toDelete)
                                        } else {
                                            // Update the quantity in the local list
                                            val index = ingredients.indexOfFirst { it.id == toDelete.id }
                                            if (index != -1) {
                                                ingredients[index] = toDelete.copy(quantity = updatedQuantity.toString())
                                            }
                                        }
                                    }
                                }

                            )
                        }
                    }

                }
            }
        }
    }
}


@Composable
fun IngredientCard(
    ingredient: StoredIngredient,
    onDelete: (StoredIngredient) -> Unit
) {
    var showDialog by remember { mutableStateOf(false) }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        showDialog = false
                        onDelete(ingredient)
                    }
                ) {
                    Text("Delete", color = Color.Red)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDialog = false }) {
                    Text("Cancel")
                }
            },
            title = { Text("Delete Ingredient?") },
            text = { Text("Are you sure you want to delete ${ingredient.name}?") }
        )
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .background(Color(0xFFF7F7F7), RoundedCornerShape(12.dp))
            .padding(12.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column {
                Text(ingredient.name, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                Text("Quantity: ${ingredient.quantity}")
                Text("Expires: ${ingredient.expirationDate}")
                Text("Category: ${ingredient.category}")
                Text("Storage: ${ingredient.storage}")
            }
            IconButton(onClick = { showDialog = true }) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Delete",
                    tint = Color.Red
                )
            }
        }
    }
}



fun deleteIngredientFromFirestore(
    ingredient: StoredIngredient,
    onResult: (Int) -> Unit // Return updated quantity
) {
    val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return
    val db = FirebaseFirestore.getInstance()
    val docRef = db.collection("users")
        .document(userId)
        .collection("ingredients")
        .document(ingredient.id)

    docRef.get().addOnSuccessListener { snapshot ->
        val currentQuantity = snapshot.getString("quantity")?.toIntOrNull() ?: 1

        if (currentQuantity <= 1) {
            docRef.delete().addOnSuccessListener {
                onResult(0)
            }
        } else {
            val newQuantity = currentQuantity - 1
            docRef.update("quantity", newQuantity.toString())
                .addOnSuccessListener {
                    onResult(newQuantity)
                }
        }
    }
}


