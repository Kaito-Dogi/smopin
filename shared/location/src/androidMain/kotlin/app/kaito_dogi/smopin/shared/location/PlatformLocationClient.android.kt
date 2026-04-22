package app.kaito_dogi.smopin.shared.location

import android.Manifest
import android.app.Application
import android.os.Looper
import androidx.annotation.RequiresPermission
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

@Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")
internal actual class PlatformLocationClient(
  private val fusedLocationClient: FusedLocationProviderClient,
  private val application: Application,
) {
  @RequiresPermission(
    anyOf = [
      Manifest.permission.ACCESS_FINE_LOCATION,
      Manifest.permission.ACCESS_COARSE_LOCATION,
    ],
  )
  actual fun getLocation(
    isPreciseEnabled: Boolean,
    intervalDuration: Duration,
  ): Flow<LocationDataModel?> = callbackFlow {
    val locationCallback = object : LocationCallback() {
      override fun onLocationResult(locationResult: LocationResult) {
        for (currentLocation in locationResult.locations) {
          trySend(element = currentLocation.let(block = LocationMapper::toDataModel))
        }
      }
    }

    val locationRequest = LocationRequest.Builder(intervalDuration.toLong(unit = DurationUnit.MILLISECONDS)).setPriority(if (isPreciseEnabled) Priority.PRIORITY_HIGH_ACCURACY else Priority.PRIORITY_BALANCED_POWER_ACCURACY).build()

    val looper = Looper.getMainLooper()

    fusedLocationClient.requestLocationUpdates(
      locationRequest,
      locationCallback,
      looper,
    )

    awaitClose {
      fusedLocationClient.removeLocationUpdates(locationCallback)

      // TODO: Looper の扱いを調べる
      looper.quitSafely()
    }
  }
}
