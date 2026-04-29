package app.kaito_dogi.smopin.feature.map.effect

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import app.kaito_dogi.smopin.feature.map.ext.toLatLng
import app.kaito_dogi.smopin.feature.map.state.MapUiState
import com.google.android.gms.maps.model.CameraPosition
import com.google.maps.android.compose.CameraPositionState

@Composable
internal fun AdjustCameraPositonEffect(
  uiState: MapUiState.PermissionGranted.LocationSuccess,
  cameraPositionState: CameraPositionState,
  cameraPositionZoom: Float,
  onCameraPositionAdjust: () -> Unit,
) {
  if (uiState.isCameraPositionAdjusted) return

  // 一度だけ実行するため、key に Unit を渡す
  LaunchedEffect(key1 = Unit) {
    cameraPositionState.position = CameraPosition.fromLatLngZoom(
      uiState.currentLocation.toLatLng(),
      cameraPositionZoom,
    )

    onCameraPositionAdjust()
  }
}
