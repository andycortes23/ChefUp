package com.example.test3

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Geocoder
import android.os.Handler
import android.os.Looper
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SmallTopAppBar
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.google.android.gms.location.LocationServices
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.HttpUrl
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.Call
import okhttp3.Callback
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException
import java.util.Locale

private const val PLACES_API_KEY = "AIzaSyBPMpgZnvRwyiD47P-togXkkGLLAbJ64Jo"

private enum class EntryMode { Choose, UseDevice, Manual }

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StoreFinderScreen() {
    val context = LocalContext.current
    val scope   = rememberCoroutineScope()

    var entryMode             by remember { mutableStateOf(EntryMode.Choose) }
    var hasLocationPermission by remember { mutableStateOf(false) }
    var isLoading             by remember { mutableStateOf(false) }
    var manualInput           by remember { mutableStateOf("") }
    val storeList             = remember { mutableStateListOf<String>() }

    // Pill-shaped black buttons with white text
    val pillShape  = RoundedCornerShape(24.dp)
    val pillColors = ButtonDefaults.buttonColors(
        containerColor = Color.Black,
        contentColor   = Color.White
    )

    // Runtime permission launcher
    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        hasLocationPermission = granted
        if (granted) {
            entryMode = EntryMode.UseDevice
        } else {
            Toast.makeText(context, "Location permission denied", Toast.LENGTH_SHORT).show()
        }
    }

    // Header background
    val headerBg = Color(0xFFD4FF99)

    Scaffold(
        topBar = {
            SmallTopAppBar(
                title = {
                    Text(
                        "Find Grocery Stores",
                        style = MaterialTheme.typography.titleLarge
                            .copy(fontWeight = FontWeight.Bold)
                    )
                },
                colors = TopAppBarDefaults.smallTopAppBarColors(
                    containerColor = headerBg
                )
            )
        }
    ) { innerPadding ->
        Column(
            Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
        ) {
            when (entryMode) {
                EntryMode.Choose -> Column(
                    modifier            = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Button(
                        onClick = {
                            if (ContextCompat.checkSelfPermission(
                                    context, Manifest.permission.ACCESS_FINE_LOCATION
                                ) == PackageManager.PERMISSION_GRANTED
                            ) {
                                hasLocationPermission = true
                                entryMode = EntryMode.UseDevice
                            } else {
                                permissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp),
                        shape  = pillShape,
                        colors = pillColors
                    ) {
                        Text("Use My Location")
                    }

                    Spacer(Modifier.height(16.dp))

                    Button(
                        onClick = { entryMode = EntryMode.Manual },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp),
                        shape  = pillShape,
                        colors = pillColors
                    ) {
                        Text("Enter Location Manually")
                    }
                }

                EntryMode.UseDevice -> {
                    if (!hasLocationPermission) {
                        Text("Waiting for location permission…")
                    } else {
                        LaunchedEffect(Unit) {
                            isLoading = true
                            val fused = LocationServices.getFusedLocationProviderClient(context)
                            fused.lastLocation
                                .addOnSuccessListener { loc ->
                                    if (loc != null) {
                                        fetchNearbyStoresByCoordinates(
                                            context,
                                            loc.latitude,
                                            loc.longitude,
                                            storeList
                                        ) { isLoading = false }
                                    } else {
                                        Toast.makeText(
                                            context,
                                            "Could not get last known location",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                        isLoading = false
                                    }
                                }
                                .addOnFailureListener { e ->
                                    Toast.makeText(
                                        context,
                                        "Location lookup failed: ${e.localizedMessage}",
                                        Toast.LENGTH_LONG
                                    ).show()
                                    isLoading = false
                                }
                        }

                        if (isLoading) {
                            Text("Locating stores around you…")
                        } else {
                            Column(
                                modifier            = Modifier
                                    .fillMaxWidth()
                                    .fillMaxHeight(),  // constrain height
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text("Nearby Grocery Stores:", Modifier.padding(bottom = 8.dp))

                                LazyColumn(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .weight(1f),   // use leftover space
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    items(storeList) { line ->
                                        Text(line, Modifier.padding(vertical = 4.dp))
                                    }
                                }

                                Spacer(Modifier.height(16.dp))

                                Button(
                                    onClick = {
                                        entryMode = EntryMode.Choose
                                        storeList.clear()
                                    },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(48.dp),
                                    shape  = pillShape,
                                    colors = pillColors
                                ) {
                                    Text("Back")
                                }
                            }
                        }
                    }
                }

                EntryMode.Manual -> Column(
                    modifier            = Modifier
                        .fillMaxWidth()
                        .fillMaxHeight(),      // constrain height
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    OutlinedTextField(
                        value         = manualInput,
                        onValueChange = { manualInput = it },
                        label         = { Text("Enter your location") },
                        singleLine    = true,
                        modifier      = Modifier.fillMaxWidth()
                    )

                    Spacer(Modifier.height(8.dp))

                    Button(
                        onClick = {
                            storeList.clear()
                            isLoading = true
                            scope.launch {
                                val geocoder = Geocoder(context, Locale.getDefault())
                                val addresses = withContext(Dispatchers.IO) {
                                    try {
                                        geocoder.getFromLocationName(manualInput.trim(), 1)
                                    } catch (e: IOException) {
                                        emptyList()
                                    }
                                }
                                if (addresses.isNullOrEmpty()) {
                                    Toast.makeText(
                                        context,
                                        "Could not find: $manualInput",
                                        Toast.LENGTH_LONG
                                    ).show()
                                    isLoading = false
                                } else {
                                    fetchNearbyStoresByCoordinates(
                                        context,
                                        addresses[0].latitude,
                                        addresses[0].longitude,
                                        storeList
                                    ) { isLoading = false }
                                }
                            }
                        },
                        enabled  = manualInput.isNotBlank(),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp),
                        shape  = pillShape,
                        colors = pillColors
                    ) {
                        Text("Search")
                    }

                    Spacer(Modifier.height(16.dp))

                    if (isLoading) {
                        Text("Searching…")
                    } else {
                        Text("Results:", Modifier.padding(bottom = 8.dp))

                        LazyColumn(
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(1f)  // use leftover space
                        ) {
                            items(storeList) { line ->
                                Text(
                                    line,
                                    Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 4.dp)
                                )
                            }
                        }
                    }

                    Spacer(Modifier.height(16.dp))

                    Button(
                        onClick = {
                            entryMode   = EntryMode.Choose
                            manualInput = ""
                            storeList.clear()
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp),
                        shape  = pillShape,
                        colors = pillColors
                    ) {
                        Text("Back")
                    }
                }
            }
        }
    }
}

// ─── HELPER ─────────────────────────────────────────────────
private fun fetchNearbyStoresByCoordinates(
    context: Context,
    lat: Double,
    lng: Double,
    output: MutableList<String>,
    onFinished: () -> Unit = {}
) {
    val url = HttpUrl.Builder()
        .scheme("https")
        .host("maps.googleapis.com")
        .addPathSegments("maps/api/place/nearbysearch/json")
        .addQueryParameter("location", "$lat,$lng")
        .addQueryParameter("radius", "5000")
        .addQueryParameter("type", "supermarket")
        .addQueryParameter("key", PLACES_API_KEY)
        .build()

    OkHttpClient()
        .newCall(Request.Builder().url(url).build())
        .enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                Handler(Looper.getMainLooper()).post {
                    Toast.makeText(context, "Fetch error: ${e.message}", Toast.LENGTH_LONG).show()
                    onFinished()
                }
            }
            override fun onResponse(call: Call, response: Response) {
                val list = mutableListOf<String>()
                val body = response.body?.string() ?: ""
                try {
                    val results = JSONObject(body).getJSONArray("results")
                    for (i in 0 until results.length()) {
                        val obj  = results.getJSONObject(i)
                        val name = obj.optString("name")
                        val addr = obj.optString("vicinity")
                        if (name.isNotBlank()) {
                            list.add(if (addr.isNotBlank()) "$name — $addr" else name)
                        }
                    }
                    if (list.isEmpty()) list.add("No supermarkets found.")
                } catch (e: JSONException) {
                    list.clear()
                    list.add("Parse error")
                }
                Handler(Looper.getMainLooper()).post {
                    output.clear()
                    output.addAll(list)
                    onFinished()
                }
            }
        })
}
