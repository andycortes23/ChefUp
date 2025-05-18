package com.example.test3.settings

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.test3.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChangePasswordScreen(
    onBackClicked: () -> Unit,
    onChangePasswordClicked: (oldPassword: String, newPassword: String) -> Unit
) {
    var oldPassword by remember { mutableStateOf("") }
    var newPassword by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var error by remember { mutableStateOf<String?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(WindowInsets.statusBars.asPaddingValues())
            .padding(horizontal = 24.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(16.dp))

        // Back arrow
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBackClicked) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Back")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Logo
        Box(
            modifier = Modifier
                .size(100.dp)
                .clip(CircleShape)
                .background(Color(0xFFDFFFD6)),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(id = R.drawable.image2),
                contentDescription = "Logo",
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Chef Up",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Change your password",
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium,
            color = Color.Black
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Old Password Field
        OutlinedTextField(
            value = oldPassword,
            onValueChange = { oldPassword = it },
            label = { Text("Old Password") },
            visualTransformation = PasswordVisualTransformation(),
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
            colors = TextFieldDefaults.outlinedTextFieldColors(
                focusedTextColor = Color.Black,
                unfocusedTextColor = Color.Black,
                focusedBorderColor = Color.Black,
                unfocusedBorderColor = Color.Gray,
                cursorColor = Color.Black,
                focusedLabelColor = Color.Black,
                unfocusedLabelColor = Color.Gray
            )
        )

        Spacer(modifier = Modifier.height(16.dp))

        // New Password Field
        OutlinedTextField(
            value = newPassword,
            onValueChange = { newPassword = it },
            label = { Text("New Password") },
            visualTransformation = PasswordVisualTransformation(),
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
            colors = TextFieldDefaults.outlinedTextFieldColors(
                focusedTextColor = Color.Black,
                unfocusedTextColor = Color.Black,
                focusedBorderColor = Color.Black,
                unfocusedBorderColor = Color.Gray,
                cursorColor = Color.Black,
                focusedLabelColor = Color.Black,
                unfocusedLabelColor = Color.Gray
            )
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Confirm New Password Field
        OutlinedTextField(
            value = confirmPassword,
            onValueChange = { confirmPassword = it },
            label = { Text("Confirm New Password") },
            visualTransformation = PasswordVisualTransformation(),
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
            colors = TextFieldDefaults.outlinedTextFieldColors(
                focusedTextColor = Color.Black,
                unfocusedTextColor = Color.Black,
                focusedBorderColor = Color.Black,
                unfocusedBorderColor = Color.Gray,
                cursorColor = Color.Black,
                focusedLabelColor = Color.Black,
                unfocusedLabelColor = Color.Gray
            )
        )

        Spacer(modifier = Modifier.height(16.dp))

        error?.let {
            Text(text = it, color = Color.Red, fontSize = 12.sp)
            Spacer(modifier = Modifier.height(8.dp))
        }

        Button(
            onClick = {
                if (oldPassword.isBlank() || newPassword.isBlank() || confirmPassword.isBlank()) {
                    error = "Please fill in all fields"
                } else if (newPassword != confirmPassword) {
                    error = "New passwords do not match"
                } else {
                    error = null
                    onChangePasswordClicked(oldPassword, newPassword)
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color.Black),
            shape = RoundedCornerShape(8.dp)
        ) {
            Text("Change Password", color = Color.White)
        }

        Spacer(modifier = Modifier.height(32.dp))
    }
}
