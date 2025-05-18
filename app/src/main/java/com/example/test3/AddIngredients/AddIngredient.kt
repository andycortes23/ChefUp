package com.example.test3.inventory

import android.app.DatePickerDialog
import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.test3.AddIngredients.groceryIngredientList
import com.example.test3.AddIngredients.IngredientItem
import com.example.test3.Screen
import com.example.test3.components.BottomNavBar
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.*
import kotlinx.coroutines.launch


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddIngredientScreen(
    onAddSuccess: () -> Unit,
    currentScreen: Screen,
    onTabSelected: (Screen) -> Unit
) {
    val context = LocalContext.current
    val systemUiController = rememberSystemUiController()
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()

    SideEffect {
        systemUiController.setStatusBarColor(Color(0xFFD4FF99), darkIcons = true)
    }

    var name by remember { mutableStateOf("") }
    var quantity by remember { mutableStateOf("") }
    var storage by remember { mutableStateOf("Fridge") }
    var expirationDate by remember { mutableStateOf("") }
    var showSuggestions by remember { mutableStateOf(false) }

    val calendar = Calendar.getInstance()
    val storageOptions = listOf("Fridge", "Freezer", "Pantry")

    val datePickerDialog = remember {
        DatePickerDialog(
            context,
            { _, year, month, day ->
                val cal = Calendar.getInstance().apply {
                    set(year, month, day)
                }
                expirationDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(cal.time)
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )
    }

    Scaffold(
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        snackbarHost = { SnackbarHost(snackbarHostState) },
        bottomBar = {
            BottomNavBar(
                currentScreen = currentScreen,
                onTabSelected = onTabSelected,
                onAddIngredient = {}
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .background(Color.White)
        ) {
            // Header
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFFD4FF99))
                    .padding(WindowInsets.statusBars.asPaddingValues())
                    .padding(horizontal = 16.dp, vertical = 16.dp)
            ) {
                Text(
                    text = "Add Ingredient",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Column(modifier = Modifier.padding(16.dp)) {
                // Ingredient Name with suggestions
                Box(modifier = Modifier.fillMaxWidth()) {
                    Column {
                        OutlinedTextField(
                            value = name,
                            onValueChange = {
                                name = it
                                showSuggestions = it.length >= 2
                            },
                            label = { Text("Ingredient Name") },
                            singleLine = true,
                            keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Done),
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            colors = TextFieldDefaults.outlinedTextFieldColors(
                                containerColor = Color.White,
                                focusedBorderColor = Color.Black,
                                unfocusedBorderColor = Color.Gray,
                                focusedLabelColor = Color.Black,
                                unfocusedLabelColor = Color.Gray,
                                focusedTextColor = Color.Black,
                                unfocusedTextColor = Color.Black,
                                cursorColor = Color.Black
                            )
                        )

                        val filtered = groceryIngredientList.filter {
                            it.name.contains(name, ignoreCase = true)
                        }.take(5)

                        if (showSuggestions && filtered.isNotEmpty()) {
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = 4.dp)
                                    .heightIn(max = 200.dp),
                                elevation = CardDefaults.cardElevation(4.dp)
                            ) {
                                Column {
                                    filtered.forEach { suggestion ->
                                        Text(
                                            text = suggestion.name,
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .clickable {
                                                    name = suggestion.name
                                                    showSuggestions = false
                                                }
                                                .padding(12.dp),
                                            color = Color.Black
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Quantity
                OutlinedTextField(
                    value = quantity,
                    onValueChange = { quantity = it },
                    label = { Text("Quantity") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Done),
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = TextFieldDefaults.outlinedTextFieldColors(
                        containerColor = Color.White,
                        focusedBorderColor = Color.Black,
                        unfocusedBorderColor = Color.Gray,
                        focusedLabelColor = Color.Black,
                        unfocusedLabelColor = Color.Gray,
                        focusedTextColor = Color.Black,
                        unfocusedTextColor = Color.Black,
                        cursorColor = Color.Black
                    )
                )

                Spacer(modifier = Modifier.height(12.dp))
                Text("Storage Location:", fontWeight = FontWeight.SemiBold, fontSize = 16.sp)

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    storageOptions.forEach { option ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(end = 8.dp)
                        ) {
                            RadioButton(
                                selected = storage == option,
                                onClick = { storage = option },
                                colors = RadioButtonDefaults.colors(
                                    selectedColor = Color.Black,
                                    unselectedColor = Color.LightGray,
                                    disabledSelectedColor = Color.Gray,
                                    disabledUnselectedColor = Color.LightGray
                                )
                            )
                            Text(
                                text = option,
                                color = Color.Black,
                                modifier = Modifier.padding(start = 4.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))
                Button(
                    onClick = { datePickerDialog.show() },
                    modifier = Modifier.align(Alignment.CenterHorizontally),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Black),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Select Expiration Date: ${if (expirationDate.isBlank()) "Not Set" else expirationDate}")
                }

                Spacer(modifier = Modifier.height(24.dp))
                Button(
                    onClick = {
                        saveIngredientToFirestore(
                            context = context,
                            name = name,
                            quantity = quantity,
                            storage = storage,
                            expirationDate = expirationDate,
                            onSuccess = {
                                coroutineScope.launch {
                                    snackbarHostState.showSnackbar("Ingredient added successfully")
                                }
                                // Reset form instead of leaving screen
                                name = ""
                                quantity = ""
                                expirationDate = ""
                                storage = "Fridge"
                            }
                        )
                    },
                    modifier = Modifier.align(Alignment.CenterHorizontally),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Black),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Save Ingredient")
                }
            }
        }
    }
}

fun getCategoryForIngredient(name: String): String {
    return groceryIngredientList.firstOrNull {
        it.name.equals(name.trim(), ignoreCase = true)
    }?.category ?: "Uncategorized"
}

fun saveIngredientToFirestore(
    context: Context,
    name: String,
    quantity: String,
    storage: String,
    expirationDate: String,
    onSuccess: () -> Unit
) {
    val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return
    val db = FirebaseFirestore.getInstance()
    val category = getCategoryForIngredient(name)
    val collectionRef = db.collection("users").document(userId).collection("ingredients")

    // Look for an existing ingredient with the same name and storage
    collectionRef
        .whereEqualTo("name", name.trim())
        .whereEqualTo("storage", storage)
        .get()
        .addOnSuccessListener { snapshot ->
            val existingDoc = snapshot.documents.firstOrNull()

            if (existingDoc != null) {
                // Merge quantities if the document exists
                val existingQuantity = existingDoc.getString("quantity")?.toIntOrNull() ?: 0
                val newQuantity = quantity.toIntOrNull() ?: 0
                val totalQuantity = existingQuantity + newQuantity

                collectionRef.document(existingDoc.id)
                    .update("quantity", totalQuantity.toString())
                    .addOnSuccessListener { onSuccess() }

            } else {
                // Create a new ingredient if it doesn't exist
                val newIngredient = mapOf(
                    "name" to name.trim(),
                    "quantity" to quantity,
                    "storage" to storage,
                    "expirationDate" to expirationDate,
                    "category" to category
                )

                collectionRef.add(newIngredient).addOnSuccessListener { onSuccess() }
            }
        }
        .addOnFailureListener { e ->
            e.printStackTrace()
        }
}

