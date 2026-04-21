package app.kaito_dogi.smopin.feature.map

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
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
  val uiState by viewModel.uiState.collectAsStateWithLifecycle()
  val cameraPositionState = rememberCameraPositionState()

  LaunchedEffect(key1 = Unit) {
    viewModel.onCreate()
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
  // TODO: 位置情報の許可状況に応じて MapProperties の isMyLocationEnabled を切り替える
  GoogleMap(
    cameraPositionState = cameraPositionState,
    properties = MapProperties(isMyLocationEnabled = true),
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
