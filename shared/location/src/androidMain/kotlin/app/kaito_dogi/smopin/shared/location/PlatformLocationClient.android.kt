package app.kaito_dogi.smopin.shared.location

import app.kaito_dogi.smopin.shared.data.location.LocationDataModel
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource
import kotlinx.coroutines.tasks.await

internal actual class PlatformLocationClient(
  private val fusedLocationClient: FusedLocationProviderClient,
) {
  actual suspend fun getLocation(isPreciseEnabled: Boolean): LocationDataModel = fusedLocationClient.getCurrentLocation(
    if (isPreciseEnabled) Priority.PRIORITY_HIGH_ACCURACY else Priority.PRIORITY_BALANCED_POWER_ACCURACY,
    CancellationTokenSource().token,
  ).await().let(block = LocationMapper::toDataModel)
}
