package app.kaito_dogi.smopin.feature.hoge

import app.kaito_dogi.smopin.shared.domain.smokingArea.Latitude
import app.kaito_dogi.smopin.shared.domain.smokingArea.Location
import app.kaito_dogi.smopin.shared.domain.smokingArea.Longitude
import app.kaito_dogi.smopin.shared.domain.smokingArea.SmokingArea

data class HogeUiState(
  val smokingAreaList: List<SmokingArea>,
) {
  companion object {
    fun createInitial(): HogeUiState = HogeUiState(smokingAreaList = emptyList())

    fun createPreview(): HogeUiState = HogeUiState(
      smokingAreaList = listOf(
        SmokingArea(
          name = "Shibuya Smoking Area",
          location = Location(
            latitude = Latitude(value = 35.6580),
            longitude = Longitude(value = 139.7016),
          ),
        ),
        SmokingArea(
          name = "Tokyo Station Smoking Area",
          location = Location(
            latitude = Latitude(value = 35.6812),
            longitude = Longitude(value = 139.7671),
          ),
        ),
      ),
    )
  }
}
