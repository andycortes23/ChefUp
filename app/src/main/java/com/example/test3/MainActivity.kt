package com.example.test3

import android.os.Bundle
import android.content.Intent
import android.widget.Toast
import android.graphics.Color as AndroidColor
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.example.test3.signup.SignUp
import com.google.android.gms.auth.api.signin.*
import com.google.android.gms.common.api.ApiException
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.coroutines.delay
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.compose.ui.Alignment
import androidx.compose.material3.Text
import com.example.test3.SplashPage
import com.example.test3.settings.AboutUsScreen
import com.example.test3.settings.PrivacyPolicyScreen
import com.example.test3.settings.TermsAndConditionsScreen
import com.example.test3.signup.NameEntryScreen
import androidx.compose.ui.unit.sp
import com.example.test3.signup.NameEntryScreen
import com.example.test3.inventory.Inventory


/*
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Status bar styling
        window.statusBarColor = AndroidColor.BLACK
        WindowCompat.setDecorFitsSystemWindows(window, true)
        WindowInsetsControllerCompat(window, window.decorView).isAppearanceLightStatusBars = false

        setContent {
            AboutUsScreen()
        }
    }
}
*/

sealed class Screen {
    object Splash : Screen()
    object SignUp : Screen()
    data class NameEntry(val userId: String, val email: String) : Screen()
    object Home : Screen()
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // ✅ Make status bar visible with dark icons
        window.statusBarColor = AndroidColor.WHITE
        WindowCompat.setDecorFitsSystemWindows(window, true)
        WindowInsetsControllerCompat(window, window.decorView).isAppearanceLightStatusBars = true

        FirebaseApp.initializeApp(this)

        setContent {
            val context = LocalContext.current
            var toastMessage by remember { mutableStateOf<String?>(null) }
            var currentScreen by remember { mutableStateOf<Screen>(Screen.Splash) }

            val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build()

            val googleSignInClient = GoogleSignIn.getClient(context, gso)

            val launcher = rememberLauncherForActivityResult(
                contract = ActivityResultContracts.StartActivityForResult()
            ) { result ->
                val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
                try {
                    val account = task.getResult(ApiException::class.java)
                    val credential = GoogleAuthProvider.getCredential(account.idToken, null)
                    FirebaseAuth.getInstance().signInWithCredential(credential)
                        .addOnCompleteListener { authTask ->
                            if (authTask.isSuccessful) {
                                val user = FirebaseAuth.getInstance().currentUser
                                if (user != null) {
                                    currentScreen = Screen.NameEntry(user.uid, user.email ?: "")
                                }
                            } else {
                                toastMessage = "Google sign-in failed"
                            }
                        }
                } catch (e: ApiException) {
                    toastMessage = "Google sign-in error: ${e.localizedMessage}"
                }
            }

            Crossfade(targetState = currentScreen, animationSpec = tween(500)) { screen ->
                when (screen) {
                    is Screen.Splash -> SplashScreenWithDelay(onDone = {
                        currentScreen = Screen.SignUp
                    })

                    is Screen.SignUp -> SignUp(
                        onContinueClicked = { email, password ->
                            FirebaseAuth.getInstance()
                                .createUserWithEmailAndPassword(email, password)
                                .addOnCompleteListener { task ->
                                    if (task.isSuccessful) {
                                        val user = FirebaseAuth.getInstance().currentUser
                                        if (user != null) {
                                            currentScreen = Screen.NameEntry(user.uid, user.email ?: "")
                                        }
                                    } else {
                                        toastMessage = task.exception?.message ?: "Sign-up failed"
                                    }
                                }
                        },
                        onGoogleClicked = {
                            val intent = googleSignInClient.signInIntent
                            launcher.launch(intent)
                        },
                        onLoginClicked = {
                            toastMessage = "Navigate to login"
                        }
                    )

                    is Screen.NameEntry -> NameEntryScreen(
                        userId = screen.userId,
                        userEmail = screen.email,
                        onNameSubmitted = {
                            currentScreen = Screen.Home
                        }
                    )

                    is Screen.Home -> Inventory()

                }

                toastMessage?.let {
                    Toast.makeText(context, it, Toast.LENGTH_LONG).show()
                    toastMessage = null
                }
            }
        }
    }
}

@Composable
fun SplashScreenWithDelay(onDone: () -> Unit) {
    LaunchedEffect(Unit) {
        delay(3000)
        onDone()
    }

    SplashPage()
}