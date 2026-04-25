package app.kaito_dogi.smopin.shared.data.location

import kotlinx.coroutines.flow.Flow
import kotlin.time.Duration

interface LocationDataSource {
  fun getCurrentLocationStream(
    isPreciseEnabled: Boolean,
    intervalDuration: Duration,
  ): Flow<LocationDataModel?>
}
