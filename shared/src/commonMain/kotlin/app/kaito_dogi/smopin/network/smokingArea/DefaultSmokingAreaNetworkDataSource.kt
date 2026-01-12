package app.kaito_dogi.smopin.network.smokingArea

import app.kaito_dogi.smopin.data.smokingArea.SmokingAreaNetworkDataSource
import app.kaito_dogi.smopin.domain.smokingArea.SmokingArea

internal expect class DefaultSmokingAreaNetworkDataSource : SmokingAreaNetworkDataSource {
  override suspend fun getSmokingAreaList(): List<SmokingArea>
}
