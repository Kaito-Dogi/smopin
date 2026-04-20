package app.kaito_dogi.smopin.shared.location

import app.kaito_dogi.smopin.shared.data.location.LocationDataModel
import app.kaito_dogi.smopin.shared.data.location.LocationDataSource
import dev.zacsweers.metro.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

@Inject
internal class DefaultLocationDataSource : LocationDataSource {
  override fun getCurrentLocation(): Flow<LocationDataModel?> = flow {
    emit(
      value = LocationDataModel(
        latitude = 35.0,
        longitude = 135.0,
      ),
    )
  }
}
