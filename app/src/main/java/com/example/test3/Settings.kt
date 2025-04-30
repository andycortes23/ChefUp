package com.example.test3

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.draw.clip
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.background
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.Image
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Settings
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp


@Composable
fun Settings() {
    val systemUiController = rememberSystemUiController()

    SideEffect {
        systemUiController.setStatusBarColor(
            color = Color(0xFFD4FF99),
            darkIcons = true
        )
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White) // ✅ Fix 1: full screen white background
    ) {
        Column(modifier = Modifier.fillMaxSize()) {

            // Header with status bar padding
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFFD4FF99))
                    .statusBarsPadding() // ✅ Fix 2: push down from top safely
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

            // Profile Card
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
                        Image(
                            painter = painterResource(id = R.drawable.profile_placeholder),
                            contentDescription = "Profile image",
                            modifier = Modifier
                                .size(48.dp)
                                .clip(CircleShape)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text("First Last", fontSize = 18.sp, fontWeight = FontWeight.Medium)
                    }

                    Divider()

                    SettingsSection(title = "Account Settings") {
                        SettingsItem("Edit profile")
                        SettingsItem("Change password")
                        SettingsItem("Add a payment method", trailingIcon = {
                            Icon(Icons.Default.Add, contentDescription = null)
                        })
                        SettingsItem("Push notifications", trailingContent = {
                            Switch(checked = false, onCheckedChange = {})
                        })
                        SettingsItem("Dark mode", trailingContent = {
                            Switch(checked = false, onCheckedChange = {})
                        })
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // More Section
            Card(
                modifier = Modifier
                    .padding(horizontal = 16.dp)
                    .fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                elevation = 4.dp
            ) {
                Column {
                    SettingsSection(title = "More") {
                        SettingsItem("About us")
                        SettingsItem("Privacy policy")
                        SettingsItem("Terms and conditions")
                    }
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            BottomNavBar()
        }
    }
}
@Composable
fun SettingsItem(title: String, trailingIcon: @Composable (() -> Unit)? = null, trailingContent: @Composable (() -> Unit)? = null) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { }
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
fun SettingsSection(title: String, content: @Composable ColumnScope.() -> Unit) {
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
