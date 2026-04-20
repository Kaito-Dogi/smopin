package app.kaito_dogi.smopin.shared.data.location

import kotlinx.coroutines.flow.Flow

interface LocationDataSource {
  fun getCurrentLocation(): Flow<LocationDataModel?>
}
