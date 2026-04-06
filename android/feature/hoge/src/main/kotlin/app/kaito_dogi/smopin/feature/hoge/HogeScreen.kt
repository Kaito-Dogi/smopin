package app.kaito_dogi.smopin.feature.hoge

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.LocationManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import app.kaito_dogi.smopin.shared.domain.smokingArea.Latitude
import app.kaito_dogi.smopin.shared.domain.smokingArea.Location
import app.kaito_dogi.smopin.shared.domain.smokingArea.Longitude
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.rememberCameraPositionState
import com.google.maps.android.compose.rememberMarkerState
import dev.zacsweers.metrox.viewmodel.metroViewModel

@Composable
fun HogeScreen(
  modifier: Modifier = Modifier,
  viewModel: HogeViewModel = metroViewModel(),
) {
  val context = LocalContext.current
  val uiState: HogeUiState by viewModel.uiState.collectAsStateWithLifecycle()

  val permissionLauncher = rememberLauncherForActivityResult(
    contract = ActivityResultContracts.RequestMultiplePermissions(),
  ) { resultMap ->
    val isGranted = resultMap.values.any { it }
    if (isGranted) {
      context.getCurrentLocation()?.let(viewModel::onCurrentLocationUpdate)
    }
  }

  LaunchedEffect(key1 = Unit) {
    viewModel.onCreate()
    if (context.isLocationPermissionGranted()) {
      context.getCurrentLocation()?.let(viewModel::onCurrentLocationUpdate)
    }
  }

  Scaffold(
    modifier = modifier,
  ) { innerPadding ->
    Column(
      modifier = Modifier.padding(paddingValues = innerPadding),
    ) {
      Button(
        modifier = Modifier.fillMaxWidth(),
        onClick = {
          permissionLauncher.launch(
            arrayOf(
              Manifest.permission.ACCESS_COARSE_LOCATION,
              Manifest.permission.ACCESS_FINE_LOCATION,
            ),
          )
        },
      ) {
        Text(text = "現在地を取得")
      }

      uiState.currentLocation?.let { currentLocation ->
        Text(text = "current latitude: ${currentLocation.latitude.value}")
        Text(text = "current longitude: ${currentLocation.longitude.value}")
      }

      uiState.smokingAreaList.forEach { smokingArea ->
        Text(text = smokingArea.name)
        Text(text = "latitude: ${smokingArea.location.latitude.value}")
        Text(text = "longitude: ${smokingArea.location.longitude.value}")
      }

      val initialPosition = remember(uiState.currentLocation, uiState.smokingAreaList) {
        uiState.currentLocation?.toLatLng()
          ?: uiState.smokingAreaList.getOrNull(index = 0)?.location?.toLatLng()
      }
      if (initialPosition != null) {
        val cameraPositionState = rememberCameraPositionState {
          position = CameraPosition.fromLatLngZoom(initialPosition, 17f)
        }
        val smokingArea = uiState.smokingAreaList.getOrNull(index = 0)
        val smokingAreaMakerState = rememberMarkerState(
          position = smokingArea?.location?.toLatLng() ?: initialPosition,
        )
        val currentLocationMarkerState = rememberMarkerState(position = initialPosition)

        LaunchedEffect(initialPosition) {
          cameraPositionState.position = CameraPosition.fromLatLngZoom(initialPosition, 17f)
        }

        GoogleMap(
          modifier = Modifier.weight(weight = 1f),
          cameraPositionState = cameraPositionState,
        ) {
          smokingArea?.let {
            Marker(
              state = smokingAreaMakerState,
              title = it.name,
              snippet = it.name,
            )
          }
          uiState.currentLocation?.let {
            Marker(
              state = currentLocationMarkerState,
              title = "現在地",
              snippet = "現在地",
            )
          }
        }
      }
    }
  }
}

private fun Context.getCurrentLocation(): Location? {
  val locationManager = getSystemService(Context.LOCATION_SERVICE) as? LocationManager ?: return null

  return locationManager.getLastKnownLocation(LocationManager.FUSED_PROVIDER)
    ?.toDomainModel()
    ?: locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
      ?.toDomainModel()
    ?: locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)
      ?.toDomainModel()
}

private fun Context.isLocationPermissionGranted(): Boolean {
  val fineLocationPermission =
    ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
  val coarseLocationPermission =
    ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
  return fineLocationPermission == PackageManager.PERMISSION_GRANTED
    || coarseLocationPermission == PackageManager.PERMISSION_GRANTED
}

private fun Location.toLatLng(): LatLng = LatLng(latitude.value, longitude.value)

private fun android.location.Location.toDomainModel(): Location = Location(
  latitude = Latitude(value = latitude),
  longitude = Longitude(value = longitude),
)
