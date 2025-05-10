package com.example.test3.settings

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.accompanist.systemuicontroller.rememberSystemUiController

@Composable
fun AboutUsScreen(onBack: () -> Unit = {}) {
    val systemUiController = rememberSystemUiController()

    SideEffect {
        systemUiController.setStatusBarColor(
            color = Color(0xFFC2FF87),
            darkIcons = true
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        // Header
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
                    text = "About Us",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        // Content
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            Text(
                text = "At Chef-Up, we believe that cooking at home should be stress-free, sustainable, and satisfying. " +
                        "Our app is designed to help busy individuals and families take control of their kitchen by tracking ingredients, " +
                        "minimizing waste, and generating easy-to-follow meal plans. Whether you're a culinary beginner or a seasoned chef, " +
                        "Chef-Up makes meal prep smarter, faster, and more enjoyable.",
                fontSize = 16.sp,
                lineHeight = 22.sp
            )
        }
    }
}
