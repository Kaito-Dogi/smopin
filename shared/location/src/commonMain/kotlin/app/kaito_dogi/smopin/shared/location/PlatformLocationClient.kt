package app.kaito_dogi.smopin.shared.location

import app.kaito_dogi.smopin.shared.data.location.LocationDataModel
import kotlinx.coroutines.flow.Flow
import kotlin.time.Duration

internal expect class PlatformLocationClient {
  fun getLocationStream(
    isPreciseEnabled: Boolean,
    intervalDuration: Duration,
  ): Flow<LocationDataModel>
}
