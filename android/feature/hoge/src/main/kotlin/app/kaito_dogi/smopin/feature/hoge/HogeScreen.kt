package app.kaito_dogi.smopin.feature.hoge

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import app.kaito_dogi.smopin.shared.domain.smokingArea.Location
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
  val uiState: HogeUiState by viewModel.uiState.collectAsStateWithLifecycle()

  LaunchedEffect(key1 = Unit) {
    viewModel.onCreate()
  }

  Scaffold(
    modifier = modifier,
  ) { innerPadding ->
    Column(
      modifier = Modifier.padding(paddingValues = innerPadding),
    ) {
      uiState.smokingAreaList.forEach { smokingArea ->
        Text(text = smokingArea.name)
        Text(text = "latitude: ${smokingArea.location.latitude.value}")
        Text(text = "longitude: ${smokingArea.location.longitude.value}")
      }

      val initialSmokingArea = uiState.smokingAreaList.getOrNull(index = 0)
      if (initialSmokingArea != null) {
        val initialPosition = remember(key1 = initialSmokingArea) { initialSmokingArea.location.toLatLng() }
        val smokingAreaMakerState = rememberMarkerState(position = initialPosition)
        val cameraPositionState = rememberCameraPositionState {
          position = CameraPosition.fromLatLngZoom(initialPosition, 10f)
        }
        GoogleMap(
          modifier = Modifier.weight(weight = 1f),
          cameraPositionState = cameraPositionState,
        ) {
          Marker(
            state = smokingAreaMakerState,
            title = initialSmokingArea.name,
            snippet = initialSmokingArea.name,
          )
        }
      }
    }
  }
}

fun Location.toLatLng() = LatLng(latitude.value, longitude.value)
