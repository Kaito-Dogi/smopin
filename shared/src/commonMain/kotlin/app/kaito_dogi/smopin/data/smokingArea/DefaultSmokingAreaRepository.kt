package app.kaito_dogi.smopin.data.smokingArea

import app.kaito_dogi.smopin.domain.smokingArea.SmokingArea
import app.kaito_dogi.smopin.domain.smokingArea.SmokingAreaRepository
import app.kaito_dogi.smopin.network.smokingArea.FakeSmokingAreaNetworkDataSource

internal class DefaultSmokingAreaRepository(
  private val smokingAreaNetworkDataSource: FakeSmokingAreaNetworkDataSource,
) : SmokingAreaRepository {
  override suspend fun getSmokingAreaList(): List<SmokingArea> {
    return smokingAreaNetworkDataSource.getSmokingAreaList()
  }
}
