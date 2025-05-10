package com.example.test3.settings

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import androidx.compose.ui.Alignment


@Composable
fun PrivacyPolicyScreen(onBack: () -> Unit = {}) {
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
                    text = "Privacy Policy",
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
                text = "We respect your privacy. This policy explains how Chef-Up collects, uses, and protects your information:",
                fontSize = 16.sp,
                lineHeight = 22.sp,
                modifier = Modifier.padding(bottom = 12.dp)
            )

            val sections = listOf(
                "What We Collect" to listOf(
                    "Email, name (if provided)",
                    "App preferences and usage behavior"
                ),
                "How We Use It" to listOf(
                    "To personalize your experience",
                    "Improve app performance and features",
                    "Send optional notifications (like inventory reminders)"
                ),
                "Data Sharing" to listOf(
                    "We do not sell your personal information.",
                    "We may share non-personal data with analytics providers to improve the app."
                ),
                "Your Choices" to listOf(
                    "You can manage notifications and data in Settings."
                ),
                "Security" to listOf(
                    "We use encryption and secure storage to protect your data."
                )
            )

            sections.forEachIndexed { index, (heading, bullets) ->
                Text(
                    text = "${index + 1}. $heading",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    modifier = Modifier.padding(top = 8.dp, bottom = 4.dp)
                )
                bullets.forEach { bullet ->
                    Text(
                        text = "• $bullet",
                        fontSize = 16.sp,
                        lineHeight = 22.sp,
                        modifier = Modifier.padding(start = 12.dp, bottom = 4.dp)
                    )
                }
            }
        }
    }
}
