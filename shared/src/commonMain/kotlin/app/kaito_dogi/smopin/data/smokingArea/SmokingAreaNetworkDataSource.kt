package app.kaito_dogi.smopin.data.smokingArea

import app.kaito_dogi.smopin.domain.smokingArea.SmokingArea

interface SmokingAreaNetworkDataSource {
  suspend fun getSmokingAreaList(): List<SmokingArea>
}
