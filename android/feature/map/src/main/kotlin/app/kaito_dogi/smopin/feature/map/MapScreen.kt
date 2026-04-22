package app.kaito_dogi.smopin.feature.map

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import app.kaito_dogi.smopin.feature.map.ext.toLatLng
import com.google.android.gms.maps.model.CameraPosition
import com.google.maps.android.compose.CameraPositionState
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.rememberCameraPositionState
import com.google.maps.android.compose.rememberMarkerState
import dev.zacsweers.metrox.viewmodel.metroViewModel

@Composable
fun MapEntry() {
  MapScreen()
}

@Composable
internal fun MapScreen(
  modifier: Modifier = Modifier,
  viewModel: MapViewModel = metroViewModel(),
) {
  val context = LocalContext.current
  val uiState by viewModel.uiState.collectAsStateWithLifecycle()
  val cameraPositionState = rememberCameraPositionState()
  val locationPermissionLauncher = rememberLauncherForActivityResult(
    contract = ActivityResultContracts.RequestMultiplePermissions(),
  ) { grantResultMap ->
    val isPreciseGranted: Boolean = grantResultMap[Manifest.permission.ACCESS_FINE_LOCATION] == true
    val isCoarseGranted: Boolean = grantResultMap[Manifest.permission.ACCESS_COARSE_LOCATION] == true
    viewModel.onPermissionResult(
      isGranted = isPreciseGranted || isCoarseGranted,
      isPrecise = isPreciseGranted,
    )
  }

  LaunchedEffect(key1 = Unit) {
    viewModel.onCreate()
  }

  LaunchedEffect(key1 = uiState.uiState, key2 = uiState.shouldRequestPermission) {
    if (uiState.uiState !is MapUiState.UiState.PermissionRequested) return@LaunchedEffect

    val isPreciseGranted: Boolean = ContextCompat.checkSelfPermission(
      context,
      Manifest.permission.ACCESS_FINE_LOCATION,
    ) == PackageManager.PERMISSION_GRANTED
    val isCoarseGranted: Boolean = ContextCompat.checkSelfPermission(
      context,
      Manifest.permission.ACCESS_COARSE_LOCATION,
    ) == PackageManager.PERMISSION_GRANTED

    if (isPreciseGranted || isCoarseGranted) {
      viewModel.onPermissionResult(
        isGranted = true,
        isPrecise = isPreciseGranted,
      )
      return@LaunchedEffect
    }

    if (uiState.shouldRequestPermission) {
      viewModel.onPermissionRequested()
      locationPermissionLauncher.launch(
        arrayOf(
          Manifest.permission.ACCESS_FINE_LOCATION,
          Manifest.permission.ACCESS_COARSE_LOCATION,
        ),
      )
    } else {
      viewModel.onPermissionResult(
        isGranted = false,
        isPrecise = false,
      )
    }
  }

  uiState.currentLocation?.let { currentLocation ->
    LaunchedEffect(key1 = currentLocation) {
      cameraPositionState.position = CameraPosition.fromLatLngZoom(currentLocation.toLatLng(), 17f)
    }
  }

  MapScreen(
    uiState = uiState,
    cameraPositionState = cameraPositionState,
    onMapLoaded = viewModel::onMapLoaded,
    modifier = modifier,
  )
}

@Composable
private fun MapScreen(
  uiState: MapUiState,
  cameraPositionState: CameraPositionState,
  onMapLoaded: () -> Unit,
  modifier: Modifier = Modifier,
) = Scaffold(
  modifier = modifier,
) { innerPadding ->
  if (!uiState.uiState.isMapShown) return@Scaffold

  GoogleMap(
    cameraPositionState = cameraPositionState,
    properties = MapProperties(isMyLocationEnabled = uiState.uiState is MapUiState.UiState.PermissionGranted),
    onMapLoaded = onMapLoaded,
    contentPadding = innerPadding,
  ) {
    uiState.smokingAreaList.forEach { smokingArea ->
      Marker(
        state = rememberMarkerState(
          key = smokingArea.name,
          position = smokingArea.location.toLatLng(),
        ),
        title = smokingArea.name,
      )
    }
  }
}

@Preview
@Composable
private fun MapScreenPreview() {
  MaterialTheme {
    MapScreen(
      uiState = MapUiState.createInitial(),
      cameraPositionState = rememberCameraPositionState(),
      onMapLoaded = {},
    )
  }
}
