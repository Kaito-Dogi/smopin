package app.kaito_dogi.smopin.network.smokingArea

import app.kaito_dogi.smopin.data.smokingArea.SmokingAreaNetworkDataSource
import app.kaito_dogi.smopin.domain.smokingArea.Latitude
import app.kaito_dogi.smopin.domain.smokingArea.Location
import app.kaito_dogi.smopin.domain.smokingArea.Longitude
import app.kaito_dogi.smopin.domain.smokingArea.SmokingArea

internal actual class DefaultSmokingAreaNetworkDataSource : SmokingAreaNetworkDataSource {
  actual override suspend fun getSmokingAreaList(): List<SmokingArea> {
    return List(size = 3) {
      SmokingArea(
        name = "Mock Smoking Area $it",
        location = Location(
          latitude = Latitude(value = 35.6889544 + it * 0.1),
          longitude = Longitude(value = 139.6992443 + it * 0.1),
        ),
      )
    }
  }
}
