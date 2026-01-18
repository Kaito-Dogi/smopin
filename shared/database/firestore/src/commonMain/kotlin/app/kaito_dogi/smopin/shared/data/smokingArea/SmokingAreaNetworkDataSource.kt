package app.kaito_dogi.smopin.shared.data.smokingArea

import app.kaito_dogi.smopin.shared.doma

interface SmokingAreaNetworkDataSource {
  suspend fun getSmokingAreaList(): List<SmokingArea>
}
