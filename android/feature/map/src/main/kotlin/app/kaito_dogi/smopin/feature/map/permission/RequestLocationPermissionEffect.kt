package app.kaito_dogi.smopin.feature.map.permission

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import app.kaito_dogi.smopin.feature.map.LocationPermissionState
import app.kaito_dogi.smopin.feature.map.ext.LOCATION_PERMISSION_LIST
import app.kaito_dogi.smopin.feature.map.ext.toLocationPermissionState

// FIXME: 権限取得処理を共通化する & リファクタする
@Composable
internal fun RequestLocationPermissionEffect(
  locationPermissionState: LocationPermissionState,
  onLocationPermissionStateChange: (LocationPermissionState) -> Unit,
) {
  val context = LocalContext.current
  val locationPermissionLauncher = rememberLauncherForActivityResult(
    contract = ActivityResultContracts.RequestMultiplePermissions(),
    onResult = { permissionMap ->
      onLocationPermissionStateChange(permissionMap.toLocationPermissionState())
    },
  )

  LaunchedEffect(
    key1 = context,
    key2 = locationPermissionState,
  ) {
    if (locationPermissionState !is LocationPermissionState.NotRequested) {
      return@LaunchedEffect
    }

    when (val currentLocationPermissionState = context.toLocationPermissionState()) {
      is LocationPermissionState.Granted -> onLocationPermissionStateChange(currentLocationPermissionState)
      LocationPermissionState.Denied, LocationPermissionState.NotRequested -> locationPermissionLauncher.launch(LOCATION_PERMISSION_LIST)
    }
  }
}
