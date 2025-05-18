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
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.HttpUrl
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Callback
import okhttp3.Call
import okhttp3.Response
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException
import java.util.Locale


private const val PLACES_API_KEY = "AIzaSyBPMpgZnvRwyiD47P-togXkkGLLAbJ64Jo"


private enum class EntryMode { Choose, UseDevice, Manual }


@Composable
fun StoreFinderScreen() {
    val context = LocalContext.current


    var entryMode             by remember { mutableStateOf(EntryMode.Choose) }
    var hasLocationPermission by remember { mutableStateOf(false) }
    var isLoading             by remember { mutableStateOf(false) }
    var manualInput           by remember { mutableStateOf("") }
    var manualSearchTrigger   by remember { mutableStateOf("") }
    val storeList             = remember { mutableStateListOf<String>() }


    // 1️⃣ Permission launcher
    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        hasLocationPermission = granted
        if (!granted) {
            Toast.makeText(context, "Location permission denied", Toast.LENGTH_SHORT).show()
        }
    }


    Box(
        Modifier
            .fillMaxSize()
            .padding(16.dp),
        contentAlignment = Alignment.TopCenter
    ) {
        when(entryMode) {
            // ─── CHOOSE ───────────────────────
            EntryMode.Choose -> Column(Modifier.fillMaxWidth()) {
                Button(
                    onClick  = { entryMode = EntryMode.UseDevice },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Use My Location")
                }
                Spacer(Modifier.height(16.dp))
                Button(
                    onClick  = { entryMode = EntryMode.Manual },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Enter Location Manually")
                }
            }


            // ─── DEVICE ───────────────────────
            EntryMode.UseDevice -> {
                if (!hasLocationPermission) {
                    permissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
                }
                LaunchedEffect(hasLocationPermission) {
                    if (hasLocationPermission) {
                        isLoading = true
                        val fused = LocationServices
                            .getFusedLocationProviderClient(context)
                        if (ContextCompat.checkSelfPermission(
                                context, Manifest.permission.ACCESS_FINE_LOCATION
                            ) == PackageManager.PERMISSION_GRANTED
                        ) {
                            fused.getCurrentLocation(
                                Priority.PRIORITY_HIGH_ACCURACY,
                                null
                            )
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
                                            "Could not get location",
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
                    }
                }


                if (isLoading) {
                    Text("Locating stores around you…")
                } else {
                    // ← wrap header + list in a Column!
                    Column(
                        Modifier
                            .fillMaxSize()
                    ) {
                        Text(
                            "Nearby Grocery Stores:",
                            Modifier.padding(bottom = 8.dp)
                        )
                        LazyColumn(
                            Modifier.fillMaxSize()
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
                }
            }


            // ─── MANUAL ───────────────────────
            EntryMode.Manual -> Column(Modifier.fillMaxWidth()) {
                OutlinedTextField(
                    value         = manualInput,
                    onValueChange = { manualInput = it },
                    label         = { Text("Enter city, state or address") },
                    singleLine    = true,
                    modifier      = Modifier.fillMaxWidth()
                )
                Spacer(Modifier.height(8.dp))
                Button(
                    onClick   = { manualSearchTrigger = manualInput.trim() },
                    enabled   = manualInput.isNotBlank(),
                    modifier  = Modifier.fillMaxWidth()
                ) {
                    Text("Search")
                }
                Spacer(Modifier.height(16.dp))
                Text("Results:", Modifier.padding(bottom = 8.dp))
                LazyColumn {
                    items(storeList) { line ->
                        Text(line, Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp))
                    }
                }
            }
        }
    }


    // ─── GEOCODE + FETCH ON MANUAL ──────────────────────────────
    LaunchedEffect(manualSearchTrigger) {
        if (entryMode == EntryMode.Manual && manualSearchTrigger.isNotBlank()) {
            val geocoder = Geocoder(context, Locale.getDefault())
            val addresses = withContext(Dispatchers.IO) {
                try {
                    geocoder.getFromLocationName(manualSearchTrigger, 1)
                } catch (e: IOException) {
                    emptyList()
                }
            }
            if (addresses.isNullOrEmpty()) {
                Toast
                    .makeText(context, "Could not find: $manualSearchTrigger", Toast.LENGTH_LONG)
                    .show()
            } else {
                fetchNearbyStoresByCoordinates(
                    context,
                    addresses[0].latitude,
                    addresses[0].longitude,
                    storeList
                ) { /* no-op */ }
            }
        }
    }
}


// ─── HELPER ────────────────────────────────────────────────────
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
        .addQueryParameter("location",  "$lat,$lng")
        .addQueryParameter("radius",    "5000")
        .addQueryParameter("type",      "supermarket")
        .addQueryParameter("key",       PLACES_API_KEY)
        .build()


    OkHttpClient()
        .newCall(Request.Builder().url(url).build())
        .enqueue(object: Callback {
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
                    for(i in 0 until results.length()) {
                        val obj     = results.getJSONObject(i)
                        val name    = obj.optString("name")
                        val address = obj.optString("vicinity")
                        if (name.isNotBlank()) {
                            list.add(if (address.isNotBlank()) "$name — $address" else name)
                        }
                    }
                    if (list.isEmpty()) list.add("No supermarkets found.")
                } catch(e: JSONException) {
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
