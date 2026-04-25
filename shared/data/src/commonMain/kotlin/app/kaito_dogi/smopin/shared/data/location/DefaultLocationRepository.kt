package app.kaito_dogi.smopin.shared.data.location

import app.kaito_dogi.smopin.shared.domain.smokingArea.location.Location
import app.kaito_dogi.smopin.shared.domain.smokingArea.location.LocationRepository
import dev.zacsweers.metro.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlin.time.Duration

@Inject
internal class DefaultLocationRepository(
  private val locationDataSource: LocationDataSource,
) : LocationRepository {
  override fun getCurrentLocationStream(
    isPrecise: Boolean,
    intervalDuration: Duration,
  ): Flow<Location?> = locationDataSource.getCurrentLocationStream(
    isPrecise = isPrecise,
    intervalDuration = intervalDuration,
  )
    .map(transform = LocationMapper::toDomainModel)
}
