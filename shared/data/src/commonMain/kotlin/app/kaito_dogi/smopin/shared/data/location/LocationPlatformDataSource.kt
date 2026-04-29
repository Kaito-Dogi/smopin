package app.kaito_dogi.smopin.shared.data.location

import kotlinx.coroutines.flow.Flow
import kotlin.time.Duration

interface LocationPlatformDataSource {
  fun getCurrentLocationStream(
    isPrecise: Boolean,
    intervalDuration: Duration,
  ): Flow<LocationDataModel>
}
