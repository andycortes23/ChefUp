package com.example.test3

import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.googlefonts.GoogleFont
import androidx.compose.ui.text.googlefonts.Font
import androidx.compose.ui.text.googlefonts.GoogleFont.Provider
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.TextStyle
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.EmojiFoodBeverage
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.ui.draw.clip
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.text.font.FontWeight
import com.google.accompanist.systemuicontroller.rememberSystemUiController


val provider2 = Provider(
    providerAuthority = "com.google.android.gms.fonts",
    providerPackage = "com.google.android.gms",
    certificates = R.array.com_google_android_gms_fonts_certs // default certificate array
)

val robotoFont2 = GoogleFont("Roboto")

val fontFamily2 = FontFamily(
    Font(googleFont = robotoFont2, fontProvider = provider2, weight = FontWeight.Normal),
    Font(googleFont = robotoFont2, fontProvider = provider2, weight = FontWeight.Bold)
)

@Composable
fun Inventory() {
    val systemUiController = rememberSystemUiController()

    SideEffect {
        systemUiController.setStatusBarColor(
            color = Color(0xFFD4FF99), // or whatever background your inventory needs
            darkIcons = true // Light icons = false, Dark icons = true
        )
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFD4FF99))
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 32.dp)
                .background(Color.White)
        ) {
            TopSection()
            CategorySection()
            Spacer(modifier = Modifier.height(16.dp))
            StorageSection()
            Spacer(modifier = Modifier.weight(1f))
            BottomNavBar()
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopSection() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFFD4FF99))
            .padding(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 8.dp)
    ) {
        OutlinedTextField(
            value = "",
            onValueChange = {},
            placeholder = { Text("Search") },
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White, RoundedCornerShape(12.dp)),
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
            shape = RoundedCornerShape(12.dp),
            colors = TextFieldDefaults.outlinedTextFieldColors(
                containerColor = Color.White
            )
        )
    }
}

@Composable
fun CategorySection() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFFD4FF99))
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
                "Veggies" to R.drawable.veggies,
                "Grains" to R.drawable.grains,
                "Proteins" to R.drawable.proteins
            ).forEach { (label, imageRes) ->
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Image(
                        painter = painterResource(id = imageRes),
                        contentDescription = label,
                        modifier = Modifier
                            .size(72.dp)
                            .clip(CircleShape)
                            .border(1.dp, Color.LightGray, CircleShape)
                            .background(Color.White)

                    )
                    Text(label)
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))
    }
}


@Composable
fun StorageSection() {
    Column(modifier = Modifier.padding(horizontal = 16.dp)) {
        listOf("Fridge", "Pantry", "All Storage").forEach { title ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 20.dp), // larger spacing
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "$title:",
                    fontWeight = FontWeight.Bold,
                    fontSize = 28.sp
                )
                Icon(Icons.Default.ArrowForward, contentDescription = null)
            }
        }

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(top = 20.dp)
        ) {
            Text("Add Storage Unit", fontWeight = FontWeight.Bold, fontSize = 20.sp)
            Spacer(modifier = Modifier.width(8.dp))
            Icon(Icons.Default.AddCircle, contentDescription = "Add")
        }
    }
}

@Composable
fun BottomNavBar() {
    BottomNavigation(
        backgroundColor = Color.White,
        contentColor = Color.Black,
        modifier = Modifier.height(56.dp)
    ) {
        BottomNavigationItem(
            icon = {
                Icon(
                    Icons.Default.Home,
                    contentDescription = "Home",
                    modifier = Modifier.offset(y = (-4).dp)
                )
            },
            selected = true,
            onClick = {}
        )
        BottomNavigationItem(
            icon = {
                Icon(
                    Icons.Default.CameraAlt,
                    contentDescription = "Camera",
                    modifier = Modifier.offset(y = (-4).dp)
                )
            },
            selected = false,
            onClick = {}
        )
        BottomNavigationItem(
            icon = {
                Icon(
                    Icons.Default.Map,
                    contentDescription = "Map",
                    modifier = Modifier.offset(y = (-4).dp)
                )
            },
            selected = false,
            onClick = {}
        )
        BottomNavigationItem(
            icon = {
                Icon(
                    Icons.Default.EmojiFoodBeverage,
                    contentDescription = "Chef",
                    modifier = Modifier.offset(y = (-4).dp)
                )
            },
            selected = false,
            onClick = {}
        )
        BottomNavigationItem(
            icon = {
                Icon(
                    Icons.Default.Settings,
                    contentDescription = "Settings",
                    modifier = Modifier.offset(y = (-4).dp)
                )
            },
            selected = false,
            onClick = {}
        )
    }
}
