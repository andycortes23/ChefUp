package com.example.test3

import android.os.Build
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
import com.example.test3.inventory.BottomNavBar
import com.example.test3.signup.NameEntryScreen
import com.example.test3.inventory.InventoryScreen
import com.example.test3.login.LoginScreen
import com.google.firebase.firestore.FirebaseFirestore
import com.example.test3.settings.Settings
import androidx.compose.material3.Scaffold
import com.example.test3.inventory.AddIngredientScreen
import com.example.test3.settings.ChangePasswordScreen
import com.google.firebase.auth.EmailAuthProvider
import androidx.core.app.ActivityCompat
import android.Manifest
import android.util.Log
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.ktx.Firebase



/*
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        window.statusBarColor = AndroidColor.BLACK
        WindowCompat.setDecorFitsSystemWindows(window, true)
        WindowInsetsControllerCompat(window, window.decorView).isAppearanceLightStatusBars = false

        setContent {
            AboutUsScreen() //Change this to the screen you would like to displau and comment out the bottom code
        }
    }
}
*/



sealed class Screen {
    object Splash : Screen()
    object SignUp : Screen()
    object Login : Screen()
    data class NameEntry(val userId: String, val email: String, val fromGoogle: Boolean = false) : Screen()
    object Home : Screen()
    object Terms : Screen()
    object Privacy : Screen()
    object Settings : Screen()
    object About : Screen()
    object Profile : Screen()
    object ChangePassword : Screen()
    object AddIngredients : Screen()
}


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        FirebaseApp.initializeApp(this)
        Firebase.analytics.logEvent(FirebaseAnalytics.Event.APP_OPEN, null)


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                101
            )
        }
        FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val token = task.result
                Log.d("FCM_TOKEN", "Token: $token")
            }
        }
        FirebaseApp.initializeApp(this)
        Firebase.analytics.logEvent(FirebaseAnalytics.Event.APP_OPEN, null)


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                101
            )
        }
        FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val token = task.result
                Log.d("FCM_TOKEN", "Token: $token")
            }
        }


        window.statusBarColor = AndroidColor.WHITE
        WindowCompat.setDecorFitsSystemWindows(window, true)
        WindowInsetsControllerCompat(window, window.decorView).isAppearanceLightStatusBars = true

        FirebaseApp.initializeApp(this)

        setContent {
            val context = LocalContext.current
            var toastMessage by remember { mutableStateOf<String?>(null) }
            var currentScreen by remember { mutableStateOf<Screen>(Screen.Splash) }
            var previousScreen by remember { mutableStateOf<Screen?>(null) }

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
                                    val db = FirebaseFirestore.getInstance()
                                    db.collection("users").document(user.uid)
                                        .set(
                                            mapOf(
                                                "firstName" to account.givenName,
                                                "lastName" to account.familyName,
                                                "email" to account.email
                                            )
                                        )
                                        .addOnSuccessListener {
                                            currentScreen = Screen.Home
                                        }
                                        .addOnFailureListener {
                                            toastMessage = "Failed to save Google user data"
                                        }
                                }
                            } else {
                                toastMessage = "Google sign-in failed"
                            }
                        }
                } catch (e: ApiException) {
                    toastMessage = "Google sign-in error: ${e.localizedMessage}"
                }
            }

            Scaffold(
                bottomBar = {
                    if (currentScreen is Screen.Home || currentScreen is Screen.Settings) {
                        BottomNavBar(
                            currentScreen = currentScreen,
                            onTabSelected = { selected -> currentScreen = selected }
                        )
                    }
                }
            ) { innerPadding ->
                Crossfade(targetState = currentScreen, animationSpec = tween(500)) { screen ->
                    when (screen) {
                        is Screen.Splash -> SplashScreenWithDelay { currentScreen = Screen.SignUp }

                        is Screen.SignUp -> Box(Modifier.padding(bottom = innerPadding.calculateBottomPadding())) {
                            SignUp(
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
                                onLoginClicked = { currentScreen = Screen.Login },
                                onTermsClicked = {
                                    previousScreen = currentScreen
                                    currentScreen = Screen.Terms
                                },
                                onPrivacyClicked = {
                                    previousScreen = currentScreen
                                    currentScreen = Screen.Privacy
                                }
                            )
                        }

                        is Screen.Login -> Box(Modifier.padding(bottom = innerPadding.calculateBottomPadding())) {
                            LoginScreen(
                                onLoginSuccess = { currentScreen = Screen.Home },
                                onBackToSignUp = { currentScreen = Screen.SignUp },
                                onGoogleClicked = {
                                    val intent = googleSignInClient.signInIntent
                                    launcher.launch(intent)
                                },
                                onTermsClicked = {
                                    previousScreen = currentScreen
                                    currentScreen = Screen.Terms
                                },
                                onPrivacyClicked = {
                                    previousScreen = currentScreen
                                    currentScreen = Screen.Privacy
                                }
                            )
                        }

                        is Screen.NameEntry -> Box(Modifier.padding(bottom = innerPadding.calculateBottomPadding())) {
                            NameEntryScreen(
                                userId = screen.userId,
                                userEmail = screen.email,
                                onNameSubmitted = { currentScreen = Screen.Home }
                            )
                        }

                        is Screen.Home -> Box(Modifier.padding(bottom = innerPadding.calculateBottomPadding())) {
                            InventoryScreen()
                        }

                        is Screen.Settings -> Box(Modifier.padding(bottom = innerPadding.calculateBottomPadding())) {
                            Settings(onNavigate = {
                                previousScreen = currentScreen
                                currentScreen = it
                            })
                        }

                        is Screen.Terms -> Box(Modifier.padding(bottom = innerPadding.calculateBottomPadding())) {
                            TermsAndConditionsScreen(onBack = {
                                currentScreen = previousScreen ?: Screen.Settings
                            })
                        }

                        is Screen.Privacy -> Box(Modifier.padding(bottom = innerPadding.calculateBottomPadding())) {
                            PrivacyPolicyScreen(onBack = {
                                currentScreen = previousScreen ?: Screen.Settings
                            })
                        }

                        is Screen.About -> Box(Modifier.padding(bottom = innerPadding.calculateBottomPadding())) {
                            AboutUsScreen(onBack = {
                                currentScreen = previousScreen ?: Screen.Settings
                            })
                        }
                        is Screen.Profile -> Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("Edit Profile screen coming soon!", fontSize = 20.sp)
                        }
                        is Screen.ChangePassword -> Box(Modifier.padding(bottom = innerPadding.calculateBottomPadding())) {
                            ChangePasswordScreen(
                                onBackClicked = {
                                    currentScreen = Screen.Settings
                                },
                                onChangePasswordClicked = { oldPassword, newPassword ->
                                    val user = FirebaseAuth.getInstance().currentUser
                                    if (user != null && user.email != null) {
                                        val credential = EmailAuthProvider.getCredential(user.email!!, oldPassword)
                                        user.reauthenticate(credential)
                                            .addOnSuccessListener {
                                                user.updatePassword(newPassword)
                                                    .addOnSuccessListener {
                                                        toastMessage = "Password changed successfully"
                                                        currentScreen = Screen.Settings
                                                    }
                                                    .addOnFailureListener {
                                                        toastMessage = "Failed to update password: ${it.localizedMessage}"
                                                    }
                                            }
                                            .addOnFailureListener {
                                                toastMessage = "Old password is incorrect"
                                            }
                                    } else {
                                        toastMessage = "User not logged in"
                                    }
                                }
                            )
                        }
                        is Screen.AddIngredients -> Box(Modifier.padding(bottom = innerPadding.calculateBottomPadding())) {
                            AddIngredientScreen(onAddSuccess = {
                                currentScreen = Screen.Home
                            })
                        }




                    }

                    toastMessage?.let {
                        Toast.makeText(context, it, Toast.LENGTH_LONG).show()
                        toastMessage = null
                    }
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