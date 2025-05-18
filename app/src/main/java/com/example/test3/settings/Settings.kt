package com.example.test3.settings

import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import com.example.test3.R
import com.example.test3.Screen
import com.example.test3.components.BottomNavBar
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.launch

@Composable
fun Settings(
    onNavigate: (Screen) -> Unit,
    currentScreen: Screen,
    onTabSelected: (Screen) -> Unit,
    onAddIngredient: () -> Unit,
    onSignOut: () -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val uid = FirebaseAuth.getInstance().currentUser?.uid

    var firstName by remember { mutableStateOf("First") }
    var lastName by remember { mutableStateOf("Last") }

    val userId = FirebaseAuth.getInstance().currentUser?.uid
    val savedImageUri by ProfileImageStore.getProfileImageUri(context, userId ?: "").collectAsState(initial = null)


    // Load user name from Firestore
    LaunchedEffect(uid) {
        if (uid != null) {
            val doc = FirebaseFirestore.getInstance()
                .collection("users")
                .document(uid)
                .get()
                .await()

            firstName = doc.getString("firstName") ?: "First"
            lastName = doc.getString("lastName") ?: "Last"
        }
    }

    val systemUiController = rememberSystemUiController()
    SideEffect {
        systemUiController.setStatusBarColor(Color(0xFFD4FF99), darkIcons = true)
    }

    Scaffold(
        bottomBar = {
            BottomNavBar(
                currentScreen = currentScreen,
                onTabSelected = onTabSelected,
                onAddIngredient = onAddIngredient
            )
        }
    ) { innerPadding ->
        val bottomInset = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding()

        Column(
            modifier = Modifier
                .padding(innerPadding)
                .padding(bottom = bottomInset)
                .fillMaxSize()
                .background(Color.White)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFFD4FF99))
                    .padding(WindowInsets.statusBars.asPaddingValues())
                    .padding(horizontal = 16.dp, vertical = 16.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Settings,
                        contentDescription = "Settings",
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Settings",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Card(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                elevation = 4.dp
            ) {
                Column {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        val savedImageUri by ProfileImageStore.getProfileImageUri(context, userId ?: "").collectAsState(initial = null)

                        Image(
                            painter = savedImageUri?.let { rememberAsyncImagePainter(Uri.parse(it)) }
                                ?: painterResource(id = R.drawable.profile_placeholder),
                            contentDescription = "Profile image",
                            modifier = Modifier
                                .size(64.dp)
                                .clip(CircleShape)
                        )


                        Spacer(modifier = Modifier.width(12.dp))
                        Text("$firstName $lastName", fontSize = 18.sp, fontWeight = FontWeight.Medium)
                    }

                    Divider()

                    SettingsSection(title = "Account Settings") {
                        SettingsItem(title = "Edit profile", onClick = { onNavigate(Screen.Profile) })
                        SettingsItem(title = "Change password", onClick = { onNavigate(Screen.ChangePassword) })
                    }
                }
            }

            Card(
                modifier = Modifier
                    .padding(horizontal = 16.dp)
                    .fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                elevation = 4.dp
            ) {
                Column {
                    SettingsSection("More") {
                        SettingsItem(title = "About us", onClick = { onNavigate(Screen.About) })
                        SettingsItem(title = "Privacy policy", onClick = { onNavigate(Screen.Privacy) })
                        SettingsItem(title = "Terms and conditions", onClick = { onNavigate(Screen.Terms) })
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            var showSignOutDialog by remember { mutableStateOf(false) }

            if (showSignOutDialog) {
                AlertDialog(
                    onDismissRequest = { showSignOutDialog = false },
                    title = { Text("Confirm Sign Out") },
                    text = { Text("Are you sure you want to sign out?") },
                    confirmButton = {
                        TextButton(onClick = {
                            FirebaseAuth.getInstance().signOut()
                            onSignOut()
                        }) {
                            Text("Yes")
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = { showSignOutDialog = false }) {
                            Text("Cancel")
                        }
                    }
                )
            }

            Button(
                onClick = { showSignOutDialog = true },
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(horizontal = 16.dp)
                    .fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(backgroundColor = Color(0xFFFF6B6B))
            ) {
                Text("Sign Out", color = Color.White)
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}


@Composable
fun SettingsItem(
    title: String,
    onClick: () -> Unit,
    trailingIcon: @Composable (() -> Unit)? = null,
    trailingContent: @Composable (() -> Unit)? = null
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(horizontal = 16.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(title, fontSize = 16.sp)
        trailingContent?.invoke() ?: trailingIcon?.invoke() ?: Icon(
            Icons.Default.ArrowForward,
            contentDescription = null
        )
    }
}

@Composable
fun SettingsSection(
    title: String,
    content: @Composable ColumnScope.() -> Unit
) {
    Column(modifier = Modifier.padding(vertical = 8.dp)) {
        Text(
            text = title,
            color = Color.Gray,
            fontSize = 14.sp,
            modifier = Modifier.padding(start = 16.dp, bottom = 4.dp)
        )
        content()
    }
}
