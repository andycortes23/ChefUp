package com.example.test3

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.layout.ContentScale

import com.google.accompanist.systemuicontroller.rememberSystemUiController
import androidx.compose.runtime.SideEffect


@Composable
fun SplashPage() {
    val systemUiController = rememberSystemUiController()

    SideEffect {
        systemUiController.setStatusBarColor(
            color = Color(0xFFC2FF87),
            darkIcons = true
        )
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFC2FF87)),
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = painterResource(id = R.drawable.image1),
            contentDescription = "Splash Logo",
            contentScale = ContentScale.Fit, // or use Crop if you want it to fill vertically
            modifier = Modifier
                .fillMaxWidth(0.6f) // image fills 60% of screen width, adjust as needed
                .aspectRatio(1f)    // keeps image square
        )
    }
}
