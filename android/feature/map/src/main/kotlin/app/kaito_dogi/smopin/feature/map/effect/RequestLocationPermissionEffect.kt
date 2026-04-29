package app.kaito_dogi.smopin.feature.map.effect

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat

@Composable
internal fun RequestLocationPermissionEffect(
  onLocationPermissionGranted: (Boolean) -> Unit,
  onLocationPermissionDenied: () -> Unit,
) {
  val context = LocalContext.current
  val locationPermissionLauncher = rememberLauncherForActivityResult(
    contract = ActivityResultContracts.RequestMultiplePermissions(),
    onResult = {
      when {
        it[PERMISSION_ACCESS_FINE_LOCATION] == true -> onLocationPermissionGranted(true)
        it[PERMISSION_ACCESS_COARSE_LOCATION] == true -> onLocationPermissionGranted(false)
        else -> onLocationPermissionDenied()
      }
    },
  )

  LaunchedEffect(key1 = context) {
    val isAccessFineLocationPermissionGranted = ContextCompat.checkSelfPermission(
      context,
      PERMISSION_ACCESS_FINE_LOCATION,
    ) == PackageManager.PERMISSION_GRANTED

    val isAccessCoarseLocationPermissionGranted = ContextCompat.checkSelfPermission(
      context,
      PERMISSION_ACCESS_COARSE_LOCATION,
    ) == PackageManager.PERMISSION_GRANTED

    when {
      isAccessFineLocationPermissionGranted -> onLocationPermissionGranted(true)
      isAccessCoarseLocationPermissionGranted -> onLocationPermissionGranted(false)
      else -> locationPermissionLauncher.launch(input = PERMISSION_LIST.toTypedArray())
    }
  }
}

private const val PERMISSION_ACCESS_FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION
private const val PERMISSION_ACCESS_COARSE_LOCATION = Manifest.permission.ACCESS_COARSE_LOCATION
private val PERMISSION_LIST = listOf(
  PERMISSION_ACCESS_FINE_LOCATION,
  PERMISSION_ACCESS_COARSE_LOCATION,
)
