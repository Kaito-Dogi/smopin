package app.kaito_dogi.smopin.shared.data.smokingArea

import app.kaito_dogi.smopin.shared.domain.smokingArea.SmokingArea
import app.kaito_dogi.smopin.shared.domain.smokingArea.SmokingAreaRepository
import dev.zacsweers.metro.Inject

@Inject
internal class DefaultSmokingAreaRepository(
  private val smokingAreaNetworkDataSource: SmokingAreaNetworkDataSource,
) : SmokingAreaRepository {
  override suspend fun getSmokingAreaList(): List<SmokingArea> =
    smokingAreaNetworkDataSource.getSmokingAreaList()
      .map(transform = SmokingAreaMapper::toDomainModel)
}
