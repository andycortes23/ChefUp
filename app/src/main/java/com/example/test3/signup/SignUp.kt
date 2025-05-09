package com.example.test3.signup

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
import com.example.test3.login.fontFamily
import com.example.test3.R


val provider1 = Provider(
    providerAuthority = "com.google.android.gms.fonts",
    providerPackage = "com.google.android.gms",
    certificates = R.array.com_google_android_gms_fonts_certs // default certificate array
)

val robotoFont1 = GoogleFont("Roboto")

val fontFamily1 = FontFamily(
    Font(googleFont = robotoFont1, fontProvider = provider1, weight = FontWeight.Normal),
    Font(googleFont = robotoFont1, fontProvider = provider1, weight = FontWeight.Bold)
)



@Composable
fun SignUp() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {

        // Splash Page Image
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
                contentDescription = "Splash Image",
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
        }


        // Splash Page Title Text
        Text(
            text = "Chef Up",
            style = TextStyle(
                fontSize = 24.sp,
                lineHeight = 36.sp,
                fontFamily = fontFamily,
                fontWeight = FontWeight(600),
                color = Color(0xFF000000),
                textAlign = TextAlign.Center,
            ),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
        )

        // Content Section
        Column(
            modifier = Modifier
                .width(375.dp)
                .height(335.dp)
                .padding(start = 24.dp, end = 24.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp, Alignment.Top),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            // First Text in Content: "Create an account"
            Text(
                text = "Create an account",
                style = TextStyle(
                    fontSize = 16.sp,
                    lineHeight = 24.sp,
                    fontFamily = fontFamily,
                    fontWeight = FontWeight(600),
                    color = Color(0xFF000000),
                    textAlign = TextAlign.Center,
                ),
                modifier = Modifier
                    .width(131.dp)
                    .height(24.dp)
            )

            // Second Text in Content: "Enter your email to sign up for this app"
            Text(
                text = "Enter your email to sign up for this app",
                style = TextStyle(
                    fontSize = 14.sp,
                    lineHeight = 21.sp,
                    fontFamily = fontFamily,
                    fontWeight = FontWeight(400),
                    color = Color(0xFF000000),
                    textAlign = TextAlign.Center,
                ),
                modifier = Modifier
                    .width(240.dp)
                    .height(21.dp)
            )

            // Input Field for Email
            Column(
                modifier = Modifier
                    .width(327.dp)
                    .height(96.dp)
                    .padding(start = 16.dp, top = 8.dp, end = 16.dp, bottom = 8.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp, Alignment.Top),
                horizontalAlignment = Alignment.Start
            ) {
                // Input Field
                Row(
                    horizontalArrangement = Arrangement.spacedBy(16.dp, Alignment.Start),
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .border(width = 1.dp, color = Color(0xFFE0E0E0), shape = RoundedCornerShape(size = 8.dp))
                        .width(327.dp)
                        .height(40.dp)
                        .background(color = Color(0xFFFFFFFF), shape = RoundedCornerShape(size = 8.dp))
                ) {
                    // Text inside the Input
                    Text(
                        text = "email@domain.com",
                        style = TextStyle(
                            fontSize = 14.sp,
                            lineHeight = 19.6.sp,
                            fontFamily = fontFamily,
                            fontWeight = FontWeight(400),
                            color = Color(0xFF828282),
                        ),
                        modifier = Modifier
                            .padding(start = 16.dp)
                            .width(295.dp)
                            .height(20.dp)
                    )
                }

                // Continue Button
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally),
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .width(327.dp)
                        .height(40.dp)
                        .background(color = Color(0xFF000000), shape = RoundedCornerShape(size = 8.dp))
                        .padding(start = 16.dp, end = 16.dp)
                ) {
                    // Button Text
                    Text(
                        text = "Continue",
                        style = TextStyle(
                            fontSize = 14.sp,
                            lineHeight = 19.6.sp,
                            fontFamily = fontFamily,
                            fontWeight = FontWeight(500),
                            color = Color(0xFFFFFFFF),
                        )
                    )
                }
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 32.dp)
            ) {
                // Left line
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(1.dp)
                        .background(Color(0xFFE6E6E6))
                )

                // "Or" text
                Text(
                    text = "or",
                    style = TextStyle(
                        fontSize = 14.sp,
                        lineHeight = 19.6.sp,
                        fontFamily = fontFamily,
                        fontWeight = FontWeight(400),
                        color = Color(0xFF828282),
                        textAlign = TextAlign.Center
                    ),
                    modifier = Modifier.padding(horizontal = 8.dp)
                )

                // Right line
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(1.dp)
                        .background(Color(0xFFE6E6E6))
                )
            }

            // Continue with Google Button
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally),
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .width(327.dp)
                    .height(40.dp)
                    .background(color = Color(0xFFEEEEEE), shape = RoundedCornerShape(size = 8.dp))
                    .padding(start = 82.5.dp, top = 10.dp, end = 81.5.dp, bottom = 10.dp)
            ) {
                // Image inside the button
                Image(
                    painter = painterResource(id = R.drawable.google),
                    contentDescription = "Google Logo",
                    modifier = Modifier
                        .padding(0.08333.dp)
                        .width(20.dp)
                        .height(20.dp),
                    contentScale = ContentScale.None
                )

                // Text inside the button
                Text(
                    text = "Continue with Google",
                    style = TextStyle(
                        fontSize = 14.sp,
                        lineHeight = 19.6.sp,
                        fontFamily = fontFamily,
                        fontWeight = FontWeight(500),
                        color = Color(0xFF000000),
                    ),
                    modifier = Modifier
                        .width(135.dp)
                        .height(20.dp)
                )
            }

            // Terms of Service Text
            Text(
                text = "By clicking continue, you agree to our Terms of Service and Privacy Policy",
                style = TextStyle(
                    fontSize = 12.sp,
                    lineHeight = 18.sp,
                    fontFamily = fontFamily,
                    fontWeight = FontWeight(400),
                    color = Color(0xFF828282),
                    textAlign = TextAlign.Center,
                ),
                modifier = Modifier
                    .width(327.dp)
                    .height(36.dp)
            )
        }
        // Login link text
        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Click here to Login",
            style = TextStyle(
                fontSize = 12.sp,
                lineHeight = 18.sp,
                fontFamily = fontFamily,
                fontWeight = FontWeight(400),
                color = Color(0xFF828282),
                textAlign = TextAlign.Center,
            ),
            modifier = Modifier
                .padding(top = 8.dp)
                .align(Alignment.CenterHorizontally)
        )
    }
}


