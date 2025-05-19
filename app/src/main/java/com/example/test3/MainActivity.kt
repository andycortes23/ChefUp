package com.example.test3

import com.example.test3.mealplanner.Recipe

import android.os.Build
import android.os.Bundle
import android.widget.Toast
import android.graphics.Color as AndroidColor
import androidx.fragment.app.FragmentActivity
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
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.ktx.Firebase
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings
import android.Manifest
import androidx.core.app.ActivityCompat
import com.google.firebase.messaging.FirebaseMessaging
import com.google.android.libraries.places.api.Places
import com.google.firebase.analytics.ktx.analytics
import android.util.Log
import com.example.test3.components.BottomNavBar
import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.sp
import androidx.compose.material3.Text
import com.example.test3.inventory.AddIngredientScreen
import com.example.test3.mealplanner.MealPlanGenScreen
import com.example.test3.settings.ChangePasswordScreen
import com.example.test3.inventory.IngredientListScreen
import com.example.test3.inventory.IngredientListByCategoryScreen
import com.example.test3.settings.EditProfileScreen
import com.example.test3.mealplanner.MealDetailScreen
import com.example.test3.mealplanner.MealOfflineScreen
import androidx.compose.material3.Snackbar
import androidx.compose.ui.graphics.Color
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.biometric.BiometricPrompt.PromptInfo
import androidx.core.content.ContextCompat
import android.content.Context


/*
class MainActivity : FragmentActivity() {
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
    data object Splash : Screen()
    data object SignUp : Screen()
    data object Login : Screen()
    data class NameEntry(val userId: String, val email: String, val fromGoogle: Boolean = false) : Screen()
    object Home : Screen()
    object Terms : Screen()
    object Privacy : Screen()
    object Settings : Screen()
    object About : Screen()
    object Profile : Screen()
    object AddIngredients : Screen()
    object MealPlanGen : Screen()
    object ChangePassword : Screen()
    object StoreFinder : Screen()
    object MealOffline : Screen()
    data class MealDetail(val recipe: Recipe) : Screen()

    data class StorageDetail(val storage: String) : Screen()
    data class CategoryDetail(val category: String) : Screen()
}

class MainActivity : FragmentActivity() {
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

        if (!Places.isInitialized()) {
            Places.initialize(applicationContext, "AIzaSyBPMpgZnvRwyiD47P-togXkkGLLAbJ64Jo")
        }

        showMainContent()
    }

    private fun showMainContent() {
        setContent {
            val context = LocalContext.current
            val connectivityObserver = remember { ConnectivityObserver(context) }
            val isOnline = remember { mutableStateOf(isNetworkAvailable(context)) }

            LaunchedEffect(Unit) {
                connectivityObserver.connectionStatus.collect {
                    isOnline.value = isNetworkAvailable(context)
                }
            }
            val snackbarHostState = remember { SnackbarHostState() }
            val scope = rememberCoroutineScope()
            var currentScreen by remember { mutableStateOf<Screen>(Screen.Splash) }
            var hasShownInitialStatus by remember { mutableStateOf(false) }
            var lastOnlineState by remember { mutableStateOf<Boolean?>(null) }

            LaunchedEffect(isOnline.value, currentScreen) {
                if (currentScreen !is Screen.Splash) {
                    if (!hasShownInitialStatus) {
                        hasShownInitialStatus = true
                        lastOnlineState = isOnline.value
                        scope.launch {
                            val message = if (isOnline.value) "Online!" else "You're offline. Some features may not work."
                            snackbarHostState.showSnackbar(message)
                        }
                    } else if (lastOnlineState != isOnline.value) {
                        lastOnlineState = isOnline.value
                        scope.launch {
                            val message = if (isOnline.value) "Online!" else "You're offline. Some features may not work."
                            snackbarHostState.showSnackbar(message)
                        }
                    }
                }
            }
            var toastMessage by remember { mutableStateOf<String?>(null) }
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
                snackbarHost = {
                    SnackbarHost(hostState = snackbarHostState) { data ->
                        val backgroundColor = when (data.visuals.message) {
                            "Online!" -> Color(0xFF4CAF50) // Green
                            "You're offline. Some features may not work." -> Color.Red
                            else -> Color.DarkGray
                        }

                        Snackbar(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            containerColor = backgroundColor
                        ) {
                            Text(text = data.visuals.message, color = Color.White)
                        }
                    }
                },
                bottomBar = {
                    if (currentScreen is Screen.Home || currentScreen is Screen.Settings || currentScreen is Screen.MealPlanGen || currentScreen is Screen.AddIngredients || currentScreen is Screen.StoreFinder) {
                        BottomNavBar(
                            currentScreen = currentScreen,
                            onTabSelected = { selected ->
                                if (selected is Screen.StoreFinder && !isOnline.value) {
                                    scope.launch {
                                        snackbarHostState.showSnackbar("You're offline. Some features may not work.")
                                    }
                                }
                                currentScreen = selected
                            },
                            onAddIngredient = { currentScreen = Screen.AddIngredients }
                        )
                    }
                }
            ) { innerPadding ->
                Crossfade(targetState = currentScreen, animationSpec = tween(500)) { screen ->
                    when (screen) {
                        is Screen.Splash -> {
                            val context = LocalContext.current
                            SplashScreenWithDelay {
                                val user = FirebaseAuth.getInstance().currentUser
                                if (user != null) {
                                    promptBiometricAuthentication(context) {
                                        currentScreen = Screen.Home
                                    }
                                } else {
                                    currentScreen = Screen.Login
                                }
                            }
                        }


                        is Screen.SignUp -> Box(Modifier.padding(bottom = innerPadding.calculateBottomPadding())) {
                            // 2FA dialog state for sign up
                            var show2FADialog by remember { mutableStateOf(false) }
                            var newUserId by remember { mutableStateOf("") }
                            var newUserEmail by remember { mutableStateOf("") }
                            val localContext = LocalContext.current
                            SignUp(
                                onContinueClicked = { email, password ->
                                    FirebaseAuth.getInstance()
                                        .createUserWithEmailAndPassword(email, password)
                                        .addOnCompleteListener { task ->
                                            if (task.isSuccessful) {
                                                val user = FirebaseAuth.getInstance().currentUser
                                                if (user != null) {
                                                    show2FADialog = true
                                                    newUserId = user.uid
                                                    newUserEmail = user.email ?: ""
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
                            if (show2FADialog) {
                                androidx.compose.material3.AlertDialog(
                                    onDismissRequest = {
                                        show2FADialog = false
                                        currentScreen = Screen.NameEntry(newUserId, newUserEmail)
                                    },
                                    title = { Text("Enable 2FA?") },
                                    text = { Text("Would you like to enable fingerprint 2-factor authentication?") },
                                    confirmButton = {
                                        androidx.compose.material3.TextButton(onClick = {
                                            show2FADialog = false
                                            promptBiometricAuthentication(localContext) {
                                                currentScreen = Screen.NameEntry(newUserId, newUserEmail)
                                            }
                                        }) {
                                            Text("Yes")
                                        }
                                    },
                                    dismissButton = {
                                        androidx.compose.material3.TextButton(onClick = {
                                            show2FADialog = false
                                            currentScreen = Screen.NameEntry(newUserId, newUserEmail)
                                        }) {
                                            Text("No")
                                        }
                                    }
                                )
                            }
                        }

                        is Screen.Login -> Box(Modifier.padding(bottom = innerPadding.calculateBottomPadding())) {
                            var show2FADialog by remember { mutableStateOf(false) }
                            val localContext = LocalContext.current
                            // Show dialog when login success
                            LoginScreen(
                                onLoginSuccess = {
                                    show2FADialog = true
                                },
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
                            if (show2FADialog) {
                                androidx.compose.material3.AlertDialog(
                                    onDismissRequest = { show2FADialog = false; currentScreen = Screen.Home },
                                    title = { Text("Enable 2FA?") },
                                    text = { Text("Would you like to enable fingerprint 2-factor authentication?") },
                                    confirmButton = {
                                        androidx.compose.material3.TextButton(onClick = {
                                            show2FADialog = false
                                            promptBiometricAuthentication(localContext) {
                                                currentScreen = Screen.Home
                                            }
                                        }) {
                                            Text("Yes")
                                        }
                                    },
                                    dismissButton = {
                                        androidx.compose.material3.TextButton(onClick = {
                                            show2FADialog = false
                                            currentScreen = Screen.Home
                                        }) {
                                            Text("No")
                                        }
                                    }
                                )
                            }
                        }

                        is Screen.NameEntry -> Box(Modifier.padding(bottom = innerPadding.calculateBottomPadding())) {
                            NameEntryScreen(
                                userId = screen.userId,
                                userEmail = screen.email,
                                onNameSubmitted = { currentScreen = Screen.Home }
                            )
                        }

                        is Screen.Home -> Box(Modifier.padding(bottom = innerPadding.calculateBottomPadding())) {
                            InventoryScreen(
                                currentScreen = currentScreen,
                                onTabSelected = { selected -> currentScreen = selected },
                                onAddIngredient = { currentScreen = Screen.AddIngredients },
                                onStorageClick = { selectedStorage ->
                                    currentScreen = Screen.StorageDetail(selectedStorage)
                                },
                                onCategoryClick = { selectedCategory ->
                                    currentScreen = Screen.CategoryDetail(selectedCategory)
                                }
                            )
                        }




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
                        is Screen.Profile -> Box(Modifier.padding(bottom = innerPadding.calculateBottomPadding())) {
                            EditProfileScreen(
                                onBackClicked = { currentScreen = Screen.Settings },
                                onSaveSuccess = { currentScreen = Screen.Settings }
                            )
                        }
                        is Screen.AddIngredients -> Box(Modifier.padding(bottom = innerPadding.calculateBottomPadding())) {
                            AddIngredientScreen(
                                onAddSuccess = { currentScreen = Screen.Home },
                                currentScreen = currentScreen,
                                onTabSelected = { selected -> currentScreen = selected }
                            )
                        }
                        is Screen.MealPlanGen -> Box(Modifier.padding(bottom = innerPadding.calculateBottomPadding())) {
                            MealPlanGenScreen(
                                onRecipeSelected = { recipe ->
                                    currentScreen = Screen.MealDetail(recipe)
                                },
                                onOfflineClick = {
                                    currentScreen = Screen.MealOffline
                                },
                                isOnline = isOnline.value,
                                snackbarHostState = snackbarHostState
                            )
                        }
                        is Screen.MealOffline -> Box(Modifier.padding(bottom = innerPadding.calculateBottomPadding())) {
                            MealOfflineScreen(
                                onRecipeSelected = { recipe ->
                                    currentScreen = Screen.MealDetail(recipe)
                                },
                                onBack = { currentScreen = Screen.MealPlanGen }
                            )
                        }
                        is Screen.MealDetail -> Box(Modifier.padding(bottom = innerPadding.calculateBottomPadding())) {
                            MealDetailScreen(
                                recipe = screen.recipe,
                                onBack = { currentScreen = Screen.MealPlanGen }
                            )
                        }
                        is Screen.ChangePassword -> Box(Modifier.padding(bottom = innerPadding.calculateBottomPadding())) {
                            ChangePasswordScreen(
                                onBackClicked = { currentScreen = Screen.Settings },
                                onChangePasswordClicked = { oldPass, newPass ->
                                }
                            )
                        }
                        is Screen.StorageDetail -> IngredientListScreen(
                            storageFilter = screen.storage,
                            onBack = { currentScreen = Screen.Home }
                        )

                        is Screen.CategoryDetail -> IngredientListByCategoryScreen(
                            category = screen.category,
                            onBack = { currentScreen = Screen.Home }
                        )
                        is Screen.StoreFinder -> Box(Modifier.padding(bottom = innerPadding.calculateBottomPadding())) {
                            StoreFinderScreen()
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

private fun promptBiometricAuthentication(
    context: Context,
    onSuccess: () -> Unit
) {
    val biometricManager = BiometricManager.from(context)

    if (biometricManager.canAuthenticate(BiometricManager.Authenticators.BIOMETRIC_STRONG) ==
        BiometricManager.BIOMETRIC_SUCCESS) {

        val executor = ContextCompat.getMainExecutor(context)
        val biometricPrompt = BiometricPrompt(context as FragmentActivity, executor,
            object : BiometricPrompt.AuthenticationCallback() {
                override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                    super.onAuthenticationSucceeded(result)
                    onSuccess()
                }

                override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                    super.onAuthenticationError(errorCode, errString)
                    (context as? FragmentActivity)?.finish()
                }
            })

        val promptInfo = BiometricPrompt.PromptInfo.Builder()
            .setTitle("Biometric login for Chef Up")
            .setSubtitle("Authenticate to access the app")
            .setNegativeButtonText("Cancel")
            .build()

        biometricPrompt.authenticate(promptInfo)
    } else {
        onSuccess()
    }
}