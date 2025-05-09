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

class TermsAndConditionsActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Test3Theme {
                TermsAndConditions()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TermsAndConditions() {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Terms & Conditions") },
                navigationIcon = {
                    IconButton(onClick = { /* Add back logic */ }) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(horizontal = 16.dp, vertical = 24.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Text(
                text = "Last Updated: May 6th, 2025",
                style = MaterialTheme.typography.labelMedium,
                color = Color.Gray
            )

            Spacer(modifier = Modifier.height(16.dp))

            Section(
                title = "1. Usage",
                content = "Chef-Up is for personal use only. Don’t copy, misuse, or sell content."
            )

            Section(
                title = "2. Data",
                content = "We collect minimal data to improve features. Your privacy matters."
            )

            Section(
                title = "3. Content",
                content = "Recipes and suggestions are informational only. Always consult experts for health concerns."
            )

            Section(
                title = "4. Accounts",
                content = "You’re responsible for your login and activity."
            )

            Section(
                title = "5. Updates",
                content = "We may update these terms anytime. Continued use = acceptance."
            )
        }
    }
}

@Composable
fun Section(title: String, content: String) {
    Spacer(modifier = Modifier.height(12.dp))
    Text(text = title, fontWeight = FontWeight.Bold, fontSize = 16.sp)
    Spacer(modifier = Modifier.height(4.dp))
    Text(text = content, fontSize = 16.sp, lineHeight = 22.sp)
}


