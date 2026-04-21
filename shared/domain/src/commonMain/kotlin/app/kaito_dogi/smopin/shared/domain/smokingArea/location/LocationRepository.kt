package app.kaito_dogi.smopin.shared.domain.smokingArea.location

import kotlinx.coroutines.flow.Flow

interface LocationRepository {
  fun getCurrentLocationStream(isPreciseEnabled: Boolean): Flow<Location>
}
