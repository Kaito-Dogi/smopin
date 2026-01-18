package app.kaito_dogi.smopin.shared.data.smokingArea

internal class DefaultSmokingAreaRepository(
  private val smokingAreaNetworkDataSource: FakeSmokingAreaNetworkDataSource,
) : SmokingAreaRepository {
  override suspend fun getSmokingAreaList(): List<SmokingArea> {
    return smokingAreaNetworkDataSource.getSmokingAreaList()
  }
}
