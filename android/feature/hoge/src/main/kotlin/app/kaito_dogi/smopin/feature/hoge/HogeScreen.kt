package app.kaito_dogi.smopin.feature.hoge

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.LifecycleResumeEffect
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import app.kaito_dogi.smopin.shared.domain.smokingArea.SmokingArea
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.CameraPositionState
import com.google.android.gms.maps.model.CameraPosition
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState
import dev.zacsweers.metrox.viewmodel.metroViewModel

@Composable
fun HogeScreen(
  modifier: Modifier = Modifier,
  viewModel: HogeViewModel = metroViewModel(),
) {
  val uiState: HogeUiState by viewModel.uiState.collectAsStateWithLifecycle()

  LifecycleResumeEffect(key1 = Unit) {
    viewModel.onResume()

    onPauseOrDispose {
      // do nothing
    }
  }

  SmokingAreaMap(
    smokingAreaList = uiState.smokingAreaList,
    modifier = modifier,
  )
}

@Composable
private fun SmokingAreaMap(
  smokingAreaList: List<SmokingArea>,
  modifier: Modifier = Modifier,
) {
  val firstLocation = smokingAreaList.firstOrNull()?.let {
    LatLng(it.location.latitude.value, it.location.longitude.value)
  } ?: LatLng(35.6889544, 139.6992443)
  val cameraPositionState: CameraPositionState = rememberCameraPositionState()

  LaunchedEffect(key1 = firstLocation) {
    cameraPositionState.position = CameraPosition.fromLatLngZoom(firstLocation, 13f)
  }

  GoogleMap(
    modifier = modifier.fillMaxSize(),
    cameraPositionState = cameraPositionState,
    uiSettings = MapUiSettings(zoomControlsEnabled = false),
  ) {
    smokingAreaList.forEach { smokingArea ->
      Marker(
        state = MarkerState(
          position = LatLng(
            smokingArea.location.latitude.value,
            smokingArea.location.longitude.value,
          ),
        ),
        title = smokingArea.name,
      )
    }
  }
}
