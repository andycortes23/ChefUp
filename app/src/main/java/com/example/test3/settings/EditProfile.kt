package com.example.test3.settings

import android.content.Intent
import android.net.Uri
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.OutlinedTextField
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import com.example.test3.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditProfileScreen(
    onBackClicked: () -> Unit,
    onSaveSuccess: () -> Unit
) {
    val context = LocalContext.current
    val uid = FirebaseAuth.getInstance().currentUser?.uid ?: return
    val userId = uid
    val scope = rememberCoroutineScope()

    var firstName by remember { mutableStateOf(TextFieldValue("")) }
    var lastName by remember { mutableStateOf(TextFieldValue("")) }
    var error by remember { mutableStateOf<String?>(null) }

    // State for new, unsaved image
    var imageUri by remember { mutableStateOf<Uri?>(null) }

    // Load previously saved image
    val savedImageUri by ProfileImageStore.getProfileImageUri(context, userId).collectAsState(initial = null)

    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        if (uri != null) {
            try {
                context.contentResolver.takePersistableUriPermission(
                    uri,
                    Intent.FLAG_GRANT_READ_URI_PERMISSION
                )
                imageUri = uri // ✅ Only save locally on "Save"
            } catch (e: SecurityException) {
                Log.e("EditProfile", "Permission error: ${e.message}")
            }
        }
    }

    val snackbarHostState = remember { SnackbarHostState() }
    var showSuccessSnackbar by remember { mutableStateOf(false) }

    // Load Firestore name
    LaunchedEffect(uid) {
        val doc = FirebaseFirestore.getInstance().collection("users").document(uid).get().await()
        firstName = TextFieldValue(doc.getString("firstName") ?: "")
        lastName = TextFieldValue(doc.getString("lastName") ?: "")
    }

    // Show snackbar on success
    LaunchedEffect(showSuccessSnackbar) {
        if (showSuccessSnackbar) {
            snackbarHostState.showSnackbar("Profile updated!")
            delay(1500)
            onSaveSuccess()
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
                .padding(padding)
                .padding(24.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            // Back button
            Row(modifier = Modifier.fillMaxWidth()) {
                IconButton(onClick = onBackClicked) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Determine image to show
            val displayUri = imageUri ?: savedImageUri?.let { Uri.parse(it) }

            // Profile Image
            Box(
                modifier = Modifier
                    .size(100.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFDFFFD6))
                    .clickable { imagePickerLauncher.launch("image/*") },
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = displayUri?.let { rememberAsyncImagePainter(it) }
                        ?: rememberAsyncImagePainter(R.drawable.profile_placeholder),
                    contentDescription = "Profile Picture",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text("Edit your profile", fontSize = 16.sp)
            Spacer(modifier = Modifier.height(24.dp))

            OutlinedTextField(
                value = firstName,
                onValueChange = { firstName = it },
                label = { Text("First Name") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = lastName,
                onValueChange = { lastName = it },
                label = { Text("Last Name") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            error?.let {
                Text(text = it, color = Color.Red, fontSize = 12.sp)
                Spacer(modifier = Modifier.height(8.dp))
            }

            Button(
                onClick = {
                    if (firstName.text.isBlank() || lastName.text.isBlank()) {
                        error = "Please fill in both fields"
                        return@Button
                    }

                    FirebaseFirestore.getInstance().collection("users")
                        .document(uid)
                        .update(
                            mapOf(
                                "firstName" to firstName.text.trim(),
                                "lastName" to lastName.text.trim()
                            )
                        )
                        .addOnSuccessListener {
                            scope.launch {
                                // ✅ Save image only if one was picked
                                if (imageUri != null) {
                                    ProfileImageStore.saveProfileImageUri(
                                        context,
                                        userId,
                                        imageUri.toString()
                                    )
                                }
                                showSuccessSnackbar = true
                            }
                        }
                        .addOnFailureListener {
                            error = "Failed to update: ${it.localizedMessage}"
                        }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color.Black),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text("Save Changes", color = Color.White)
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}
