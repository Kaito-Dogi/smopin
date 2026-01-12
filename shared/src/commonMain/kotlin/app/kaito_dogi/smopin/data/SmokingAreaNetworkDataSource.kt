package app.kaito_dogi.smopin.data

import app.kaito_dogi.smopin.domain.smokingArea.SmokingArea

interface SmokingAreaNetworkDataSource {
  suspend fun getSmokingAreaList(): List<SmokingArea>
}
