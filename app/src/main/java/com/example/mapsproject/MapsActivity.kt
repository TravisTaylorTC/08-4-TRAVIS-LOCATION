package com.example.mapsproject

import android.os.Bundle
import android.util.AttributeSet
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.example.mapsproject.ui.theme.MapsProjectTheme
import com.utsman.osmandcompose.OpenStreetMap
import com.utsman.osmandcompose.rememberCameraState
import org.osmdroid.tileprovider.constants.OpenStreetMapTileProviderConstants
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView

class MapsActivity : ComponentActivity() {



    override fun onCreate(savedInstanceState: Bundle?){
        super.onCreate(savedInstanceState)

        setContent{
            MapsProjectTheme(){
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ){
                    // define camera state
                    val cameraState = rememberCameraState {
                        geoPoint = GeoPoint(-6.3970066, 106.8224316)
                        zoom = 12.0 // optional, default is 5.0
                    }

                    // add node
                    OpenStreetMap(
                        modifier = Modifier.fillMaxSize(),
                        cameraState = cameraState
                    )
                }
            }
        }
    }
}