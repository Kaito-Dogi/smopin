package app.kaito_dogi.smopin.feature.map

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import app.kaito_dogi.smopin.feature.map.ext.toLatLng
import app.kaito_dogi.smopin.feature.map.permission.RequestLocationPermissionEffect
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.CameraPositionState
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.rememberCameraPositionState
import com.google.maps.android.compose.rememberMarkerState
import dev.zacsweers.metrox.viewmodel.metroViewModel

private val DEFAULT_CAMERA_POSITION_TARGET = LatLng(35.6905, 139.6995)
private const val DEFAULT_CAMERA_POSITION_ZOOM = 17f

@Composable
internal fun MapScreen(
  modifier: Modifier = Modifier,
  viewModel: MapViewModel = metroViewModel(),
) {
  val uiState by viewModel.uiState.collectAsStateWithLifecycle()
  val cameraPositionState = rememberCameraPositionState {
    position = CameraPosition.fromLatLngZoom(
      DEFAULT_CAMERA_POSITION_TARGET,
      DEFAULT_CAMERA_POSITION_ZOOM,
    )
  }

  LaunchedEffect(key1 = Unit) {
    viewModel.onCreate()
  }

  RequestLocationPermissionEffect(
    uiState = uiState,
    onLocationPermissionGranted = viewModel::onLocationPermissionGranted,
    onLocationPermissionDenied = viewModel::onLocationPermissionDenied,
  )

  val currentUiState = uiState
  if (currentUiState is MapUiState.PermissionGranted.LocationSuccess && !currentUiState.hasCameraPositionAdjustedToCurrentLocation) {
    // 一度だけ実行するため、key に Unit を渡す
    LaunchedEffect(key1 = Unit) {
      cameraPositionState.position = CameraPosition.fromLatLngZoom(
        currentUiState.currentLocation.toLatLng(),
        DEFAULT_CAMERA_POSITION_ZOOM,
      )

      viewModel.onCameraPositionAdjustedToCurrentLocation()
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
  modifier = modifier.fillMaxSize(),
) { innerPadding ->
  GoogleMap(
    cameraPositionState = cameraPositionState,
    properties = MapProperties(isMyLocationEnabled = uiState is MapUiState.PermissionGranted),
    onMapLoaded = onMapLoaded,
    contentPadding = innerPadding,
  ) {
    uiState.smokingAreaList.forEach { smokingArea ->
      // TODO: key の指定を考える
      Marker(
        state = rememberMarkerState(
          key = smokingArea.name,
          position = smokingArea.location.toLatLng(),
        ),
        title = smokingArea.name,
      )
    }
  }

  if (!uiState.isMapLoaded) {
    Box(
      modifier = Modifier.fillMaxSize(),
      contentAlignment = Alignment.Center,
    ) {
      CircularProgressIndicator()
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
