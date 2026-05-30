package app.kaito_dogi.smopin.shared.location

import android.Manifest
import android.os.Looper
import androidx.annotation.RequiresPermission
import app.kaito_dogi.smopin.shared.common.AppException
import app.kaito_dogi.smopin.shared.data.location.LocationDataModel
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.Priority
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.conflate
import kotlin.time.Duration
import kotlin.time.DurationUnit

internal class DefaultPlatformLocationClient(
  private val fusedLocationClient: FusedLocationProviderClient,
) : PlatformLocationClient {
  @RequiresPermission(anyOf = [Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION])
  override fun getCurrentLocationStream(
    isPrecise: Boolean,
    intervalDuration: Duration,
  ): Flow<LocationDataModel> = callbackFlow {
    require(value = intervalDuration.isFinite() && intervalDuration > Duration.ZERO) {
      "intervalDuration must be finite and greater than zero: $intervalDuration"
    }

    val intervalMillis = intervalDuration.toLong(unit = DurationUnit.MILLISECONDS).apply {
      require(value = this >= 1L) {
        "intervalDuration must be at least 1 millisecond when converted to milliseconds: $intervalDuration"
      }
    }

    val locationCallback = object : LocationCallback() {
      override fun onLocationResult(locationResult: LocationResult) {
        for (currentLocation in locationResult.locations) {
          currentLocation?.let {
            // conflate で最新の値のみを send するため、isFailure や isClosed で close しない
            val channelResult = trySend(element = it.let(block = LocationMapper::toDataModel))

            // isFailure の場合は後続の Location を send できないため、for ループを抜ける
            if (channelResult.isFailure) {
              break
            }
          }
        }
      }
    }

    val locationRequest = LocationRequest.Builder(intervalMillis)
      .setPriority(if (isPrecise) Priority.PRIORITY_HIGH_ACCURACY else Priority.PRIORITY_BALANCED_POWER_ACCURACY)
      .setMinUpdateIntervalMillis(intervalMillis)
      .build()

    fusedLocationClient.requestLocationUpdates(
      locationRequest,
      locationCallback,
      Looper.getMainLooper(),
    ).addOnFailureListener { cause: Exception ->
      // TODO: エラーハンドリング
      close(cause = AppException.Unknown(cause = cause))
    }

    awaitClose {
      fusedLocationClient.removeLocationUpdates(locationCallback)
    }
  }.conflate()
}
