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
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
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

  LaunchedEffect(key1 = Unit) {
    viewModel.onCreate()
  }

  MapScreen(
    uiState = uiState,
    onMapLoaded = viewModel::onMapLoaded,
    modifier = modifier,
  )
}

@Composable
private fun MapScreen(
  uiState: MapUiState,
  onMapLoaded: () -> Unit,
  modifier: Modifier = Modifier,
) = Scaffold(
  modifier = modifier,
) { innerPadding ->
  GoogleMap(
    onMapLoaded = onMapLoaded,
    contentPadding = innerPadding,
  ) {
    uiState.smokingAreaList.map { smokingArea ->
      Marker(
        state = rememberMarkerState(position = smokingArea.location.toLatLng()),
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
      onMapLoaded = {},
    )
  }
}
