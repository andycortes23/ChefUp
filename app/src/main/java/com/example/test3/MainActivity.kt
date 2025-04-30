package com.example.test3

import android.os.Bundle
import android.graphics.Color as AndroidColor
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsControllerCompat
import kotlinx.coroutines.delay

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // ✅ Set the status bar color to black with white icons
        window.statusBarColor = AndroidColor.BLACK
        WindowCompat.setDecorFitsSystemWindows(window, true) // Make content NOT draw under status bar
        WindowInsetsControllerCompat(window, window.decorView).isAppearanceLightStatusBars = false

        setContent {
            SplashScreenWithFadeTransition()
        }
    }
}

@Composable
fun SplashScreenWithFadeTransition() {
    val showSplash = remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        delay(3000)
        showSplash.value = false
    }

    Crossfade(
        targetState = showSplash.value,
        animationSpec = tween(durationMillis = 1000)
    ) { isSplash ->
        if (isSplash) {
            SplashPage() // Your splash screen Composable
        } else {
            // Wrap InventoryScreen in a black top Box to blend with status bar
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black) // black to match the status bar
            ) {
                Settings() // Your main app screen
            }
        }
    }
}
