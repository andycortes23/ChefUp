package com.example.test3.settings

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.test3.ui.theme.Test3Theme
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material.Text
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.accompanist.systemuicontroller.rememberSystemUiController


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TermsAndConditionsScreen(onBack: () -> Unit = {}) {
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
        // Header bar
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFFD4FF99))
                .statusBarsPadding()
                .padding(horizontal = 16.dp, vertical = 16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Back",
                    modifier = Modifier
                        .clickable { onBack() }
                        .size(24.dp)
                )
                Spacer(modifier = Modifier.width(16.dp))
                Text(
                    text = "Terms and Conditions",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        // Body content (scrollable)
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Text(
                text = "Last Updated: May 6th, 2025",
                fontWeight = FontWeight.SemiBold,
                fontSize = 14.sp,
                modifier = Modifier.padding(bottom = 12.dp)
            )

            Text(
                text = "Welcome to Chef-Up! By using our app, you agree to the following terms:",
                fontSize = 16.sp,
                lineHeight = 22.sp,
                modifier = Modifier.padding(bottom = 12.dp)
            )

            val terms = listOf(
                "Usage" to "Chef-Up is for personal, non-commercial use only. Don't misuse, duplicate, or resell any content or features.",
                "Data" to "We collect limited info (like login credentials and preferences) to improve your experience. We don’t sell your data.",
                "Content" to "Recipes and food data are for reference only. Always consult health professionals for dietary concerns.",
                "Account" to "Keep your login info safe. You're responsible for activity under your account.",
                "Modifications" to "We may update features or terms. Continued use means you accept the latest version."
            )

            terms.forEachIndexed { index, (title, body) ->
                Text(
                    text = "${index + 1}. $title",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    modifier = Modifier.padding(bottom = 4.dp)
                )
                Text(
                    text = body,
                    fontSize = 16.sp,
                    lineHeight = 22.sp,
                    modifier = Modifier.padding(bottom = 12.dp)
                )
            }
        }
    }
}



