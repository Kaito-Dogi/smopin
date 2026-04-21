package app.kaito_dogi.smopin.shared.data.location

import app.kaito_dogi.smopin.shared.domain.smokingArea.location.Location
import app.kaito_dogi.smopin.shared.domain.smokingArea.location.LocationRepository
import dev.zacsweers.metro.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

@Inject
internal class DefaultLocationRepository(
  private val locationDataSource: LocationDataSource,
) : LocationRepository {
  override fun getCurrentLocation(isPreciseEnabled: Boolean): Flow<Location?> = locationDataSource.getCurrentLocation(isPreciseEnabled = isPreciseEnabled)
    .map(transform = LocationMapper::toDomainModel)
}
