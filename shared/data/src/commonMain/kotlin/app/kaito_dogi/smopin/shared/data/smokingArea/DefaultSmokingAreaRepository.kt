package app.kaito_dogi.smopin.shared.data.smokingArea

import app.kaito_dogi.smopin.shared.domain.smokingArea.SmokingArea
import app.kaito_dogi.smopin.shared.domain.smokingArea.SmokingAreaRepository

internal class DefaultSmokingAreaRepository(
  private val smokingAreaNetworkDataSource: SmokingAreaNetworkDataSource,
) : SmokingAreaRepository {
  override suspend fun getSmokingAreaList(): List<SmokingArea> {
    return smokingAreaNetworkDataSource.getSmokingAreaList()
      .map(transform = SmokingAreaDataModel::toDomainModel)
  }
}
