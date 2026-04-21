package app.kaito_dogi.smopin.shared.location

import app.kaito_dogi.smopin.shared.data.location.LocationDataModel
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.Priority
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlin.time.Duration
import kotlin.time.DurationUnit

internal actual class PlatformLocationClient(
  private val fusedLocationClient: FusedLocationProviderClient,
) {
  actual fun getLocationStream(
    isPreciseEnabled: Boolean,
    intervalDuration: Duration,
  ): Flow<LocationDataModel> = callbackFlow {
    val locationCallback = object : LocationCallback() {
      override fun onLocationResult(locationResult: LocationResult) {
        val location = locationResult.lastLocation ?: return
        trySend(
          element = LocationMapper.toDataModel(location = location),
        )
      }
    }
    val priority = if (isPreciseEnabled) Priority.PRIORITY_HIGH_ACCURACY else Priority.PRIORITY_BALANCED_POWER_ACCURACY
    val request = LocationRequest.Builder(
      priority,
      intervalDuration.toLong(unit = DurationUnit.MILLISECONDS),
    ).build()
    fusedLocationClient.requestLocationUpdates(
      request,
      locationCallback,
      null,
    )
    awaitClose {
      fusedLocationClient.removeLocationUpdates(locationCallback)
    }
  }
}
