package com.example.mapsproject

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.location.Geocoder
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.mapsproject.ui.theme.MapsProjectTheme
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.utsman.osmandcompose.OpenStreetMap
import com.utsman.osmandcompose.rememberCameraState
import kotlinx.serialization.Serializable
import org.osmdroid.util.GeoPoint
import android.location.Location
import android.util.Log
import android.widget.FrameLayout
import android.widget.Toast
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.TextField
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.google.accompanist.permissions.rememberPermissionState
import com.utsman.osmandcompose.CameraState
import com.utsman.osmandcompose.OverlayManagerState
import com.utsman.osmandcompose.rememberOverlayManagerState
import kotlinx.coroutines.tasks.await
import java.io.IOException

@Serializable
object Main

@Serializable
object SimpleMap

private const val REQUEST_LOCATION_PERMISSION = 1001

class MainActivity : ComponentActivity() {

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var currLocation: GeoPoint


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

//        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
//        if (!hasLocationPermission(context = this)) {
//            requestLocationPermission(this)
//        }
//        else getLastLocation()


        enableEdgeToEdge()
        setContent {
            MapsProjectTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navHostController = rememberNavController()

                    MainGraph(navHostController)
                }
            }
        }
    }

    fun hasLocationPermission(context: Context): Boolean {
        return ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED
    }

    fun requestLocationPermission(activity: Activity) {
        ActivityCompat.requestPermissions(
            activity,
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ),
            REQUEST_LOCATION_PERMISSION
        )
    }

    /*override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String?>,
        grantResults: IntArray,
        deviceId: Int
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults, deviceId)
        when (requestCode) {
            REQUEST_LOCATION_PERMISSION -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Permission granted, proceed with location-related tasks
                    getLastLocation()
                } else {
                    Toast.makeText(this, "Failed to get location", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }*/

    @SuppressLint("MissingPermission")
    fun getLastLocation() {
        fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
            if (location != null) {
                currLocation.latitude = location.latitude
                currLocation.longitude = location.longitude
            } else {

                //fusedLocationClient.getCu
            }
        }
    }
}

@SuppressLint("MissingPermission")
@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun MainGraph(
    navController: NavHostController
) {
    val context = LocalContext.current
    val locationPermissionState = rememberMultiplePermissionsState(
        listOf(
            android.Manifest.permission.ACCESS_COARSE_LOCATION,
            android.Manifest.permission.ACCESS_FINE_LOCATION
        )
    )
    lateinit var geoPoint: GeoPoint
    var getGeoPoint = false

    val lastLocation = produceState<Location?>(initialValue = null) {
        if (locationPermissionState.allPermissionsGranted) {
            val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
            fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
                value = location
            }
        } else {
            // Handle the case where permissions are not granted
            // You might want to request permissions here or show a rationale
            locationPermissionState.launchMultiplePermissionRequest()

        }
    }.value

    if (lastLocation != null) {
        getGeoPoint = true
        geoPoint = GeoPoint(lastLocation.latitude, lastLocation.longitude)
    }

    NavHost(navController = navController, startDestination = Main) {
        composable<Main> {
            MainScreen(
                arrayOf({
                    navController.navigate(route = SimpleMap)
                })
            )
        }

        composable<SimpleMap> {
            if (getGeoPoint) {
                SimpleMapScreen(geoPoint)
            } else SimpleMapScreen()
        }
    }
}

@Composable
fun MainScreen(navigations: Array<() -> Unit>) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        for (arg in navigations) {
            MapButton(
                name = "Simple Map with Marker",
                navigateAway = arg
            )
        }
//        MapButton(
//            name = "Simple Map with Marker"
//        )
//        MapButton(
//            name = "Simple Map with Marker"
//        )
    }
}


@Composable
fun MapButton(name: String, navigateAway: () -> Unit, modifier: Modifier = Modifier) {

    Button(
        onClick = { navigateAway() }
    ) {
        Text(name)
    }
}

@Composable
fun SimpleMapScreen(location: GeoPoint = GeoPoint(33.7501, -84.3885)) {
    // define camera state
    val cameraState = rememberCameraState {
        geoPoint = location
        zoom = 18.0 // optional, default is 5.0
    }

    var text by remember { mutableStateOf("") }
    var changeText = { newText: String -> text = newText }

    val context = LocalContext.current
    val geocoder = Geocoder(context)

    // add node
    //StatelessSimpleMapScreen(text, cameraState, changeText)

    Box() {
        OpenStreetMap(
            modifier = Modifier.fillMaxSize(),
            cameraState = cameraState,
        )
        Row() {
            TextField(
                value = text, // Current text value
                onValueChange = { newText -> text = newText }, // Update state on change
                label = { Text("Enter your text") } // Optional label
            )
            Button(
                onClick = {
                    try {
                        val addresses = geocoder.getFromLocationName(text, 1)
                        // Process the list of Address objects
                        if (!addresses.isNullOrEmpty()){
                            cameraState.geoPoint = GeoPoint(addresses[0].latitude, addresses[0].longitude)
                        }
                    } catch (e: IOException) {
                        // Handle network or other errors
                        e.printStackTrace()
                    }
                    text = ""
                }
            ) {
                Text("Go!")
            }
        }
    }
}

@Composable
fun StatelessSimpleMapScreen(text: String, cameraState: CameraState, changeText: (String) -> Unit) {
    Box() {
        OpenStreetMap(
            modifier = Modifier.fillMaxSize(),
            cameraState = cameraState,
        )
        Row() {
            TextField(
                value = text, // Current text value
                onValueChange = { changeText }, // Update state on change
                label = { Text("Enter your text") } // Optional label
            )
            Button(
                onClick = {}
            ) {
                Text("Go!")
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun MapsPreview() {
    MapsProjectTheme { SimpleMapScreen() }
}



