package app.kaito_dogi.smopin.shared.domain.smokingArea.location

import kotlinx.coroutines.flow.Flow
import kotlin.time.Duration

interface LocationRepository {
  fun getCurrentLocation(
    isPreciseEnabled: Boolean,
    intervalDuration: Duration,
  ): Flow<Location?>
}
