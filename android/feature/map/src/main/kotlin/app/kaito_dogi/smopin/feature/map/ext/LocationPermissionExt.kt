package app.kaito_dogi.smopin.feature.map.ext

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat
import app.kaito_dogi.smopin.feature.map.LocationPermissionState

// FIXME: リファクタする
internal val LOCATION_PERMISSION_LIST = arrayOf(
  Manifest.permission.ACCESS_FINE_LOCATION,
  Manifest.permission.ACCESS_COARSE_LOCATION,
)

internal fun Context.toLocationPermissionState(): LocationPermissionState {
  val hasFineLocationPermission = hasPermission(permission = Manifest.permission.ACCESS_FINE_LOCATION)
  val hasCoarseLocationPermission = hasPermission(permission = Manifest.permission.ACCESS_COARSE_LOCATION)

  return when {
    hasFineLocationPermission -> LocationPermissionState.Granted(isPrecise = true)
    hasCoarseLocationPermission -> LocationPermissionState.Granted(isPrecise = false)
    else -> LocationPermissionState.Denied
  }
}

internal fun Map<String, Boolean>.toLocationPermissionState(): LocationPermissionState = when {
  this[Manifest.permission.ACCESS_FINE_LOCATION] == true -> LocationPermissionState.Granted(isPrecise = true)
  this[Manifest.permission.ACCESS_COARSE_LOCATION] == true -> LocationPermissionState.Granted(isPrecise = false)
  else -> LocationPermissionState.Denied
}

private fun Context.hasPermission(permission: String): Boolean = ContextCompat.checkSelfPermission(
  this,
  permission,
) == PackageManager.PERMISSION_GRANTED
