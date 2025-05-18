package com.example.test3

import android.os.Build
import android.os.Bundle
import android.widget.Toast
import android.graphics.Color as AndroidColor
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
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
import com.example.test3.settings.AboutUsScreen
import com.example.test3.settings.PrivacyPolicyScreen
import com.example.test3.settings.TermsAndConditionsScreen
import com.example.test3.signup.NameEntryScreen
import com.example.test3.inventory.InventoryScreen
import com.example.test3.login.LoginScreen
import com.google.firebase.firestore.FirebaseFirestore
import com.example.test3.settings.Settings
import androidx.compose.material3.Scaffold
import com.example.test3.settings.ChangePasswordScreen
import com.google.firebase.auth.EmailAuthProvider
import androidx.core.app.ActivityCompat
import android.Manifest
import android.util.Log
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.ktx.Firebase
import com.example.test3.components.BottomNavBar
import com.example.test3.inventory.AddIngredientScreen
import com.example.test3.inventory.IngredientListByCategoryScreen
import com.example.test3.inventory.IngredientListScreen
import com.example.test3.settings.EditProfileScreen
import com.example.test3.mealplanner.MealPlanGenScreen
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings

/*
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        window.statusBarColor = AndroidColor.BLACK
        WindowCompat.setDecorFitsSystemWindows(window, true)
        WindowInsetsControllerCompat(window, window.decorView).isAppearanceLightStatusBars = false

        setContent {
            AboutUsScreen() //Change this to the screen you would like to display and comment out the bottom code
        }
    }
}
*/



sealed class Screen {
    data object Splash : Screen()
    data object SignUp : Screen()
    data object Login : Screen()
    data class NameEntry(val userId: String, val email: String, val fromGoogle: Boolean = false) : Screen()
    data object Home : Screen()
    data object Terms : Screen()
    data object Privacy : Screen()
    data object Settings : Screen()
    data object About : Screen()
    data object Profile : Screen()
    data object ChangePassword : Screen()
    data object AddIngredients : Screen()
    data object MealPlanGen : Screen()
    data object OfflineMeals : Screen()

    data class MealDetail(val recipe: com.example.test3.mealplanner.Recipe) : Screen()

    data class IngredientList(val storage: String) : Screen()
    data class CategoryList(val category: String) : Screen()
}


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        FirebaseApp.initializeApp(this)
        Firebase.analytics.logEvent(FirebaseAnalytics.Event.APP_OPEN, null)

        val config = FirebaseRemoteConfig.getInstance()
        val settings = FirebaseRemoteConfigSettings.Builder()
            .setMinimumFetchIntervalInSeconds(0) // Use 3600+ in production
            .build()
        config.setConfigSettingsAsync(settings)
        config.fetchAndActivate()

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
                    if (currentScreen is Screen.Home || currentScreen is Screen.Settings || currentScreen is Screen.MealPlanGen) {
                        BottomNavBar(
                            currentScreen = currentScreen,
                            onTabSelected = { selected -> currentScreen = selected },
                            onAddIngredient = { currentScreen = Screen.AddIngredients }
                        )
                    }
                }
            ) { innerPadding ->
                Crossfade(targetState = currentScreen, animationSpec = tween(500)) { screen ->
                    when (screen) {
                        is Screen.Splash -> SplashScreenWithDelay {
                            val user = FirebaseAuth.getInstance().currentUser
                            if (user != null) {
                                currentScreen = Screen.Home
                            } else {
                                currentScreen = Screen.Login
                            }
                        }


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

                        is Screen.Home -> InventoryScreen(
                            currentScreen = currentScreen,
                            onTabSelected = { selected -> currentScreen = selected },
                            onAddIngredient = { currentScreen = Screen.AddIngredients },
                            onStorageSelected = { storage -> currentScreen = Screen.IngredientList(storage) },
                            onCategorySelected = { category -> currentScreen = Screen.CategoryList(category) }
                        )



                        is Screen.Settings -> Settings(
                            onNavigate = { screen -> currentScreen = screen },
                            currentScreen = currentScreen,
                            onTabSelected = { selected -> currentScreen = selected },
                            onAddIngredient = { currentScreen = Screen.AddIngredients },
                            onSignOut = { currentScreen = Screen.Login}
                        )


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
                        is Screen.Profile -> EditProfileScreen(
                            onBackClicked = { currentScreen = Screen.Settings },
                            onSaveSuccess = { currentScreen = Screen.Settings }
                        )
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
                        is Screen.AddIngredients -> AddIngredientScreen(
                            onAddSuccess = { currentScreen = Screen.Home },
                            currentScreen = currentScreen,
                            onTabSelected = { selected -> currentScreen = selected }
                        )
                        is Screen.IngredientList -> IngredientListScreen(
                            storageFilter = screen.storage,
                            onBack = { currentScreen = Screen.Home }
                        )
                        is Screen.CategoryList -> IngredientListByCategoryScreen(
                            category = screen.category,
                            onBack = { currentScreen = Screen.Home }
                        )
                        is Screen.MealPlanGen -> MealPlanGenScreen(
                            onRecipeSelected = { selectedRecipe ->
                                currentScreen = Screen.MealDetail(selectedRecipe)
                            },
                            onOfflineClick = {
                                currentScreen = Screen.OfflineMeals
                            }
                        )
                        is Screen.OfflineMeals -> com.example.test3.mealplanner.MealOfflineScreen(
                            onRecipeSelected = { selectedRecipe ->
                                currentScreen = Screen.MealDetail(selectedRecipe)
                            },
                            onBack = { currentScreen = Screen.MealPlanGen }
                        )
                        is Screen.MealDetail -> com.example.test3.mealplanner.MealDetailScreen(
                            recipe = screen.recipe,
                            onBack = { currentScreen = Screen.MealPlanGen }
                        )







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