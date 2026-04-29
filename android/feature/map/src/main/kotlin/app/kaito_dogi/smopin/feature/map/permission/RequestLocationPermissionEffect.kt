package app.kaito_dogi.smopin.feature.map.permission

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import app.kaito_dogi.smopin.feature.map.MapUiState

// FIXME: 権限取得処理を共通化する & リファクタする
@Composable
internal fun RequestLocationPermissionEffect(
  uiState: MapUiState,
  onLocationPermissionGranted: (Boolean) -> Unit,
  onLocationPermissionDenied: () -> Unit,
) {
  val context = LocalContext.current
  val locationPermissionLauncher = rememberLauncherForActivityResult(
    contract = ActivityResultContracts.RequestMultiplePermissions(),
    onResult = { permissionMap ->
      when {
        permissionMap[Manifest.permission.ACCESS_FINE_LOCATION] == true -> onLocationPermissionGranted(true)
        permissionMap[Manifest.permission.ACCESS_COARSE_LOCATION] == true -> onLocationPermissionGranted(false)
        else -> onLocationPermissionDenied()
      }
    },
  )

  LaunchedEffect(
    key1 = context,
    key2 = uiState,
  ) {
    if (uiState !is MapUiState.PermissionNotRequested) {
      return@LaunchedEffect
    }

    val hasFineLocationPermission = context.hasPermission(permission = Manifest.permission.ACCESS_FINE_LOCATION)
    val hasCoarseLocationPermission = context.hasPermission(permission = Manifest.permission.ACCESS_COARSE_LOCATION)

    when {
      hasFineLocationPermission -> onLocationPermissionGranted(true)
      hasCoarseLocationPermission -> onLocationPermissionGranted(false)
      else -> locationPermissionLauncher.launch(input = LOCATION_PERMISSION_LIST)
    }
  }
}

private val LOCATION_PERMISSION_LIST = arrayOf(
  Manifest.permission.ACCESS_FINE_LOCATION,
  Manifest.permission.ACCESS_COARSE_LOCATION,
)

private fun Context.hasPermission(permission: String): Boolean = ContextCompat.checkSelfPermission(
  this,
  permission,
) == PackageManager.PERMISSION_GRANTED
