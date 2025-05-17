package com.example.test3.login

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.draw.shadow
import com.example.test3.R

import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.googlefonts.GoogleFont
import androidx.compose.ui.text.googlefonts.Font


import androidx.compose.ui.text.googlefonts.GoogleFont.Provider

import androidx.compose.foundation.shape.CircleShape
import androidx.compose.ui.draw.clip
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.firebase.auth.FirebaseAuth
import com.example.test3.login.fontFamily
import androidx.compose.foundation.clickable
import androidx.compose.foundation.text.ClickableText
import androidx.compose.ui.text.*
import androidx.compose.ui.text.style.TextDecoration



val provider = Provider(
    providerAuthority = "com.google.android.gms.fonts",
    providerPackage = "com.google.android.gms",
    certificates = R.array.com_google_android_gms_fonts_certs
)

val robotoFont = GoogleFont("Roboto")

val fontFamily = FontFamily(
    Font(googleFont = robotoFont, fontProvider = provider, weight = FontWeight.Normal),
    Font(googleFont = robotoFont, fontProvider = provider, weight = FontWeight.Bold)
)

@Composable
fun LoginScreen(
    onLoginSuccess: () -> Unit,
    onBackToSignUp: () -> Unit,
    onGoogleClicked: () -> Unit,
    onTermsClicked: () -> Unit,
    onPrivacyClicked: () -> Unit

) {
    val context = LocalContext.current
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var error by remember { mutableStateOf<String?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Chef Up",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = fontFamily,
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center,
            color = Color.Black
        )

        Spacer(modifier = Modifier.height(16.dp))

        Box(
            modifier = Modifier
                .size(107.dp)
                .clip(CircleShape)
                .background(Color(0x42C2FF87))
                .border(1.dp, Color.Black, CircleShape)
                .shadow(4.dp, CircleShape, clip = true)
        ) {
            Image(
                painter = painterResource(id = R.drawable.image2),
                contentDescription = "Logo",
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

        Text("Login to your account", fontWeight = FontWeight.SemiBold, fontSize = 16.sp)

        Spacer(modifier = Modifier.height(8.dp))

        Text("Enter your email to login to this app", fontSize = 14.sp, color = Color.Gray)

        Spacer(modifier = Modifier.height(24.dp))

        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email") },
            singleLine = true,
            keyboardOptions = KeyboardOptions(
                capitalization = KeyboardCapitalization.None,
                autoCorrect = false,
                keyboardType = KeyboardType.Email
            ),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Password") },
            singleLine = true,
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        error?.let {
            Text(it, color = Color.Red, fontSize = 12.sp)
            Spacer(modifier = Modifier.height(8.dp))
        }

        Button(
            onClick = {
                if (email.isBlank() || password.isBlank()) {
                    error = "Please enter both email and password"
                } else {
                    error = null
                    FirebaseAuth.getInstance()
                        .signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                onLoginSuccess()
                            } else {
                                error = task.exception?.message ?: "Login failed"
                            }
                        }
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color.Black)
        ) {
            Text("Continue", color = Color.White)
        }
        Spacer(modifier = Modifier.height(16.dp))

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Divider(color = Color.Gray, modifier = Modifier.weight(1f))
            Text("  or  ", color = Color.Gray)
            Divider(color = Color.Gray, modifier = Modifier.weight(1f))
        }

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally),
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
                .background(color = Color(0xFFEEEEEE), shape = RoundedCornerShape(8.dp))
                .padding(horizontal = 16.dp)
                .clickable { onGoogleClicked() }
        ) {
            Image(
                painter = painterResource(id = R.drawable.google),
                contentDescription = "Google Logo",
                modifier = Modifier
                    .width(20.dp)
                    .height(20.dp),
                contentScale = ContentScale.Fit
            )

            Text(
                text = "Continue with Google",
                style = TextStyle(
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    fontFamily = fontFamily,
                    color = Color.Black
                )
            )
        }


        Spacer(modifier = Modifier.height(24.dp))

        val annotatedText = buildAnnotatedString {
            append("By clicking continue, you agree to our ")

            pushStringAnnotation(tag = "terms", annotation = "terms")
            withStyle(SpanStyle(fontWeight = FontWeight.Bold)) {
                append("Terms of Service")
            }
            pop()

            append(" and ")

            pushStringAnnotation(tag = "privacy", annotation = "privacy")
            withStyle(SpanStyle(fontWeight = FontWeight.Bold)) {
                append("Privacy Policy")
            }
            pop()
        }

        ClickableText(
            text = annotatedText,
            style = TextStyle(
                fontSize = 12.sp,
                color = Color.Gray,
                textAlign = TextAlign.Center
            ),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            onClick = { offset ->
                annotatedText.getStringAnnotations(tag = "terms", start = offset, end = offset)
                    .firstOrNull()?.let {
                        onTermsClicked()
                    }

                annotatedText.getStringAnnotations(tag = "privacy", start = offset, end = offset)
                    .firstOrNull()?.let {
                        onPrivacyClicked()
                    }
            }
        )

        Spacer(modifier = Modifier.height(16.dp))

        TextButton(onClick = onBackToSignUp) {
            Text("Click here to Sign Up", color = Color.Black, fontSize = 12.sp)
        }
    }
}


